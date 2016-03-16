package tools.dbconnector6.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import tools.dbconnector6.MainControllerInterface;
import tools.dbconnector6.entity.ReservedWord;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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
        if (query.length()<=1) {
            return false;
        }

        final boolean upperCase = isUpperCase(query);

        List<ReservedWord> list;
        list = reservedWordList.stream()
                .filter(word -> word.getWord().toLowerCase().startsWith(query.toLowerCase()))
                .map(word -> {
                    word.setWord(upperCase ? word.getWord().toUpperCase() : word.getWord().toLowerCase());
                    return word;
                })
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

    private boolean isUpperCase(String query) {
        StringBuilder queryBuffer = (new StringBuilder(query)).reverse();

        for (int loop=0; loop<queryBuffer.length(); loop++) {
            if (!isCharacter(queryBuffer.charAt(loop))) {
                continue;
            }
            if (isUpperCharacter(queryBuffer.charAt(loop))) {
                return true;
            }
            return false;
        }
        return false;
    }

    private boolean isCharacter(char c) {
        char[] alphabets = new char[]{
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        };

        for (char alphabet: alphabets) {
            if (c==alphabet) {
                return true;
            }
        }
        return false;
    }

    private boolean isUpperCharacter(char c) {
        char[] alphabets = new char[]{
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        };

        for (char alphabet: alphabets) {
            if (c==alphabet) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void onKeyPressed(KeyEvent event){
        switch (event.getCode()) {
            case ENTER:
                mainControllerInterface.hideReservedWordStage();
                selected();
                break;

            case TAB:
                mainControllerInterface.mainControllerRequestFocus();
                break;

            case ESCAPE:
                mainControllerInterface.hideReservedWordStage();
                mainControllerInterface.mainControllerRequestFocus();
                break;
        }
    }

    @FXML
    public void onKeyReleased(KeyEvent event){
    }

    @FXML
    public void onKeyTyped(KeyEvent event){
    }

    @FXML
    public void onMouseClicked(MouseEvent event){
        if (event.getClickCount()>=2 && event.getButton()== MouseButton.PRIMARY) {
            selected();
        }
    }

    private void selected() {
        int index = reservedWordListView.getSelectionModel().getSelectedIndex();
        if (index==-1) {
            return;
        }

        ObservableList<ReservedWord> items = reservedWordListView.getItems();
        mainControllerInterface.selectReservedWord(items.get(index).getWord());
    }

}

