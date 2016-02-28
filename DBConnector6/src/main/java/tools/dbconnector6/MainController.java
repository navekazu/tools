package tools.dbconnector6;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tools.dbconnector6.ui.QueryTextAreaController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController extends Application implements Initializable {
    private Stage primaryStage;

    @FXML
    private TextField filterTextField;

    @FXML
    private TextField searchTextField;

    @FXML
    private Button searchButton;

    @FXML
    private TreeView dbStructureTreeView;

    @FXML
    private TabPane tableStructureTabPane;

    @FXML
    private Tab tablePropertyTab;

    @FXML
    private Tab tableColumnTab;

    @FXML
    private Tab tableIndexTab;

    @FXML
    private TextArea queryTextArea;

    @FXML
    private TableView queryResultTableView;

    @FXML
    private TextArea logTextArea;

    private QueryTextAreaController queryTextAreaController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setTitle("DBConnector6");
        primaryStage.setScene(scene);
        primaryStage.show();

        connect();

        // 初期フォーカスを検索ワード入力欄に（initializeの中ではフォーカス移動できない）
        MainController c = loader.getController();
        c.focusQueryTextArea();
    }

    public void focusQueryTextArea() {
        queryTextArea.requestFocus();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        queryTextAreaController = new QueryTextAreaController(this, queryTextArea);
    }

    @FXML
    private void onConnect(ActionEvent event) {
        connect();
    }

    @FXML
    private void onDisconnect(ActionEvent event) {

    }

    @FXML
    private void onCommit(ActionEvent event) {

    }

    @FXML
    private void onRollback(ActionEvent event) {

    }

    @FXML
    private void onCancel(ActionEvent event) {

    }

    private void connect() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Connect.fxml"));
            loader.load();
            Parent root = loader.getRoot();
            Scene scene = new Scene(root);
            Stage confirmDialog = new Stage(StageStyle.UTILITY);
            confirmDialog.setScene(scene);
            confirmDialog.initOwner(primaryStage);
            confirmDialog.initModality(Modality.APPLICATION_MODAL);
            confirmDialog.setTitle("Connect");
            confirmDialog.showAndWait(); // ダイアログが閉じるまでブロックされる

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
