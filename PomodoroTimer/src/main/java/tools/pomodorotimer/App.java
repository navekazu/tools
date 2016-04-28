package tools.pomodorotimer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import java.net.URL;
import java.time.LocalTime;
import java.util.*;

public class App extends Application implements Initializable {
    @FXML private Label timerLabel;
    @FXML private Label promptLabel;
    private ImageView doingImage;
    private ImageView breakImage;
    private App controller;

    private double dragStartX;
    private double dragStartY;

    @Override
    public void start(Stage primaryStage) throws Exception {
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setTitle("Pomodoro timer");
        primaryStage.setScene(scene);
        controller = loader.getController();


        // 透明にする
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(null);

        // 常に前画面
        primaryStage.setAlwaysOnTop(true);


        // シーンのドラッグ
        scene.setOnMousePressed(e -> {
            dragStartX = e.getSceneX();
            dragStartY = e.getSceneY();
        });
        scene.setOnMouseDragged(e -> {
            primaryStage.setX(e.getScreenX() - dragStartX);
            primaryStage.setY(e.getScreenY() - dragStartY);
        });

        TimerService timerService = new TimerService();
        timerService.restart();

        primaryStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        doingImage = new ImageView();
        breakImage = new ImageView();
        loadAndSetImage(doingImage, "image/tomato-doing.png");
        loadAndSetImage(breakImage, "image/tomato-break.png");

        timerLabel.setGraphic(breakImage);
//        breakImage.addEventHandler();
    }
    private void loadAndSetImage(ImageView imageView, String resource) {
        Image image = new Image(resource);
        imageView.setImage(image);
    }

    @FXML
    private void onStartEnd(ActionEvent event) {
    }

    @FXML
    private void onConfig(ActionEvent event) {
    }

    @FXML
    private void onExit(ActionEvent event) {
        Platform.exit();
    }

    private void setPromptLabel(final String text) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                controller.promptLabel.setText(text);
                controller.timerLabel.setGraphic(controller.breakImage);
            }
        });
    }

    private void updatePomodoro(int minute, int second) {
        final int finalMinute = minute % 30;
        String text = String.format("%02d:%02d", finalMinute, second);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                controller.promptLabel.setText(text);
                controller.timerLabel.setGraphic(isDoingTime(finalMinute)? controller.doingImage: controller.breakImage);
            }
        });
    }
    protected boolean isDoingTime(int minute) {
        return minute<25;
    }

    private class TimerService extends Service<Void> {
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    while (true) {
                        if (isCancelled()) {
                            break;
                        }

                        LocalTime localTime = LocalTime.now();
                        int minute = localTime.getMinute();
                        int second = localTime.getSecond();
                        updatePomodoro(minute, second);


                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException interrupted) {
                            if (isCancelled()) {
                                break;
                            }
                        }
                    }
                    return null;
                }
            };
        }
    }

    public static void main( String[] args ) {
        Application.launch(App.class, args);
    }
}
