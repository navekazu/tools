package tools.passwordmanager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
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
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
import java.util.ArrayList;
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
            TreeData rootData = convertTreeData((PmTreeItem)c2.mainTreeView.getRoot());

            try (FileOutputStream out = new FileOutputStream(DEFAULT_DATA_FILE.toFile())) {
                JAXB.marshal(rootData, out);
            }
        } catch (IOException e) {
            alert(e);
        }
    }

    private TreeData convertTreeData(PmTreeItem item) {
        TreeData treeData = item.getValue();
        List<TreeData> childList = new ArrayList<>();
        ObservableList<TreeItem<TreeData>> list = item.getChildren();
        list.stream().forEach(value -> childList.add(convertTreeData((PmTreeItem)value)));
        treeData.setChildList(childList);
        return treeData;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadData();
        mainTreeView.setRoot(rootItem);
        mainTreeView.getSelectionModel().selectedItemProperty().addListener(new MainTreeViewChangeListener());
        mainTreeView.getSelectionModel().selectFirst();
        mainTreeView.setCellFactory(p -> new TextFieldTreeCellImpl());

        // コントローラーのフィールドとデータクラスのプロパティーを紐付ける
        colName.setCellValueFactory(new PropertyValueFactory<Pair, String>("key"));
        colName.setCellFactory(TextFieldTableCell.<Pair>forTableColumn());

        colValue.setCellValueFactory(new PropertyValueFactory<Pair, String>("value"));
        colValue.setCellFactory(TextFieldTableCell.<Pair>forTableColumn());

        /*
        mainTableView.addEventFilter(TableColumn.CellEditEvent.ANY,
                new EventHandler<TableColumn.CellEditEvent>(){
                    @Override
                    public void handle(TableColumn.CellEditEvent event) {

                    }
                });
        */
        /*
        mainTableView.addEventFilter(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {

                    }
                });
        */
        /*
        mainTableView.addEventFilter(TableColumn.CellEditEvent.ANY,
                nec EventHandler<TableColumn.CellEditEvent>(){

                });
        */
        /*
        mainTableView.addEventFilter(TableColumn.CellEditEvent.ANY,
                new EventHandler<Event>() {
                    @Override
                    public void handle(Event event) {
                        System.out.println("handle:"+event.toString());

                    }
                });
        */
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

        return createDefaultTreeData("Root");
    }
    private TreeData createDefaultTreeData() {
        return createDefaultTreeData("Group");
    }
    private TreeData createDefaultTreeData(String treeTitle) {
        TreeData treeData = new TreeData();
        treeData.setName(treeTitle);
        treeData.addPairs(new Pair("ID", "", false, false));
        treeData.addPairs(new Pair("Password", "", false, false));
        treeData.addPairs(new Pair("Site", "", false, false));
        return treeData;
    }

    @FXML
    public void handleMainTableViewMouseClicked(MouseEvent event) {
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
    public void handleTableViewNameStart(TableColumn.CellEditEvent<Pair, String> event) {
        if (!event.getRowValue().isKeyEditable()) {
            // 効かない・・・
            event.consume();
        }
    }

    @FXML
    public void handleTableViewNameCommited(TableColumn.CellEditEvent<Pair, String> event) {
        event.getRowValue().setKey(event.getNewValue());
        removeEmptyRow(true);
        flushTableData();
    }

    @FXML
    public void handleTableViewValueCommited(TableColumn.CellEditEvent<Pair, String> event) {
        event.getRowValue().setValue(event.getNewValue());
        removeEmptyRow(true);
        flushTableData();
    }

    @FXML
    public void handleTableViewCancel(TableColumn.CellEditEvent<Pair, String> event) {
        removeEmptyRow(false);
        flushTableData();
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

    @FXML
    private void handleMenuClose(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void handleMenuGroupNew(ActionEvent event) {
        TreeData treeData = createDefaultTreeData();
        PmTreeItem newTreeItem = new PmTreeItem(treeData);
        PmTreeItem treeItem = (PmTreeItem)mainTreeView.getSelectionModel().getSelectedItem();
        treeItem.addChildren(newTreeItem);
    }

    @FXML
    private void handleMenuGroupEdit(ActionEvent event) {
        mainTreeView.edit((PmTreeItem)mainTreeView.getFocusModel().getFocusedItem());
    }

    @FXML
    private void handleMenuGroupDelete(ActionEvent event) {
        PmTreeItem item = (PmTreeItem)mainTreeView.getFocusModel().getFocusedItem();
        removeTreeItem(mainTreeView.getRoot(), item);
    }
    private void removeTreeItem(TreeItem parentItem, TreeItem item) {
        ObservableList<TreeItem> childrens = parentItem.getChildren();
        for (TreeItem child: childrens) {
            if (child==item) {
                childrens.remove(item);
                break;
            }
            removeTreeItem(child, item);
        }
    }

    @FXML
    private void handleMenuContentsNew(ActionEvent event) {
        // 空のデータを追加して、"Name"を編集状態にする
        ObservableList<Pair> list = mainTableView.getItems();
        Pair pair = new Pair("", "", true, true);
        list.add(pair);
        mainTableView.edit(list.size()-1, colName);
    }

    @FXML
    private void handleMenuContentsEdit(ActionEvent event) {
        Pair pair = (Pair)mainTableView.getSelectionModel().getSelectedItem();
        TableColumn col = colName;

        if (!pair.isKeyEditable()) {
            col = colValue;
        }

        mainTableView.edit(mainTableView.getFocusModel().getFocusedCell().getRow(), col);
    }

    @FXML
    private void handleMenuContentsDelete(ActionEvent event) {
        ObservableList<Pair> list = mainTableView.getItems();
        list.remove(mainTableView.getFocusModel().getFocusedCell().getRow());
    }

    private void flushTableData() {
        TreeData treeData = (TreeData)((TreeItem)mainTreeView.getSelectionModel().getSelectedItem()).getValue();
        List<Pair> flushList = new ArrayList<>();
        ObservableList<Pair> list = mainTableView.getItems();

        list.forEach(t -> flushList.add(t));

        treeData.setPairs(flushList);
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

        public void addChildren(PmTreeItem newTreeItem) {
            ObservableList list = getChildren();
            list.add(newTreeItem);
        }
    }
    private void alert(Exception e) {
        e.printStackTrace();
    }

    private class TextFieldTreeCellImpl extends TreeCell<TreeData> {
        private TextField textField;

        @Override
        public void startEdit() {
            super.startEdit();

            if (textField == null) {
                createTextField();
            }
            setText(null);
            setGraphic(textField);
            textField.selectAll();
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            setText((String) getItem().toString());
            setGraphic(getTreeItem().getGraphic());
        }
        @Override
        public void updateItem(TreeData item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getString());
                    setGraphic(getTreeItem().getGraphic());
                }
            }
        }
        private void createTextField() {
            textField = new TextField(getString());
            textField.setOnKeyReleased((KeyEvent t) -> {
                if (t.getCode() == KeyCode.ENTER && !textField.getText().trim().equals("")) {
                    TreeData d = getItem();
                    d.setName(textField.getText());
                    commitEdit(d);
                } else if (t.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                }
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }

    }
}

