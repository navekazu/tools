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
