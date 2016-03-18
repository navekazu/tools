package tools.dbconnector6.service;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import tools.dbconnector6.BackgroundServiceInterface;
import tools.dbconnector6.controller.DbStructureTreeItem;
import tools.dbconnector6.MainControllerInterface;
import tools.dbconnector6.entity.TableColumnTab;
import tools.dbconnector6.entity.TablePropertyTab;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class TableStructureUpdateService implements BackgroundServiceInterface<Void, Void> {
    private MainControllerInterface mainControllerInterface;
    public TableStructureUpdateService(MainControllerInterface mainControllerInterface) {
        this.mainControllerInterface = mainControllerInterface;
    }

    @Override
    public void run(Task task) throws SQLException {
        DbStructureTreeItem tableItem = (DbStructureTreeItem)mainControllerInterface.getDbStructureParam().dbStructureTreeView.getSelectionModel().getSelectedItem();
        if (tableItem==null || mainControllerInterface.getConnection()==null) {
            return ;
        }

        ObservableList<TablePropertyTab> tablePropertyList = mainControllerInterface.getTableStructureTabParam().tablePropertyTableView.getItems();
        ObservableList<TableColumnTab> tableColumnList = mainControllerInterface.getTableStructureTabParam().tableColumnTableView.getItems();

        tablePropertyList.clear();
        tableColumnList.clear();

        DatabaseMetaData metaData = mainControllerInterface.getConnection().getMetaData();

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

    @Override
    public void cancel() throws Exception {

    }

    @Override
    public void updateUIPreparation(Void uiParam) throws Exception {
    }

    @Override
    public void updateUI(Void uiParam) throws Exception {

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
                    .size(getIntForce(resultSet, "COLUMN_SIZE"))
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
