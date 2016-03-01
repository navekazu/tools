package tools.dbconnector6.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

public class ControllerManager {
    private static final ControllerManager singleton = new ControllerManager();
    private static final Map<String, StageDefine> stageDefinedMap = new HashMap<>();
    static {
        stageDefinedMap.put("main", StageDefine.builder().fxml("/fxml/Main.fxml").title("DBConnector6").build());
        stageDefinedMap.put("connect", StageDefine.builder().fxml("/fxml/Connect.fxml").title("Connect").build());
    }
    public static ControllerManager getControllerManager() {
        return singleton;
    }


    private Stage primaryStage;

    public Stage getMainStage(Stage primaryStage) throws IOException {
        StageDefine stageDefine = getStageDefine("main");

        this.primaryStage = primaryStage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource(stageDefine.getFxml()));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setTitle(stageDefine.getTitle());
        primaryStage.setScene(scene);

        return primaryStage;
    }

    public Stage getSubStage(String name) throws IOException {
        StageDefine stageDefine = getStageDefine(name);

        FXMLLoader loader = new FXMLLoader(getClass().getResource(stageDefine.getFxml()));
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    private class StageDefine {
        private String fxml;
        private String title;
    }

    private StageDefine getStageDefine(String name) {
        if (!stageDefinedMap.containsKey(name)) {
            throw new IllegalArgumentException(String.format("'%s' is not found in stageDefinedMap.", name));
        }
        return stageDefinedMap.get(name);
    }
}
