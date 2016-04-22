package tools.dbconnector6.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;
import tools.dbconnector6.MainControllerInterface;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class EditorChooserController extends SubController implements Initializable {

    // Scene overview
    // +--------------------------------------------+
    // |           editorPathTextField              |
    // |                                            |
    // +--------------------------------------------+
    @FXML private TextField editorPathTextField;

    private boolean ok;

    /**
     * コントローラのルート要素が完全に処理された後に、コントローラを初期化するためにコールされます。<br>
     * @param location ルート・オブジェクトの相対パスの解決に使用される場所、または場所が不明の場合は、null
     * @param resources ート・オブジェクトのローカライズに使用されるリソース、
     *                  またはルート・オブジェクトがローカライズされていない場合は、null。
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setContents(String editorPath) {
        editorPathTextField.setText(editorPath);
        ok = false;
    }

    @FXML
    private void onRef(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select SQL editor");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All files", "*.*"));

        // 入力済みのファイルを基に初期ディレクトリを設定
        if (editorPathTextField.getText().length()>=1) {
            File selectedFile = new File(editorPathTextField.getText());
            if (selectedFile.exists()) {
                fileChooser.setInitialDirectory(selectedFile.getParentFile());
            }
        }

        File selectedFile = fileChooser.showOpenDialog(editorPathTextField.getScene().getWindow());
        if (selectedFile != null) {
            editorPathTextField.setText(selectedFile.getPath());
        }
    }

    @FXML
    private void onOk(ActionEvent e) {
        ok = true;
        editorPathTextField.getScene().getWindow().hide();
    }

    @FXML
    private void onCancel(ActionEvent e) {
        editorPathTextField.getScene().getWindow().hide();
    }

    public boolean isOk() {
        return ok;
    }

    public String getEditorPath() {
        return editorPathTextField.getText();
    }
}
