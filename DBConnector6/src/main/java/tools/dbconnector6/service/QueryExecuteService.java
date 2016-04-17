package tools.dbconnector6.service;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import tools.dbconnector6.MainControllerInterface;
import tools.dbconnector6.entity.Connect;
import tools.dbconnector6.queryresult.QueryResult;
import tools.dbconnector6.queryresult.QueryResultCellValue;
import tools.dbconnector6.serializer.QueryHistorySerializer;
import tools.dbconnector6.transfer.ResultDataTransfer;
import tools.dbconnector6.transfer.ResultDataTransferClipboard;

import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.List;

/**
 * SQLクエリを実行するサービス。
 */
public class QueryExecuteService implements BackgroundServiceInterface<List<TableColumn<QueryResult, String>>, List<List<QueryResultCellValue>>> {
    private static final DecimalFormat RESPONSE_TIME_FORMAT = new DecimalFormat("#,##0.000");               // ToDo:書式付き出力に置き換えたい
    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#,##0");                          // ToDo:書式付き出力に置き換えたい
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
    private static final int FLUSH_ROW_COUNT = 1000;
    private static final int SILENT_MODE_COUNT = 100;
    private QueryHistorySerializer queryHistorySerializer;

    // メイン画面へのアクセス用インターフェース
    private MainControllerInterface mainControllerInterface;

    /**
     * コンストラクタ。<br>
     * @param mainControllerInterface メイン画面へのアクセス用インターフェース
     */
    public QueryExecuteService(MainControllerInterface mainControllerInterface) {
        this.mainControllerInterface = mainControllerInterface;
        this.queryHistorySerializer = new QueryHistorySerializer();
    }

    @Override
    public void run(Task task) throws Exception {
        if (!mainControllerInterface.isConnect()) {
            return ;
        }

        // 実行するSQLを取得
        String[] queries = splitQuery(mainControllerInterface.getQuery());

        // SILENT_MODE_COUNTを超えたらサイレントモードで実行し、ログを抑制する
        boolean silentMode = false;
        if (SILENT_MODE_COUNT<=queries.length) {
            mainControllerInterface.writeLog("Execute in silent mode. (Because too many queries. %,3d counts)", queries.length);
            silentMode = true;
        }

        // 個々のSQLを実行
        int executeQueryCount = 0;
        try {
            for (String query: queries) {
                queryHistorySerializer.appendText(createQueryHistory(query));
                executeQuery(task, query, silentMode);
                if (task.isCancelled()) {
                    break;
                }

                executeQueryCount++;
            }

            if (task.isCancelled()) {
                mainControllerInterface.writeLog("Query cancelled.");
            } else if (queries.length >= 2) {
                mainControllerInterface.writeLog("Total query count: %,3d", executeQueryCount);
            }
        } catch(Exception e) {
            mainControllerInterface.writeLog(e);
            // 複数クエリの場合、実行出来たクエリ数を出力
            if (queries.length >= 2) {
                mainControllerInterface.writeLog("Succeeded query count: %,3d", executeQueryCount);
            }
        }
    }

