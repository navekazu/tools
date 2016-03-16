package tools.dbconnector6.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * コントローラ管理
 */
public class ControllerManager {

    /**
     * コントローラ管理のシングルトンオブジェクト
     */
    private static final ControllerManager singleton = new ControllerManager();

    /**
     * ステージ定義のマップ
     */
    private static final Map<String, StageDefine> stageDefinedMap = new HashMap<>();
    static {
        stageDefinedMap.put("main", StageDefine.builder().fxml("/fxml/Main.fxml").title("DBConnector6").build());
        stageDefinedMap.put("connect", StageDefine.builder().fxml("/fxml/Connect.fxml").title("Connect").build());
        stageDefinedMap.put("reservedWord", StageDefine.builder().fxml("/fxml/ReservedWord.fxml").title("ReservedWord").build());
        stageDefinedMap.put("alertDialog", StageDefine.builder().fxml("/fxml/Alert.fxml").title("Message").build());
    }

    /**
     * コントローラ管理のシングルトンオブジェクトを取得する
     * @return コントローラ管理のシングルトンオブジェクト
     */
    public static ControllerManager getControllerManager() {
        return singleton;
    }

    /**
     * アプリケーション起動時のステージ
     */
    private Stage primaryStage;

    /**
     * メイン画面のステージを取得する
     * @param loader FXMLを指定したFXMLLoader
     * @param primaryStage アプリケーション起動時のステージ
     * @return メイン画面のFXMLを読み込んだアプリケーション起動時のステージ
     * @throws IOException FXML読み込みに失敗した場合
     */
    public Stage getMainStage(FXMLLoader loader, Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        StageDefine stageDefine = getStageDefine("main");

        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setTitle(stageDefine.getTitle());
        primaryStage.setScene(scene);

        return primaryStage;
    }


    /**
     * アプリケーション起動時のステージを親に持つ子のステージを取得する
     * @param loader FXMLを指定したFXMLLoader
     * @param name ステージ定義のマップ名
     * @return 子のステージ
     * @throws IOException FXML読み込みに失敗した場合
     */
    public Stage getSubStage(FXMLLoader loader, String name) throws IOException {
        StageDefine stageDefine = getStageDefine(name);

        loader.load();
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        Stage subStage = new Stage(StageStyle.UTILITY);
        subStage.setScene(scene);
        subStage.initOwner(primaryStage);
        subStage.initModality(Modality.APPLICATION_MODAL);
        subStage.setTitle(stageDefine.getTitle());

        return subStage;
    }

    /**
     * アプリケーション起動時のステージを親に持つ透明な子のステージを取得する
     * @param loader FXMLを指定したFXMLLoader
     * @param name ステージ定義のマップ名
     * @return 子のステージ
     * @throws IOException FXML読み込みに失敗した場合
     */
    public Stage getTransparentSubStage(FXMLLoader loader, String name) throws IOException {
        StageDefine stageDefine = getStageDefine(name);

        loader.load();
        Parent root = loader.getRoot();
        Scene scene = new Scene(root, Color.TRANSPARENT);
        Stage subStage = new Stage(StageStyle.UTILITY);
        subStage.initStyle(StageStyle.TRANSPARENT);
        subStage.setScene(scene);
        subStage.initOwner(primaryStage);
        subStage.initModality(Modality.NONE);

        return subStage;
    }

    /**
     * ステージ定義名からFXMLLoaderを取得する
     * @param name ステージ定義名
     * @return FXMLLoader
     */
    public FXMLLoader getLoarder(String name) {
        return new FXMLLoader(getClass().getResource(getStageDefine(name).getFxml()));
    }

    /**
     * ステージ定義
     */
    @AllArgsConstructor
    @Data
    @NoArgsConstructor
    @Builder
    private static class StageDefine {
        /**
         * FXMLファイル名
         */
        private String fxml;

        /**
         * ステージのタイトル
         */
        private String title;
    }

    /**
     * ステージ定義を取得する
     * @param name ステージ定義名
     * @return ステージ定義
     */
    private StageDefine getStageDefine(String name) {
        if (!stageDefinedMap.containsKey(name)) {
            throw new IllegalArgumentException(String.format("'%s' is not found in stageDefinedMap.", name));
        }
        return stageDefinedMap.get(name);
    }
}
