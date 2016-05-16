package tools.dbconnector6.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * エディタ（現在はSQLエディタしか用途はない）選択画面用コントローラ。<br>
 */
public class EditorChooserController extends SubController implements Initializable {

    // Scene overview
    // +--------------------------------------------+
    // |           editorPathTextField        [...] |
    // |                               [OK][Cancel] |
    // +--------------------------------------------+
    @FXML private TextField editorPathTextField;        // エディタへのパス

    // OKボタンを押下して画面を閉じたか？
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

    /**
     * エディタへのパスを設定する。<br>
     * @param editorPath エディタへのパス
     */
    public void setEditorPath(String editorPath) {
        editorPathTextField.setText(editorPath);
        ok = false;
    }

    /**
     * エディタへのパスを取得する。<br>
     * @return エディタへのパス
     */
    public String getEditorPath() {
        return editorPathTextField.getText();
    }

    /**
     * 画面を閉じる時、OKボタンで確定をして閉じたかを返す。<br>
     * @return OKボタンを押下して画面を閉じた場合は true、それ以外は false
     */
    public boolean isOk() {
        return ok;
    }

    /***************************************************************************
     *                                                                         *
     * Event handler                                                           *
     *                                                                         *
     **************************************************************************/

    // ...ボタンのアクションイベントハンドラ
    // ファイル選択ダイアログを表示して、エディタの指定を行う。
    // 既にエディタを選択していた場合、そのエディタがあるフォルダを基にファイル選択ダイアログを表示する
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

    // OKボタンのアクションイベントハンドラ
    // okフラグをtrueで更新して自画面を閉じる。
    @FXML
    private void onOk(ActionEvent e) {
        ok = true;
        editorPathTextField.getScene().getWindow().hide();
    }

    // Cancelボタンのアクションイベントハンドラ
    // okフラグは更新せず自画面を閉じる。
    @FXML
    private void onCancel(ActionEvent e) {
        editorPathTextField.getScene().getWindow().hide();
    }

}
