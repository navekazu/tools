/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.dbcomparator.control;

import java.awt.Color;
import java.awt.Component;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ProgressMonitor;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import tools.dbcomparator.DBSubject;
import tools.dbcomparator.TableCompareThread;
import tools.dbcomparator.entity.CompareResultRecord;
import tools.dbcomparator.entity.DataInfo;
import tools.dbcomparator.ui.CompareResultFrame;

/**
 *
 * @author k_watanabe
 */
public class CompareResultFrameControl extends CompareResultFrame {
    private TableCompareThread target;
    private List<JTable> resultTableList;
    private List<JScrollPane> resultScrollPaneList;
    private List<DefaultTableModel> resultTableModelList;
    private AutoScrollMonitor autoScrollMonitor;

    public void setResult(TableCompareThread target) {
        this.target = target;
        setTitle(target.getTableName());

        autoScrollMonitor = new AutoScrollMonitor();
        autoScrollMonitor.start();
        CompareWindowAdapter compareWindowAdapter = new CompareWindowAdapter();
        compareWindowAdapter.start();
    }
    private void createResultTableList() {
        resultTableList = new ArrayList<>();
        resultScrollPaneList = new ArrayList<>();
        resultTableModelList = new ArrayList<>();
        loadingProgressBar.setMaximum(target.getUnmatchList().size());

        try {
            int progress = 0;
            for (DataInfo dataInfo: target.getUnmatchList()) {
                DefaultTableModel model = new DefaultTableModel();
                model.addColumn("Column");

                for (DBSubject dbSubject: target.getDbSubjectList()) {
                    model.addColumn(dbSubject.getSubjectName());
                }

                CompareResultRecord record = target.getCompareResultRecord(dataInfo);
                for (String columnName: record.getColumnNameList()) {
                    String[] rowData = new String[target.getDbSubjectList().size()+1];
                    rowData[0] = columnName;
                    for (int loop=0; loop<target.getDbSubjectList().size(); loop++) {
                        rowData[loop+1] = record.getSubjectValue(loop, columnName);
                    }
                    model.addRow(rowData);
                }

                JTable table = new JTable(model);
                table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                table.setDefaultRenderer(Object.class, new CompareResultRenderer());

                resultTableList.add(table);
                resultScrollPaneList.add(new JScrollPane(table));
                resultTableModelList.add(model);
                progress++;
                loadingProgressBar.setValue(progress);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private class CompareResultRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if(isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            }else{
                setForeground(table.getForeground());
                setBackground(table.getBackground());

                DefaultTableModel model = (DefaultTableModel)table.getModel();
                boolean unmatch = false;
                String tableValue = null;
                for (int loop=1; loop<model.getColumnCount(); loop++) {
                    if (loop==1) {
                        tableValue = (String)model.getValueAt(row, loop);
                    } else {
                        if (tableValue==null&& model.getValueAt(row, loop)==null) {
                            
                        } else if (tableValue!=null&& tableValue.equals(model.getValueAt(row, loop))) {
                        
                        } else {
                            unmatch = true;
                        }
                    }
                }
                if (unmatch) {
                    setBackground(new Color(255, 200, 200));
                }
            }

            return component;
        }
    }
    private class CompareWindowAdapter extends Thread {
        @Override
        public void run() {
            autoRollCheckBox.setEnabled(false);
            delayTimeTextField.setEnabled(false);
            scrollCheckBox.setEnabled(false);
            compareTabbedPane.removeAll();

            createResultTableList();

            int loop = 0;
            for (DataInfo dataInfo: target.getUnmatchList()) {
                compareTabbedPane.add("Result "+(loop+1), resultScrollPaneList.get(loop));
                loop++;
            }
            autoRollCheckBox.setEnabled(true);
            delayTimeTextField.setEnabled(true);
            scrollCheckBox.setEnabled(true);
        }
    }
    private class CompareProgressMonitor extends Thread {
        private Component parentComponent;
        private int max;
        private ProgressMonitor progressMonitor;
        public CompareProgressMonitor(Component parentComponent, int max) {
            this.parentComponent = parentComponent;
            this.max = max;
        }

        @Override
        public void run() {
            progressMonitor = new ProgressMonitor(parentComponent, "Initializing.", "Load unmatch list.", 0, max);
            progressMonitor.setProgress(0);
        }
        public void setProgress(int progress) {
            if (progressMonitor!=null) {
                progressMonitor.setProgress(progress);
            }
        }
        public void close() {
            if (progressMonitor!=null) {
                progressMonitor.close();
            }
        }
    }

    private class AutoScrollMonitor extends Thread {
        @Override
        public void run() {
            while(true) {
                try {
                    if (autoRollCheckBox.isSelected()) {
                        int index = compareTabbedPane.getSelectedIndex();
                        index++;
                        index = index%compareTabbedPane.getTabCount();
                        compareTabbedPane.setSelectedIndex(index);

                        if (scrollCheckBox.isSelected()) {
                            JScrollPane pane = resultScrollPaneList.get(index);
                            JScrollBar bar = pane.getVerticalScrollBar();
                            int blockIncrement = bar.getBlockIncrement(1);
                            int value = bar.getValue();
                            value = value + blockIncrement;
                            if (bar.getMaximum()<value) {
                                value = bar.getMaximum();
                            }
                            bar.setValue(value);
                        }
                    }
                    long delay = 1000;
                    try {
                        delay = Long.parseLong(delayTimeTextField.getText());
                    } catch(NumberFormatException e) {
                    }
                    Thread.sleep(delay);
                } catch (InterruptedException ex) {
                    Logger.getLogger(CompareResultFrameControl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
