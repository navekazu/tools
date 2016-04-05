package tools.dbconnector6.service;

import javafx.application.Platform;
import javafx.concurrent.Task;
import tools.dbconnector6.BackgroundServiceInterface;
import tools.dbconnector6.MainControllerInterface;
import tools.dbconnector6.serializer.TemporaryQuerySerializer;

import java.nio.file.Path;

public class SqlEditorLaunchService implements BackgroundServiceInterface<Void, String> {
    private MainControllerInterface mainControllerInterface;
    public SqlEditorLaunchService(MainControllerInterface mainControllerInterface) {
        this.mainControllerInterface = mainControllerInterface;
    }

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
        updateUIPreparation(null);
        process.waitFor();

        // エディタが編集したテンポラリファイルの内容をクエリエリアに貼り付け
        updateUI(temporaryQuerySerializer.readText(temporaryQueryPath));
    }

    @Override
    public void cancel() throws Exception {
        callHideWaitDialog();
    }

    @Override
    public void cancelled() {
        callHideWaitDialog();
    }

    @Override
    public void failed() {
        callHideWaitDialog();
    }

    private void callHideWaitDialog() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mainControllerInterface.hideWaitDialog();
            }
        });
    }

    @Override
    public void updateUIPreparation(final Void uiParam) throws Exception {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mainControllerInterface.showWaitDialog(
                        "Waiting until the end of SQL edit.",
                        "Please save and exit the SQL editor.");
            }
        });
    }

    @Override
    public void updateUI(final String uiParam) throws Exception {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mainControllerInterface.updateSelectedQuery(uiParam);
                mainControllerInterface.hideWaitDialog();
            }
        });
    }
}
