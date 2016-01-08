package tools.passwordmanager;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sun.plugin2.jvm.RemoteJVMLauncher;
import tools.passwordmanager.model.Pair;
import tools.passwordmanager.model.TreeData;

import javax.xml.bind.JAXB;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;

public class MainController extends Application implements Initializable {
    // デフォルトデータファイル（ ~/.PasswordManager/data ）
    private static final Path DEFAULT_DATA_FILE = Paths.get(System.getProperty("user.home"), ".PasswordManager", "data");

    @FXML
    private TreeView mainTreeView;
    private PmTreeItem rootItem;

    @FXML
    private TableView mainTableView;
    @FXML
    private TableColumn colName;
    @FXML
    private TableColumn colValue;

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
        Parent root = loader.load();
        MainController c = loader.getController();
        primaryStage.setOnCloseRequest(t -> abortAction(t, c));
        Scene scene = new Scene(root, 800, 400);
        primaryStage.setTitle("Password manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
    }

    private void abortAction(WindowEvent t, MainController c2) {
        if (!existsDefaultDataFile()) {
            // 初期ファイルがなければ作成する
            createEmptyDefaultDataFile();
        }
        try {
            TreeData rootData = (TreeData)c2.mainTreeView.getRoot().getValue();
            Pair pair = new Pair("aaa", "bbb");
            rootData.getPairs().add(pair);

            try (FileOutputStream out = new FileOutputStream(DEFAULT_DATA_FILE.toFile())) {
                JAXB.marshal(rootData, out);
            }
        } catch (IOException e) {
            alert(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadData();
        mainTreeView.setRoot(rootItem);
        mainTreeView.getSelectionModel().selectedItemProperty().addListener(new MainTreeViewChangeListener());
        mainTreeView.getSelectionModel().selectFirst();

        // コントローラーのフィールドとデータクラスのプロパティーを紐付ける
        colName.setCellValueFactory(new PropertyValueFactory<Pair, String>("key"));
        colName.setCellFactory(TextFieldTableCell.<Pair>forTableColumn());

        colValue.setCellValueFactory(new PropertyValueFactory<Pair, String>("value"));
        colValue.setCellFactory(TextFieldTableCell.<Pair>forTableColumn());
    }

    private void loadData() {
        TreeData rootData = null;

        if (!existsDefaultDataFile()) {
            // 初期ファイルがなければ作成する
            rootData = createEmptyDefaultDataFile();

        } else {
            // 初期ファイルがあれば読み込む
            try (FileInputStream in = new FileInputStream(DEFAULT_DATA_FILE.toFile())) {
                rootData = JAXB.unmarshal(in, TreeData.class);
            } catch (IOException e) {
                alert(e);
            }
        }

        rootItem = new PmTreeItem(rootData);
    }

    private boolean existsDefaultDataFile() {
        return Files.exists(DEFAULT_DATA_FILE);
    }

    private TreeData createEmptyDefaultDataFile() {
        try {
            Files.createDirectories(DEFAULT_DATA_FILE.getParent());
//            Files.createFile(DEFAULT_DATA_FILE);
        } catch (IOException e) {
            alert(e);
        }
        TreeData rootData = new TreeData();
        rootData.setName("Root");

        TreeData childData = new TreeData();
        childData.setName("Child");
        childData.getPairs().add(new Pair("child1", "child1"));
        rootData.addChild(childData);

        return rootData;
    }

    @FXML
    public void handleMainTableViewMouseClicked(MouseEvent event) {
        // 左ダブルクリック以外は終了
        if (!(event.getButton()==MouseButton.PRIMARY && event.getClickCount()==2)) {
            return ;
        }

        // 空のデータを追加して、"Name"を編集状態にする
        ObservableList<Pair> list = mainTableView.getItems();
        Pair pair = new Pair("", "");
        list.add(pair);
        mainTableView.edit(list.size()-1, colName);
    }

    private class MainTreeViewChangeListener implements ChangeListener {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            TreeData data = (TreeData)((PmTreeItem)newValue).getValue();

            ObservableList<Pair> list = mainTableView.getItems();
            list.clear();

            for (Pair pair: data.getPairs()) {
                list.add(pair);
            }
        }
    }

    @FXML
    public void handleTableViewNameCommeted(TableColumn.CellEditEvent<Pair, String> event) {
        event.getRowValue().setKey(event.getNewValue());
        removeEmptyRow(true);
    }

    @FXML
    public void handleTableViewValueCommeted(TableColumn.CellEditEvent<Pair, String> event) {
        event.getRowValue().setValue(event.getNewValue());
        removeEmptyRow(true);
    }

    @FXML
    public void handleTableViewCancel(TableColumn.CellEditEvent<Pair, String> event) {
        removeEmptyRow(false);
    }

    private void removeEmptyRow(boolean needConfirm) {
        ObservableList<Pair> list = mainTableView.getItems();
        for (Pair pair: list) {
            if (pair.isBlank()) {
                if (needConfirm) {
                    // 確認メッセージ・・・ない
                }
                list.remove(pair);
                break;
            }
        }
    }

    private void flushTableData() {

    }

    private class PmTreeItem extends TreeItem<TreeData> {
        public PmTreeItem(TreeData value) {
            super(value);
            setExpanded(true);
            expandChild(value);
        }
        public void expandChild(TreeData value) {
            for (TreeData child: value.getChildList()) {
                ObservableList list = getChildren();
                list.add(new PmTreeItem(child));
            }
        }
    }

    private void alert(Exception e) {
        e.printStackTrace();
    }
}

