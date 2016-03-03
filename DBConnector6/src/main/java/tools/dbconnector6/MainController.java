package tools.dbconnector6;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import tools.dbconnector6.controller.ControllerManager;
import tools.dbconnector6.entity.QueryResult;
import tools.dbconnector6.entity.TableColumnTab;
import tools.dbconnector6.entity.TablePropertyTab;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import static tools.dbconnector6.DbStructureTreeItem.ItemType.DATABASE;

public class MainController extends Application implements Initializable, MessageInterface {
    private static SimpleDateFormat logDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

    @FXML
    private TextField filterTextField;

    @FXML
    private TextField schemaTextField;

    @FXML
    private Button searchButton;

    @FXML
    private TreeView dbStructureTreeView;
    private DbStructureTreeItem dbStructurRootItem;

    @FXML
    private TabPane tableStructureTabPane;

    @FXML
    private Tab tablePropertyTab;

    @FXML
    private TableView tablePropertyTableView;
    @FXML
    private TableColumn<TablePropertyTab, String> keyTableColumn;
    @FXML
    private TableColumn<TablePropertyTab, String> valueTableColumn;

    @FXML
    private Tab tableColumnTab;

    @FXML
    private TableView tableColumnTableView;
    @FXML
    private TableColumn<TableColumnTab, String> nameTableColumn;
    @FXML
    private TableColumn<TableColumnTab, String> typeTableColumn;
    @FXML
    private TableColumn<TableColumnTab, Integer> sizeTableColumn;
    @FXML
    private TableColumn<TableColumnTab, Integer> decimalDigitsTableColumn;
    @FXML
    private TableColumn<TableColumnTab, String> nullableTableColumn;
    @FXML
    private TableColumn<TableColumnTab, Integer> primaryKeyTableColumn;
    @FXML
    private TableColumn<TableColumnTab, String> remarksTableColumn;
    @FXML
    private TableColumn<TableColumnTab, String> columnDefaultTableColumn;
    @FXML
    private TableColumn<TableColumnTab, String> autoincrementTableColumn;
    @FXML
    private TableColumn<TableColumnTab, String> generatedColumnTableColumn;

    @FXML
    private Tab tableIndexTab;

    @FXML
    private ComboBox tableIndexComboBox;
    @FXML
    private TextField tablePrimaryKeyTextField;
    @FXML
    private TextField tableUniqueKeyTextField;
    @FXML
    private ListView tableIndexListView;

    @FXML
    private TextArea queryTextArea;

    @FXML
    private TableView queryResultTableView;

    @FXML
    private TextArea logTextArea;

    private Connection connection;
    private BackgroundCallback queryResultUpdateService;
    private BackgroundCallback dbStructureTreeViewUpdateService;
    private BackgroundCallback tableStructureTreeViewUpdateService;
    private BackgroundCallback tableStructureTabPaneUpdateService;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = ControllerManager.getControllerManager().getLoarder("main");
        ControllerManager.getControllerManager().getMainStage(loader, primaryStage).show();

