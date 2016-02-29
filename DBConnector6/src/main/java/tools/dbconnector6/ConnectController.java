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

public class ConnectController extends Application implements Initializable {

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
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setTitle("Connect");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Stage stage = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/Connect.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(new Scene(root));
        stage.setTitle("My modal window");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(null);
        stage.show();
    }
}
