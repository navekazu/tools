package tools.dbconnector6;

import javafx.concurrent.Task;

import java.sql.SQLException;

public interface BackgroundCallbackInterface<P, M> {
    public void run(Task task) throws Exception;
    public void cancel() throws Exception;
    public void updateUIPreparation(P uiParam) throws Exception;
    public void updateUI(M uiParam) throws Exception;
}
