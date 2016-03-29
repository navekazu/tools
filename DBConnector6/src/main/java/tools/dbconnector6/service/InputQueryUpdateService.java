package tools.dbconnector6.service;

import javafx.application.Platform;
import javafx.concurrent.Task;
import tools.dbconnector6.BackgroundServiceInterface;
import tools.dbconnector6.MainControllerInterface;
import tools.dbconnector6.serializer.TemporaryQuerySerializer;

import java.nio.file.Path;

public class InputQueryUpdateService implements BackgroundServiceInterface<Void, String> {
    private MainControllerInterface mainControllerInterface;
    public InputQueryUpdateService(MainControllerInterface mainControllerInterface) {
        this.mainControllerInterface = mainControllerInterface;
    }

    @Override
    public void run(Task task) throws Exception {
        if (mainControllerInterface.getEditorPath().trim().length()<=0) {
            mainControllerInterface.writeLog("SQL editor not setup.");
            return;
        }

        TemporaryQuerySerializer temporaryQuerySerializer = new TemporaryQuerySerializer();
        Path temporaryQueryPath = temporaryQuerySerializer.createTempolaryFile(mainControllerInterface.getSelectedQuery());

        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(new String[]{mainControllerInterface.getEditorPath(), temporaryQueryPath.toString()});

        updateUIPreparation(null);
        process.waitFor();

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
    public void updateUIPreparation(Void uiParam) throws Exception {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mainControllerInterface.showWaitDialog("SQL編集の終了まで待機しています。", "編集が完了したらファイルを保存してエディタを終了してください。");
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
