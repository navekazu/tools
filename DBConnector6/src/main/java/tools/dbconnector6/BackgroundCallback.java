package tools.dbconnector6;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.sql.SQLException;

public class BackgroundCallback extends Service {
    private BackgroundCallbackInterface bci;

    public BackgroundCallback(BackgroundCallbackInterface bci) {
        this.bci = bci;
    }

    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
/*
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            bci.run();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
*/
                bci.run();

                return null;

            }
        };
    }
}
