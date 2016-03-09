package tools.dbconnector6.service;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
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
import java.util.Comparator;
import java.util.List;

import static tools.dbconnector6.DbStructureTreeItem.ItemType.DATABASE;

public class DbStructureUpdateService implements BackgroundCallbackInterface<Void, DbStructureTreeItem> {
    private MainControllerInterface mainControllerInterface;
    public DbStructureUpdateService(MainControllerInterface mainControllerInterface) {
        this.mainControllerInterface = mainControllerInterface;
    }

    @Override
    public void run(Task task) throws Exception {
        updateUIPreparation(null);

        if (mainControllerInterface.getConnection()==null) {
            return ;
        }

        DatabaseMetaData meta = mainControllerInterface.getConnection().getMetaData();
        List<DbStructureTreeItem> schemaList = getSchemaList(meta);

        for (DbStructureTreeItem item: schemaList) {
            Service service = new Service() {
                @Override
                protected Task createTask() {
                    return new SchemaSearchTask(meta, item);
                }
            };
            service.restart();
        }
    }

    @Override
    public void cancel() throws Exception {

    }

    private class  SchemaSearchTask extends Task {
        DatabaseMetaData meta;
        DbStructureTreeItem item;
        public SchemaSearchTask(DatabaseMetaData meta, DbStructureTreeItem item) {
            this.meta = meta;
            this.item = item;

        }
        @Override
        protected Object call() throws Exception {
            List<DbStructureTreeItem> subList = new ArrayList<>();
            DbStructureTreeItem.ItemType itemType;

            mainControllerInterface.writeLog("Schema parsing...(%s)", item.getSchema());

            // サポートされていないAPIを呼ぶと例外が発生するので、そのときは握りつぶして次を呼び出す
            try {
                ResultSet resultSet = meta.getTableTypes();
                itemType = DbStructureTreeItem.ItemType.TABLE;
                while (resultSet.next()) {
                    subList.add(createGroupItem(meta.getTables(null, item.getSchema(), getFilterTextFieldParamValue(), new String[]{resultSet.getString("TABLE_TYPE")})
                            , "TABLE_NAME", "TABLE_SCHEM", resultSet.getString("TABLE_TYPE"), itemType));
                }
                resultSet.close();
            } catch (Throwable e) {
            }

            try {
                itemType = DbStructureTreeItem.ItemType.FUNCTION;
                subList.add(createGroupItem(meta.getFunctions(null, item.getSchema(), getFilterTextFieldParamValue())
                        , "FUNCTION_NAME", "FUNCTION_SCHEM", itemType.getName(), itemType));
            } catch (Throwable e) {
            }

            try {
                itemType = DbStructureTreeItem.ItemType.PROCEDURE;
                subList.add(createGroupItem(meta.getProcedures(null, item.getSchema(), getFilterTextFieldParamValue())
                        , "PROCEDURE_NAME", "PROCEDURE_SCHEM", itemType.getName(), itemType));
            } catch (Throwable e) {
            }
            item.getChildren().addAll(subList);

            mainControllerInterface.writeLog("Schema parsed. (%s)", item.getSchema());

            updateUI(item);
            return null;
        }
    }

    private List<DbStructureTreeItem> getSchemaList(DatabaseMetaData meta) {
        List<DbStructureTreeItem> schemaList = new ArrayList<>();

        try {
            ResultSet resultSet = meta.getSchemas();

            while(resultSet.next()) {
                schemaList.add(new DbStructureTreeItem(DbStructureTreeItem.ItemType.SCHEMA
                        , resultSet.getString("TABLE_SCHEM"), resultSet.getString("TABLE_SCHEM")));
            }
        } catch(Throwable e) {
            schemaList.clear();
        }

        // 空ならダミーのアイテムを入れる
        if (schemaList.isEmpty()) {
            schemaList.add(new DbStructureTreeItem(DbStructureTreeItem.ItemType.SCHEMA, "(none schema)", ""));
        }

        return schemaList;
    }

    @Override
    public void updateUIPreparation(Void uiParam) throws Exception {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mainControllerInterface.getDbStructureParam().dbStructurRootItem.setValue(mainControllerInterface.getDbStructureParam().dbStructurRootItem.getItemType().getName());
                ObservableList<TreeItem<String>> subList = mainControllerInterface.getDbStructureParam().dbStructurRootItem.getChildren();
                subList.clear();
                mainControllerInterface.getDbStructureParam().dbStructurRootItem.setExpanded(true);
            }
        });
    }

    @Override
    public void updateUI(DbStructureTreeItem uiParam) throws Exception {
        final TreeItem<String> dispatchParam = uiParam;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mainControllerInterface.getDbStructureParam().dbStructurRootItem.setValue(mainControllerInterface.getDbStructureParam().dbStructurRootItem.getItemType().getName());
                ObservableList<TreeItem<String>> subList = mainControllerInterface.getDbStructureParam().dbStructurRootItem.getChildren();
                subList.add(dispatchParam);
                FXCollections.sort(subList, new Comparator<TreeItem<String>>() {
                    @Override
                    public int compare(TreeItem<String> o1, TreeItem<String> o2) {
                        return o1.getValue().compareTo(o2.getValue());
                    }
                });
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

    private String getTextFieldParamValue(TextField textField) {
        return "".equals(textField.getText()) ? null : String.format("%%%s%%", textField.getText());
    }
}
