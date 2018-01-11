package tools.marksheetaccumulator.contract;

import android.provider.BaseColumns;

public final class QuestionReaderContract extends BaseContract {
    private QuestionReaderContract() {

    }

    public static String getCreateEntry() {
        return BaseContract.builder()
                .tableName(QuestionEntry.TABLE_NAME)
                .primaryKey(QuestionEntry._ID)
                .column(QuestionEntry.COLUMN_NAME_MEMBER_ID, BaseContract.NUMBER_TYPE, true, null)
                .column(QuestionEntry.COLUMN_NAME_MAKRKSHEET_ID, BaseContract.NUMBER_TYPE, true, null)
                .column(QuestionEntry.COLUMN_NAME_QUESTION_NO, BaseContract.NUMBER_TYPE, true, null)
                .column(QuestionEntry.COLUMN_NAME_CHOICE, BaseContract.NUMBER_TYPE, true, null)
//                .column(QuestionEntry.COLUMN_NAME_RIGHT_FLAG, BaseContract.NUMBER_TYPE, true, null)       // ver2:drop
                .column(QuestionEntry.COLUMN_NAME_RIGHT_NO, BaseContract.NUMBER_TYPE, false, null)           // ver2:add
                .foreignKey("FK_"+QuestionEntry.TABLE_NAME,
                        QuestionEntry.COLUMN_NAME_MAKRKSHEET_ID,
                        MarksheetReaderContract.MarksheetEntry.TABLE_NAME,
                        MarksheetReaderContract.MarksheetEntry._ID,
                        true, true)
                .build();
    }

    public static String getDropEntry() {
        return dropTable(QuestionEntry.TABLE_NAME);
    }

    public static class QuestionEntry implements BaseColumns {
        public static final String TABLE_NAME = "question";
        public static final String COLUMN_NAME_MEMBER_ID = "member_id";                 // メンバーID
        public static final String COLUMN_NAME_MAKRKSHEET_ID = "marksheet_id";          // マークシートID
        public static final String COLUMN_NAME_QUESTION_NO = "question_no";             // 質問番号
        public static final String COLUMN_NAME_CHOICE = "choice";                       // 選択肢
//        public static final String COLUMN_NAME_RIGHT_FLAG = "right_flag";               // 正解             // ver2:drop
        public static final String COLUMN_NAME_RIGHT_NO = "right_no";                   // 正解               // ver2:add
    }
}
