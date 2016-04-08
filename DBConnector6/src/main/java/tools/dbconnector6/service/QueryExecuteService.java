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
import tools.dbconnector6.BackgroundServiceInterface;
import tools.dbconnector6.MainControllerInterface;
import tools.dbconnector6.entity.Connect;
import tools.dbconnector6.queryresult.QueryResult;
import tools.dbconnector6.queryresult.QueryResultCellValue;
import tools.dbconnector6.serializer.QueryHistorySerializer;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.List;

public class QueryExecuteService implements BackgroundServiceInterface<List<TableColumn<QueryResult, String>>, List<List<QueryResultCellValue>>> {
    private static final DecimalFormat RESPONSE_TIME_FORMAT = new DecimalFormat("#,##0.000");
    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#,##0");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
    private static final int FLUSH_ROW_COUNT = 1000;
    private QueryHistorySerializer queryHistorySerializer;

    private MainControllerInterface mainControllerInterface;
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
        } catch(Exception e) {
            mainControllerInterface.writeLog(e);
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
                return ;
            }

            if (!executeResult) {
                // 結果なし
                mainControllerInterface.writeLog("Success. count: %S", NUMBER_FORMAT.format(statement.getUpdateCount()));
                return ;
            }

            // 結果あり
            startTime = System.currentTimeMillis();
            try (ResultSet resultSet = statement.getResultSet()) {
                EvidenceInfo evidenceInfo = new EvidenceInfo();

                // カラム情報を取得し、一覧のヘッダ部を作成する
                ResultSetMetaData metaData = resultSet.getMetaData();
                List<TableColumn<QueryResult, String>> colList = new ArrayList<>();

                for (int loop=0; loop<metaData.getColumnCount(); loop++) {
                    TableColumn<QueryResult, String> col = new TableColumn<>(metaData.getColumnName(loop+1));
                    final int index = loop;
                    final Pos pos = QueryResultCellValue.getAlignment(loop+1, metaData);
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
                    evidenceInfo.appendHeader(metaData.getColumnName(loop + 1));
                }
                updateUIPreparation(colList);
                evidenceInfo.flushHeader();

                if (task.isCancelled()) {
                    return;
                }

                // 結果を取得し、一覧のボディ部を作成する
                List<List<QueryResultCellValue>> rowList = new ArrayList<>();
                long rowCount = 0;
                while (resultSet.next()) {
                    List<QueryResultCellValue> data = new ArrayList<>();
                    for (int loop=0; loop<metaData.getColumnCount(); loop++) {
                        QueryResultCellValue cellValue = QueryResultCellValue.createQueryResultCellValue(loop+1, metaData, resultSet);
                        data.add(cellValue);
                        evidenceInfo.appendRow(cellValue.getEvidenceModeString());
                    }
                    rowList.add(data);
                    rowCount++;
                    evidenceInfo.flushRow();

                    if (task.isCancelled()) {
                        return;
                    }

                    // FLUSH_ROW_COUNT毎に一覧へ反映する
                    if (rowList.size() >= FLUSH_ROW_COUNT) {
                        updateUI(new ArrayList<>(rowList));
                        rowList.clear();
                    }
                }

                evidenceInfo.pasteToClipboard();

                if (task.isCancelled()) {
                    return;
                }

                updateUI(new ArrayList<>(rowList));
                endTime = System.currentTimeMillis();
                mainControllerInterface.writeLog("Success. count: %s  recieved data time: %s sec", NUMBER_FORMAT.format(rowCount), RESPONSE_TIME_FORMAT.format(((double) (endTime - startTime))/1000.0));
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

    @Override
    public void updateUIPreparation(final List<TableColumn<QueryResult, String>> uiParam) throws Exception {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mainControllerInterface.getQueryParam().queryResultTableView.getItems().clear();
                mainControllerInterface.getQueryParam().queryResultTableView.getColumns().clear();

                ObservableList<TableColumn<QueryResult, String>> columnList = mainControllerInterface.getQueryParam().queryResultTableView.getColumns();
                columnList.addAll(uiParam);
            }
        });
    }

    @Override
    public void updateUI(final List<List<QueryResultCellValue>> uiParam) throws Exception {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                List<QueryResult> list = new ArrayList<>();
                for (List<QueryResultCellValue> m: uiParam) {
                    QueryResult r = new QueryResult();
                    r.setData(m);
                    list.add(r);
                }
                List<QueryResult> items = mainControllerInterface.getQueryParam().queryResultTableView.getItems();
                items.addAll(list);
            }
        });
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

    private class EvidenceInfo {

        private StringBuilder evidenceInfo = null;
        private StringBuilder header = null;
        private StringBuilder row = null;

        public EvidenceInfo() {
            evidenceInfo = new StringBuilder();
        }

        public void appendHeader(String data) {
            if (!(mainControllerInterface.isEvidenceMode() && mainControllerInterface.isEvidenceModeIncludeHeader())) {
                return;
            }
            if (header==null) {
                header = new StringBuilder();
            } else {
                header.append(mainControllerInterface.getEvidenceDelimiter());
            }
            header.append(data);
        }
        public void flushHeader() {
            if (!(mainControllerInterface.isEvidenceMode() && mainControllerInterface.isEvidenceModeIncludeHeader())) {
                return;
            }
            evidenceInfo.append(header);
            evidenceInfo.append("\n");
            header = null;
        }
        public void appendRow(String data) {
            if (!mainControllerInterface.isEvidenceMode()) {
                return;
            }
            if (row==null) {
                row = new StringBuilder();
            } else {
                row.append(mainControllerInterface.getEvidenceDelimiter());
            }
            row.append(data);
        }
        public void flushRow() {
            if (!mainControllerInterface.isEvidenceMode()) {
                return;
            }
            evidenceInfo.append(row);
            evidenceInfo.append("\n");
            row = null;
        }

        private void append(StringBuilder sb, String data) {
            if (header==null) {
                header = new StringBuilder();
            } else {
                header.append(mainControllerInterface.getEvidenceDelimiter());
            }
            header.append(data);
        }

        public void pasteToClipboard() {
            if (!mainControllerInterface.isEvidenceMode()) {
                return;
            }

            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Clipboard clip = toolkit.getSystemClipboard();
            StringSelection stringSelection = new StringSelection(evidenceInfo.toString());
            clip.setContents(stringSelection, stringSelection);
        }
    }
}