package tools.dbconnector6.service;

import javafx.concurrent.Task;
import tools.dbconnector6.BackgroundServiceInterface;
import tools.dbconnector6.MainControllerInterface;
import tools.dbconnector6.entity.ReservedWord;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ReservedWordUpdateService implements BackgroundServiceInterface<Void, Void> {

    private MainControllerInterface mainControllerInterface;
    private Set<ReservedWord> reservedWordList;

    private static final String[] PRESET_RESERVED_WORD = new String[]{
            "select", "distinct", "from", "where", "group", "order", "by", "asc", "desc", "having",
            "insert", "into", "values",
            "update", "set",
            "delete", "truncate",
            "create", "alter", "drop",
            "table", "unique", "index",
            "and", "or", "not", "is", "between", "in", "like", "exists",
    };

    public ReservedWordUpdateService(MainControllerInterface mainControllerInterface, Set<ReservedWord> reservedWordList) {
        this.mainControllerInterface = mainControllerInterface;
        this.reservedWordList = reservedWordList;
    }


    @Override
    public void run(Task task) throws Exception {
        synchronized (reservedWordList) {
            reservedWordList.clear();
            addSQLReservedWord();
        }

        if (!mainControllerInterface.isConnectWithoutMessage()) {
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

        Set<ReservedWord> allTables = new HashSet<>();
        Set<ReservedWord> allColumns = new HashSet<>();
        final List<String> finalTypes = new ArrayList<>(types);

        // スキーマごとにスレッドを立てて、スキーマ単位にテーブル名と絡む姪を解析
        // ToDo: 同時実行スレッド上限(32bit Windowsで2048本)を考慮して実装する必要がある
        // ToDo: 全スレッドが終了したことをユーザーに知らせる仕組みが必要
        schemas.parallelStream().forEach(schema -> {
            mainControllerInterface.writeLog("Reserved word parsing... %s", schema);

            // テーブル一覧
            Set<ReservedWord> tables = new HashSet<>();
            try (ResultSet resultSet = dmd.getTables(null, schema, "%", (String[]) finalTypes.toArray(new String[0]))) {
                tables = getMetadataReservedWord(ReservedWord.ReservedWordType.TABLE, resultSet, "TABLE_NAME");
                allTables.addAll(tables);
                synchronized (reservedWordList) {
                    reservedWordList.addAll(tables);
                }
            } catch(SQLException e){}

            // カラム一覧
            for (ReservedWord reservedWord : tables) {
                Set<ReservedWord> columns = new HashSet<>();
                try (ResultSet resultSet = dmd.getColumns(null, schema, reservedWord.getWord(), null)) {
                    columns = getMetadataReservedWord(ReservedWord.ReservedWordType.COLUMN, resultSet, "COLUMN_NAME");
                    allColumns.addAll(columns);
                    synchronized (reservedWordList) {
                        reservedWordList.addAll(columns);
                    }
                } catch(SQLException e){}
            }
        });
    }

    @Override
    public void cancel() throws Exception {

    }

    @Override
    public void cancelled() {

    }

    @Override
    public void failed() {

    }

    @Override
    public String getNotRunningMessage() {
        return "";
    }

    private List<String> getResultList(ResultSet resultSet, String name) throws SQLException {
        List<String> list = new ArrayList<>();
        while(resultSet.next()) {
            list.add(resultSet.getString(name));
        }
        return list;
    }

    private Set<ReservedWord> getMetadataReservedWord(ReservedWord.ReservedWordType reservedWordType, ResultSet resultSet, String name) throws SQLException {
        Set<ReservedWord> set = new HashSet<>();
        while (resultSet.next()) {
            set.add(new ReservedWord(reservedWordType, resultSet.getString(name)));
        }
        return set;
    }

    private void addSQLReservedWord() {
        Arrays.stream(PRESET_RESERVED_WORD).forEach(word -> reservedWordList.add(new ReservedWord(ReservedWord.ReservedWordType.SQL, word)));
    }

    @Override
    public void updateUIPreparation(final Void uiParam) throws Exception {
    }

    @Override
    public void updateUI(final Void uiParam) throws Exception {
    }
}
