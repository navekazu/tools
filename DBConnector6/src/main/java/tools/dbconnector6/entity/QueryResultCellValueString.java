package tools.dbconnector6.entity;

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
}
