package tools.dbconnector6.queryresult;

import javafx.geometry.Pos;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class QueryResultCellValueDate extends QueryResultCellValue<Date> {
    private static final Format STANDARD_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
    private static final Format EVIDENCE_MODE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

    @Override
    protected Format getStandardFormat() {
        return STANDARD_FORMAT;
    }

    @Override
    protected Format getEvidenceModeFormat() {
        return EVIDENCE_MODE_FORMAT;
    }

    @Override
    protected Pos getAlignment() {
        return Pos.CENTER_LEFT;
    }
}
