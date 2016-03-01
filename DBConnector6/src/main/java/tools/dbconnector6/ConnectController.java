package tools.dbconnector6;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ConnectController implements Initializable {

    @FXML
    private TableView connectTableView;

    @FXML
    private Button addButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button loadButton;

    @FXML
    private ComboBox historyComboBox;

    @FXML
    private TextField libraryPathTextField;

    @FXML
    private TextField driverTextField;

    @FXML
    private TextField urlTextField;

    @FXML
    private TextField userTextField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button testButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
