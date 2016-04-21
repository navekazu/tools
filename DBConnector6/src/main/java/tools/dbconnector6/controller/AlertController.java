package tools.dbconnector6.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import tools.dbconnector6.MainControllerInterface;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * アラート画面コントローラ
 */
public class AlertController extends SubController implements Initializable {

    // Scene overview
    // +--------------------------------------------+
    // |           messageLabel                     |
    // |           detailsLabel                     |
    // |                                 [okButton] |
    // +--------------------------------------------+
    @FXML private Label messageLabel;
    @FXML private Label detailsLabel;
    @FXML private Button okButton;

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

    public void setWaitMode(boolean waitMode) {
        okButton.setVisible(!waitMode);
    }
}
