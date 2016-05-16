package tools.dbconnector6.service;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import tools.dbconnector6.MainControllerInterface;
import tools.dbconnector6.controller.DbStructureTreeItem;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 画面左上のデータベース構造を解析し表示するサービス。<br>
 */
public class DbStructureUpdateService implements BackgroundServiceInterface<Void, DbStructureTreeItem> {
    // メイン画面へのアクセス用インターフェース
    private MainControllerInterface mainControllerInterface;

    /**
     * コンストラクタ。<br>
     * @param mainControllerInterface メイン画面へのアクセス用インターフェース
     */
    public DbStructureUpdateService(MainControllerInterface mainControllerInterface) {
        this.mainControllerInterface = mainControllerInterface;
    }

    /**
     * 接続先データベースのスキーマから、テーブル一覧（シノニム、テーブル、ビュー、など）、
     * ファンクション一覧、プロシージャ一覧を取得し、データベース構造を画面左上にツリー形式で表示する。<br>
     * 解析はスキーマごとにスレッドを立てて行う。<br>
     * @param task 生成したバックグラウンド実行を行うTaskのインスタンス
     * @throws Exception 何らかのエラーが発生し処理を中断する場合
     */
    @Override
    public void run(Task task) throws Exception {
        // クリーンナップ
        prepareUpdate(null);

        if (!mainControllerInterface.isConnectWithoutOutputMessage()) {
            return ;
        }

        // スキーマの一覧を取得
        DatabaseMetaData meta = mainControllerInterface.getConnection().getMetaData();
        List<DbStructureTreeItem> schemaList = getSchemaList(meta);

        // スキーマごとにスレッドを立てて、スキーマ単位に構造を解析
        // ToDo: 同時実行スレッド上限(32bit Windowsで2048本)を考慮して実装する必要がある
        // ToDo: 全スレッドが終了したことをユーザーに知らせる仕組みが必要
        schemaList.parallelStream().forEach(item -> {
            Service service = new Service() {
                @Override
                protected Task createTask() {
                    return new SchemaSearchTask(meta, item);
                }
            };
            service.restart();
        });
    }

