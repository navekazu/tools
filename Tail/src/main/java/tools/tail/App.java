package tools.tail;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import tools.tail.controller.MainController;

public class App extends Application {
    public static void main(String[] args) {
        Application.launch(App.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parameters parameters = getParameters();
        String path = null;

        for (String param: parameters.getRaw()) {
            path = param;
            break;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        primaryStage.setTitle("Tail" + (path==null? "": " - "+path));
        primaryStage.setScene(scene);

        MainController mainController = fxmlLoader.getController();
        mainController.setPath(path);

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                mainController.stop();
            }
        });


        primaryStage.show();
    }
}
