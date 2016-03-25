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
            "select", "from", "where", "group by", "order by", "asc", "desc", "having",
            "update", "set",
            "delete",
            "insert into", "values",
            "create", "drop", "table", "index",
            "and", "or", "not", "is",
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

        if (mainControllerInterface.getConnection()!=null) {
            mainControllerInterface.writeLog("Reserved word parsing...");
            DatabaseMetaData dmd = mainControllerInterface.getConnection().getMetaData();

            try {
                // テーブルタイプの取得
                List<String> types = new ArrayList<>();
                try (ResultSet resultSet = dmd.getTableTypes()) {
                    types = getResultList(resultSet, "TABLE_TYPE");
                } catch(SQLException e){}

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
                for (String schema : schemas) {
                    mainControllerInterface.writeLog("Reserved word parsing... %s", schema);

                    // テーブル一覧
                    Set<ReservedWord> tables = new HashSet<>();
                    try (ResultSet resultSet = dmd.getTables(null, schema, "%", (String[]) types.toArray(new String[0]))) {
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
                }

                mainControllerInterface.writeLog("Reserved word parsed. table count:%,d, colimn count:%,d ", allTables.size(), allColumns.size());

            } catch(Exception e) {
                mainControllerInterface.writeLog(e);
            }
        }
    }

    @Override
    public void cancel() throws Exception {

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
    public void updateUIPreparation(Void uiParam) throws Exception {
    }

    @Override
    public void updateUI(Void uiParam) throws Exception {
    }
}
