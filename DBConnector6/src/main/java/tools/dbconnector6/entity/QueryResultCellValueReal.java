package tools.dbconnector6.entity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class QueryResultCellValueReal extends QueryResultCellValue<BigDecimal> {
    private static final Format STANDARD_FORMAT = new DecimalFormat("#,##0.0");
    private static final Format EVIDENCE_MODE_FORMAT = new DecimalFormat("0.0");

    @Override
    protected Format getStandardFormat() {
        return STANDARD_FORMAT;
    }

    @Override
    protected Format getEvidenceModeFormat() {
        return EVIDENCE_MODE_FORMAT;
    }
}
