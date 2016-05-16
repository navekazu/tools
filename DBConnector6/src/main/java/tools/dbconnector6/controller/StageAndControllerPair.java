package tools.dbconnector6.controller;

import javafx.stage.Stage;

/**
 * StageとSubController（メイン画面から呼ばれるサブ画面）をまとめたPair
 * @param <C> Controllerクラス
 */
public class StageAndControllerPair<C extends SubController> {
    /**
     * Stageクラス
     */
    public Stage stage;

    /**
     * SubController（メイン画面から呼ばれるサブ画面）
     */
    public C controller;
}
