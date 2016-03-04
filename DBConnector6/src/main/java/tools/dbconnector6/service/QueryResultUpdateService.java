package tools.dbconnector6.service;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import tools.dbconnector6.BackgroundCallbackInterface;
import tools.dbconnector6.MainControllerInterface;
import tools.dbconnector6.entity.QueryResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryResultUpdateService implements BackgroundCallbackInterface<List<TableColumn<QueryResult, String>>, List<Map<String, String>>> {
    private MainControllerInterface mainControllerInterface;
    public QueryResultUpdateService(MainControllerInterface mainControllerInterface) {
        this.mainControllerInterface = mainControllerInterface;
    }

    @Override
    public void run() throws Exception {
        // 実行するSQLを取得
        String sql = getSql();



        List<TableColumn<QueryResult, String>> colList = new ArrayList<>();
        for (int loop = 0; loop < 10; loop++) {
            TableColumn<QueryResult, String> col = new TableColumn<QueryResult, String>("列" + loop);
            final String key = "列" + loop;
            col.setCellValueFactory(
                    new Callback<TableColumn.CellDataFeatures<QueryResult, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue<String> call(TableColumn.CellDataFeatures<QueryResult, String> p) {
                            // 列名をキーに、値を返す。
                            return new SimpleStringProperty(p.getValue().getData(key));
                        }
                    });
            colList.add(col);
        }
        updateUIPreparation(colList);

        List<Map<String, String>> rowList = new ArrayList<>();
        for (int row=0; row<10000; row++) {
            Map<String, String> data = new HashMap<String, String>();
            for (int i = 0; i < 10; i++) {
                data.put("列" + i, "" + row);
            }
            rowList.add(data);

            if (rowList.size()>=10) {
                updateUI(rowList);
                rowList.clear();
            }
        }
    }

    @Override
    public void updateUIPreparation(List<TableColumn<QueryResult, String>> uiParam) throws Exception {
        final List<TableColumn<QueryResult, String>> dispatchParam = new ArrayList<>(uiParam);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ObservableList<TableColumn<QueryResult, String>> columnList = mainControllerInterface.getQueryParam().queryResultTableView.getColumns();
                columnList.clear();
                columnList.addAll(dispatchParam);
            }
        });

    }

    @Override
    public void updateUI(List<Map<String, String>> uiParam) throws Exception {
        final List<Map<String, String>> dispatchParam = new ArrayList<>(uiParam);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (Map<String, String> m: dispatchParam) {
                    QueryResult r = new QueryResult();
                    r.setData(m);
                    mainControllerInterface.getQueryParam().queryResultTableView.getItems().add(r);
                }
            }
        });
    }

    protected String getSql() {
        //  選択したテキストが実行するSQLだが、選択テキストがない場合はテキストエリア全体をSQLとする
        String sql = mainControllerInterface.getQueryParam().queryTextArea.getSelectedText();
        if (sql.length()<=0) {
            sql = mainControllerInterface.getQueryParam().queryTextArea.getText();
        }
        return sql;
    }

    protected String[] splitSql(String sql) {
        return sql.trim().split(";\n");
    }
}
