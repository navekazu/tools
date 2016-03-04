package tools.dbconnector6.service;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import tools.dbconnector6.BackgroundCallbackInterface;
import tools.dbconnector6.DbStructureTreeItem;
import tools.dbconnector6.MainControllerInterface;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DbStructureUpdateService implements BackgroundCallbackInterface<Void, List<TreeItem<String>>> {
    private MainControllerInterface mainControllerInterface;
    public DbStructureUpdateService(MainControllerInterface mainControllerInterface) {
        this.mainControllerInterface = mainControllerInterface;
    }

    @Override
    public void run() throws Exception {
        if (mainControllerInterface.getConnection()==null) {
            updateUI(new ArrayList<>());
            return ;
        }

        List<TreeItem<String>> subList = new ArrayList<>();
        DatabaseMetaData meta = mainControllerInterface.getConnection().getMetaData();
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

        updateUI(subList);
    }

    @Override
    public void updateUIPreparation(Void uiParam) throws Exception {
    }

    @Override
    public void updateUI(List<TreeItem<String>> uiParam) throws Exception {
        final List<TreeItem<String>> dispatchParam = new ArrayList<>(uiParam);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mainControllerInterface.getDbStructureParam().dbStructurRootItem.setValue(mainControllerInterface.getDbStructureParam().dbStructurRootItem.getItemType().getName());
                ObservableList<TreeItem<String>> subList = mainControllerInterface.getDbStructureParam().dbStructurRootItem.getChildren();
                subList.clear();
                subList.addAll(dispatchParam);
                mainControllerInterface.getDbStructureParam().dbStructurRootItem.setExpanded(true);
            }
        });
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

    private String getFilterTextFieldParamValue() {
        return getTextFieldParamValue(mainControllerInterface.getDbStructureParam().filterTextField);
    }

    private String getSchemaTextFieldParamValue() {
        return getTextFieldParamValue(mainControllerInterface.getDbStructureParam().schemaTextField);
    }

    private String getTextFieldParamValue(TextField textField) {
        return "".equals(textField.getText()) ? null : String.format("%%%s%%", textField.getText());
    }
}
