package tools.dbconnector6;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class BackgroundService extends Service<Void> {
    private BackgroundServiceInterface bci;
    private MainControllerInterface mci;

    public BackgroundService(BackgroundServiceInterface bci, MainControllerInterface mci) {
        this.bci = bci;
        this.mci = mci;
    }

    @Override
    protected Task createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    bci.run(this);
                } catch (Exception e) {
                    mci.writeLog(e);
                }
                return null;
            }
        };
    }

    @Override
    public boolean cancel() {
        if (!isRunning()) {
            mci.writeLog(bci.getNotRunningMessage());
            return true;
        }

        boolean cancel = super.cancel();
        try {
            bci.cancel();
        } catch (Exception e) {
            cancel = false;
            mci.writeLog(e);
        }

        return cancel;
    }

    @Override
    protected void cancelled() {
        bci.cancelled();
    }

    @Override
    protected void failed() {
        bci.failed();
    }
}
