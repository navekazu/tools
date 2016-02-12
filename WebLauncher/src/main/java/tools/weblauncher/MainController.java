package tools.weblauncher;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class MainController extends Application implements Initializable {
    public static final Path DEFAULT_LAUNCH_BROWSER_PATH = Paths.get(System.getProperty("user.home"), ".WebLauncherBrowserPath");
    public static final Path DEFAULT_LAUNCH_URL = Paths.get(System.getProperty("user.home"), ".WebLauncherUrl");
    private static final String REPLACE_WORD_REGEX = "\\$\\{searchWord\\}";

    @FXML
    private TextField browserTextField;

    @FXML
    private ChoiceBox urlChoiceBox;

    @FXML
    private TextField searchWordTextField;

    @FXML
    private Label errorLabel;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 700, 200);
        primaryStage.setTitle("Web Launcher");
        primaryStage.setScene(scene);
        primaryStage.show();

        // 初期フォーカスを検索ワード入力欄に（initializeの中ではフォーカス移動できない）
        MainController c = loader.getController();
        c.focusSearchWordTextField();
    }

    public void focusSearchWordTextField() {
        searchWordTextField.requestFocus();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            // browserTextFieldの初期化
            List<String> list = loadTemplate(DEFAULT_LAUNCH_BROWSER_PATH, new String[]{
                    "C:\\Program Files\\Mozilla Firefox\\firefox.exe"});
            browserTextField.setText(list.get(0));

            // TemplateChoiceBoxの初期化
            urlChoiceBox.getItems().addAll(loadTemplate(DEFAULT_LAUNCH_URL, new String[]{
                    "http://eow.alc.co.jp/search?q=${searchWord}",
                    "https://www.google.co.jp/search?q=${searchWord}"}));
            urlChoiceBox.getSelectionModel().select(0);

        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText(e.toString());
        }
    }

    public List<String> loadTemplate(Path path, String[] defaultValues) throws IOException {
        if (!Files.exists(path)) {
            createDefaultTemplate(path, defaultValues);
        }
        return Files.readAllLines(path, Charset.defaultCharset());
    }

    private void createDefaultTemplate(Path path, String[] defaultValues) throws IOException {
        Files.write(path, Arrays.asList(defaultValues), Charset.defaultCharset());
    }

    @FXML
    private void onSearchWordTextFieldAction(ActionEvent event) {
        executeSearch();
    }

    @FXML
    private void onSearch(ActionEvent event) {
        executeSearch();
    }

    private void executeSearch() {
        String url = (String) urlChoiceBox.getSelectionModel().getSelectedItem();
        url = url.replaceAll(REPLACE_WORD_REGEX, searchWordTextField.getText());

        errorLabel.setText("");

        try {
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(String.format("\"%s\" \"%s\"", browserTextField.getText(), url));
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText(e.toString());
        }

        // 入力したSearch wordを全選択に
        searchWordTextField.selectAll();
    }
}
