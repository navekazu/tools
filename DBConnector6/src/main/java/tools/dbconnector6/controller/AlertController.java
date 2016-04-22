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
 * アラート画面コントローラ。<br>
 * ウェイトモードを設定することで、メッセージを通知するだけではなく、アプリを待機モードにする。<br>
 */
public class AlertController extends SubController implements Initializable {

    // Scene overview
    // +--------------------------------------------+
    // |           messageLabel                     |
    // |           detailsLabel                     |
    // |                                 [okButton] |
    // +--------------------------------------------+
    @FXML private Label messageLabel;   // メッセージのタイトル
    @FXML private Label detailsLabel;   // メッセージの詳細
    @FXML private Button okButton;      // OKボタン

    /**
     * コントローラのルート要素が完全に処理された後に、コントローラを初期化するためにコールされます。<br>
     * @param location ルート・オブジェクトの相対パスの解決に使用される場所、または場所が不明の場合は、null
     * @param resources ート・オブジェクトのローカライズに使用されるリソース、
     *                  またはルート・オブジェクトがローカライズされていない場合は、null。
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    // OKボタンのイベントハンドラ。
    // 自画面を閉じる。
    @FXML
    private void onOkButton(ActionEvent event) {
        messageLabel.getScene().getWindow().hide();
    }

    /**
     * 表示メッセージの初期化。<br>
     * @param message メッセージのタイトル
     * @param details メッセージの詳細
     */
    public void setContents(String message, String details) {
        messageLabel.setText(message);
        detailsLabel.setText(details);
    }

    /**
     * ウェイトモードを設定する。<br>
     * true の場合メッセージ表示中は操作できないようOKボタンの表示は行わないため、
     * 呼び出し元でStageに対してhide()メソッドで非表示にする必要がある。
     * @param waitMode ウェイトモード。trueの場合画面を閉じるOKボタンの表示を行わない。
     */
    public void setWaitMode(boolean waitMode) {
        okButton.setVisible(!waitMode);
        // TODO: ウィンドウのクローズボタンも制御する必要あり
    }
}
