package tools.pomodorotimer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tools.pomodorotimer.entity.Config;
import tools.pomodorotimer.entity.Mode;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class MainController extends Application implements Initializable {
    @FXML private Label timerLabel;
    @FXML private Label promptLabel;
    @FXML private Label clockLabel;
    private ImageView doingImage;
    private ImageView breakImage;
    private MainController controller;

    private double dragStartX;
    private double dragStartY;

    private Stage primaryStage;
    private Mode mode;

    private Stage pomodoroListStage;
    private PomodoroListController pomodoroListController;

    @Override
    public void start(Stage primaryStage) throws Exception {
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setTitle("Pomodoro timer");
        primaryStage.setScene(scene);
        controller = loader.getController();
        controller.primaryStage = primaryStage;

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

        // 画面更新のサービスを起動
        TimerService timerService = new TimerService();
        timerService.restart();

        primaryStage.setOnShown(event -> loadConfig(primaryStage));
        primaryStage.setOnCloseRequest(event -> writeConfig(primaryStage));

        primaryStage.show();
    }

    private void loadConfig(Stage primaryStage) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Path path = Paths.get(System.getProperty("user.home"), ".PomodoroTimerConfig");
            FileInputStream in = new FileInputStream(path.toFile());
            Config config = mapper.readValue(in, Config.class);

            primaryStage.setX(config.getScreenX());
            primaryStage.setY(config.getScreenY());
            mode = config.getMode();

            if (mode==null) {
                mode = Mode.CLOCK_MODE;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeConfig(Stage primaryStage) {
        Config config = Config.builder()
                .screenX(primaryStage.getX())
                .screenY(primaryStage.getY())
                .mode(mode)
                .workTime(25)
                .shortBreakTime(5)
                .longBreakTime(30)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        try {
            Path path = Paths.get(System.getProperty("user.home"), ".PomodoroTimerConfig");
            FileOutputStream out = new FileOutputStream(path.toFile());
            mapper.writeValue(out, config);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        doingImage = new ImageView();
        breakImage = new ImageView();
        loadAndSetImage(doingImage, "image/tomato-doing.png");
        loadAndSetImage(breakImage, "image/tomato-break.png");

        timerLabel.setGraphic(breakImage);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PomodoroList.fxml"));
            Parent root = loader.load();
            pomodoroListStage = new Stage(StageStyle.UTILITY);
            Scene scene = new Scene(root);
            pomodoroListStage.setTitle("Pomodoro list");
            pomodoroListStage.setScene(scene);
            pomodoroListController = loader.getController();
            pomodoroListStage.initStyle(StageStyle.TRANSPARENT);
            scene.setFill(null);
            // シーンのドラッグ
            scene.setOnMousePressed(e -> {
                dragStartX = e.getSceneX();
                dragStartY = e.getSceneY();
            });
            scene.setOnMouseDragged(e -> {
                pomodoroListStage.setX(e.getScreenX() - dragStartX);
                pomodoroListStage.setY(e.getScreenY() - dragStartY);
            });

//            pomodoroListStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        writeConfig(primaryStage);
        Platform.exit();
    }

    private void updatePomodoro(int hour, int minute, int second) {
        final int halfMinute = minute % 30;
        final String promptText = String.format("%02d:%02d", halfMinute, second);
        final String clockText = String.format("%02d:%02d:%02d", hour, minute, second);

        Platform.runLater(() -> {
            controller.promptLabel.setText(promptText);
            controller.clockLabel.setText(clockText);
            controller.timerLabel.setGraphic(isDoingTime(halfMinute)? controller.doingImage: controller.breakImage);
        });
    }
    protected boolean isDoingTime(int minute) {
        return minute<25;
    }

    private class TimerService extends Service<Void> {
        @Override
        protected Task<Void> createTask() {
            return new TimerTask();
        }
    }

    private class TimerTask extends Task<Void> {
        @Override
        protected Void call() throws Exception {
            while (true) {
                if (isCancelled()) {
                    break;
                }

                LocalTime localTime = LocalTime.now();
                int hour = localTime.getHour();
                int minute = localTime.getMinute();
                int second = localTime.getSecond();
                updatePomodoro(hour, minute, second);

                try {
                    Thread.sleep(10);
                } catch (InterruptedException interrupted) {
                    break;
                }
            }
            return null;
        }
    }
}
