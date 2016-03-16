package tools.dbconnector6.queryresult;

import javafx.geometry.Pos;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.Format;

public abstract class QueryResultCellValue<V> {
    protected static final String NULL_PROMPT = "(null)";
    protected V value;

    public QueryResultCellValue build(V value) {
        this.value = value;
        return this;
    }

    @Override
    public String toString() {
        if (value==null) {
            return NULL_PROMPT;
        }
        return getFormattedString();
    }

    protected abstract Format getStandardFormat();
    protected abstract Format getEvidenceModeFormat();
    protected abstract Pos getAlignment();

    public boolean isNullValue() {
        return value==null;
    }

    public String getFormattedString() {
        return value==null? NULL_PROMPT: getValueString(getStandardFormat());
    }
    public String getEvidenceModeString() {
        return getValueString(getEvidenceModeFormat());
    }
    protected String getValueString(Format f) {
        return value==null? "": (f==null? value.toString(): f.format(value));
    }

    public static Pos getAlignment(int column, ResultSetMetaData meta) throws SQLException {
        QueryResultCellValue queryResultCellValue = QueryResultCellValue.createQueryResultCellValue(column, meta);
        return queryResultCellValue.getAlignment();
    }

    public static QueryResultCellValue createQueryResultCellValue(int column, ResultSetMetaData meta, ResultSet resultSet) throws SQLException {
        QueryResultCellValue queryResultCellValue = QueryResultCellValue.createQueryResultCellValue(column, meta);

        if (queryResultCellValue instanceof QueryResultCellValueNumber
            ||queryResultCellValue instanceof QueryResultCellValueReal) {
            queryResultCellValue.build(resultSet.getBigDecimal(column));
        } else if (queryResultCellValue instanceof QueryResultCellValueDate) {
            queryResultCellValue.build(resultSet.getDate(column));
        } else {
            queryResultCellValue.build(resultSet.getString(column));
        }

        return queryResultCellValue;

    }
    private static QueryResultCellValue createQueryResultCellValue(int column, ResultSetMetaData meta) throws SQLException {
        int type = meta.getColumnType(column);

        switch(type) {
            case Types.BIGINT:                  // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型BIGINTを識別します。
            case Types.DECIMAL:                 // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型DECIMALを識別します。
            case Types.DOUBLE:                  // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型DOUBLEを識別します。
            case Types.FLOAT:                   // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型FLOATを識別します。
            case Types.INTEGER:                 // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型INTEGERを識別します。
            case Types.NUMERIC:                 // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型NUMERICを識別します。
            case Types.REAL:                    // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型REALを識別します。
            case Types.TINYINT:                 // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型TINYINTを識別します。
                if (meta.getScale(column)==0) {
                    return new QueryResultCellValueNumber();
                }
                return new QueryResultCellValueReal();

            case Types.DATE:                    // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型DATEを識別します。
            case Types.TIME:                    // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型TIMEを識別します。
            case Types.TIME_WITH_TIMEZONE:      // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型TIME WITH TIMEZONEを識別します。
            case Types.TIMESTAMP:               // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型TIMESTAMPを識別します。
            case Types.TIMESTAMP_WITH_TIMEZONE: // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型TIMESTAMP WITH TIMEZONEを識別します。
                return new QueryResultCellValueDate();

/*
            case Types.ARRAY:                   // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型ARRAYを識別します。
            case Types.BINARY:                  // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型BINARYを識別します。
            case Types.BIT:                     // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型BITを識別します。
            case Types.BLOB:                    // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型BLOBを識別します。
            case Types.BOOLEAN:                 // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型BOOLEANを識別します。
            case Types.CHAR:                    // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型CHARを識別します。
            case Types.CLOB:                    // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型CLOBを識別します。
            case Types.DATALINK:                // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型DATALINKを識別します。
            case Types.DISTINCT:                // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型DISTINCTを識別します。
            case Types.JAVA_OBJECT:             // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型JAVA_OBJECTを識別します。
            case Types.LONGNVARCHAR:            // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型LONGNVARCHARを識別します。
            case Types.LONGVARBINARY:           // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型LONGVARBINARYを識別します。
            case Types.LONGVARCHAR:             // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型LONGVARCHARを識別します。
            case Types.NCHAR:                   // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型NCHARを識別します。
            case Types.NCLOB:                   // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型NCLOBを識別します。
            case Types.NULL:                    // Javaプログラミング言語の定数で、ジェネリックSQL値NULLを識別します。
            case Types.NVARCHAR:                // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型NVARCHARを識別します。
            case Types.OTHER:                   // SQL型がデータベース固有のものであり、getObjectメソッドとsetObjectメソッドを介してアクセスできるJavaオブジェクトにマッピングされることを示す、Javaプログラミング言語の定数です。
            case Types.REF:                     // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型REFを識別します。
            case Types.REF_CURSOR:              // 汎用SQL型REF CURSORを識別するJavaプログラミング言語の定数(型コードとも呼ばれる)。
            case Types.ROWID:                   // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型ROWIDを識別します。
            case Types.SMALLINT:                // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型SMALLINTを識別します。
            case Types.SQLXML:                  // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型XMLを識別します。
            case Types.STRUCT:                  // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型STRUCTを識別します。
            case Types.VARBINARY:               // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型VARBINARYを識別します。
            case Types.VARCHAR:                 // Javaプログラミング言語の定数で、型コードとも呼ばれ、汎用SQL型VARCHARを識別します。
*/
        }
        return new QueryResultCellValueString();
    }
}
