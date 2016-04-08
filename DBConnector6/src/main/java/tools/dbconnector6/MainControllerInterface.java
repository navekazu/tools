package tools.dbconnector6;

import javafx.scene.control.*;
import tools.dbconnector6.controller.DbStructureTreeItem;
import tools.dbconnector6.entity.Connect;
import tools.dbconnector6.entity.TableColumnTab;
import tools.dbconnector6.entity.TablePropertyTab;

import java.sql.Connection;

public interface MainControllerInterface {
    public void writeLog(String message, Object... args);
    public void writeLog(Throwable e);
    public void connectNotify();
    public Connection getConnection();
    public Connect getConnectParam();
    public void selectReservedWord(String word);
    public void mainControllerRequestFocus();
    public void hideReservedWordStage();
    public void showAlertDialog(String message, String detail);
    public void showWaitDialog(String message, String detail);
    public void hideWaitDialog();
    public boolean isEvidenceMode();
    public boolean isEvidenceModeIncludeHeader();
    public String getEvidenceDelimiter();
    public String getQuery();
    public String getSelectedQuery();
    public void updateSelectedQuery(String query);
    public void addQueryWord(String word, boolean shiftDown);
    public String getEditorPath();
    public boolean isConnect();
    public boolean isConnectWithoutMessage();

    public BackgroundService getDbStructureUpdateService();
    public BackgroundService getTableStructureTabPaneUpdateService();
    public BackgroundService getTableStructureUpdateService();
    public BackgroundService getQueryExecuteService();

    public DbStructureParam getDbStructureParam();
    public TableStructureTabParam getTableStructureTabParam();
    public QueryParam getQueryParam();

    public class DbStructureParam {
        public TextField filterTextField;
        public TreeView dbStructureTreeView;
        public DbStructureTreeItem dbStructureRootItem;
    }
    public class TableStructureTabParam {
        public TabPane tableStructureTabPane;

        public Tab tablePropertyTab;
        public TableView tablePropertyTableView;
        public TableColumn<TablePropertyTab, String> keyTableColumn;
        public TableColumn<TablePropertyTab, String> valueTableColumn;

        public Tab tableColumnTab;
        public TableView tableColumnTableView;
        public TableColumn<TableColumnTab, String> nameTableColumn;
        public TableColumn<TableColumnTab, String> typeTableColumn;
        public TableColumn<TableColumnTab, Integer> sizeTableColumn;
        public TableColumn<TableColumnTab, Integer> decimalDigitsTableColumn;
        public TableColumn<TableColumnTab, String> nullableTableColumn;
        public TableColumn<TableColumnTab, Integer> primaryKeyTableColumn;
        public TableColumn<TableColumnTab, String> remarksTableColumn;
        public TableColumn<TableColumnTab, String> columnDefaultTableColumn;
        public TableColumn<TableColumnTab, String> autoincrementTableColumn;
        public TableColumn<TableColumnTab, String> generatedColumnTableColumn;

        public Tab tableIndexTab;
        public ComboBox tableIndexNameComboBox;
        public TextField tableIndexPrimaryKeyTextField;
        public TextField tableIndexUniqueKeyTextField;
        public ListView tableIndexListView;
    }

    public class QueryParam {
        public TextArea queryTextArea;
        public TableView queryResultTableView;

    }
}