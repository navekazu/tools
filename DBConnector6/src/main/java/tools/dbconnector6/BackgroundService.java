package tools.dbconnector6;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class BackgroundService extends Service {
    private BackgroundServiceInterface bci;

    public BackgroundService(BackgroundServiceInterface bci) {
        this.bci = bci;
    }

    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                bci.run(this);
                return null;

            }
        };
    }

    @Override
    public boolean cancel() {
        if (!isRunning()) {
            return true;
        }

        boolean cancel = super.cancel();
        try {
            bci.cancel();
        } catch (Exception e) {
            cancel = false;
            e.printStackTrace();
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
