package tools.marksheetaccumulator.contract;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseContract {
    protected static final String TEXT_TYPE = " TEXT";
    protected static final String NUMBER_TYPE = " NUMBER";
    protected static final String DATE_TYPE = " DATE";
    protected static final String DATETIME_TYPE = " DATETIME";

    protected static class Column {
        public String colName;
        public String colType;
        public boolean notNull;
        public String defaultValue;
        public Column(String colName, String colType, boolean notNull, String defaultValue) {
            this.colName = colName;
            this.colType = colType;
            this.notNull = notNull;
            this.defaultValue = defaultValue;
        }
    }

    protected static String createTable(String tableName, String pkName, Column... columns) {
        StringBuilder sb = new StringBuilder();

        sb.append("CREATE TABLE " + tableName + " (");
        sb.append(pkName + " INTEGER PRIMARY KEY,");

        for (Column col: columns) {
            sb.append(col.colName + " "+col.colType+(col.notNull? "NOT NULL ": "NULL ")+",");
        }
        sb.deleteCharAt(sb.lastIndexOf(","));

        sb.append(")");

        return sb.toString();
    }

    protected static TableBuilder builder() {
        return new TableBuilder();

    }
    protected static class TableBuilder {
        private String tableName;
        private String primaryKey;
        private List<Column> columnList = new ArrayList<>();
        private String foreignKeyName;
        private String foreignKeyColumn;
        private String foreignKeyReferenceTable;
        private String foreignKeyReferenceColumn;
        private boolean foreignKeyDeleteCascade;
        private boolean foreignKeyUpdateCascade;

        TableBuilder tableName(String tableName) {
            this.tableName = tableName;
            return this;
        }
        TableBuilder primaryKey(String primaryKey) {
            this.primaryKey = primaryKey;
            return this;
        }
        TableBuilder column(String colName, String colType, boolean notNull, String defaultValue) {
            this.columnList.add(new Column(colName, colType, notNull, defaultValue));
            return this;
        }
        TableBuilder foreignKey(String name, String column, String referenceTable, String referenceColumn, boolean deleteCascade, boolean updateCascade) {
            this.foreignKeyName = name;
            this.foreignKeyColumn = column;
            this.foreignKeyReferenceTable = referenceTable;
            this.foreignKeyReferenceColumn = referenceColumn;
            this.foreignKeyDeleteCascade = deleteCascade;
            this.foreignKeyUpdateCascade = updateCascade;
            return this;
        }

        String build() {
            StringBuilder sb = new StringBuilder();

            sb.append("CREATE TABLE " + tableName + " (");
            sb.append(primaryKey + " INTEGER PRIMARY KEY,");

            for (Column col: columnList) {
                sb.append(String.format("%s %s %s %s,",
                        col.colName,
                        col.colType,
                        (col.notNull? "NOT NULL": "NULL"),
                        (col.defaultValue!=null? "DEFAULT "+col.defaultValue: "")));
            }

            if (foreignKeyName!=null) {
                sb.append("CONSTRAINT " + foreignKeyName + " ");
                sb.append("FOREIGN KEY (" + foreignKeyColumn + ") ");
                sb.append("REFERENCES " + foreignKeyReferenceTable + " (" + foreignKeyReferenceColumn + ") ");
                sb.append(foreignKeyDeleteCascade? "ON DELETE CASCADE ": "");
                sb.append(foreignKeyUpdateCascade? "ON UPDATE CASCADE ": "");
                sb.append(",");
            }

            sb.deleteCharAt(sb.lastIndexOf(","));

            sb.append(")");

            return sb.toString();
        }
    }

    protected static String dropTable(String tableName) {
        return "DROP TABLE IF EXISTS "+tableName;
    }
}