        // 初期フォーカスを検索ワード入力欄に（initializeの中ではフォーカス移動できない）
        MainController c = loader.getController();
        c.focusQueryTextArea();
    }

    public void focusQueryTextArea() {
        queryTextArea.requestFocus();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbStructurRootItem = new DbStructureTreeItem(DATABASE, DATABASE.getName(), null);
        dbStructureTreeView.setRoot(dbStructurRootItem);
        dbStructureTreeView.getSelectionModel().selectedItemProperty().addListener(new DbStructureTreeViewChangeListener());

        dbStructureTreeViewUpdateService = new BackgroundCallback(new DbStructureTreeViewUpdate());
        tableStructureTreeViewUpdateService = new BackgroundCallback(new TableStructureTreeViewUpdate());
        tableStructureTabPaneUpdateService = new BackgroundCallback(new TableStructureTabPaneUpdate());

        keyTableColumn.setCellValueFactory(new PropertyValueFactory<TablePropertyTab, String>("key"));
        valueTableColumn.setCellValueFactory(new PropertyValueFactory<TablePropertyTab, String>("value"));

        nameTableColumn.setCellValueFactory(new PropertyValueFactory<TableColumnTab, String>("name"));
        typeTableColumn.setCellValueFactory(new PropertyValueFactory<TableColumnTab, String>("type"));
        sizeTableColumn.setCellValueFactory(new PropertyValueFactory<TableColumnTab, Integer>("size"));
        decimalDigitsTableColumn.setCellValueFactory(new PropertyValueFactory<TableColumnTab, Integer>("decimalDigits"));
        nullableTableColumn.setCellValueFactory(new PropertyValueFactory<TableColumnTab, String>("nullable"));
        primaryKeyTableColumn.setCellValueFactory(new PropertyValueFactory<TableColumnTab, Integer>("primaryKey"));
        remarksTableColumn.setCellValueFactory(new PropertyValueFactory<TableColumnTab, String>("remarks"));
        columnDefaultTableColumn.setCellValueFactory(new PropertyValueFactory<TableColumnTab, String>("columnDefault"));
        autoincrementTableColumn.setCellValueFactory(new PropertyValueFactory<TableColumnTab, String>("autoincrement"));
        generatedColumnTableColumn.setCellValueFactory(new PropertyValueFactory<TableColumnTab, String>("generatedColumn"));

        queryResultUpdateService = new BackgroundCallback(new QueryResultUpdate());

        dbStructureTreeViewUpdateService.restart();
        tableStructureTreeViewUpdateService.restart();
        tableStructureTabPaneUpdateService.restart();

        queryResultTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML
    private void onClose(ActionEvent event) throws SQLException {
        if (connection!=null) {
            connection.close();
            connection = null;
        }
    }

    @FXML
    private void onUndo(ActionEvent event) {
    }

    @FXML
    private void onRedo(ActionEvent event) {
    }

    @FXML
    private void onCut(ActionEvent event) {
    }

    @FXML
    private void onCopy(ActionEvent event) {
    }

    @FXML
    private void onPaste(ActionEvent event) {
    }

    @FXML
    private void onSettingSqlEditor(ActionEvent event) {
    }

    @FXML
    private void onCallSqlEditor(ActionEvent event) {
    }

    @FXML
    private void onConnect(ActionEvent event) throws IOException, SQLException {

        if (connection!=null) {
            connection.close();
            connection = null;
        }

        FXMLLoader loader = ControllerManager.getControllerManager().getLoarder("connect");
        Stage stage = ControllerManager.getControllerManager().getSubStage(loader, "connect");

        ConnectController controller = loader.getController();
        controller.setMessageInterface(this);
        stage.showAndWait();

        connection = controller.getConnection();
        if (connection!=null) {
            writeLog("Connected.");
            dbStructureTreeViewUpdateService.restart();
        }

        stage.close();
    }

    @FXML
    private void onDisconnect(ActionEvent event) throws SQLException {
        if (connection!=null) {
            connection.close();
            connection = null;
            writeLog("Disconnected.");
        }
    }

    @FXML
    private void onExecuteQuery(ActionEvent event) {
        queryResultUpdateService.restart();
    }


    @FXML
    private void onPasteAndExecuteQuery(ActionEvent event) {
    }

    @FXML
    private void onCancelQuery(ActionEvent event) {
    }

    @FXML
    private void onQueryScript(ActionEvent event) {
    }

    @FXML
    private void onCommit(ActionEvent event) {
    }

    @FXML
    private void onRollback(ActionEvent event) {
    }

    @FXML
    private void onCheckIsolation(ActionEvent event) {
    }

    @FXML
    private void onEvidenceMode(ActionEvent event) {
    }

    @FXML
    private void onIncludeHeader(ActionEvent event) {
    }

    @FXML
    private void onEvidenceDelimiterTab(ActionEvent event) {
    }

    @FXML
    private void onEvidenceDelimiterComma(ActionEvent event) {
    }

    @FXML
    private void onEvidenceDelimiterSpace(ActionEvent event) {
    }

    @FXML
    private void onSearchButton(ActionEvent event) {
        if (connection!=null) {
            dbStructureTreeViewUpdateService.restart();
        }
    }

    public synchronized void writeLog(String message) {
        String logText = logDateFormat.format(new Date())+" "+message;
//        logTextArea.setText(logTextArea.getText() + logText + "\n");
//        logTextArea.setScrollTop(Double.MAX_VALUE);
        logTextArea.appendText(logText + "\n");
    }

    public class DbStructureTreeViewUpdate implements BackgroundCallbackInterface {
        @Override
        public void run() throws SQLException {
            if (connection==null) {
                dbStructurRootItem.setValue(dbStructurRootItem.getItemType().getName());
                return ;
            }

            ObservableList<TreeItem<String>> subList = dbStructurRootItem.getChildren();
            DatabaseMetaData meta = connection.getMetaData();
            DbStructureTreeItem.ItemType itemType;

            // サポートされていないAPIを呼ぶと例外が発生するので、そのときは握りつぶして次を呼び出す
            ResultSet resultSet = meta.getTableTypes();
            try {
                itemType = DbStructureTreeItem.ItemType.TABLE;
                while (resultSet.next()) {
                    subList.add(createGroupItem(meta.getTables(null, getSchemaTextFieldParamValue(), getFilterTextFieldParamValue(), new String[]{resultSet.getString("TABLE_TYPE")})
                            , "TABLE_NAME", "TABLE_SCHEM", resultSet.getString("TABLE_TYPE"), itemType));
                }
                resultSet.close();
            } catch(Throwable e) {}

            try {
                itemType = DbStructureTreeItem.ItemType.FUNCTION;
                subList.add(createGroupItem(meta.getFunctions(null, getSchemaTextFieldParamValue(), getFilterTextFieldParamValue())
                        , "FUNCTION_NAME", "FUNCTION_SCHEM", itemType.getName(), itemType));
            } catch(Throwable e) {}

            try {
                itemType = DbStructureTreeItem.ItemType.PROCEDURE;
                subList.add(createGroupItem(meta.getProcedures(null, getSchemaTextFieldParamValue(), getFilterTextFieldParamValue())
                        , "PROCEDURE_NAME", "PROCEDURE_SCHEM", itemType.getName(), itemType));
            } catch(Throwable e) {}

            try {
                itemType = DbStructureTreeItem.ItemType.SCHEMA;
                subList.add(createGroupItem(meta.getSchemas(null, getSchemaTextFieldParamValue())
                        , "TABLE_SCHEM", "TABLE_SCHEM", itemType.getName(), itemType));
            } catch(Throwable e) {}

            dbStructurRootItem.setExpanded(true);
        }

        private DbStructureTreeItem createGroupItem(ResultSet resultSet, String colName, String schemaName, String name, DbStructureTreeItem.ItemType itemType) throws SQLException {
            DbStructureTreeItem tableItem = new DbStructureTreeItem(DbStructureTreeItem.ItemType.GROUP, name, null);

            List<DbStructureTreeItem> l = new ArrayList<>();
            while (resultSet.next()) {
                l.add(new DbStructureTreeItem(itemType, resultSet.getString(colName), resultSet.getString(schemaName)));
            }
            Collections.sort(l);

            ObservableList<TreeItem<String>> tableList = tableItem.getChildren();
            tableList.addAll(l);

            resultSet.close();
            return tableItem;
        }
    }

    private String getFilterTextFieldParamValue() {
        return getTextFieldParamValue(filterTextField);
    }
    private String getSchemaTextFieldParamValue() {
        return getTextFieldParamValue(schemaTextField);
    }
    private String getTextFieldParamValue(TextField textField) {
        return "".equals(textField.getText())? null: String.format("%%%s%%", textField.getText());
    }

    public class DbStructureTreeViewChangeListener implements ChangeListener {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            tableStructureTreeViewUpdateService.restart();
        }
    }

    public class TableStructureTreeViewUpdate implements BackgroundCallbackInterface {
        @Override
        public void run() {
            DbStructureTreeItem tableItem = (DbStructureTreeItem)dbStructureTreeView.getSelectionModel().getSelectedItem();
            if (tableItem==null || connection==null) {
                tablePropertyTab.setDisable(true);
                tableColumnTab.setDisable(true);
                tableIndexTab.setDisable(true);
            } else {
                switch (tableItem.getItemType()) {
                    case DATABASE:
                        tablePropertyTab.setDisable(false);
                        tableColumnTab.setDisable(true);
                        tableIndexTab.setDisable(true);
                        break;
                    case TABLE:
                        tablePropertyTab.setDisable(false);
                        tableColumnTab.setDisable(false);
                        tableIndexTab.setDisable(false);
                        break;
                    default:
                        tablePropertyTab.setDisable(true);
                        tableColumnTab.setDisable(true);
                        tableIndexTab.setDisable(true);
                        break;
                }
            }

            tableStructureTabPaneUpdateService.restart();
//            tableStructureTabPane.getSelectionModel().select(tablePropertyTab);
        }
    }

    public class TableStructureTabPaneUpdate implements BackgroundCallbackInterface {
        @Override
        public void run() throws SQLException {
            DbStructureTreeItem tableItem = (DbStructureTreeItem)dbStructureTreeView.getSelectionModel().getSelectedItem();
            if (tableItem==null || connection==null) {
                return ;
            }

            ObservableList<TablePropertyTab> tablePropertyList = tablePropertyTableView.getItems();
            ObservableList<TableColumnTab> tableColumnList = tableColumnTableView.getItems();

            tablePropertyList.clear();
            tableColumnList.clear();

            DatabaseMetaData metaData = connection.getMetaData();

            switch (tableItem.getItemType()) {
                case DATABASE:
                    updateTablePropertyFromDatabase(metaData, tablePropertyList);
                    break;
                case TABLE:
                    updateTablePropertyFromTable(tableItem, metaData, tablePropertyList);
                    updateTableColumnFromTable(tableItem, metaData, tableColumnList);
                    break;
            }

        }

        private void updateTablePropertyFromDatabase(DatabaseMetaData metaData, ObservableList<TablePropertyTab> list) throws SQLException {

            list.add(TablePropertyTab.builder().key("Database product version").value(metaData.getDatabaseProductVersion()).build());
            list.add(TablePropertyTab.builder().key("Database major version").value(Integer.toString(metaData.getDatabaseMajorVersion())).build());
            list.add(TablePropertyTab.builder().key("Database minor version").value(Integer.toString(metaData.getDatabaseMinorVersion())).build());

            list.add(TablePropertyTab.builder().key("Driver product name").value(metaData.getDriverName()).build());
            list.add(TablePropertyTab.builder().key("Driver product version").value(metaData.getDriverVersion()).build());
            list.add(TablePropertyTab.builder().key("Driver major version").value(Integer.toString(metaData.getDriverMajorVersion())).build());
            list.add(TablePropertyTab.builder().key("Driver minor version").value(Integer.toString(metaData.getDriverMinorVersion())).build());

            list.add(TablePropertyTab.builder().key("JDBC major version").value(Integer.toString(metaData.getJDBCMajorVersion())).build());
            list.add(TablePropertyTab.builder().key("JDBC minor version").value(Integer.toString(metaData.getJDBCMinorVersion())).build());

            list.add(TablePropertyTab.builder().key("Numeric functions").value(metaData.getNumericFunctions()).build());
            list.add(TablePropertyTab.builder().key("String functions").value(metaData.getStringFunctions()).build());
            list.add(TablePropertyTab.builder().key("System functions").value(metaData.getSystemFunctions()).build());
            list.add(TablePropertyTab.builder().key("Time date functions").value(metaData.getTimeDateFunctions()).build());

            list.add(TablePropertyTab.builder().key("Extra name characters").value(metaData.getExtraNameCharacters()).build());
            list.add(TablePropertyTab.builder().key("Identifier quote string").value(metaData.getIdentifierQuoteString()).build());

            list.add(TablePropertyTab.builder().key("Max binary literal length").value(Integer.toString(metaData.getMaxBinaryLiteralLength())).build());
            list.add(TablePropertyTab.builder().key("Max char literal length").value(Integer.toString(metaData.getMaxCharLiteralLength())).build());
            list.add(TablePropertyTab.builder().key("Max column name length").value(Integer.toString(metaData.getMaxColumnNameLength())).build());
            list.add(TablePropertyTab.builder().key("Max column name length").value(Integer.toString(metaData.getMaxColumnNameLength())).build());
            list.add(TablePropertyTab.builder().key("Max columns in group by").value(Integer.toString(metaData.getMaxColumnsInGroupBy())).build());
            list.add(TablePropertyTab.builder().key("Max columns in index").value(Integer.toString(metaData.getMaxColumnsInIndex())).build());
            list.add(TablePropertyTab.builder().key("Max columns in order by").value(Integer.toString(metaData.getMaxColumnsInOrderBy())).build());
            list.add(TablePropertyTab.builder().key("Max columns in select").value(Integer.toString(metaData.getMaxColumnsInSelect())).build());
            list.add(TablePropertyTab.builder().key("Max columns in table").value(Integer.toString(metaData.getMaxColumnsInTable())).build());
            list.add(TablePropertyTab.builder().key("Max connections").value(Integer.toString(metaData.getMaxConnections())).build());
            list.add(TablePropertyTab.builder().key("Max cursor name length").value(Integer.toString(metaData.getMaxCursorNameLength())).build());
            list.add(TablePropertyTab.builder().key("Max index length").value(Integer.toString(metaData.getMaxIndexLength())).build());
            list.add(TablePropertyTab.builder().key("Max procedure name length").value(Integer.toString(metaData.getMaxProcedureNameLength())).build());
            list.add(TablePropertyTab.builder().key("Max row size").value(Integer.toString(metaData.getMaxRowSize())).build());
            list.add(TablePropertyTab.builder().key("Max schema name length").value(Integer.toString(metaData.getMaxSchemaNameLength())).build());
            list.add(TablePropertyTab.builder().key("Max statement length").value(Integer.toString(metaData.getMaxStatementLength())).build());
            list.add(TablePropertyTab.builder().key("Max statements").value(Integer.toString(metaData.getMaxStatements())).build());
            list.add(TablePropertyTab.builder().key("Max table name length").value(Integer.toString(metaData.getMaxTableNameLength())).build());
            list.add(TablePropertyTab.builder().key("Max tables in select").value(Integer.toString(metaData.getMaxTablesInSelect())).build());
            list.add(TablePropertyTab.builder().key("Max user name length").value(Integer.toString(metaData.getMaxUserNameLength())).build());
        }

        private void updateTablePropertyFromTable(DbStructureTreeItem tableItem, DatabaseMetaData metaData, ObservableList<TablePropertyTab> list) throws SQLException {
            showResultSet(metaData.getTables(null, tableItem.getSchema(), tableItem.getValue(), null), list);
        }

        private void showResultSet(ResultSet resultSet, ObservableList<TablePropertyTab> list) throws SQLException {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while(resultSet.next()) {
                for (int loop=0; loop<columnCount; loop++) {
                    list.add(TablePropertyTab.builder().key(metaData.getColumnName(loop + 1)).value(resultSet.getString(loop + 1)).build());
                }
            }
            resultSet.close();
        }

        private void updateTableColumnFromTable(DbStructureTreeItem tableItem, DatabaseMetaData metaData, ObservableList<TableColumnTab> tableColumnList) throws SQLException {
            ResultSet resultSet = metaData.getColumns(null, tableItem.getSchema(), tableItem.getValue(), null);

            while (resultSet.next()) {
                TableColumnTab data = TableColumnTab.builder()
                    .name(getStringForce(resultSet, "COLUMN_NAME"))
                    .type(getStringForce(resultSet, "TYPE_NAME"))
                    .size(getIntForce(resultSet,"COLUMN_SIZE"))
                    .decimalDigits(getIntForce(resultSet, "DECIMAL_DIGITS"))
                    .nullable(getStringForce(resultSet, "NULLABLE"))
//                    .primaryKey(getStringForce(resultSet, "TYPE_NAME"))
                    .remarks(getStringForce(resultSet, "REMARKS"))
                    .columnDefault(getStringForce(resultSet, "COLUMN_DEF"))
                    .autoincrement(getStringForce(resultSet, "IS_AUTOINCREMENT"))
                    .generatedColumn(getStringForce(resultSet, "IS_GENERATEDCOLUMN"))
                        .build();

                tableColumnList.add(data);
            }
            resultSet.close();
        }

        private String getStringForce(ResultSet resultSet, String columnName) {
            try {
                return resultSet.getString(columnName);
            } catch (Exception e) {
                return "";
            }
        }

        private int getIntForce(ResultSet resultSet, String columnName) {
            try {
                return resultSet.getInt(columnName);
            } catch (Exception e) {
                return 0;
            }
        }

    }

    public class QueryResultUpdate implements BackgroundCallbackInterface {
        @Override
        public void run() {
            ObservableList<TableColumn<String, String>> columnList = queryResultTableView.getColumns();
            ObservableList<Map<String, String>> recordList = queryResultTableView.getItems();

            columnList.clear();
//            for (int loop = 0; loop<1000; loop++) {
//                final int l = loop;
//                Platform.runLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        columnList.add(new TableColumn("test"+l));
//                        System.out.println(l);
//                    }
//                });
//            }

            /*
            recordList.clear();
            for (int loop = 0; loop<1000000; loop++) {
                Map<String, String> l = new HashMap<>();
                l.put("test1", "data "+loop);
                recordList.add(l);
            }
            */
/*
            ObservableList<TableColumn<String,String>> colList = queryResultTableView.getColumns();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    colList.add(new TableColumn("test1"));
                }
            });

            // 1件ずつ書き込み
            ObservableList<Map<String, String>> recordProperty = queryResultTableView.getItems();
            List<Integer> list = new ArrayList<>();
            for (int loop = 0; loop<100000; loop++) {
                list.add(loop);

                if (list.size()>=100) {
                    final List<Integer> dispatchList = new ArrayList<>(list);
                    list.clear();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            for (Integer i : dispatchList) {
//                                writeLog("test " + i);
                                Map<String, String> l = new HashMap<>();
                                l.put("test1", "test "+i);
                                recordProperty.add(l);
                            }
                        }
                    });
                }
            }
*/

            List<TableColumn<QueryResult, String>> colList = new ArrayList<>();
            for (int loop = 0; loop < 10; loop++) {
                TableColumn<QueryResult, String> col = new TableColumn<QueryResult, String>("列" + loop);
                final String key = "列" + loop;
                col.setCellValueFactory(
                        new Callback<TableColumn.CellDataFeatures<QueryResult, String>, ObservableValue<String>>() {
                            @Override
                            public ObservableValue<String> call(TableColumn.CellDataFeatures<QueryResult, String> p) {
                                // 列名をキーに、値を返す。
                                return new SimpleStringProperty(p.getValue().getData(key));
                            }
                        });
                colList.add(col);
            }
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    queryResultTableView.getColumns().addAll(colList);
                }
            });

            List<Map<String, String>> rowList = new ArrayList<>();
            for (int row=0; row<20000; row++) {
                Map<String, String> data = new HashMap<String, String>();
                for (int i = 0; i < 10; i++) {
                    data.put("列" + i, "" + row);
                }
                rowList.add(data);

                if (rowList.size()>=1000) {
                    final List<Map<String, String>> dispatchList = new ArrayList<>(rowList);
                    rowList.clear();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            for (Map<String, String> m: dispatchList) {
                                QueryResult r = new QueryResult();
                                r.setData(m);
                                queryResultTableView.getItems().add(r);
                                queryResultTableView.scrollTo(1000000);
                            }
                        }
                    });
                }
            }
        }
    }
    public class QueryResultUpdateService extends Service {
        private ObjectProperty<ObservableList<TableColumn<String,String>>> columnProperty = new SimpleObjectProperty();
        private ObjectProperty<ObservableList<Map<String, String>>> recordProperty = new SimpleObjectProperty();

        public final void setColumnProperty(ObservableList<TableColumn<String,String>> list) {
            columnProperty.set(list);
        }
        public final ObjectProperty<ObservableList<TableColumn<String,String>>> columnProperty() {
            return columnProperty;
        }
        public final void setRecordProperty(ObservableList<Map<String, String>> list) {
            recordProperty.set(list);
        }
        public final ObjectProperty<ObservableList<Map<String, String>>> objectProperty() {
            return recordProperty;
        }

        @Override
        protected Task createTask() {
            final ObservableList<TableColumn<String,String>> colList = columnProperty().get();
            final ObservableList<Map<String, String>> recordList = objectProperty().get();
            return new Task<Void>() {
                @Override
                protected Void call() {
                    Statement statement = null;
                    try {
                        statement = connection.createStatement();
                        statement.executeQuery(queryTextArea.getText());
                        connection.commit();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
//                    ObservableList<TableColumn<String,String>> colList = columnProperty.get();
                    colList.clear();
                    colList.add(new TableColumn("test1"));
//                    colList.add(new TableColumn("test2"));
//                    colList.add(new TableColumn("test3"));

                    recordList.clear();
/*
                    for (int loop = 0; loop<10; loop++) {
                        Map<String, String> l = new HashMap<>();
                        l.put("test1", "data "+loop);
                        recordList.add(l);
                    }
*/
//                    ObservableList<Connect> list = recordProperty.get();
//                    list.add(Connect.builder().libraryPath("aaaa").build());

//                    TableColumn<RData, String> col =
//                            new TableColumn<RData, String>("列" + i);
                    return null;
                }
            };
        }
    }
}
