package tools.dbconnector6.service;

import javafx.application.Platform;
import javafx.concurrent.Task;
import tools.dbconnector6.controller.DbStructureTreeItem;
import tools.dbconnector6.MainControllerInterface;
import tools.dbconnector6.entity.TableColumnTab;
import tools.dbconnector6.entity.TableIndexTab;
import tools.dbconnector6.entity.TablePropertyTab;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public class TableStructureUpdateService implements BackgroundServiceInterface<Void, TableStructureUpdateService.TableStructures> {
    // メイン画面へのアクセス用インターフェース
    private MainControllerInterface mainControllerInterface;

    /**
     * コンストラクタ。<br>
     * @param mainControllerInterface メイン画面へのアクセス用インターフェース
     */
    public TableStructureUpdateService(MainControllerInterface mainControllerInterface) {
        this.mainControllerInterface = mainControllerInterface;
    }

    public class TableStructures {
        public List<TablePropertyTab> tablePropertyList;
        public List<TableColumnTab> tableColumnList;
        public List<TableIndexTab> tableIndexList;
    }

    @Override
    public void run(Task task) throws Exception {
        DbStructureTreeItem tableItem = (DbStructureTreeItem)mainControllerInterface.getDbStructureParam().dbStructureTreeView.getSelectionModel().getSelectedItem();
        if (tableItem==null || !mainControllerInterface.isConnectWithoutMessage()) {
            prepareUpdate(null);
            return ;
        }

        TableStructures tableStructures = new TableStructures();
        tableStructures.tablePropertyList = new ArrayList<>();
        tableStructures.tableColumnList = new ArrayList<>();
        tableStructures.tableIndexList = new ArrayList<>();

        DatabaseMetaData metaData = mainControllerInterface.getConnection().getMetaData();

        switch (tableItem.getItemType()) {
            case DATABASE:
                updateTablePropertyFromDatabase(metaData, tableStructures.tablePropertyList);
                break;
            case TABLE:
                updateTablePropertyFromTable(tableItem, metaData, tableStructures.tablePropertyList);
                updateTableColumnFromTable(tableItem, metaData, tableStructures.tableColumnList);
                updateTableIndexFromTable(tableItem, metaData, tableStructures.tableIndexList);
                break;
        }
        prepareUpdate(null);
        update(tableStructures);

    }

    @Override
    public void prepareUpdate(final Void prepareUpdateParam) throws Exception {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mainControllerInterface.getTableStructureTabParam().tablePropertyTableView.getItems().clear();
                mainControllerInterface.getTableStructureTabParam().tableColumnTableView.getItems().clear();
                mainControllerInterface.getTableStructureTabParam().tableIndexNameComboBox.getItems().clear();
                mainControllerInterface.getTableStructureTabParam().tableIndexPrimaryKeyTextField.setText("");
                mainControllerInterface.getTableStructureTabParam().tableIndexUniqueKeyTextField.setText("");
                mainControllerInterface.getTableStructureTabParam().tableIndexListView.getItems().clear();
            }
        });
    }

    @Override
    public void update(final TableStructures updateParam) throws Exception {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mainControllerInterface.getTableStructureTabParam().tablePropertyTableView.getItems().addAll(updateParam.tablePropertyList);
                mainControllerInterface.getTableStructureTabParam().tableColumnTableView.getItems().addAll(updateParam.tableColumnList);
                mainControllerInterface.getTableStructureTabParam().tableIndexNameComboBox.getItems().addAll(updateParam.tableIndexList);
                if (updateParam.tableIndexList.size()>=1) {
                    mainControllerInterface.getTableStructureTabParam().tableIndexNameComboBox.getSelectionModel().select(0);
                }
            }
        });
    }

    @Override
    public void cancel() {

    }

    @Override
    public void cancelled() {

    }

    @Override
    public void failed() {

    }

    @Override
    public String getNotRunningMessage() {
        return "";
    }

    private void updateTablePropertyFromDatabase(DatabaseMetaData metaData, List<TablePropertyTab> list) throws SQLException {

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

    private void updateTablePropertyFromTable(DbStructureTreeItem tableItem, DatabaseMetaData metaData, List<TablePropertyTab> list) throws SQLException {
        try (ResultSet resultSet = metaData.getTables(null, tableItem.getSchema(), tableItem.getValue(), null)) {
            showResultSet(resultSet, list);
        }
    }

    private void showResultSet(ResultSet resultSet, List<TablePropertyTab> list) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (resultSet.next()) {
            for (int loop = 0; loop < columnCount; loop++) {
                list.add(TablePropertyTab.builder().key(metaData.getColumnName(loop + 1)).value(resultSet.getString(loop + 1)).build());
            }
        }
    }

    private static final Map<Integer, String> NULLABLE_MAP = new HashMap<>();
    static {
        NULLABLE_MAP.put(DatabaseMetaData.columnNoNulls, "No Null");
        NULLABLE_MAP.put(DatabaseMetaData.columnNullable, "");
        NULLABLE_MAP.put(DatabaseMetaData.columnNullableUnknown, "Unknown");
    }
    private void updateTableColumnFromTable(DbStructureTreeItem tableItem, DatabaseMetaData metaData, List<TableColumnTab> tableColumnList) throws SQLException {
        Map<String, Integer> primaryKeys = new HashMap<>();

        // プライマリーキーの一覧を作成
        // key -> プライマリーキーとなる列名
        // value -> 順序
        try (ResultSet resultSet = metaData.getPrimaryKeys(null, tableItem.getSchema(), tableItem.getValue())) {
            while (resultSet.next()) {
                primaryKeys.put(resultSet.getString("COLUMN_NAME"), resultSet.getInt("KEY_SEQ"));
            }
        }

        // tableColumnListの更新
        try (ResultSet resultSet = metaData.getColumns(null, tableItem.getSchema(), tableItem.getValue(), null)) {
            while (resultSet.next()) {
                TableColumnTab data = TableColumnTab.builder()
                        .name(getStringForce(resultSet, "COLUMN_NAME"))
                        .type(getStringForce(resultSet, "TYPE_NAME"))
                        .size(getIntForce(resultSet, "COLUMN_SIZE"))
                        .decimalDigits(getIntForce(resultSet, "DECIMAL_DIGITS"))
                        .nullable(NULLABLE_MAP.get(getIntForce(resultSet, "NULLABLE")))
                        .primaryKey(primaryKeys.get(getStringForce(resultSet, "COLUMN_NAME")))
                        .remarks(getStringForce(resultSet, "REMARKS"))
                        .columnDefault(getStringForce(resultSet, "COLUMN_DEF"))
                        .autoincrement(getStringForce(resultSet, "IS_AUTOINCREMENT"))
                        .generatedColumn(getStringForce(resultSet, "IS_GENERATEDCOLUMN"))
                        .build();

                tableColumnList.add(data);
            }
        }
    }

    private void updateTableIndexFromTable(DbStructureTreeItem tableItem, DatabaseMetaData metaData, List<TableIndexTab> tableIndexList) throws SQLException {
        String primaryKeyName = null;

        // Primary key
        try (ResultSet resultSet = metaData.getPrimaryKeys(null, tableItem.getSchema(), tableItem.getValue())) {
            TableIndexTab tableIndexTab = null;
            Map<Short, String> columns = null;

            while (resultSet.next()) {
                if (tableIndexTab==null) {
                    tableIndexTab = TableIndexTab.builder()
                            .indexName(resultSet.getString("PK_NAME")==null? "Primary key": resultSet.getString("PK_NAME"))
                            .primaryKey(true)
                            .uniqueKey(true)
                            .build();
                    columns = new HashMap<>();
                    primaryKeyName = resultSet.getString("PK_NAME");
                }
                columns.put(resultSet.getShort("KEY_SEQ"), resultSet.getString("COLUMN_NAME"));
            }

            if (tableIndexTab!=null) {
                tableIndexTab.setColumnList(mapToList(columns));
                tableIndexList.add(tableIndexTab);
            }
        }

        // Index
        try (ResultSet resultSet = metaData.getIndexInfo(null, tableItem.getSchema(), tableItem.getValue(), false, false)) {
            TableIndexTab tableIndexTab = null;
            Map<Short, String> columns = null;

            while (resultSet.next()) {
                // "表のインデックスの記述に連動して返される表の統計情報"は無視
                if (resultSet.getShort("TYPE")==DatabaseMetaData.tableIndexStatistic) {
                    continue;
                }

                // プライマリキー名が取れていてインデックスと同じ名前なら、既にプライマリキーとして追加しているので読み飛ばす
                if (primaryKeyName!=null && primaryKeyName.equals(resultSet.getString("INDEX_NAME"))) {
                    continue;
                }

                // 名前が変わったらtableIndexTabを作成
                if (tableIndexTab==null || !tableIndexTab.getIndexName().equals(resultSet.getString("INDEX_NAME"))) {
                    if (tableIndexTab!=null) {
                        tableIndexTab.setColumnList(mapToList(columns));
                        tableIndexList.add(tableIndexTab);
                    }
                    tableIndexTab = TableIndexTab.builder()
                            .indexName(resultSet.getString("INDEX_NAME") == null ? "Index" : resultSet.getString("INDEX_NAME"))
                            .primaryKey(false)
                            .uniqueKey(!resultSet.getBoolean("NON_UNIQUE"))
                            .build();
                    columns = new HashMap<>();
                }
                columns.put(resultSet.getShort("ORDINAL_POSITION"), resultSet.getString("COLUMN_NAME"));
            }
            if (tableIndexTab!=null) {
                tableIndexTab.setColumnList(mapToList(columns));
                tableIndexList.add(tableIndexTab);
            }
        }
    }

    private List<String> mapToList(final Map<Short, String> map) {
        List<String> columnList = new ArrayList<>();
        Set<Short> keyList = map.keySet();
        keyList.stream().forEach(i -> columnList.add(map.get(i)));

        return columnList;
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
