package tools.dbconnector6.transfer;

import javafx.scene.control.TableColumn;
import tools.dbconnector6.queryresult.QueryResult;
import tools.dbconnector6.queryresult.QueryResultCellValue;

import java.util.List;

/**
 * 結果一覧の内容を転送する
 */
public abstract class ResultDataTransfer {
    protected boolean evidenceMode;
    protected boolean includeHeader;
    protected String delimiter;
    protected StringBuilder resultHeader;
    protected StringBuilder resultData;

    public ResultDataTransfer(boolean evidenceMode, boolean includeHeader, String delimiter) {
        this.evidenceMode = evidenceMode;
        this.includeHeader = includeHeader;
        this.delimiter = delimiter;

        this.resultHeader = new StringBuilder();
        this.resultData = new StringBuilder();
    }

    public void setHeader(List<TableColumn<QueryResult, String>> header) {
        if (!(evidenceMode && includeHeader)) {
            return;
        }
        String[] arr = header.stream()
                .map(column -> column.getText())
                .toArray(String[]::new);
        resultHeader.append(String.join(delimiter, arr));
        resultHeader.append("\n");
    }

    public void addData(List<QueryResultCellValue> list) {
        if (!evidenceMode) {
            return;
        }
        String[] arr = list.stream()
                .map(cell -> cell.getEvidenceModeString())
                .toArray(String[]::new);
        resultData.append(String.join(delimiter, arr));
        resultData.append("\n");
    }

    protected String getTransferData() {
        StringBuilder data = new StringBuilder();
        data.append(resultHeader);
        data.append(resultData);
        return data.toString();
    }

    public abstract void transfer();
}
