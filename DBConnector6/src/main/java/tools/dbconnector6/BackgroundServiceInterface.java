package tools.dbconnector6;

import javafx.concurrent.Task;

public interface BackgroundServiceInterface<P, M> {
    public void run(Task task) throws Exception;
    public void cancel() throws Exception;
    public void cancelled();
    public void failed();
    public void updateUIPreparation(final P uiParam) throws Exception;
    public void updateUI(final M uiParam) throws Exception;
    public String getNotRunningMessage();
}
