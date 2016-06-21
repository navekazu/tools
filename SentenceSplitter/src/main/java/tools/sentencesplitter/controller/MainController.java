package tools.sentencesplitter.controller;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MainController extends Application implements Initializable {

    @FXML TextField splitCharacterField;
    @FXML ChoiceBox intervalLineBox;
    @FXML Button splitButton;
    @FXML TextArea sourceArea;
    @FXML TextArea resultArea;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setTitle("Sentence splitter");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<String> intervalLineList = intervalLineBox.getItems();
        Stream.of("0", "1", "2", "3", "4", "5").forEach(intervalLineList::add);
        intervalLineBox.getSelectionModel().select(4);

        sourceArea.setWrapText(true);
        resultArea.setWrapText(true);
    }

    @FXML
    private void onSplit(ActionEvent event) {

        // splitCharacterFieldでsplitすると、splitCharacterFieldが消失するのでsplitCharacterFieldがあったら改行コードを入れた文字列を作成する
        StringBuilder builder = new StringBuilder();
        Stream.of(sourceArea.getText().split(""))
                .forEach(c -> {
                    builder.append(c);
                    if (splitCharacterField.getText().contains(c)) {
                        builder.append("\n");
                    }
                });

        // 改行後のスペースが邪魔なので、改行でsplitした後、rrimして再度改行を付ける
        StringBuilder result = new StringBuilder();
        Stream.of(builder.toString().split("\n"))
                .forEach(l -> {
                    result.append(l.trim());
                    result.append("\n");
                    IntStream.range(0, intervalLineBox.getSelectionModel().getSelectedIndex())
                            .forEach(i -> result.append("\n"));
                });

        resultArea.setText(result.toString());
    }
}
