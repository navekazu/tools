/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.dbcomparator;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
import java.util.*;
import tools.dbcomparator.entity.DBInfo;
import tools.dbcomparator.entity.DataInfo;
import tools.dbcomparator.entity.TableInfo;

/**
 *
 * @author k_watanabe
 */
public class DBSubject {
    private String subjectName;
    private DBInfo dbInfo;
    private List<String> includeList;
    private List<String> excludeList;
    private Connection connection;
    private Map<String, TableInfo> tableMap;
    private Map<String, PreparedStatement> pkPreparedStatementMap;
    private Map<String, PreparedStatement> dataPreparedStatementMapMulti;
    private Map<String, PreparedStatement> dataPreparedStatementMapSingle;

    private static Map<String, Driver> loadedDrivers = new HashMap<>();;
    
    public static final int MULTI_PREPARED_STATEMENT_SIZE = 10;

    public DBSubject(String subjectName) {
        this.subjectName = subjectName;
    }
    
    public void connect() throws MalformedURLException, ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
        Properties info = new Properties();

        if (!isEmptyString(dbInfo.getUser())) {
            info.setProperty("user", dbInfo.getUser());
        }
        if (!isEmptyString(dbInfo.getPassword())) {
            info.setProperty("password", dbInfo.getPassword());
        }

