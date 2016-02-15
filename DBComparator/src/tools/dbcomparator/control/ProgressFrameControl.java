/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.dbcomparator.control;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import tools.dbcomparator.CompareProgressListener;
import tools.dbcomparator.DBComparatorThread;
import tools.dbcomparator.ui.ProgressFrame;

/**
 *
 * @author k_watanabe
 */
public class ProgressFrameControl extends ProgressFrame implements CompareProgressListener {
    private Set<String> allTableList;
    private DefaultTableModel model;
    private NumberFormat nfNum = NumberFormat.getNumberInstance();
    private int finishCount = 0;
    private DBComparatorThread dbComparatorThread;

    public ProgressFrameControl(DBComparatorThread dbComparatorThread) {
        this.dbComparatorThread = dbComparatorThread;
        model = (DefaultTableModel)progressTable.getModel();
//        progressTable.setModel(model);
        progressTable.setDefaultRenderer(Object.class, new ProgressRenderer());

        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        flushStatus();
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(ProgressFrameControl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        t.start();

        progressTable.addMouseListener(new ProgressTableMouseListener());
    }

    private class ProgressTableMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent ev) {
            if (ev.getClickCount()==2 && ev.getButton()==MouseEvent.BUTTON1) {
                Point pt = ev.getPoint();
                int row = progressTable.rowAtPoint(pt);
                if(row>=0) {
                    int status = (Integer)model.getValueAt(row, 5);
                    switch (status) {
                        case 1:
                            // OK
                            dbComparatorThread.showUnmatchList((String)model.getValueAt(row, 0));
                            break;
                        case 2:
                            // Error
                            dbComparatorThread.showUnmatchList((String)model.getValueAt(row, 0));
                            break;
                        case 3:
                            // ?
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    public void setAllTableList(Set<String> allTableList) {
        this.allTableList = allTableList;
        for (String table: this.allTableList) {
            model.addRow(new Object[]{table, 0, "Wait", 0, 0, 0});
        }
    }

    @Override
    public void searchPK(String table, int target, int current, int count) {
/*
        if (count%100!=0) {
            return;
        }
*/
        String rowTableName;
        for (int row=0; row<model.getRowCount(); row++) {
            rowTableName = (String)model.getValueAt(row, 0);
            if (table.equals(rowTableName)) {
                model.setValueAt("Search PK "+nfNum.format(target+1)+"("+nfNum.format(current)+"/"+nfNum.format(count)+")", row, 2);
                break;
            }
        }
    }

    @Override
    public void recordCount(String table, int count) {
//        DBComparator.writeVerbose("ProgressFrameControl recordCount. Table->"+table+" count->"+count);
        String rowTableName;
        for (int row=0; row<model.getRowCount(); row++) {
            rowTableName = (String)model.getValueAt(row, 0);
            if (table.equals(rowTableName)) {
                model.setValueAt(count, row, 4);
                break;
            }
        }
    }

    @Override
    public void progress(String table, int progress) {
//        DBComparator.writeVerbose("ProgressFrameControl progress. Table->"+table+" count->"+progress);
/*
        if (progress%100!=0) {
            return;
        }
*/
        String rowTableName;
        int count;
        for (int row=0; row<model.getRowCount(); row++) {
            rowTableName = (String)model.getValueAt(row, 0);
            count = (Integer)model.getValueAt(row, 4);
            if (table.equals(rowTableName)) {
                synchronized(model) {
                    model.setValueAt(progress, row, 1);
                    model.setValueAt(progress, row, 3);
                    model.setValueAt("Search data ("+nfNum.format(progress)+"/"+nfNum.format(count)+")", row, 2);
                }
                break;
            }
        }
    }

    @Override
    public void finish(String table, int status, int errorCount) {
        String rowTableName;
        int count;
        for (int row=0; row<model.getRowCount(); row++) {
            rowTableName = (String)model.getValueAt(row, 0);
            count = (Integer)model.getValueAt(row, 4);
            if (table.equals(rowTableName)) {
                model.setValueAt("Finish "+(status==1? "(OK)": status==2? "(Error)": "(?)")+" "+nfNum.format(errorCount)+"/"+nfNum.format(count), row, 2);
                model.setValueAt(status, row, 5);
                break;
            }
        }
        finishCount++;
        allProgress.setMaximum(model.getRowCount());
        allProgress.setValue(finishCount);
    }
    
    private void flushStatus() {
        Runtime runtime = Runtime.getRuntime();
        int max = (int)(runtime.maxMemory()/1024L);
        int use = (int)((runtime.maxMemory()-runtime.freeMemory())/1024L);
        int count = dbComparatorThread.getCompareThreadListSize();
        memoryLabel.setText("Memory(KB) use/max ("+nfNum.format(use)+"/"+nfNum.format(max)+")  Active thread:"+nfNum.format(count));
    }

    private class ProgressRenderer extends DefaultTableCellRenderer {
        private final JProgressBar b = new JProgressBar(0, 100);
        public ProgressRenderer() {
            super();
            setOpaque(true);
            b.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (column==1) {
                int status = (Integer)model.getValueAt(row, 5);
                if (status!=0) {
                    b.setMaximum(1);
                    b.setValue(1);
                } else {
                    int count = (Integer)model.getValueAt(row, 4);
                    Integer i = (Integer)value;
                    b.setMaximum(count);
                    b.setValue(i+1);
                }
                return b;
            } else {
                Component c;
                c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if(isSelected) {
                    setForeground(table.getSelectionForeground());
                    setBackground(table.getSelectionBackground());
                }else{
                    setForeground(table.getForeground());
                    if (!(column==2)) {
                        setBackground(table.getBackground());
                    } else {
                        int status = (Integer)model.getValueAt(row, 5);
                        switch (status) {
                            case 1:
                                // OK
                                setBackground(new Color(200, 255, 200));
                                break;
                            case 2:
                                // Error
                                setBackground(new Color(255, 200, 200));
                                break;
                            case 3:
                                // ?
                                setBackground(new Color(255, 255, 200));
                                break;
                            default:
                                setBackground(table.getBackground());
                                break;
                        }
                    }
                }
            }
            return this;

        }
    }
    
}
