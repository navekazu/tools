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
import java.util.Set;
import java.util.stream.Collectors;

public class ReservedWordController implements Initializable {

    @FXML private ListView reservedWordListView;

    private MainControllerInterface mainControllerInterface;

    // 予約語の一覧（SQLの予約語・全テーブル名・全カラム名が入る）
    private Set<ReservedWord> reservedWordList;

    public void setMainControllerInterface(MainControllerInterface mainControllerInterface) {
        this.mainControllerInterface = mainControllerInterface;
    }

    public void setRservedWordList(Set<ReservedWord> reservedWordList) {
        this.reservedWordList = reservedWordList;
    }

    /**
     * 予約語を入力されたか、予約語一覧と突き合わせる
     * @param event キーイベントの内容
     * @param query キャレット位置にある入力済みSQL
     * @return 予約語一覧に入力済みSQLがあれば true、なければ false
     */
    public boolean isInputReservedWord(KeyEvent event, String query) {
        if (query.length()<=1) {
            return false;
        }

        final boolean upperCase = isUpperCase(query);

        List<ReservedWord> list;
        synchronized (reservedWordList) {
            list = reservedWordList.stream()
                    .filter(word -> word.getWord().toLowerCase().startsWith(query.toLowerCase()))
                    .map(word -> {
                        word.setWord(upperCase ? word.getWord().toUpperCase() : word.getWord().toLowerCase());
                        return word;
                    })
                    .collect(Collectors.toList());
        }

        if (list.isEmpty()) {
            return false;
        }

        ObservableList<ReservedWord> items = reservedWordListView.getItems();
        items.clear();
        items.addAll(list);
        reservedWordListView.getSelectionModel().select(0);
        return true;
    }

    /**
     * 最後に入力した文字が大文字か判定する
     * @param query 入力済みSQL
     * @return 大文字の場合 true、それ以外は false
     */
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

    /**
     * 入力文字が文字なのか判定
     * @param c 入力文字
     * @return 文字の場合 true、それ以外は false
     */
    private static final char[] ALPHABETS_ALL = new char[]{
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
    };
    private boolean isCharacter(char c) {
        for (char alphabet: ALPHABETS_ALL) {
            if (c==alphabet) {
                return true;
            }
        }
        return false;
    }

    /**
     * 入力文字が大文字なのか判定
     * @param c 入力文字
     * @return 大文字の場合 true、それ以外は false
     */
    private static final char[] ALPHABETS_UPPERCASE = new char[]{
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
    };
    private boolean isUpperCharacter(char c) {
        for (char alphabet: ALPHABETS_UPPERCASE) {
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
            mainControllerInterface.hideReservedWordStage();
            selected();
        }
    }

    /**
     * 選択した予約語をメイン画面に通知する
     */
    private void selected() {
        int index = reservedWordListView.getSelectionModel().getSelectedIndex();
        if (index==-1) {
            return;
        }

        ObservableList<ReservedWord> items = reservedWordListView.getItems();
        mainControllerInterface.selectReservedWord(items.get(index).getWord());
    }

}

