/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.dbcomparator;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.dbcomparator.entity.ComparatorConfig;
import tools.dbcomparator.entity.CompareTarget;
import tools.dbcomparator.entity.DBInfo;

/**
 *
 * @author k_watanabe
 */
public class DBComparator {
    public static boolean verbose = true;

    public static void writeVerbose(String message) {
        if (verbose) {
            System.out.println(message);
        }
    }

    public DBComparator(ComparatorConfig comparatorConfig) throws Exception {
        List<CompareTarget> targetList = comparatorConfig.getTargetList();
        DBComparatorThread dbComparatorThread = new DBComparatorThread();
        dbComparatorThread.setCompareTarget(targetList.get(0), comparatorConfig.getIncludeList(), comparatorConfig.getExcludeList());
        dbComparatorThread.connect();

        Thread thread = new Thread(dbComparatorThread);
        thread.start();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        // TODO code application logic here
        CompareTarget compareTarget = new CompareTarget();
/*
        DBInfo dbInfo1 = new DBInfo();
        dbInfo1.setLibraryPath("ojdbc6.jar");
        dbInfo1.setDriver("oracle.jdbc.driver.OracleDriver");
        dbInfo1.setUrl("jdbc:oracle:thin:@172.16.60.125:1523:MKDB");
        dbInfo1.setUser("MKUSER2");
        dbInfo1.setPassword("maikin");
        compareTarget.addDbInfoList(dbInfo1);

        DBInfo dbInfo2 = new DBInfo();
        dbInfo2.setLibraryPath("ojdbc6.jar");
        dbInfo2.setDriver("oracle.jdbc.driver.OracleDriver");
        dbInfo2.setUrl("jdbc:oracle:thin:@172.16.60.125:1523:MKDB");
        dbInfo2.setUser("MKUSER3");
        dbInfo2.setPassword("maikin");
        compareTarget.addDbInfoList(dbInfo2);
*/
        DBInfo dbInfo1 = new DBInfo();
        dbInfo1.setLibraryPath("sqlite-jdbc-3.7.15.jar");
        dbInfo1.setDriver("org.sqlite.JDBC");
        dbInfo1.setUrl("jdbc:sqlite:C:/整備状況把握ツール/Data/gyomu_Data_nopassword");
        dbInfo1.setUser(null);
        dbInfo1.setPassword(null);
        compareTarget.addDbInfoList(dbInfo1);

        DBInfo dbInfo2 = new DBInfo();
        dbInfo2.setLibraryPath("sqlite-jdbc-3.7.15.jar");
        dbInfo2.setDriver("org.sqlite.JDBC");
        dbInfo2.setUrl("jdbc:sqlite:C:/整備状況把握ツール/Data/gyomu_Data_nopassword_old");
        dbInfo2.setUser(null);
        dbInfo2.setPassword(null);
        compareTarget.addDbInfoList(dbInfo2);

        DBInfo dbInfo3 = new DBInfo();
        dbInfo3.setLibraryPath("sqlite-jdbc-3.7.15.jar");
        dbInfo3.setDriver("org.sqlite.JDBC");
        dbInfo3.setUrl("jdbc:sqlite:C:/整備状況把握ツール/Data/gyomu_Data_nopassword_old2");
        dbInfo3.setUser(null);
        dbInfo3.setPassword(null);
        compareTarget.addDbInfoList(dbInfo3);

        List<CompareTarget> targetList = new ArrayList<>();
        targetList.add(compareTarget);
        List<String> excludeList = new ArrayList<>();
        excludeList.add("FOO_TABLE");
        excludeList.add("BAR_TABLE");

        ComparatorConfig comparatorConfigSample = new ComparatorConfig();
        comparatorConfigSample.setShowGUIProgress(true);
        comparatorConfigSample.setTargetList(targetList);
        comparatorConfigSample.setExcludeList(excludeList);

        try {
            XStream xs = new XStream();
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream("DBComparatorConfig_sample.xml"));
            xs.toXML(comparatorConfigSample, out);
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DBComparator.class.getName()).log(Level.SEVERE, null, ex);
        }

        ComparatorConfig comparatorConfig = new ComparatorConfig();
        try {
            XStream xs = new XStream(new DomDriver());
            BufferedInputStream in = new BufferedInputStream(new FileInputStream("DBComparatorConfig.xml"));
            xs.fromXML(in, comparatorConfig);
            in.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DBComparator.class.getName()).log(Level.SEVERE, null, ex);
        }
        new DBComparator(comparatorConfig);
    }
}