    /**
     * データベース構造のツリーをクリアする。<br>
     * @param prepareUpdateParam 不使用
     * @throws Exception 何らかのエラーが発生した場合
     */
    @Override
    public void prepareUpdate(final Void prepareUpdateParam) throws Exception {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mainControllerInterface.getDbStructureParam().dbStructureRootItem.setValue(mainControllerInterface.getDbStructureParam().dbStructureRootItem.getItemType().getName());
                ObservableList<TreeItem<String>> subList = mainControllerInterface.getDbStructureParam().dbStructureRootItem.getChildren();
                subList.clear();
                mainControllerInterface.getDbStructureParam().dbStructureRootItem.setExpanded(true);
            }
        });
    }

    /**
     * データベース構造のツリーを更新する。<br>
     * 呼び出しはスキーマ単位で行われる。<br>
     * @param updateParam ツリーのルートにぶら下がる子要素
     * @throws Exception 何らかのエラーが発生した場合
     */
    @Override
    public void update(final DbStructureTreeItem updateParam) throws Exception {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mainControllerInterface.getDbStructureParam().dbStructureRootItem.setValue(mainControllerInterface.getDbStructureParam().dbStructureRootItem.getItemType().getName());
                ObservableList<TreeItem<String>> subList = mainControllerInterface.getDbStructureParam().dbStructureRootItem.getChildren();
                subList.add(updateParam);
                FXCollections.sort(subList, new Comparator<TreeItem<String>>() {
                    @Override
                    public int compare(TreeItem<String> o1, TreeItem<String> o2) {
                        return o1.getValue().compareTo(o2.getValue());
                    }
                });
                mainControllerInterface.getDbStructureParam().dbStructureRootItem.setExpanded(true);
            }
        });
    }

    /**
     * バックグラウンド実行をキャンセルするたびに呼び出される。<br>
     */
    @Override
    public void cancel() {
        // ToDo:実行したスレッドを終了しないといけないはず・・・
    }

    /**
     * Serviceの状態がCANCELLED状態に遷移するたびに呼び出される。<br>
     */
    @Override
    public void cancelled() {
    }

    /**
     * Serviceの状態がFAILED状態に遷移するたびに呼び出される。<br>
     */
    @Override
    public void failed() {
    }

    /**
     * もし実行中ではない時にキャンセル要求があった場合のメッセージ。<br>
     * 何も返さない。<br>
     * @return メッセージ
     */
    @Override
    public String getNotRunningMessage() {
        return "";
    }

    /**
     * スキーマから、テーブル一覧（シノニム、テーブル、ビュー、など）、ファンクション一覧、プロシージャ一覧を取得し画面更新を行う。
     * スキーマ単位にインスタンスを作成すること。
     */
    private class  SchemaSearchTask extends Task<Void> {
        // 現在接続中のデータベースメタデータ
        private DatabaseMetaData meta;

        // 更新先のツリーアイテム
        private DbStructureTreeItem item;

        /**
         * コンストラクタ
         * @param meta 現在接続中のデータベースメタデータ。
         * @param item 更新先のツリーアイテム。ここから取得対象のスキーマ名を取得する。
         */
        public SchemaSearchTask(DatabaseMetaData meta, DbStructureTreeItem item) {
            this.meta = meta;
            this.item = item;

        }

        /**
         * 更新先のツリーアイテムから対象のスキーマ名を取得し、
         * そのスキーマのテーブル一覧（シノニム、テーブル、ビュー、など）、
         * ファンクション一覧、プロシージャ一覧を取得しツリーアイテムに追加する。<br>
         * @return nullを返す
         * @throws Exception バックグラウンド操作中に発生した未処理の例外。
         *                  データベース操作で発生した例外は握りつぶす。（後続の一覧取得は続行したい為）
         */
        @Override
        protected Void call() throws Exception {
            List<DbStructureTreeItem> subList = new ArrayList<>();
            DbStructureTreeItem.ItemType itemType;

            mainControllerInterface.writeLog("Schema parsing...(%s)", item.getSchema());

            // サポートされていないAPIを呼ぶと例外が発生するので、そのときは握りつぶして次を呼び出す

            // テーブルタイプの一覧からテーブル一覧（シノニム、テーブル、ビュー、など）を作成
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

            // ファンクション一覧を作成
            try {
                itemType = DbStructureTreeItem.ItemType.FUNCTION;
                subList.add(createGroupItem(meta.getFunctions(null, item.getSchema(), getFilterTextFieldParamValue())
                        , "FUNCTION_NAME", "FUNCTION_SCHEM", itemType.getName(), itemType));
            } catch (Throwable e) {
            }

            // プロシージャ一覧を作成
            try {
                itemType = DbStructureTreeItem.ItemType.PROCEDURE;
                subList.add(createGroupItem(meta.getProcedures(null, item.getSchema(), getFilterTextFieldParamValue())
                        , "PROCEDURE_NAME", "PROCEDURE_SCHEM", itemType.getName(), itemType));
            } catch (Throwable e) {
            }
            item.getChildren().addAll(subList);

            mainControllerInterface.writeLog("Schema parsed. (%s)", item.getSchema());

            update(item);
            return null;
        }
    }

    /**
     * 現在接続中のデータベースのスキーマ一覧を取得する。<br>
     * @param meta 現在接続中のデータベースメタデータ
     * @return スキーマ一覧
     * @throws SQLException スキーマ取得に失敗した場合
     */
    private List<DbStructureTreeItem> getSchemaList(DatabaseMetaData meta) throws SQLException {
        List<DbStructureTreeItem> schemaList = new ArrayList<>();
        ResultSet resultSet = meta.getSchemas();

        while (resultSet.next()) {
            schemaList.add(new DbStructureTreeItem(DbStructureTreeItem.ItemType.SCHEMA
                    , resultSet.getString("TABLE_SCHEM"), resultSet.getString("TABLE_SCHEM")));
        }

        // 空ならダミーのアイテムを入れる
        if (schemaList.isEmpty()) {
            schemaList.add(new DbStructureTreeItem(DbStructureTreeItem.ItemType.SCHEMA, "(none schema)", ""));
        }

        return schemaList;
    }

    /**
     * 指定されたルート要素名を親とするツリーアイテムを作成する。子の要素をResultSetから作成する。<br>
     * @param resultSet 取得元のResultSet
     * @param colName ResultSetから取得する列
     * @param schemaName ツリーアイテムに指定するスキーマ名
     * @param name 作成するツリーアイテムのルート要素名
     * @param itemType 作成するツリーアイテムのルート要素タイプ
     * @return 作成したツリーアイテム
     * @throws SQLException ResultSetからのデータ取得に失敗した場合
     */
    private DbStructureTreeItem createGroupItem(ResultSet resultSet, String colName, String schemaName, String name, DbStructureTreeItem.ItemType itemType) throws SQLException {

        List<DbStructureTreeItem> subList = new ArrayList<>();
        while (resultSet.next()) {
            subList.add(new DbStructureTreeItem(itemType, resultSet.getString(colName), resultSet.getString(schemaName)));
        }
        Collections.sort(subList);

        DbStructureTreeItem tableItem = new DbStructureTreeItem(DbStructureTreeItem.ItemType.GROUP, name, null);
        ObservableList<TreeItem<String>> tableList = tableItem.getChildren();
        tableList.addAll(subList);

        resultSet.close();
        return tableItem;
    }

    /**
     * メイン画面のフィルタ入力欄の値を取得する。<br>
     * 値は前後に "%" を入れる。値が空の場合はnullを返す。
     * @return 前後に "%" を入れたフィルタ入力欄の値を返す。値が空の場合はnullを返す。
     */
    private String getFilterTextFieldParamValue() {
        TextField textField = mainControllerInterface.getDbStructureParam().filterTextField;
        String text = textField.getText();
        return "".equals(text) ? null: String.format("%%%s%%", text);
    }
}
