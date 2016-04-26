package tools.pomodorotimer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import java.net.URL;
import java.util.*;

public class App extends Application implements Initializable {
    @FXML Label timerLabel;

    @Override
    public void start(Stage primaryStage) throws Exception {
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setTitle("Pomodoro timer");
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        timerLabel.setText("bbbb");
    }

    public static void main( String[] args ) {
        Application.launch(App.class, args);
    }
}
