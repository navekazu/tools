package tools.dbconnector6;

import javafx.concurrent.Task;

public interface BackgroundServiceInterface<P, M> {
    public void run(Task task) throws Exception;
    public void cancel() throws Exception;
    public void cancelled();
    public void failed();
    public void updateUIPreparation(P uiParam) throws Exception;
    public void updateUI(M uiParam) throws Exception;
}
