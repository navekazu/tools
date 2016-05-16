package tools.dbconnector6.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import tools.dbconnector6.MainControllerInterface;

/**
 * バックグラウンドサービスの実行制御を行う。<br>
 * 実行はBackgroundServiceInterfaceを実装したクラスのみ。<br>
 * @see tools.dbconnector6.service.BackgroundServiceInterface
 */
public class BackgroundService extends Service<Void> {
    // バックグラウンド実行の実装への参照
    private BackgroundServiceInterface backgroundServiceInterface;

    // MainControllerへのアクセス用参照
    private MainControllerInterface mainControllerInterface;

    /**
     * コンストラクタ。<br>
     * @param backgroundServiceInterface 制御対象のバックグラウンドサービス
     * @param mainControllerInterface MainControllerへのアクセス用参照
     */
    public BackgroundService(BackgroundServiceInterface backgroundServiceInterface, MainControllerInterface mainControllerInterface) {
        this.backgroundServiceInterface = backgroundServiceInterface;
        this.mainControllerInterface = mainControllerInterface;
    }

    /**
     * バックグラウンド実行を行うTaskを生成し、その中でコンストラクタで受け取ったバックグラウンド実行の実装を呼び出す。<br>
     * @return 生成したバックグラウンド実行を行うTaskのインスタンス
     */
    @Override
    protected Task createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    backgroundServiceInterface.run(this);
                } catch (Exception e) {
                    mainControllerInterface.writeLog(e);
                }
                return null;
            }
        };
    }

    /**
     * バックグラウンド実行をキャンセルする。<br>
     * @return 取り消しが成功した場合は true、それ以外は false を返す。
     */
    @Override
    public boolean cancel() {
        if (!isRunning()) {
            mainControllerInterface.writeLog(backgroundServiceInterface.getNotRunningMessage());
            return true;
        }

        boolean cancel = super.cancel();
        try {
            backgroundServiceInterface.cancel();
        } catch (Exception e) {
            cancel = false;
            mainControllerInterface.writeLog(e);
        }

        return cancel;
    }

    /**
     * Serviceの状態がCANCELLED状態に遷移するたびに呼び出される、サブクラスのprotectedコンビニエンス・メソッド。<br>
     */
    @Override
    protected void cancelled() {
        backgroundServiceInterface.cancelled();
    }

    /**
     * Serviceの状態がFAILED状態に遷移するたびに呼び出される、サブクラスのprotectedコンビニエンス・メソッド。<br>
     */
    @Override
    protected void failed() {
        backgroundServiceInterface.failed();
    }
}
