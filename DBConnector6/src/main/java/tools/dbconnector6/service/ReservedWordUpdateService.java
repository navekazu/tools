package tools.dbconnector6.service;

import javafx.concurrent.Task;
import tools.dbconnector6.MainControllerInterface;
import tools.dbconnector6.entity.ReservedWord;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * データベース接続時、データベース内のテーブル名とカラム名を走査し、予約語として登録するサービス。
 * 走査データは入力補完としてクエリ入力時にポップアップ表示する。<br>
 */
public class ReservedWordUpdateService implements BackgroundServiceInterface<Void, Void> {
    // メイン画面へのアクセス用インターフェース
    private MainControllerInterface mainControllerInterface;

    // 走査した予約語を格納する参照
    private Set<ReservedWord> reservedWordList;

    // SQL予約語
    // 初期化時に強制的に予約語として登録する
    private static final String[] PRESET_RESERVED_WORD = new String[]{
            "select", "distinct", "from", "where", "group", "order", "by", "asc", "desc", "having",
            "insert", "into", "values",
            "update", "set",
            "delete", "truncate",
            "create", "alter", "drop",
            "table", "unique", "index",
            "and", "or", "not", "is", "between", "in", "like", "exists",
    };

    /**
     * コンストラクタ。<br>
     * @param mainControllerInterface メイン画面へのアクセス用インターフェース
     * @param reservedWordList 予約語の格納先
     */
    public ReservedWordUpdateService(MainControllerInterface mainControllerInterface, Set<ReservedWord> reservedWordList) {
        this.mainControllerInterface = mainControllerInterface;
        this.reservedWordList = reservedWordList;
    }

    /**
     * バックグラウンドで実行する処理を実装する。<br>
     * スキーマ一覧を取得し、スキーマごとにスレッドを立てて、スキーマ単位にテーブル名とカラム名を解析する。
     * @param task 生成したバックグラウンド実行を行うTaskのインスタンス
     * @throws Exception 何らかのエラーが発生し処理を中断する場合
     */
    @Override
    public void run(Task task) throws Exception {
        synchronized (reservedWordList) {
            reservedWordList.clear();
            addSQLReservedWord();
        }

        if (!mainControllerInterface.isConnectWithoutOutputMessage()) {
            return;
        }

        mainControllerInterface.writeLog("Reserved word parsing...");
        DatabaseMetaData dmd = mainControllerInterface.getConnection().getMetaData();

        // テーブルタイプの取得
        List<String> types = new ArrayList<>();
        try (ResultSet resultSet = dmd.getTableTypes()) {
            types = getResultList(resultSet, "TABLE_TYPE");
        } catch(SQLException e){
            // テーブルタイプが取得できなければ解析は不可能
            mainControllerInterface.writeLog("Failed reserved word parsing.");
            return ;
        }

        // スキーマの取得
        List<String> schemas = new ArrayList<>();
        try (ResultSet resultSet = dmd.getSchemas()) {
            schemas = getResultList(resultSet, "TABLE_SCHEM");
        } catch(SQLException e){}
        if (schemas.isEmpty()) {
            schemas.add("");
        }

        final List<String> finalTypes = new ArrayList<>(types);

        // スキーマごとにスレッドを立てて、スキーマ単位にテーブル名とカラム名を解析
        // ToDo: 同時実行スレッド上限(32bit Windowsで2048本)を考慮して実装する必要がある
        // ToDo: 全スレッドが終了したことをユーザーに知らせる仕組みが必要
        schemas.parallelStream().forEach(schema -> {
            mainControllerInterface.writeLog("Reserved word parsing... (%s)", schema);

            // テーブル一覧
            Set<ReservedWord> tables = new HashSet<>();
            try (ResultSet resultSet = dmd.getTables(null, schema, "%", (String[]) finalTypes.toArray(new String[0]))) {
                tables = getMetadataReservedWord(ReservedWord.ReservedWordType.TABLE, resultSet, "TABLE_NAME");
                synchronized (reservedWordList) {
                    reservedWordList.addAll(tables);
                }
            } catch(SQLException e){}

            // カラム一覧
            for (ReservedWord reservedWord : tables) {
                Set<ReservedWord> columns = new HashSet<>();
                try (ResultSet resultSet = dmd.getColumns(null, schema, reservedWord.getWord(), null)) {
                    columns = getMetadataReservedWord(ReservedWord.ReservedWordType.COLUMN, resultSet, "COLUMN_NAME");
                    synchronized (reservedWordList) {
                        reservedWordList.addAll(columns);
                    }
                } catch(SQLException e){}
            }

            mainControllerInterface.writeLog("Reserved word parsed. (%s)", schema);
        });
    }

    /**
     * 更新の前処理。<br>
     * runメソッド内で立ち上げたスレッドで更新を行うので、ここでは何も行わない。<br>
     * @param prepareUpdateParam 前処理に必要なパラメータ
     * @throws Exception 何らかのエラーが発生した場合
     */
    @Override
    public void prepareUpdate(final Void prepareUpdateParam) throws Exception {
    }

    /**
     * 更新処理。<br>
     * runメソッド内で立ち上げたスレッドで更新を行うので、ここでは何も行わない。<br>
     * @param updateParam 更新処理に必要なパラメータ
     * @throws Exception 何らかのエラーが発生した場合
     */
    @Override
    public void update(final Void updateParam) throws Exception {
    }

    /**
     * バックグラウンド実行をキャンセルするたびに呼び出される。<br>
     */
    @Override
    public void cancel() {
    }

    /**
     * Serviceの状態がCANCELLED状態に遷移するたびに呼び出される。<br>
     */
    @Override
    public void cancelled() {
    }

    /**
     * Serviceの状態がFAILED状態に遷移するたびに呼び出される。<br>
     */
    @Override
    public void failed() {
    }

    /**
     * もし実行中ではない時にキャンセル要求があった場合のメッセージ。<br>
     * @return メッセージ
     */
    @Override
    public String getNotRunningMessage() {
        return "";
    }

    /**
     * ResultSetから指定されたカラムの値を取得し、リストで返す。<br>
     * @param resultSet 取得元のResultSet
     * @param name 取得するカラム
     * @return 指定されたカラムの値のリスト
     * @throws SQLException ResultSetからの取得に失敗した場合
     */
    private List<String> getResultList(ResultSet resultSet, String name) throws SQLException {
        List<String> list = new ArrayList<>();
        while(resultSet.next()) {
            list.add(resultSet.getString(name));
        }
        return list;
    }

    /**
     * ResultSetから指定されたカラムの値を取得し、ReservedWordのセットで返す。<br>
     * セットなので重複する値はない。<br>
     * @param reservedWordType ReservedWordに設定する予約語のタイプ
     * @param resultSet 取得元のResultSet
     * @param name 取得するカラム
     * @return 指定されたカラムの値で初期化したReservedWordのセット
     * @throws SQLException ResultSetからの取得に失敗した場合
     */
    private Set<ReservedWord> getMetadataReservedWord(ReservedWord.ReservedWordType reservedWordType, ResultSet resultSet, String name) throws SQLException {
        Set<ReservedWord> set = new HashSet<>();
        while (resultSet.next()) {
            set.add(new ReservedWord(reservedWordType, resultSet.getString(name)));
        }
        return set;
    }

    // reservedWordListにSQL予約語を追加する
    private void addSQLReservedWord() {
        Arrays.stream(PRESET_RESERVED_WORD).
                forEach(word -> reservedWordList.add(new ReservedWord(ReservedWord.ReservedWordType.SQL, word)));
    }
}
