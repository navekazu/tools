package tools.dbconnector6;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import tools.dbconnector6.entity.Connect;
import tools.dbconnector6.mapper.ConnectMapper;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

public class ConnectController implements Initializable {

    @FXML
    private TableView connectTableView;

    @FXML
    private TableColumn<Connect, String> libraryPathTableColumn;

    @FXML
    private TableColumn<Connect, String> driverTableColumn;

    @FXML
    private TableColumn<Connect, String> urlTableColumn;

    @FXML
    private TableColumn<Connect, String> userTableColumn;

    @FXML
    private TableColumn<Connect, String> passwordTableColumn;

    @FXML
    private Button addButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button loadButton;

    @FXML
    private ComboBox historyComboBox;

    @FXML
    private TextField libraryPathTextField;

    @FXML
    private TextField driverTextField;

    @FXML
    private TextField urlTextField;

    @FXML
    private TextField userTextField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button testButton;

    private MessageInterface messageInterface;
    private Connection connection;

    public void setMessageInterface(MessageInterface messageInterface) {
        this.messageInterface = messageInterface;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ConnectMapper mapper = new ConnectMapper();
        ObservableList<Connect> tableList = connectTableView.getItems();
        tableList.clear();

        libraryPathTableColumn.setCellValueFactory(new PropertyValueFactory<Connect, String>("libraryPath"));
        driverTableColumn.setCellValueFactory(new PropertyValueFactory<Connect, String>("driver"));
        urlTableColumn.setCellValueFactory(new PropertyValueFactory<Connect, String>("url"));
        userTableColumn.setCellValueFactory(new PropertyValueFactory<Connect, String>("user"));
        passwordTableColumn.setCellValueFactory(new PropertyValueFactory<Connect, String>("passeord"));

        try {
            List<Connect> list = mapper.selectAll();
            tableList.addAll(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onAdd(ActionEvent event) {
        if (!validTextField()) {
            return ;
        }

        Connect connect = Connect.builder()
                .libraryPath(libraryPathTextField.getText().trim())
                .driver(driverTextField.getText().trim())
                .url(urlTextField.getText().trim())
                .user(userTextField.getText().trim())
                .password(passwordTextField.getText().trim())
                .build();

        ObservableList<Connect> tableList = connectTableView.getItems();
        tableList.add(connect);
        connectTableView.getSelectionModel().select(tableList.size()-1);

        saveConnectList();
    }

    @FXML
    private void onUpdate(ActionEvent event) {
        if (!validTextField()) {
            return ;
        }

        int index = connectTableView.getSelectionModel().getSelectedIndex();
        if (index==-1) {
            return ;
        }

        Connect connect = Connect.builder()
                .libraryPath(libraryPathTextField.getText().trim())
                .driver(driverTextField.getText().trim())
                .url(urlTextField.getText().trim())
                .user(userTextField.getText().trim())
                .password(passwordTextField.getText().trim())
                .build();

        ObservableList<Connect> tableList = connectTableView.getItems();
        tableList.remove(index);
        tableList.add(index, connect);
        connectTableView.getSelectionModel().select(index);

        saveConnectList();
    }

    @FXML
    private void onDelete(ActionEvent event) {
        int index = connectTableView.getSelectionModel().getSelectedIndex();
        if (index==-1) {
            return ;
        }

        ObservableList<Connect> tableList = connectTableView.getItems();
        tableList.remove(index);

        saveConnectList();
    }

    private boolean validTextField() {
        if ("".equals(libraryPathTextField.getText().trim())
                && "".equals(driverTextField.getText().trim())
                && "".equals(urlTextField.getText().trim())
                && "".equals(userTextField.getText().trim())
                && "".equals(passwordTextField.getText().trim())) {
            return false;
        }
        return true;
    }
    private void saveConnectList() {
        ConnectMapper mapper = new ConnectMapper();
        ObservableList<Connect> tableList = connectTableView.getItems();
        List<Connect> list = new ArrayList<>();
        list.addAll(tableList);
        try {
            mapper.save(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onLoad(ActionEvent event) {
        int index = connectTableView.getSelectionModel().getSelectedIndex();
        if (index==-1) {
            return ;
        }

        ObservableList<Connect> tableList = connectTableView.getItems();

        Connect connect = tableList.get(index);
        libraryPathTextField.setText(connect.getLibraryPath());
        driverTextField.setText(connect.getDriver());
        urlTextField.setText(connect.getUrl());
        userTextField.setText(connect.getUser());
        passwordTextField.setText(connect.getPassword());
    }

    @FXML
    private void onOk(ActionEvent event) throws Exception {
        this.connection = connectDatabase();
        connectTableView.getScene().getWindow().hide();
    }

    @FXML
    private void onCancel(ActionEvent event) {
        this.connection = null;
        connectTableView.getScene().getWindow().hide();
    }

    @FXML
    private void onTest(ActionEvent event) throws Exception {
        Connection conn = connectDatabase();
        conn.close();
    }

    private Connection connectDatabase() throws Exception {
        if (!validTextField()) {
            return null;
        }

        Connection conn = null;
        Properties info = new Properties();

        if (!isEmptyString(userTextField.getText())) {
            info.setProperty("user", userTextField.getText());
        }
        if (!isEmptyString(passwordTextField.getText())) {
            info.setProperty("password", passwordTextField.getText());
        }

        if (!(isEmptyString(libraryPathTextField.getText())&&isEmptyString(driverTextField.getText()))) {
            // ドライバ指定あり
            URL[] lib = { new File(libraryPathTextField.getText()).toURI().toURL() };
            URLClassLoader loader = URLClassLoader.newInstance(lib);
            Class<Driver> cd = (Class<Driver>) loader.loadClass(driverTextField.getText());
            Driver driver = cd.newInstance();
            conn = driver.connect(urlTextField.getText(), info);
        } else {
            // ドライバ指定なし
//            Class.forName(entity.getDriver());
            conn = DriverManager.getConnection(urlTextField.getText(), info);
        }

        return conn;
    }

    private boolean isEmptyString(String value) {
        return (value==null || "".equals(value));
    }

    public Connection getConnection() {
        return this.connection;
    }
}
