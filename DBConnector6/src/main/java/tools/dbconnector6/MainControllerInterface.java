package tools.dbconnector6;

import javafx.scene.control.*;
import tools.dbconnector6.controller.DbStructureTreeItem;
import tools.dbconnector6.entity.Connect;
import tools.dbconnector6.entity.TableColumnTab;
import tools.dbconnector6.entity.TablePropertyTab;
import tools.dbconnector6.service.BackgroundService;

import java.sql.Connection;

/**
 * メイン画面へのアクセスを限定するためのインターフェース。<br>
 * 実装はMainController。<br>
 * @see tools.dbconnector6.controller.MainController
 */
public interface MainControllerInterface {

    /**
     * 通常ログの出力。<br>
     * 出力はString#formatメソッドの書式文字列で指定し、ログ出力エリアに出力する。<br>
     * @param format 書式文字列
     * @param args   書式文字列の書式指示子により参照される引数
     * @see java.lang.String
     */
    public void writeLog(String format, Object... args);

    /**
     * 例外ログの出力。<br>
     * 例外ログを、ログ出力エリアとログファイルに出力する。<br>
     * @param e 例外発生時の例外オブジェクト
     */
    public void writeLog(Throwable e);

    /**
     * 予約語選択画面の選択結果の通知。<br>
     * クエリー入力欄に現在入力中の単語を指定された単語に置き換える。<br>
     * @param word 選択結果の予約語
     */
    public void selectReservedWord(String word);

    /**
     * メイン画面にフォーカスを移動させる。<br>
     */
    public void mainControllerRequestFocus();

    /**
     * 予約語選択画面を閉じる
     */
    public void hideReservedWordStage();

    /**
     * 警告画面をモーダルで表示する。<br>
     * @param message メッセージタイトル
     * @param detail  メッセージの詳細
     */
    public void showAlertDialog(String message, String detail);

    /**
     * 待機画面をモーダルで表示する。<br>
     * @param message メッセージタイトル
     * @param detail  メッセージの詳細
     */
    public void showWaitDialog(String message, String detail);

    /**
     * 待機画面を閉じる。<br>
     */
    public void hideWaitDialog();

    /**
     * 現在のエビデンスモードを取得する。<br>
     * @return エビデンスモードが有効の場合は true、それ以外は false。
     */
    public boolean isEvidenceMode();

    /**
     * エビデンスにヘッダー列（カラム列）を含めるかを取得する。<br>
     * @return ヘッダー列（カラム列）を含める場合は true、それ以外は false。
     */
    public boolean isEvidenceModeIncludeHeader();

    /**
     * エビデンスの列の区切り文字を取得する。<br>
     * @return 区切り文字
     */
    public String getEvidenceDelimiter();

    public String getQuery();
    public String getSelectedQuery();
    public void updateSelectedQuery(String query);
    public void addQueryWord(String word, boolean shiftDown);

    public String getEditorPath();

    public void connectNotify();
    public Connection getConnection();
    public Connect getConnectParam();

    /**
     * データベース接続確認。未接続時にログエリアへメッセージを出力する。<br>
     * ログエリアへメッセージを出力しない場合はisConnectWithoutMessage()メソッドを利用する。<br>
     * @return データベース接続時は true 、それ以外は false を返す。
     * @see tools.dbconnector6.MainControllerInterface#isConnectWithoutOutputMessage
     */
    public boolean isConnect();

    /**
     * データベース接続確認。未接続時にログエリアへメッセージを出力しない。
     * ログエリアへメッセージを出力する場合はisConnect()メソッドを利用する。<br>
     * @return データベース接続時は true 、それ以外は false を返す。
     * @see tools.dbconnector6.MainControllerInterface#isConnect
     */
    public boolean isConnectWithoutOutputMessage();

    public BackgroundService getTableStructureUpdateService();

    public class DbStructureParam {
        public TextField filterTextField;
        public TreeView dbStructureTreeView;
        public DbStructureTreeItem dbStructureRootItem;
    }
    public class TableStructureTabParam {
        public TabPane tableStructureTabPane;

        public Tab tablePropertyTab;
        public TableView tablePropertyTableView;
        public TableColumn<TablePropertyTab, String> keyTableColumn;
        public TableColumn<TablePropertyTab, String> valueTableColumn;

        public Tab tableColumnTab;
        public TableView tableColumnTableView;
        public TableColumn<TableColumnTab, String> nameTableColumn;
        public TableColumn<TableColumnTab, String> typeTableColumn;
        public TableColumn<TableColumnTab, Integer> sizeTableColumn;
        public TableColumn<TableColumnTab, Integer> decimalDigitsTableColumn;
        public TableColumn<TableColumnTab, String> nullableTableColumn;
        public TableColumn<TableColumnTab, Integer> primaryKeyTableColumn;
        public TableColumn<TableColumnTab, String> remarksTableColumn;
        public TableColumn<TableColumnTab, String> columnDefaultTableColumn;
        public TableColumn<TableColumnTab, String> autoincrementTableColumn;
        public TableColumn<TableColumnTab, String> generatedColumnTableColumn;

        public Tab tableIndexTab;
        public ComboBox tableIndexNameComboBox;
        public TextField tableIndexPrimaryKeyTextField;
        public TextField tableIndexUniqueKeyTextField;
        public ListView tableIndexListView;
    }

    public class QueryParam {
        public TextArea queryTextArea;
        public TableView queryResultTableView;

    }

    public DbStructureParam getDbStructureParam();
    public TableStructureTabParam getTableStructureTabParam();
    public QueryParam getQueryParam();
}
