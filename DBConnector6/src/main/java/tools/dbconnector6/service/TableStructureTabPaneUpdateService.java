package tools.dbconnector6.service;

import javafx.application.Platform;
import tools.dbconnector6.BackgroundCallbackInterface;
import tools.dbconnector6.DbStructureTreeItem;
import tools.dbconnector6.MainControllerInterface;

public class TableStructureTabPaneUpdateService implements BackgroundCallbackInterface<Void, TableStructureTabPaneUpdateService.TabDisableProperty> {
    private MainControllerInterface mainControllerInterface;
    public TableStructureTabPaneUpdateService(MainControllerInterface mainControllerInterface) {
        this.mainControllerInterface = mainControllerInterface;
    }

    public class TabDisableProperty {
        boolean tableColumnTabDisable;
        boolean tableIndexTabDisable;
        boolean tablePropertyTabDisable;

        public TabDisableProperty() {
        }
        public TabDisableProperty(boolean tablePropertyTabDisable, boolean tableColumnTabDisable, boolean tableIndexTabDisable) {
            this.tableColumnTabDisable = tableColumnTabDisable;
            this.tableIndexTabDisable = tableIndexTabDisable;
            this.tablePropertyTabDisable = tablePropertyTabDisable;
        }
    }

    @Override
    public void run() throws Exception {
        TabDisableProperty property = new TabDisableProperty();

        DbStructureTreeItem tableItem = (DbStructureTreeItem)mainControllerInterface.getDbStructureParam().dbStructureTreeView.getSelectionModel().getSelectedItem();
        if (tableItem == null || mainControllerInterface.getConnection() == null) {
            property.tablePropertyTabDisable = true;
            property.tableColumnTabDisable = true;
            property.tableIndexTabDisable = true;
        } else {
            switch (tableItem.getItemType()) {
                case DATABASE:
                    property.tablePropertyTabDisable = false;
                    property.tableColumnTabDisable = true;
                    property.tableIndexTabDisable = true;
                    break;
                case TABLE:
                    property.tablePropertyTabDisable = false;
                    property.tableColumnTabDisable = false;
                    property.tableIndexTabDisable = false;
                    break;
                default:
                    property.tablePropertyTabDisable = true;
                    property.tableColumnTabDisable = true;
                    property.tableIndexTabDisable = true;
                    break;
            }
        }

        updateUI(property);
    }

    @Override
    public void updateUIPreparation(Void uiParam) throws Exception {
    }

    @Override
    public void updateUI(TabDisableProperty uiParam) throws Exception {
        final TabDisableProperty dispatchParam = new TabDisableProperty(uiParam.tablePropertyTabDisable, uiParam.tableColumnTabDisable, uiParam.tableIndexTabDisable);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                MainControllerInterface.TableStructureTabParam tabParam = mainControllerInterface.getTableStructureTabParam();
                tabParam.tablePropertyTab.setDisable(dispatchParam.tablePropertyTabDisable);
                tabParam.tableColumnTab.setDisable(dispatchParam.tableColumnTabDisable);
                tabParam.tableIndexTab.setDisable(dispatchParam.tableIndexTabDisable);

                mainControllerInterface.getTableStructureUpdateService().restart();
            }
        });
    }
}
