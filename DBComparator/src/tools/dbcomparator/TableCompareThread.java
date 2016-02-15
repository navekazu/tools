/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.dbcomparator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import tools.dbcomparator.entity.CompareResultRecord;
import tools.dbcomparator.entity.DataInfo;

/**
 *
 * @author k_watanabe
 */
public class TableCompareThread extends Thread {
    private String tableName;
    private List<DBSubject> dbSubjectList;
    private TreeSet<DataInfo> pkSet;
    private List<DataInfo> unmatchList;
    private CompareProgressListener compareProgressListener;

    public TableCompareThread(String tableName, List<DBSubject> dbSubjectList, CompareProgressListener compareProgressListener) {
        this.tableName = tableName;
        this.dbSubjectList = dbSubjectList;
        this.compareProgressListener = compareProgressListener;
    }

    public String getTableName() {
        return tableName;
    }

    public List<DataInfo> getUnmatchList() {
        return unmatchList;
    }

    public List<DBSubject> getDbSubjectList() {
        return dbSubjectList;
    }

    @Override
    public void run() {
        DBComparator.writeVerbose("Table compare start. Table->"+tableName);

        // PKがなければ対象外
        for (DBSubject dbSubject: dbSubjectList) {
            if (dbSubject.getTableInfo(tableName).getPrimaryKeyColumnName()==null) {
                DBComparator.writeVerbose("Primary key nothing. Table->"+tableName);
                if (compareProgressListener!=null) {
                    compareProgressListener.finish(tableName, 3, 0);
                }
                return ;
            }
        }

        try {
            // PK一覧作成
            if (compareProgressListener!=null) {
                compareProgressListener.searchPK(tableName, 0, 0, 0);
            }
            pkSet = new TreeSet<>(new DataInfoComparator());
            int targetLoop = 0;
            for (DBSubject dbSubject: dbSubjectList) {
                PreparedStatement preparedStatement = dbSubject.createPKStatement(tableName);
                ResultSet resultSet = preparedStatement.executeQuery();
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                int pkLoop=0;
                while (resultSet.next()) {
                    DataInfo pkInfo = new DataInfo();
                    pkInfo.columnValues = new Object[resultSetMetaData.getColumnCount()];
                    for (int loop=0; loop<pkInfo.columnValues.length; loop++) {
                        if (resultSetMetaData.getColumnType(loop+1)==Types.TIMESTAMP) {
                            pkInfo.columnValues[loop] = resultSet.getTimestamp(loop+1);
                        } else {
                            pkInfo.columnValues[loop] = resultSet.getObject(loop+1);
                        }
                    }
                    pkSet.add(pkInfo);
                    if (compareProgressListener!=null) {
                        compareProgressListener.searchPK(tableName, targetLoop, pkLoop+1, pkSet.size());
                    }
                    pkLoop++;
                }
                resultSet.close();
                dbSubject.closePKStatement(tableName);
                targetLoop++;
            }
            if (compareProgressListener!=null) {
                compareProgressListener.recordCount(tableName, pkSet.size());
            }

            // データチェック
            unmatchList = new ArrayList<>();
//            for (DataInfo pkInfo: pkSet) {
            if (!pkSet.isEmpty()) {
                int pkInfoLoopCount = pkSet.size()/DBSubject.MULTI_PREPARED_STATEMENT_SIZE;
                if (pkSet.size()%DBSubject.MULTI_PREPARED_STATEMENT_SIZE!=0) {
                    pkInfoLoopCount++;
                }

                TreeSet<DataInfo> pkSetCopy = new TreeSet<>(pkSet);
                for (int loop1=0; loop1<pkInfoLoopCount; loop1++) {
                    List<DataInfo> pkInfoList = new ArrayList<>();
                    
                    // 検索キー作成
/*
                    for (int loop2=loop1*DBSubject.MULTI_PREPARED_STATEMENT_SIZE; loop2<pkSet.size();  loop2++) {
                        if (loop2>=loop1*DBSubject.MULTI_PREPARED_STATEMENT_SIZE+DBSubject.MULTI_PREPARED_STATEMENT_SIZE) {
                            break;
                        }
                        pkInfoList.add((DataInfo)pkSet.toArray()[loop2]);
                    }
*/
                    for (int loop2=0; loop2<DBSubject.MULTI_PREPARED_STATEMENT_SIZE; loop2++) {
                        if (pkSetCopy.isEmpty()) {
                            break;
                        }
                        pkInfoList.add(pkSetCopy.pollFirst());
                    }

                    // 一括検索
                    Set<DataInfo> dataSet = new TreeSet<>(new DataInfoComparator());
                    boolean unmatchFlag = false;
                    for (int loop2=0; loop2<dbSubjectList.size(); loop2++) {
                        DBSubject dbSubject = dbSubjectList.get(loop2);
                        PreparedStatement preparedStatement = dbSubject.createMultiDataStatement(tableName, pkInfoList);
                        ResultSet resultSet = preparedStatement.executeQuery();
                        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                        int resultSetCount = 0;
                        while (resultSet.next()) {
                            DataInfo dataInfo = new DataInfo();
                            dataInfo.columnValues = new Object[resultSetMetaData.getColumnCount()];
                            for (int loop3=0; loop3<dataInfo.columnValues.length; loop3++) {
                                if (resultSetMetaData.getColumnType(loop3+1)==Types.TIMESTAMP) {
                                    dataInfo.columnValues[loop3] = resultSet.getTimestamp(loop3+1);
                                } else {
                                    dataInfo.columnValues[loop3] = resultSet.getObject(loop3+1);
                                }
                            }
                            dataSet.add(dataInfo);
                            resultSetCount++;
                        }

                        // キーの件数とデータ件数が違えばアンマッチ
                        if (pkInfoList.size()!=resultSetCount) {
                            unmatchFlag = true;
                        }
                        resultSet.close();
                    }
                    
                    // キーの件数とdataSetの件数が違えばアンマッチ
                    if (pkInfoList.size()!=dataSet.size()) {
                        unmatchFlag = true;
                    }
                    dataSet.clear();

                    // 一括検索で差異があったら、一件ずつ検索
                    if (unmatchFlag) {
                        int pkInfoListCount = 0;
                        for (DataInfo pkInfo: pkInfoList) {
                            Set<DataInfo> dataSetSingle = new TreeSet<>(new DataInfoComparator());
                            boolean nothing = false;
                            for (int loop2=0; loop2<dbSubjectList.size(); loop2++) {
                                DBSubject dbSubject = dbSubjectList.get(loop2);
                                PreparedStatement preparedStatement = dbSubject.createSingleDataStatement(tableName, pkInfo);
                                ResultSet resultSet = preparedStatement.executeQuery();
                                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                                if (resultSet.next()) {
                                    DataInfo dataInfo = new DataInfo();
                                    dataInfo.columnValues = new Object[resultSetMetaData.getColumnCount()];
                                    for (int loop3=0; loop3<dataInfo.columnValues.length; loop3++) {
//                                        if ("EUPD_DTIM".equals(resultSetMetaData.getColumnName(loop3+1))) {
//                                            dataInfo.columnValues[loop3] = null;
//                                            continue;
//                                        }
                                        if (resultSetMetaData.getColumnType(loop3+1)==Types.TIMESTAMP) {
                                            dataInfo.columnValues[loop3] = resultSet.getTimestamp(loop3+1);
                                        } else {
                                            dataInfo.columnValues[loop3] = resultSet.getObject(loop3+1);
                                        }
                                    }
                                    dataSetSingle.add(dataInfo);
                                } else {
                                    nothing = true;
                                }
                                resultSet.close();
                            }
                            if (nothing) {
                                // データなしならアンマッチ確定
                                unmatchList.add(pkInfo);
                            } else if (dataSetSingle.size()!=1) {
                                // 複数データとなるならアンマッチ
                                unmatchList.add(pkInfo);
                            }
                            dataSetSingle.clear();

                            if (compareProgressListener!=null) {
                                compareProgressListener.progress(tableName, loop1+pkInfoListCount+1);
                            }
                            pkInfoListCount++;
                        }
                        
                    }

                    if (compareProgressListener!=null) {
                        compareProgressListener.progress(tableName, loop1*DBSubject.MULTI_PREPARED_STATEMENT_SIZE);
                    }
                }
            }
/*
            for (int loop1=0;
                    (!pkSet.isEmpty()) && loop1<(pkSet.size()/DBSubject.MULTI_PREPARED_STATEMENT_SIZE*DBSubject.MULTI_PREPARED_STATEMENT_SIZE)+;
                    loop1=+DBSubject.MULTI_PREPARED_STATEMENT_SIZE) {
                List<DataInfo> pkInfoList = new ArrayList<>();

                // DBSubject.MULTI_PREPARED_STATEMENT_SIZE分、一括で読み込む
                
                Set<DataInfo> dataSet = new TreeSet<>(new DataInfoComparator());
                boolean nothing = false;
                for (int loop2=0; loop2<dbSubjectList.size(); loop2++) {
                    DBSubject dbSubject = dbSubjectList.get(loop2);
                    PreparedStatement preparedStatement = dbSubject.createSingleDataStatement(tableName, pkInfo);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

                    if (resultSet.next()) {
                        DataInfo dataInfo = new DataInfo();
                        dataInfo.columnValues = new Object[resultSetMetaData.getColumnCount()];
                        for (int loop3=0; loop3<dataInfo.columnValues.length; loop3++) {
                            if (resultSetMetaData.getColumnType(loop3+1)==Types.TIMESTAMP) {
                                dataInfo.columnValues[loop3] = resultSet.getTimestamp(loop3+1);
                            } else {
                                dataInfo.columnValues[loop3] = resultSet.getObject(loop3+1);
                            }
                        }
                        dataSet.add(pkInfo);
                    } else {
                        // データなしならアンマッチ確定
                        nothing = true;
                    }
                    resultSet.close();
                }

                if (nothing) {
                    // データなしならアンマッチ確定
                    unmatchList.add(pkInfo);
                } else if (dataSet.size()!=1) {
                    // 複数データとなるならアンマッチ
                    unmatchList.add(pkInfo);
                }

                if (compareProgressListener!=null) {
                    compareProgressListener.progress(tableName, loop1);
                }
            }

            */
            for (int loop2=0; loop2<dbSubjectList.size(); loop2++) {
                DBSubject dbSubject = dbSubjectList.get(loop2);
                dbSubject.closeMultiDataStatement(tableName);
                dbSubject.closeSingleDataStatement(tableName);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        if (compareProgressListener!=null) {
            compareProgressListener.finish(tableName, (unmatchList.size()==0? 1: 2), unmatchList.size());
        }

        DBComparator.writeVerbose("Table compare end. Table->"+tableName+" unmatch->"+unmatchList.size());
    }


    public CompareResultRecord getCompareResultRecord(DataInfo pkInfo) throws SQLException {
        CompareResultRecord record = new CompareResultRecord(dbSubjectList.size());

        // カラム一覧の作成
        List<List<String>> columnListArray = new ArrayList<>();
        for (DBSubject dbSubject: dbSubjectList) {
            columnListArray.add(dbSubject.getColumnNameList(tableName));
        }
        record.setColumnNameList(mergeColumnList(columnListArray));

        // データ取得
        for (int loop=0; loop<dbSubjectList.size(); loop++) {
            DBSubject dbSubject = dbSubjectList.get(loop);
            Map<String, String> dataMap = dbSubject.getData(tableName, pkInfo);
            if (dataMap!=null) {
                for (String key: dataMap.keySet()) {
                    record.addSubjectValue(key, loop, dataMap.get(key));
                }
            }
        }
        
        return record;
    }

    private List<String> mergeColumnList(List<List<String>> columnListArray) {
        // 一度ユニークな全カラム名のSetを作成
        Set<String> destColumnSet = new TreeSet<>();
        for (List<String> srcColumnList: columnListArray) {
            destColumnSet.addAll(srcColumnList);
        }

        // それがどこにいるか見て、それらしい並び順に入れる
        String[] destColumnList = new String[destColumnSet.size()];
        for (String destColumnName: destColumnSet) {
            int maxIndex = -1;
            int index;
            for (List<String> srcColumnList: columnListArray) {
                index = srcColumnList.indexOf(destColumnName);
                if (index>maxIndex) {
                    maxIndex = index;
                }
            }
            destColumnList[maxIndex] = destColumnName;
        }

        return Arrays.asList(destColumnList);
    }
}
