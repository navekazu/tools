/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.dbcomparator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import tools.dbcomparator.control.CompareResultFrameControl;
import tools.dbcomparator.control.ProgressFrameControl;
import tools.dbcomparator.entity.CompareTarget;
import tools.dbcomparator.entity.DBInfo;

/**
 *
 * @author k_watanabe
 */
public class DBComparatorThread implements Runnable {
    private CompareTarget compareTarget;
    private List<String> includeList;
    private List<String> excludeList;
    private List<DBSubject> dbSubjectList;
    private Set<String> allTableList;
    private List<TableCompareThread> compareThreadList;
    private List<TableCompareThread> compareFinishedList;
    private ProgressFrameControl progressFrameControl;
    private static final int MAX_THREAD_SIZE = 50;
    
    public DBComparatorThread() {
        progressFrameControl = new ProgressFrameControl(this);
        progressFrameControl.pack();
        progressFrameControl.setVisible(true);
        compareFinishedList = new ArrayList<>();
    }

    public void connect() throws Exception {
        dbSubjectList = new ArrayList<>();
        int nameIndex = 1;
        for (DBInfo dbInfo: compareTarget.getDbInfoList()) {
            DBSubject dbSubject = new DBSubject("Subject "+nameIndex);
            dbSubject.setDbInfo(dbInfo);
            dbSubject.setIncludeList(includeList);
            dbSubject.setExcludeList(excludeList);
            dbSubject.connect();
            dbSubjectList.add(dbSubject);
            nameIndex++;
        }
    }

    public CompareTarget getCompareTarget() {
        return compareTarget;
    }

    public void setCompareTarget(CompareTarget compareTarget, List<String> includeList, List<String> excludeList) {
        this.compareTarget = compareTarget;
        this.includeList = includeList;
        this.excludeList = excludeList;
    }

    public void showUnmatchList(String table) {
        TableCompareThread target = null;
        for (TableCompareThread tableCompareThread: compareFinishedList) {
            if (tableCompareThread.getTableName().equals(table)) {
                target = tableCompareThread;
            }
        }
        if (target==null || target.isAlive()) {
            return ;
        }

        CompareResultFrameControl compareResultFrameControl = new CompareResultFrameControl();
        compareResultFrameControl.setResult(target);
        compareResultFrameControl.pack();
        compareResultFrameControl.setVisible(true);
    }

    public int getCompareThreadListSize() {
        if (compareThreadList==null) {
            return 0;
        }
        synchronized(compareThreadList) {
            return compareThreadList.size();
        }
    }
    @Override
    public void run() {
        try {
            allTableList = new TreeSet<>();
//            ResultReport report = new ResultReport();
//            report.createReport();

            // テーブル取得
            for (DBSubject dbSubject: dbSubjectList) {
                dbSubject.loadTableList();
                allTableList.addAll(dbSubject.getTableList());
            }
//            report.outputTableReport(allTableList, dbSubjectList);
            progressFrameControl.setAllTableList(allTableList);

            // コンペア実行
            int compareThreadListSize = 0;
            compareThreadList = new ArrayList<>();
            CompareFinishObserveThread observeThread = new CompareFinishObserveThread();
            observeThread.start();
            for (String tableName: allTableList) {
                while (true) {
                    synchronized(compareThreadList) {
                        compareThreadListSize = compareThreadList.size();
                    }
                    if (compareThreadListSize<MAX_THREAD_SIZE) {
                        break;
                    }
                    Thread.sleep(1000);
//                    DBComparator.writeVerbose("compareThreadListSize "+compareThreadListSize);
                }

                TableCompareThread tableCompareThread = new TableCompareThread(tableName, dbSubjectList, progressFrameControl);
                tableCompareThread.start();
                synchronized(compareThreadList) {
                    compareThreadList.add(tableCompareThread);
                }
                System.gc();
            }
            observeThread.registedFlag = true;
            while (observeThread.isAlive()) {   // 生きている間（runメソッド中）は待機
                Thread.sleep(500);
            }

//            report.write();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    private class CompareFinishObserveThread extends Thread {
        public boolean registedFlag = false;
        @Override
        public void run() {
            try {
                // compareThreadListに追加がされるまで待機
                Thread.sleep(1000);

                int compareThreadListSize = 1;  // 仮に1件とする

                while (true) {
                    synchronized(compareThreadList) {
                        for (int loop=0; loop<compareThreadList.size(); loop++) {
                            TableCompareThread tableCompareThread = compareThreadList.get(loop);
                            if (!tableCompareThread.isAlive()) {
                                compareThreadList.remove(loop);
                                compareFinishedList.add(tableCompareThread);
                                loop--;
                            }
                        }
                    }

                    Thread.sleep(500);

                    synchronized(compareThreadList) {
                        compareThreadListSize = compareThreadList.size();
                    }
                    if (registedFlag && compareThreadListSize==0) {
                        break;
                    }
//                    DBComparator.writeVerbose("CompareFinishObserveThread "+compareThreadListSize);
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        
    }
}
