package tools.dbconnector6.service;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import tools.dbconnector6.BackgroundCallbackInterface;
import tools.dbconnector6.MainControllerInterface;
import tools.dbconnector6.entity.Connect;
import tools.dbconnector6.queryresult.QueryResult;
import tools.dbconnector6.queryresult.QueryResultCellValue;
import tools.dbconnector6.serializer.QueryHistorySerializer;

import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class QueryResultUpdateService implements BackgroundCallbackInterface<List<TableColumn<QueryResult, String>>, List<Map<String, QueryResultCellValue>>> {
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
    public void run(Task task) throws Exception {
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
                executeQuery(task, query);
                if (task.isCancelled()) {
                    break;
                }

                executeQueryCount++;
            }

            if (task.isCancelled()) {
                mainControllerInterface.writeLog("Query cancelled.");
            } else if (queries.length >= 2) {
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

    @Override
    public void cancel() throws Exception {
        mainControllerInterface.writeLog("Query cancelling...");
    }

    private void executeQuery(Task task, String query) throws Exception {
        Connection connection = mainControllerInterface.getConnection();
        long startTime, endTime;

        try (Statement statement = connection.createStatement()) {
            mainControllerInterface.writeLog("Executing...");
            startTime = System.currentTimeMillis();
            boolean executeResult = statement.execute(query);
            endTime = System.currentTimeMillis();
            mainControllerInterface.writeLog("Response time: %s sec", RESPONSE_TIME_FORMAT.format(((double) (endTime - startTime))/1000.0));

            if (task.isCancelled()) {
                return;
            }

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
                                        return new SimpleStringProperty(p.getValue().getData(key).getFormattedString());
                                    }
                                });
                        col.setCellFactory(new Callback<TableColumn<QueryResult, String>, TableCell<QueryResult, String>>() {
                            @Override
                            public TableCell<QueryResult, String> call(TableColumn<QueryResult, String> param) {
                                TableCell cell = new TableCell(){
                                    @Override
                                    public void updateItem(Object item, boolean empty){
                                        if(item !=null){
                                            setText(item.toString());
                                        }
                                    }
                                };

                                cell.setAlignment(Pos.CENTER_RIGHT);
                                return cell;
                            }
                        });
                        colList.add(col);
                    }
                    updateUIPreparation(colList);

                    if (task.isCancelled()) {
                        return;
                    }

                    // 結果を取得
                    List<Map<String, QueryResultCellValue>> rowList = new ArrayList<>();
                    long rowCount = 0;
                    while (resultSet.next()) {
                        Map<String, QueryResultCellValue> data = new HashMap<>();
                        for (int loop=0; loop<metaData.getColumnCount(); loop++) {
                            QueryResultCellValue cellValue = QueryResultCellValue.createQueryResultCellValue(loop+1, metaData, resultSet);
                            data.put(Integer.toString(loop), cellValue);
                        }
                        rowList.add(data);
                        rowCount++;

                        if (task.isCancelled()) {
                            return;
                        }

                        if (rowList.size() >= FLUSH_ROW_COUNT) {
                            updateUI(rowList);
                            rowList.clear();
                        }
                    }

                    if (task.isCancelled()) {
                        return;
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
    public void updateUI(List<Map<String, QueryResultCellValue>> uiParam) throws Exception {
        final List<Map<String, QueryResultCellValue>> dispatchParam = new ArrayList<>(uiParam);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (Map<String, QueryResultCellValue> m: dispatchParam) {
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
