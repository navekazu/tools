package tools.dbconnector6.service;

import javafx.application.Platform;
import javafx.concurrent.Task;
import lombok.experimental.Builder;
import tools.dbconnector6.controller.DbStructureTreeItem;
import tools.dbconnector6.MainControllerInterface;

import java.util.HashMap;
import java.util.Map;

/**
 * メイン画面左下のテーブル構造表示欄の、タブのenable/disable状態を更新するサービス。
 */
public class TableStructureTabPaneUpdateService implements BackgroundServiceInterface<Void, TableStructureTabPaneUpdateService.TabDisableProperty> {
    // メイン画面へのアクセス用インターフェース
    private MainControllerInterface mainControllerInterface;

    /**
     * コンストラクタ。<br>
     * @param mainControllerInterface メイン画面へのアクセス用インターフェース
     */
    public TableStructureTabPaneUpdateService(MainControllerInterface mainControllerInterface) {
        this.mainControllerInterface = mainControllerInterface;
    }

    /**
     * 3つのタブの状態を表現する構造体
     */
    @Builder
    public static class TabDisableProperty {
        boolean tablePropertyTabDisable;        // テーブルプロパティのタブ
        boolean tableColumnTabDisable;          // カラム一覧のタブ
        boolean tableIndexTabDisable;           // インデックス一覧のタブ
    }

    private static final Map<DbStructureTreeItem.ItemType, TabDisableProperty> TAB_MAP = new HashMap<>();
    static {
        TAB_MAP.put(DbStructureTreeItem.ItemType.DATABASE,  TabDisableProperty.builder().tablePropertyTabDisable(false).tableColumnTabDisable(true).tableIndexTabDisable(true).build());
        TAB_MAP.put(DbStructureTreeItem.ItemType.TABLE,     TabDisableProperty.builder().tablePropertyTabDisable(false).tableColumnTabDisable(false).tableIndexTabDisable(false).build());

        // 以下すべて利用不可
        TAB_MAP.put(DbStructureTreeItem.ItemType.GROUP,     TabDisableProperty.builder().tablePropertyTabDisable(true).tableColumnTabDisable(true).tableIndexTabDisable(true).build());
        TAB_MAP.put(DbStructureTreeItem.ItemType.FUNCTION,  TabDisableProperty.builder().tablePropertyTabDisable(true).tableColumnTabDisable(true).tableIndexTabDisable(true).build());
        TAB_MAP.put(DbStructureTreeItem.ItemType.PROCEDURE, TabDisableProperty.builder().tablePropertyTabDisable(true).tableColumnTabDisable(true).tableIndexTabDisable(true).build());
        TAB_MAP.put(DbStructureTreeItem.ItemType.SCHEMA,    TabDisableProperty.builder().tablePropertyTabDisable(true).tableColumnTabDisable(true).tableIndexTabDisable(true).build());
    }

    /**
     * バックグラウンドで実行する処理を実装する。<br>
     * メイン画面左上のデータベース構造の選択状態に応じて、メイン画面左下のテーブル構造表示欄のタブ状態を更新する。<br>
     * @param task 生成したバックグラウンド実行を行うTaskのインスタンス
     * @throws Exception 何らかのエラーが発生し処理を中断する場合
     */
    @Override
    public void run(Task task) throws Exception {
        TabDisableProperty property = TabDisableProperty.builder()
                .tablePropertyTabDisable(true)
                .tableColumnTabDisable(true)
                .tableIndexTabDisable(true)
                .build();

        // データベースに接続していて、かつ何らかのデータベース構造を選択している場合、規定値に則ってタブ状態を更新
        DbStructureTreeItem tableItem = (DbStructureTreeItem)mainControllerInterface.getDbStructureParam().dbStructureTreeView.getSelectionModel().getSelectedItem();
        if (mainControllerInterface.isConnectWithoutOutputMessage() && tableItem != null) {
            property = TAB_MAP.get(tableItem.getItemType());
        }

        update(property);
    }

    /**
     * 更新の前処理。<br>
     * 何もしない。<br>
     * @param prepareUpdateParam 前処理に必要なパラメータ
     * @throws Exception 何らかのエラーが発生した場合
     */
    @Override
    public void prepareUpdate(final Void prepareUpdateParam) throws Exception {
    }

    /**
     * 更新処理。<br>
     * 。<br>
     * @param updateParam 更新処理に必要なパラメータ
     * @throws Exception 何らかのエラーが発生した場合
     */
    @Override
    public void update(final TabDisableProperty updateParam) throws Exception {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                MainControllerInterface.TableStructureTabParam tabParam = mainControllerInterface.getTableStructureTabParam();
                tabParam.tablePropertyTab.setDisable(updateParam.tablePropertyTabDisable);
                tabParam.tableColumnTab.setDisable(updateParam.tableColumnTabDisable);
                tabParam.tableIndexTab.setDisable(updateParam.tableIndexTabDisable);

                mainControllerInterface.getTableStructureUpdateService().restart();
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
}
