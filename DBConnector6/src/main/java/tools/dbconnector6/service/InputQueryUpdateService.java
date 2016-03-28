package tools.dbconnector6.service;

import javafx.concurrent.Task;
import tools.dbconnector6.BackgroundServiceInterface;
import tools.dbconnector6.MainControllerInterface;

public class InputQueryUpdateService implements BackgroundServiceInterface<Void, Void> {
    private MainControllerInterface mainControllerInterface;
    public InputQueryUpdateService(MainControllerInterface mainControllerInterface) {
        this.mainControllerInterface = mainControllerInterface;
    }

    @Override
    public void run(Task task) throws Exception {

    }

    @Override
    public void cancel() throws Exception {

    }

    @Override
    public void updateUIPreparation(Void uiParam) throws Exception {

    }

    @Override
    public void updateUI(Void uiParam) throws Exception {

    }
}
