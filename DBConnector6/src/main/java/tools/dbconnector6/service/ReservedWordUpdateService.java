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
    private List<ReservedWord> reservedWordList;

    private static final String[] PRESET_RESERVED_WORD = new String[]{
            "select", "from", "where", "group by", "order by", "asc", "desc", "having",
            "update", "set",
            "delete",
            "insert into", "values",
            "create", "drop", "table", "index",
    };

    public ReservedWordUpdateService(MainControllerInterface mainControllerInterface, List<ReservedWord> reservedWordList) {
        this.mainControllerInterface = mainControllerInterface;
        this.reservedWordList = reservedWordList;
    }


    @Override
    public void run(Task task) throws Exception {
        reservedWordList.clear();
        addSQLReservedWord();

        if (mainControllerInterface.getConnection()!=null) {
            mainControllerInterface.writeLog("Reserved word parsing...");
            DatabaseMetaData dmd = mainControllerInterface.getConnection().getMetaData();
            addMetadataReservedWord(ReservedWord.ReservedWordType.TABLE, dmd.getTables(null, null, null, null), "TABLE_NAME");
            addMetadataReservedWord(ReservedWord.ReservedWordType.COLUMN, dmd.getColumns(null, null, null, null), "COLUMN_NAME");
            mainControllerInterface.writeLog("Reserved word parsed.");
        }
    }

    @Override
    public void cancel() throws Exception {

    }

    private void addMetadataReservedWord(ReservedWord.ReservedWordType reservedWordType, ResultSet resultSet, String name) throws SQLException {
        Set<ReservedWord> set = new HashSet<>();
        while (resultSet.next()) {
            set.add(new ReservedWord(reservedWordType, resultSet.getString(name)));
        }
        reservedWordList.addAll(set);
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
