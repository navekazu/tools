package tools.dbconnector6.service;

import javafx.application.Platform;
import javafx.concurrent.Task;
import tools.dbconnector6.controller.DbStructureTreeItem;
import tools.dbconnector6.MainControllerInterface;

public class TableStructureTabPaneUpdateService implements BackgroundServiceInterface<Void, TableStructureTabPaneUpdateService.TabDisableProperty> {
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
    public void run(Task task) throws Exception {
        TabDisableProperty property = new TabDisableProperty();

        DbStructureTreeItem tableItem = (DbStructureTreeItem)mainControllerInterface.getDbStructureParam().dbStructureTreeView.getSelectionModel().getSelectedItem();
        if (tableItem == null || !mainControllerInterface.isConnectWithoutMessage()) {
            property.tablePropertyTabDisable = true;
            property.tableColumnTabDisable = true;
            property.tableIndexTabDisable = true;
        } else {
            // ToDo: きれいにする
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
    public void cancel() throws Exception {

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

    @Override
    public void updateUIPreparation(final Void uiParam) throws Exception {
    }

    @Override
    public void updateUI(final TabDisableProperty uiParam) throws Exception {
//        final TabDisableProperty dispatchParam = new TabDisableProperty(uiParam.tablePropertyTabDisable, uiParam.tableColumnTabDisable, uiParam.tableIndexTabDisable);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                MainControllerInterface.TableStructureTabParam tabParam = mainControllerInterface.getTableStructureTabParam();
//                tabParam.tablePropertyTab.setDisable(dispatchParam.tablePropertyTabDisable);
//                tabParam.tableColumnTab.setDisable(dispatchParam.tableColumnTabDisable);
//                tabParam.tableIndexTab.setDisable(dispatchParam.tableIndexTabDisable);
                tabParam.tablePropertyTab.setDisable(uiParam.tablePropertyTabDisable);
                tabParam.tableColumnTab.setDisable(uiParam.tableColumnTabDisable);
                tabParam.tableIndexTab.setDisable(uiParam.tableIndexTabDisable);

                mainControllerInterface.getTableStructureUpdateService().restart();
            }
        });
    }
}
