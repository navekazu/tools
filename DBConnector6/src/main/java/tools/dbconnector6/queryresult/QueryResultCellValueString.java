package tools.dbconnector6.queryresult;

import javafx.geometry.Pos;

import java.text.Format;

public class QueryResultCellValueString extends QueryResultCellValue<String> {

    @Override
    protected Format getStandardFormat() {
        return null;
    }

    @Override
    protected Format getEvidenceModeFormat() {
        return null;
    }

    @Override
    protected Pos getAlignment() {
        return Pos.CENTER_LEFT;
    }
}
