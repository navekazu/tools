package tools.dbconnector6.transfer;

import javafx.scene.control.TableColumn;
import tools.dbconnector6.queryresult.QueryResult;
import tools.dbconnector6.queryresult.QueryResultCellValue;

import java.util.List;

/**
 * クエリ実行結果の内容を転送する抽象クラス。<br>
 * 転送先は具象クラスで実装する。<br>
 */
public abstract class ResultDataTransfer {
    private boolean evidenceMode;           // エビデンス取得モード。クエリ実行結果をエビデンスとして転送する場合は true、それ以外は false
    private boolean includeHeader;          // エビデンスにヘッダ（カラム）を含めるか？ 含める場合は true、それ以外は false
    private String delimiter;               // カラムの区切り文字
    private StringBuilder resultHeader;     // ヘッダの内容
    private StringBuilder resultData;       // クエリ実行結果の内容

    /**
     * コンストラクタ
     * @param evidenceMode エビデンス取得モード。クエリ実行結果をエビデンスとして転送する場合は true、それ以外は false
     * @param includeHeader エビデンスにヘッダ（カラム）を含めるか？ 含める場合は true、それ以外は false
     * @param delimiter カラムの区切り文字
     */
    public ResultDataTransfer(boolean evidenceMode, boolean includeHeader, String delimiter) {
        this.evidenceMode = evidenceMode;
        this.includeHeader = includeHeader;
        this.delimiter = delimiter;

        this.resultHeader = new StringBuilder();
        this.resultData = new StringBuilder();
    }

    /**
     * ヘッダ内容を更新する。<br>
     * エビデンス取得モードが true かつエビデンスにヘッダを含める場合のみ更新し、それ以外は何も行わない。
     * @param header ヘッダ内容
     */
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

    /**
     * クエリ実行結果を追記する。<br>
     * エビデンス取得モードが true の場合のみ更新し、それ以外は何も行わない。
     * @param list クエリ実行結果のレコード情報
     */
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

    /**
     * ヘッダ内容とクエリ実行結果を結合した文字列を返す。<br>
     * @return ヘッダ内容とクエリ実行結果を結合した文字列
     */
    protected String getTransferData() {
        StringBuilder data = new StringBuilder();
        data.append(resultHeader);
        data.append(resultData);
        return data.toString();
    }

    /**
     * エビデンス取得モードを返す。<br>
     * @return エビデンス取得モード
     */
    protected boolean getEvidenceMode() {
        return evidenceMode;
    }

    /**
     * クエリ実行結果の転送処理。<br>
     * 具体的な転送先は具象クラスで決定する。<br>
     */
    public abstract void transfer();
}
