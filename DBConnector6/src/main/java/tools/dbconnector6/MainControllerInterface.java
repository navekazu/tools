package tools.dbconnector6;

import javafx.scene.control.*;
import tools.dbconnector6.controller.DbStructureTreeItem;
import tools.dbconnector6.entity.Connect;
import tools.dbconnector6.entity.TableColumnTab;
import tools.dbconnector6.entity.TablePropertyTab;
import tools.dbconnector6.service.BackgroundService;

import java.sql.Connection;

/**
 * メイン画面へアクセスするためのインターフェース。<br>
 * データの取得や更新や通知を行うための定義。<br>
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

    /**
     * 実行対象のクエリを取得する。<br>
     * SQLスクリプトを読み込んでの実行中であれば読み込んだスクリプトの内容を返す。<br>
     * それ以外はクエリ入力欄の内容を返すが、
     * クエリ入力欄で選択中のクエリがあればその内容を、
     * 選択中のクエリが無ければクエリ入力欄全体の内容を返す。<br>
     * @return 実行対象のクエリ
     */
    public String getQuery();

    /**
     * クエリ入力欄の現在選択中の文字列を取得する。
     * @return クエリ入力欄の現在選択中の文字列
     */
    public String getSelectedQuery();

    /**
     * 指定された単語でクエリ入力欄の現在選択中のテキストを置き換える。<br>
     * @param word 置き換えする単語
     */
    public void updateSelectedQuery(String word);

    /**
     * 指定された単語をクエリ入力欄の現在キャレット位置に挿入する、<br>
     * クエリ結果の行タイトル（カラム名）やデータベース構造一覧の項目（テーブル名）を右ダブルクリックした際に、
     * ダブルクリックしたテキストをクエリ入力欄に挿入する入力補完機能を実現する。<br>
     * シフトを押しながら右ダブルクリックした場合、挿入した単語の後にカンマを追加する。<br>
     * @param word クエリ入力欄に挿入する単語
     * @param shiftDown シフトを押しながら挿入する場合はtrue、それ以外はfalse
     */
    public void addQueryWord(String word, boolean shiftDown);

    /**
     * 現在設定されているテキストエディタへのパスを返す。<br>
     * 設定されていない場合は空文字を返す。<br>
     * @return テキストエディタへのパス
     */
    public String getEditorPath();

    /**
     * データベース接続画面から、データベース接続時にメイン画面へ接続した旨の通知をする。<br>
     * 通知を受け取ったメイン画面は、データベース構造表示等の画面更新を行う。<br>
     */
    public void connectNotify();

    /**
     * 現在接続しているデータベースへの接続情報（ドライバ名URLやユーザ名）を取得する。<br>
     * @return データベースへの接続情報
     */
    public Connect getConnectParam();

    /**
     * 現在接続しているデータベースへのコネクションを取得する。<br>
     * @return データベースコネクション
     */
    public Connection getConnection();

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

    /**
     * TableStructureUpdateServiceクラスのrestartメソッドを呼んで更新を要求する。<br>
     */
    public void requestTableStructureUpdate();

    /**
     * メイン画面左上のデータベース構造のUI参照をまとめた構造体
     */
    public class DbStructureParam {
        public TextField filterTextField;
        public TreeView dbStructureTreeView;
        public DbStructureTreeItem dbStructureRootItem;
    }

    /**
     * メイン画面左下のテーブル構造のUI参照をまとめた構造体
     */
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

    /**
     * メイン画面右のクエリ入力・結果一覧のUI参照をまとめた構造体
     */
    public class QueryParam {
        public TextArea queryTextArea;
        public TableView queryResultTableView;
    }

    /**
     * メイン画面左上のデータベース構造のUI参照をまとめた構造体を取得する。<br>
     * @return メイン画面左上のデータベース構造のUI参照をまとめた構造体
     */
    public DbStructureParam getDbStructureParam();

    /**
     * メイン画面左下のテーブル構造のUI参照をまとめた構造体を取得する。<br>
     * @return メイン画面左下のテーブル構造のUI参照をまとめた構造体
     */
    public TableStructureTabParam getTableStructureTabParam();

    /**
     * メイン画面右のクエリ入力・結果一覧のUI参照をまとめた構造体を取得する。<br>
     * @return メイン画面右のクエリ入力・結果一覧のUI参照をまとめた構造体
     */
    public QueryParam getQueryParam();
}
