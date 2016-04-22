package tools.dbconnector6.controller;

import javafx.fxml.FXMLLoader;
import tools.dbconnector6.MainControllerInterface;

import java.io.IOException;

/**
 * メインコントローラから呼び出されるサブ画面のコントローラ基底クラス
 */
public abstract class SubController {
    // メイン画面へのアクセス用インターフェース
    protected MainControllerInterface mainControllerInterface;

    /**
     * メイン画面へのアクセス用インターフェースを設定する。<br>
     * @param mainControllerInterface メイン画面へのアクセス用インターフェースへの参照
     */
    public void setMainControllerInterface(MainControllerInterface mainControllerInterface) {
        this.mainControllerInterface = mainControllerInterface;
    }

    /**
     * StageとSubController（メイン画面から呼ばれるサブ画面）を作成する。<br>
     * @param name 作成するControllerManagerで管理している対象の名前
     * @param mainControllerInterface メイン画面へのアクセス用インターフェース
     * @return StageとSubControllerのペア
     * @throws IOException FXMLのロードに失敗した場合
     */
    public static StageAndControllerPair createStageAndControllerPair(String name, MainControllerInterface mainControllerInterface) throws IOException {
        FXMLLoader loader = ControllerManager.getControllerManager().getLoarder(name);

        StageAndControllerPair pair = new StageAndControllerPair();
        pair.stage = ControllerManager.getControllerManager().getSubStage(loader, name);
        pair.controller = loader.getController();

        pair.controller.setMainControllerInterface(mainControllerInterface);

        return pair;
    }

    /**
     * StageとSubController（メイン画面から呼ばれるサブ画面）を作成する。<br>
     * Stageはタイトルバーがないものを作成する。<br>
     * @param name 作成するControllerManagerで管理している対象の名前
     * @param mainControllerInterface メイン画面へのアクセス用インターフェース
     * @return StageとSubControllerのペア
     * @throws IOException FXMLのロードに失敗した場合
     */
    public static StageAndControllerPair createTransparentStageAndControllerPair(String name, MainControllerInterface mainControllerInterface) throws IOException {
        FXMLLoader loader = ControllerManager.getControllerManager().getLoarder(name);

        StageAndControllerPair pair = new StageAndControllerPair();
        pair.stage = ControllerManager.getControllerManager().getTransparentSubStage(loader, name);
        pair.controller = loader.getController();

        pair.controller.setMainControllerInterface(mainControllerInterface);

        return pair;
    }
}
