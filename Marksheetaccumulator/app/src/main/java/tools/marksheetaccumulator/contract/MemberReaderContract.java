package tools.marksheetaccumulator.contract;

import android.provider.BaseColumns;

public final class MemberReaderContract extends BaseContract {
    private MemberReaderContract() {

    }

    public static String getCreateEntry() {
        return BaseContract.builder()
                .tableName(MemberEntry.TABLE_NAME)
                .primaryKey(MemberEntry._ID)
                .column(MemberEntry.COLUMN_NAME_NAME, BaseContract.TEXT_TYPE, true, null)
                .build();
    }

    public static String getDropEntry() {
        return dropTable(MemberEntry.TABLE_NAME);
    }

    public static class MemberEntry implements BaseColumns {
        public static final String TABLE_NAME = "member";
        public static final String COLUMN_NAME_NAME = "name";                           // 名前
    }
}