        DBComparator.writeVerbose(subjectName+" Connecting... URL->"+dbInfo.getUrl()+" User->"+dbInfo.getUser());
        if (!(isEmptyString(dbInfo.getLibraryPath())&&isEmptyString(dbInfo.getDriver()))) {
            // ドライバ指定あり
            Driver driver = loadedDrivers.get(dbInfo.getLibraryPath());
            if (driver==null) {
                URL[] lib = { new File(dbInfo.getLibraryPath()).toURI().toURL() };
                URLClassLoader loader = URLClassLoader.newInstance(lib);
                Class<Driver> cd = (Class<Driver>) loader.loadClass(dbInfo.getDriver());
                driver = cd.newInstance();
                loadedDrivers.put(dbInfo.getLibraryPath(), driver);
            }
            connection = driver.connect(dbInfo.getUrl(), info);
            DBComparator.writeVerbose(subjectName+" Connecting success.");
        } else {
            // ドライバ指定なし
//            Class.forName(entity.getDriver());
            connection = DriverManager.getConnection(dbInfo.getUrl(), info);
            DBComparator.writeVerbose(subjectName+" Connecting success.");
        }
    }

    public TableInfo getTableInfo(String name) {
        return tableMap.get(name);
    }

    public void loadTableList() throws SQLException {
        ResultSet resultSet;
        tableMap = new HashMap<>();
        pkPreparedStatementMap = new HashMap<>();
        dataPreparedStatementMapMulti = new HashMap<>();
        dataPreparedStatementMapSingle = new HashMap<>();
        DatabaseMetaData meta = connection.getMetaData();

        // テーブル一覧
        resultSet = meta.getTables(null, dbInfo.getUser(), null, new String[]{"TABLE"});
        DBComparator.writeVerbose(subjectName+" Loading table list...");
        while (resultSet.next()) {
            TableInfo tableInfo = new TableInfo();

            tableInfo.setTableName(resultSet.getString("TABLE_NAME"));
            DBComparator.writeVerbose(subjectName+" "+resultSet.getString("TABLE_NAME"));

            if (!isExcludeTable(tableInfo.getTableName())) {
                tableMap.put(tableInfo.getTableName(), tableInfo);
            }
        }
        DBComparator.writeVerbose(subjectName+" Loaded table count->"+tableMap.size());
        resultSet.close();

        // PK取得
        for (String key: tableMap.keySet()) {
            DBComparator.writeVerbose(subjectName+" Loading primary key...");
            TableInfo tableInfo = tableMap.get(key);
            List<String> primaryKeyColumnName = new ArrayList<>();
            List<Integer> primaryKeyColumnType = new ArrayList<>();

            resultSet = meta.getPrimaryKeys(null, dbInfo.getUser(), tableInfo.getTableName());
            while (resultSet.next()) {
                primaryKeyColumnName.add(resultSet.getString("COLUMN_NAME"));
            }
            resultSet.close();

            for (String columnName: primaryKeyColumnName) {
                resultSet = meta.getColumns(null, dbInfo.getUser(), tableInfo.getTableName(), columnName);
                resultSet.next();   // 必ず1つヒットするはず
                primaryKeyColumnType.add(resultSet.getInt("DATA_TYPE"));
                resultSet.close();
            }

            if (primaryKeyColumnName.size()>=1) {
                tableInfo.setPrimaryKeyColumnName(primaryKeyColumnName);
            }
            if (primaryKeyColumnType.size()>=1) {
                tableInfo.setPrimaryKeyColumnType(primaryKeyColumnType);
            }

            DBComparator.writeVerbose(subjectName+" Loaded primary key. table->"+tableInfo.getTableName()+ " count->"+primaryKeyColumnName.size());
        }

        // レコード数取得
        for (String key: tableMap.keySet()) {
            DBComparator.writeVerbose(subjectName+" Loading record count...");
            TableInfo tableInfo = tableMap.get(key);
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery("select count(*) CNT from "+tableInfo.getTableName());
            while (resultSet.next()) {
                tableInfo.setCount(resultSet.getInt("CNT"));
            }
            DBComparator.writeVerbose(subjectName+" Loadedrecord count. count->"+tableInfo.getCount());
            resultSet.close();
        }
    }

    public PreparedStatement createPKStatement(String table) throws SQLException {

        PreparedStatement preparedStatement = pkPreparedStatementMap.get(table);
        if (preparedStatement!=null) {
            return preparedStatement;
        }

        StringBuilder cols = new StringBuilder();
        TableInfo tableInfo = tableMap.get(table);

        if (tableInfo==null) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        for (String colName: tableInfo.getPrimaryKeyColumnName()) {
            if (cols.length()!=0) {
                cols.append(",");
            }
            cols.append(colName);
        }
        preparedStatement = connection.prepareStatement("select "+cols.toString()+" from "+table);
        pkPreparedStatementMap.put(table, preparedStatement);

        return preparedStatement;
    }

    public void closePKStatement(String table) throws SQLException {

        PreparedStatement preparedStatement = pkPreparedStatementMap.get(table);
        if (preparedStatement!=null) {
            preparedStatement.close();
            pkPreparedStatementMap.remove(table);
        }
    }

    public PreparedStatement createMultiDataStatement(String table, List<DataInfo> pkInfoList) throws SQLException {
        return createDataStatement(table, dataPreparedStatementMapMulti, MULTI_PREPARED_STATEMENT_SIZE, pkInfoList);
    }

    public PreparedStatement createSingleDataStatement(String table, DataInfo pkInfo) throws SQLException {
        List<DataInfo> pkInfoList = new ArrayList<>();
        pkInfoList.add(pkInfo);
        return createDataStatement(table, dataPreparedStatementMapSingle, 1, pkInfoList);
    }

    private PreparedStatement createDataStatement(String table, Map<String, PreparedStatement> dataPreparedStatementMap, int preparedStatementSize, List<DataInfo> pkInfoList) throws SQLException {
        PreparedStatement preparedStatement = null;
        if (dataPreparedStatementMap!=null) {
            preparedStatement = dataPreparedStatementMap.get(table);
        }
        if (preparedStatement!=null) {
            setStakeHolder(table, preparedStatement, preparedStatementSize, pkInfoList);
            return preparedStatement;
        }
        String where = getPKWhereStatement(table);
        StringBuilder wheres = new StringBuilder();
        TableInfo tableInfo = tableMap.get(table);

        if (tableInfo==null) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        for (int loop=0; loop<preparedStatementSize; loop++) {
            if (wheres.length()!=0) {
                wheres.append(" or ");
            }
            wheres.append("(");
            wheres.append(where);
            wheres.append(")");
        }

//        DBComparator.writeVerbose("Execute sql ->"+"select * from "+table+" where "+wheres.toString());
        preparedStatement = connection.prepareStatement("select * from "+table+" where "+wheres.toString());
        setStakeHolder(table, preparedStatement, preparedStatementSize, pkInfoList);
        if (dataPreparedStatementMap!=null) {
            dataPreparedStatementMap.put(table, preparedStatement);
        }
        return preparedStatement;
    }

    private void setStakeHolder(String table, PreparedStatement preparedStatement, int preparedStatementSize, List<DataInfo> pkInfoList) throws SQLException {
        TableInfo tableInfo = tableMap.get(table);

        if (tableInfo==null) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        int stakeHolderCount = 0;
        for (int loop=0; loop<preparedStatementSize; loop++) {
            if (loop<pkInfoList.size()) {
                DataInfo pkInfo = pkInfoList.get(loop);
                for (int loop2=0; loop2<pkInfo.columnValues.length; loop2++) {
                    Object obj = pkInfo.columnValues[loop2];
                    preparedStatement.setObject(stakeHolderCount+1, obj);
                    stakeHolderCount++;
                }
            } else {
                List<Integer> primaryKeyColumnType = tableInfo.getPrimaryKeyColumnType();
                if (primaryKeyColumnType==null) {
                    DBComparator.writeVerbose("primaryKeyColumnType is null. Table->"+tableInfo.getTableName());
                }
                for (Integer type: primaryKeyColumnType) {
                    preparedStatement.setNull(stakeHolderCount+1, type);
                    stakeHolderCount++;
                }
            }
        }
    }

    private String getPKWhereStatement(String table) {
        StringBuilder where = new StringBuilder();
        TableInfo tableInfo = tableMap.get(table);

        if (tableInfo==null) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        for (String colName: tableInfo.getPrimaryKeyColumnName()) {
            if (where.length()!=0) {
                where.append(" and ");
            }
            where.append(colName+"=?");
        }
        return where.toString();
    }

    public void closeSingleDataStatement(String table) throws SQLException {
        PreparedStatement preparedStatement = dataPreparedStatementMapSingle.get(table);
        if (preparedStatement!=null) {
            preparedStatement.close();
            dataPreparedStatementMapSingle.remove(table);
        }
    }
    public void closeMultiDataStatement(String table) throws SQLException {
        PreparedStatement preparedStatement = dataPreparedStatementMapMulti.get(table);
        if (preparedStatement!=null) {
            preparedStatement.close();
            dataPreparedStatementMapMulti.remove(table);
        }
    }

    public List<String> getColumnNameList(String tableName) throws SQLException {
        List<String> columnNameList = new ArrayList<>();
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet result = meta.getColumns(null, dbInfo.getUser(), tableName, null);
        
        while (result.next()) {
            columnNameList.add(result.getString("COLUMN_NAME"));
        }
        result.close();

        return columnNameList;
    }

    public Map<String, String> getData(String tableName, DataInfo pkInfo) throws SQLException {
        Map<String, String> dataMap = null;
        List<DataInfo> pkInfoList = new ArrayList<>();
        pkInfoList.add(pkInfo);

        PreparedStatement preparedStatement = createDataStatement(tableName, null, 1, pkInfoList);
        ResultSet resultSet = preparedStatement.executeQuery();
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        if (resultSet.next()) {
            dataMap = new HashMap<>();
            for (int loop=0; loop<resultSetMetaData.getColumnCount(); loop++) {
                dataMap.put(resultSetMetaData.getColumnName(loop+1), resultSet.getString(loop+1));
            }
        }
        resultSet.close();
        preparedStatement.close();

        return dataMap;
    }

    private boolean isExcludeTable(String tableName) {
        if (excludeList==null) {
        System.out.println("tableName:"+tableName+" null");
            return false;
        }
        System.out.println("tableName:"+tableName+" exclude:"+excludeList.contains(tableName));
        return excludeList.contains(tableName);
    }

    public Set<String> getTableList() {
        return tableMap.keySet();
    }
    public void setDbInfo(DBInfo dbInfo) {
        this.dbInfo = dbInfo;
    }

    public String getSubjectName() {
        return subjectName;
    }

    private boolean isEmptyString(String value) {
        return (value==null || "".equals(value));
    }

    public void setExcludeList(List<String> excludeList) {
        this.excludeList = excludeList;
    }

    public void setIncludeList(List<String> includeList) {
        this.includeList = includeList;
    }
}
