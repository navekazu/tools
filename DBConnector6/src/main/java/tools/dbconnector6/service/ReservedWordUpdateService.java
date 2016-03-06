package tools.dbconnector6.service;

import tools.dbconnector6.BackgroundCallbackInterface;
import tools.dbconnector6.MainControllerInterface;
import tools.dbconnector6.entity.ReservedWord;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReservedWordUpdateService implements BackgroundCallbackInterface<Void, Void> {

    private MainControllerInterface mainControllerInterface;
    private List<ReservedWord> reservedWordList;

    public ReservedWordUpdateService(MainControllerInterface mainControllerInterface, List<ReservedWord> reservedWordList) {
        this.mainControllerInterface = mainControllerInterface;
        this.reservedWordList = reservedWordList;
    }


    @Override
    public void run() throws Exception {
        reservedWordList.clear();
        addSQLReservedWord();
        DatabaseMetaData dmd =mainControllerInterface.getConnection().getMetaData();
        addMetadataReservedWord(ReservedWord.ReservedWordType.TABLE, dmd.getTables(null,  null, null, null), "TABLE_NAME");
        addMetadataReservedWord(ReservedWord.ReservedWordType.COLUMN, dmd.getColumns(null, null, null, null), "COLUMN_NAME");
    }

    private void addMetadataReservedWord(ReservedWord.ReservedWordType reservedWordType, ResultSet resultSet, String name) throws SQLException {
        Set<ReservedWord> set = new HashSet<>();
        while (resultSet.next()) {
            set.add(new ReservedWord(reservedWordType, resultSet.getString(name)));
        }
        reservedWordList.addAll(set);
    }

    private void addSQLReservedWord() {
        reservedWordList.add(new ReservedWord(ReservedWord.ReservedWordType.SQL, "select"));
        reservedWordList.add(new ReservedWord(ReservedWord.ReservedWordType.SQL, "from"));
        reservedWordList.add(new ReservedWord(ReservedWord.ReservedWordType.SQL, "where"));
        reservedWordList.add(new ReservedWord(ReservedWord.ReservedWordType.SQL, "group by"));
        reservedWordList.add(new ReservedWord(ReservedWord.ReservedWordType.SQL, "order by"));
        reservedWordList.add(new ReservedWord(ReservedWord.ReservedWordType.SQL, "asc"));
        reservedWordList.add(new ReservedWord(ReservedWord.ReservedWordType.SQL, "desc"));
        reservedWordList.add(new ReservedWord(ReservedWord.ReservedWordType.SQL, "having"));

        reservedWordList.add(new ReservedWord(ReservedWord.ReservedWordType.SQL, "update"));
        reservedWordList.add(new ReservedWord(ReservedWord.ReservedWordType.SQL, "set"));

        reservedWordList.add(new ReservedWord(ReservedWord.ReservedWordType.SQL, "insert into"));
        reservedWordList.add(new ReservedWord(ReservedWord.ReservedWordType.SQL, "values"));

        reservedWordList.add(new ReservedWord(ReservedWord.ReservedWordType.SQL, "create"));
        reservedWordList.add(new ReservedWord(ReservedWord.ReservedWordType.SQL, "drop"));
        reservedWordList.add(new ReservedWord(ReservedWord.ReservedWordType.SQL, "table"));
        reservedWordList.add(new ReservedWord(ReservedWord.ReservedWordType.SQL, "index"));
    }

    @Override
    public void updateUIPreparation(Void uiParam) throws Exception {
    }

    @Override
    public void updateUI(Void uiParam) throws Exception {
    }
}
