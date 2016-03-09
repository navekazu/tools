package tools.dbconnector6;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AlertController implements Initializable {

    @FXML
    private Label messageLabel;

    @FXML
    private Label detailsLabel;

    private MainControllerInterface mainControllerInterface;

    public void setMainControllerInterface(MainControllerInterface mainControllerInterface) {
        this.mainControllerInterface = this.mainControllerInterface;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void onOkButton(ActionEvent event) {
        messageLabel.getScene().getWindow().hide();
    }

    public void setContents(String message, String details) {
        messageLabel.setText(message);
        detailsLabel.setText(details);

    }
}
