package tools.dbconnector6.service;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import tools.dbconnector6.BackgroundCallbackInterface;
import tools.dbconnector6.MainControllerInterface;
import tools.dbconnector6.entity.Connect;
import tools.dbconnector6.entity.QueryResult;
import tools.dbconnector6.serializer.QueryHistorySerializer;

import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class QueryResultUpdateService implements BackgroundCallbackInterface<List<TableColumn<QueryResult, String>>, List<Map<String, String>>> {
    private static final DecimalFormat RESPONSE_TIME_FORMAT = new DecimalFormat("#,##0.000");
    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#,##0");
    private static final int FLUSH_ROW_COUNT = 1000;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
    private QueryHistorySerializer queryHistorySerializer;

    private MainControllerInterface mainControllerInterface;
    public QueryResultUpdateService(MainControllerInterface mainControllerInterface) {
        this.mainControllerInterface = mainControllerInterface;
        this.queryHistorySerializer = new QueryHistorySerializer();
    }

    @Override
    public void run() throws Exception {
        if (mainControllerInterface.getConnection()==null) {
            mainControllerInterface.writeLog("No connect.");
            return ;
        }

        // 実行するSQLを取得
        String[] queries = splitQuery(getQuery());

        // 個々のSQLを実行
        int executeQueryCount = 0;
        try {
            for (String query: queries) {
                queryHistorySerializer.appendText(createQueryHistory(query));
                executeQuery(query);
                executeQueryCount++;
            }

            if (queries.length >= 2) {
                mainControllerInterface.writeLog("Total query count: %d", executeQueryCount);
            }
        } catch(SQLException e) {
            mainControllerInterface.writeLog(e.getMessage());
            // 複数クエリの場合、実行出来たクエリ数を出力
            if (queries.length >= 2) {
                mainControllerInterface.writeLog("Succeeded query count: %d", executeQueryCount);
            }
        }
    }

    private void executeQuery(String query) throws Exception {
        Connection connection = mainControllerInterface.getConnection();
        long startTime, endTime;

        try (Statement statement = connection.createStatement()) {
            mainControllerInterface.writeLog("Executing...");
            startTime = System.currentTimeMillis();
            boolean executeResult = statement.execute(query);
            endTime = System.currentTimeMillis();
            mainControllerInterface.writeLog("Response time: %s sec", RESPONSE_TIME_FORMAT.format(((double) (endTime - startTime))/1000.0));

            if (executeResult) {
                // 結果あり
                startTime = System.currentTimeMillis();
                try (ResultSet resultSet = statement.getResultSet()) {
                    // カラム情報を取得
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    List<TableColumn<QueryResult, String>> colList = new ArrayList<>();

                    for (int loop=0; loop<metaData.getColumnCount(); loop++) {
                        TableColumn<QueryResult, String> col = new TableColumn<>(metaData.getColumnName(loop+1));
                        final String key = Integer.toString(loop);
                        col.setCellValueFactory(
                                new Callback<TableColumn.CellDataFeatures<QueryResult, String>, ObservableValue<String>>() {
                                    @Override
                                    public ObservableValue<String> call(TableColumn.CellDataFeatures<QueryResult, String> p) {
                                        return new SimpleStringProperty(p.getValue().getData(key));
                                    }
                                });
                        colList.add(col);
                    }
                    updateUIPreparation(colList);

                    // 結果を取得
                    List<Map<String, String>> rowList = new ArrayList<>();
                    long rowCount = 0;
                    while (resultSet.next()) {
                        Map<String, String> data = new HashMap<String, String>();
                        for (int loop=0; loop<metaData.getColumnCount(); loop++) {
                            data.put(Integer.toString(loop), resultSet.getString(loop+1));
                        }
                        rowList.add(data);
                        rowCount++;

                        if (rowList.size() >= FLUSH_ROW_COUNT) {
                            updateUI(rowList);
                            rowList.clear();
                        }
                    }
                    updateUI(rowList);
                    endTime = System.currentTimeMillis();
                    mainControllerInterface.writeLog("Success. count: %s  recieved data time: %s sec", NUMBER_FORMAT.format(rowCount), RESPONSE_TIME_FORMAT.format(((double) (endTime - startTime))/1000.0));
                }
            } else {
                // 結果なし
                mainControllerInterface.writeLog("Success. count: %S", NUMBER_FORMAT.format(statement.getUpdateCount()));
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
                mainControllerInterface.getQueryParam().queryResultTableView.getItems().clear();
                mainControllerInterface.getQueryParam().queryResultTableView.scrollTo(0);
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

    protected String getQuery() {
        //  選択したテキストが実行するSQLだが、選択テキストがない場合はテキストエリア全体をSQLとする
        String sql = mainControllerInterface.getQueryParam().queryTextArea.getSelectedText();
        if (sql.length()<=0) {
            sql = mainControllerInterface.getQueryParam().queryTextArea.getText();
        }
        return sql;
    }

    protected String[] splitQuery(String sql) {
        String[] split = sql.trim().split("(;\n|/\n)");
        return Arrays.stream(split)
                .map(s -> {
                    return s.trim();
                })
                .map(s -> {
                    return s.endsWith(";")? s.substring(0, s.length()-1): s;
                })
                .map(s -> {
                    return s.endsWith("/")? s.substring(0, s.length()-1): s;
                })
                .map(s -> {
                    return s.trim();
                })
                .filter(s -> s.length()>=1)
                .toArray(String[]::new);
    }

    private String createQueryHistory(String query) {
        Connect connect = mainControllerInterface.getConnectParam();
        StringBuilder builder = new StringBuilder();

        builder.append("====================================================================================================\n");
        builder.append(String.format("%s Lib[%s] Dir[%s] URL[%s] Use[%s]\n"
                , DATE_FORMAT.format(new Date())
                , (connect.getLibraryPath()==null? "": connect.getLibraryPath())
                , (connect.getDriver()==null? "": connect.getDriver())
                , (connect.getUrl()==null? "": connect.getUrl())
                , (connect.getUser()==null? "": connect.getUser())));
        builder.append("----------------------------------------------------------------------------------------------------\n");
        builder.append(query);
        builder.append("\n");

        return builder.toString();
    }
}
