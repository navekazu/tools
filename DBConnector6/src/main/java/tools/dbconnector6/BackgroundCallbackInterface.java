package tools.dbconnector6;

import java.sql.SQLException;

public interface BackgroundCallbackInterface<P, M> {
    public void run() throws Exception;
    public void updateUIPreparation(P uiParam) throws Exception;
    public void updateUI(M uiParam) throws Exception;
}
