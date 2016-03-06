package tools.dbconnector6;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import tools.dbconnector6.entity.ReservedWord;

import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static javafx.scene.input.KeyCode.*;

public class ReservedWordController implements Initializable {

    @FXML
    private ListView reservedWordListView;

    private MainControllerInterface mainControllerInterface;
    private List<ReservedWord> reservedWordList;

    public void setMainControllerInterface(MainControllerInterface mainControllerInterface) {
        this.mainControllerInterface = mainControllerInterface;
    }

    public void setRservedWordList(List<ReservedWord> reservedWordList) {
        this.reservedWordList = reservedWordList;
    }

    public boolean notifyKeyPressed(KeyEvent event) {
        switch (event.getCode()) {
            case ESCAPE:
                return true;
        }
        return false;
    }

    public boolean notifyQueryInput(KeyEvent event, String query) {
        List<ReservedWord> list = reservedWordList.stream()
                .filter(word -> word.getWord().toLowerCase().startsWith(query.toLowerCase()))
                .collect(Collectors.toList());

        if (list.isEmpty()) {
            return false;
        }

        ObservableList<ReservedWord> items = reservedWordListView.getItems();
        items.clear();
        items.addAll(list);
        reservedWordListView.getSelectionModel().select(0);
        return true;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void onKeyPressed(KeyEvent event){
    }

    @FXML
    public void onKeyReleased(KeyEvent event){
    }

    @FXML
    public void onKeyTyped(KeyEvent event){
    }

    @FXML
    public void onMouseClicked(MouseEvent event){
    }

}
