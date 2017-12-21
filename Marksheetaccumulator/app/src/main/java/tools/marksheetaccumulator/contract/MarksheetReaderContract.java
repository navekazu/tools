package tools.marksheetaccumulator.contract;

import android.provider.BaseColumns;

public final class MarksheetReaderContract extends BaseContract {
    private MarksheetReaderContract() {

    }

    public static String getCreateEntry() {
        return BaseContract.builder()
                .tableName(MarksheetEntry.TABLE_NAME)
                .primaryKey(MarksheetEntry._ID)
                .column(MarksheetEntry.COLUMN_NAME_MEMBER_ID, BaseContract.NUMBER_TYPE, true, null)
                .column(MarksheetEntry.COLUMN_NAME_TITLE, BaseContract.TEXT_TYPE, true, null)
                .column(MarksheetEntry.COLUMN_NAME_QUESTION_NUMBER, BaseContract.NUMBER_TYPE, true, null)
                .column(MarksheetEntry.COLUMN_NAME_QUESTION_OPTIONS, BaseContract.NUMBER_TYPE, true, null)
                .column(MarksheetEntry.COLUMN_NAME_OPTION_NUMBER, BaseContract.NUMBER_TYPE, true, null)
                .column(MarksheetEntry.COLUMN_NAME_CREATE_DATE, BaseContract.DATETIME_TYPE, true, "CURRENT_TIMESTAMP")
                .column(MarksheetEntry.COLUMN_NAME_UPDATE_DATE, BaseContract.DATETIME_TYPE, true, "CURRENT_TIMESTAMP")
                .build();
    }

    public static String getDropEntry() {
        return dropTable(MarksheetEntry.TABLE_NAME);
    }

    public static class MarksheetEntry implements BaseColumns {
        public static final String TABLE_NAME = "marksheet";
        public static final String COLUMN_NAME_MEMBER_ID = "member_id";                 // メンバーID
        public static final String COLUMN_NAME_TITLE = "title";                         // タイトル
        public static final String COLUMN_NAME_QUESTION_NUMBER = "question_number";     // 問題数
        public static final String COLUMN_NAME_QUESTION_OPTIONS = "question_options";   // 選択肢
        public static final String COLUMN_NAME_OPTION_NUMBER = "option_number";         // 選択肢数
        public static final String COLUMN_NAME_CREATE_DATE = "create_date";             // 作成日時
        public static final String COLUMN_NAME_UPDATE_DATE = "update_date";             // 更新日時
    }
}
