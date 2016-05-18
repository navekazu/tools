package tools.dbconnector6.service;

import javafx.application.Platform;
import javafx.concurrent.Task;
import tools.dbconnector6.MainControllerInterface;
import tools.dbconnector6.serializer.TemporaryQuerySerializer;

import java.nio.file.Path;

/**
 * SQLエディタを立ち上げ、編集内容を受け取ってSQLクエリ編集領域にその編集内容を貼り付けるサービス。
 */
public class SqlEditorLaunchService implements BackgroundServiceInterface<Void, String> {
    // メイン画面へのアクセス用インターフェース
    private MainControllerInterface mainControllerInterface;

    /**
     * コンストラクタ。<br>
     * @param mainControllerInterface メイン画面へのアクセス用インターフェース
     */
    public SqlEditorLaunchService(MainControllerInterface mainControllerInterface) {
        this.mainControllerInterface = mainControllerInterface;
    }

    /**
     * バックグラウンドで実行する処理を実装する。<br>
     * テンポラリファイルを作成し、SQLエディタでそのファイルを編集してもらい、結果をSQLクエリ編集領域に貼り付ける。<br>
     * @param task 生成したバックグラウンド実行を行うTaskのインスタンス
     * @throws Exception 何らかのエラーが発生し処理を中断する場合
     */
    @Override
    public void run(Task task) throws Exception {
        if (mainControllerInterface.getEditorPath().trim().length()<=0) {
            mainControllerInterface.writeLog("SQL editor is not available. Please set up the SQL editor.");
            return;
        }

        // テンポラリファイルを作成
        TemporaryQuerySerializer temporaryQuerySerializer = new TemporaryQuerySerializer();
        Path temporaryQueryPath = temporaryQuerySerializer.createTempolaryFile(mainControllerInterface.getSelectedQuery());

        // テンポラリファイルを実行時引数にしてエディタを起動
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(new String[]{mainControllerInterface.getEditorPath(), temporaryQueryPath.toString()});

        // エディタが終了するまで待機
        prepareUpdate(null);
        process.waitFor();

        // エディタが編集したテンポラリファイルの内容をクエリエリアに貼り付け
        update(temporaryQuerySerializer.readText(temporaryQueryPath));
    }

    /**
     * 更新の前処理。<br>
     * 待機メッセージを表示<br>
     * @param prepareUpdateParam 前処理に必要なパラメータ
     * @throws Exception 何らかのエラーが発生した場合
     */
    @Override
    public void prepareUpdate(final Void prepareUpdateParam) throws Exception {
        Platform.runLater(() -> mainControllerInterface.showWaitDialog(
                "Waiting until the end of SQL edit.",
                "Please save and exit the SQL editor."));
    }

    /**
     * 更新処理。<br>
     * 編集結果をSQLクエリ編集領域に貼り付ける。<br>
     * @param updateParam 編集結果
     * @throws Exception 何らかのエラーが発生した場合
     */
    @Override
    public void update(final String updateParam) throws Exception {
        Platform.runLater(() -> {
            mainControllerInterface.updateSelectedQuery(updateParam);
            mainControllerInterface.hideWaitDialog();
        });
    }

    /**
     * バックグラウンド実行をキャンセルするたびに呼び出される。<br>
     * 待機メッセージを非表示にする。<br>
     */
    @Override
    public void cancel() {
        callHideWaitDialog();
    }

    /**
     * Serviceの状態がCANCELLED状態に遷移するたびに呼び出される。<br>
     * 待機メッセージを非表示にする。<br>
     */
    @Override
    public void cancelled() {
        callHideWaitDialog();
    }

    /**
     * Serviceの状態がFAILED状態に遷移するたびに呼び出される。<br>
     * 待機メッセージを非表示にする。<br>
     */
    @Override
    public void failed() {
        callHideWaitDialog();
    }

    /**
     * もし実行中ではない時にキャンセル要求があった場合のメッセージ。<br>
     * @return メッセージ
     */
    @Override
    public String getNotRunningMessage() {
        return "";
    }

    // 待機メッセージを非表示にする。
    private void callHideWaitDialog() {
        Platform.runLater(() -> mainControllerInterface.hideWaitDialog());
    }
}