    @Override
    public void prepareUpdate(final List<TableColumn<QueryResult, String>> prepareUpdateParam) throws Exception {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mainControllerInterface.getQueryParam().queryResultTableView.getItems().clear();
                mainControllerInterface.getQueryParam().queryResultTableView.getColumns().clear();

                ObservableList<TableColumn<QueryResult, String>> columnList = mainControllerInterface.getQueryParam().queryResultTableView.getColumns();
                columnList.addAll(prepareUpdateParam);
            }
        });
    }

    @Override
    public void update(final List<List<QueryResultCellValue>> updateParam) throws Exception {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                List<QueryResult> list = new ArrayList<>();
                for (List<QueryResultCellValue> m: updateParam) {
                    QueryResult r = new QueryResult();
                    r.setRecordData(m);
                    list.add(r);
                }
                List<QueryResult> items = mainControllerInterface.getQueryParam().queryResultTableView.getItems();
                items.addAll(list);
            }
        });
    }

    @Override
    public void cancel() throws Exception {
        mainControllerInterface.writeLog("Query cancelling...");
    }

    @Override
    public void cancelled() {

    }

    @Override
    public void failed() {

    }

    @Override
    public String getNotRunningMessage() {
        return "Not executing now.";
    }

    private void executeQuery(Task task, String query, boolean silentMode) throws Exception {
        Connection connection = mainControllerInterface.getConnection();
        long startTime, endTime;

        try (Statement statement = connection.createStatement()) {
            if (!silentMode) {
                mainControllerInterface.writeLog("Executing...");
            }
            startTime = System.currentTimeMillis();
            boolean executeResult = statement.execute(query);
            endTime = System.currentTimeMillis();
            if (!silentMode) {
                mainControllerInterface.writeLog("Response time: %s sec", RESPONSE_TIME_FORMAT.format(((double) (endTime - startTime))/1000.0));
            }

            if (task.isCancelled()) {
                return ;
            }

            if (!executeResult) {
                // 結果なし
                if (!silentMode) {
                    mainControllerInterface.writeLog("Success. count: %S", NUMBER_FORMAT.format(statement.getUpdateCount()));
                }
                return ;
            }

            // 結果あり
            startTime = System.currentTimeMillis();
            try (ResultSet resultSet = statement.getResultSet()) {
                ResultDataTransfer resultDataTransfer = new ResultDataTransferClipboard(mainControllerInterface.isEvidenceMode(), mainControllerInterface.isEvidenceModeIncludeHeader(), mainControllerInterface.getEvidenceDelimiter());

                // カラム情報を取得し、一覧のヘッダ部を作成する
                ResultSetMetaData metaData = resultSet.getMetaData();
                List<TableColumn<QueryResult, String>> colList = new ArrayList<>();

                for (int loop=0; loop<metaData.getColumnCount(); loop++) {
                    TableColumn<QueryResult, String> col = new TableColumn<>(metaData.getColumnName(loop+1));
                    final int index = loop;
                    final Pos pos = QueryResultCellValue.getAlignment(metaData, loop+1);
                    col.setCellValueFactory(
                            new Callback<TableColumn.CellDataFeatures<QueryResult, String>, ObservableValue<String>>() {
                                @Override
                                public ObservableValue<String> call(TableColumn.CellDataFeatures<QueryResult, String> p) {
                                    return new SimpleStringProperty(p.getValue().getData(index).getFormattedString());
                                }
                            });
                    col.setCellFactory(new Callback<TableColumn<QueryResult, String>, TableCell<QueryResult, String>>() {
                        @Override
                        public TableCell<QueryResult, String> call(TableColumn<QueryResult, String> param) {
                            return new TableCell<QueryResult, String>() {
                                @Override
                                public void updateItem(String item, boolean empty) {
                                    updateCellItem(this, item, empty, index, pos);
                                }
                            };
                        }
                    });

                    colList.add(col);
                }
                prepareUpdate(colList);
                resultDataTransfer.setHeader(colList);

                if (task.isCancelled()) {
                    return;
                }

                // 結果を取得し、一覧のボディ部を作成する
                List<List<QueryResultCellValue>> rowList = new ArrayList<>();
                long rowCount = 0;
                while (resultSet.next()) {
                    List<QueryResultCellValue> data = new ArrayList<>();
                    for (int loop=0; loop<metaData.getColumnCount(); loop++) {
                        QueryResultCellValue cellValue = QueryResultCellValue.createQueryResultCellValue(metaData, resultSet, loop+1);
                        data.add(cellValue);
                    }
                    rowList.add(data);
                    resultDataTransfer.addData(data);
                    rowCount++;

                    if (task.isCancelled()) {
                        return;
                    }

                    // FLUSH_ROW_COUNT毎に一覧へ反映する
                    if (rowList.size() >= FLUSH_ROW_COUNT) {
                        update(new ArrayList<>(rowList));
                        rowList.clear();
                    }
                }

                resultDataTransfer.transfer();

                if (task.isCancelled()) {
                    return;
                }

                update(new ArrayList<>(rowList));
                endTime = System.currentTimeMillis();
                if (!silentMode) {
                    mainControllerInterface.writeLog("Success. count: %s  recieved data time: %s sec", NUMBER_FORMAT.format(rowCount), RESPONSE_TIME_FORMAT.format(((double) (endTime - startTime)) / 1000.0));
                }
            }
        }
    }

    private void updateCellItem(TableCell<QueryResult, String> tableCell, String item, boolean empty, int index, Pos pos) {
        if (item == null) {
            return;
        }
        tableCell.setText(item.toString());

        TableRow row = tableCell.getTableRow();
        if (row == null) {
            return;
        }

        ObservableList<QueryResult> list = tableCell.getTableView().getItems();
        QueryResult queryResult = list.get(row.getIndex());
        QueryResultCellValue cellValue = queryResult.getData(index);
        if (cellValue.isNullValue()) {
            tableCell.setAlignment(Pos.CENTER);
            tableCell.setTextFill(Color.BLUE);
        } else {
            tableCell.setAlignment(pos);
            tableCell.setTextFill(Color.BLACK);
        }
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
                .map(s -> {
                    return isOneWord(s)? String.format("select * from %s", s): s;
                })
                .filter(s -> s.length()>=1)
                .toArray(String[]::new);
    }
    private boolean isOneWord(String sql) {
        return (sql.length()>=1 && sql.indexOf(" ")==-1 && sql.indexOf("\t")==-1 && sql.indexOf("\n")==-1);
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
