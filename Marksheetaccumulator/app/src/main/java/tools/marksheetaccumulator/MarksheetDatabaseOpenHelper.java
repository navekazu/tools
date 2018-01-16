package tools.marksheetaccumulator;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import tools.marksheetaccumulator.contract.BaseContract;
import tools.marksheetaccumulator.contract.MarksheetReaderContract;
import tools.marksheetaccumulator.contract.MemberReaderContract;
import tools.marksheetaccumulator.contract.QuestionReaderContract;

public class MarksheetDatabaseOpenHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "MarksheetDatabase.db";

    private static MarksheetDatabaseOpenHelper helper = null;
    public synchronized static MarksheetDatabaseOpenHelper createInstance(Context context) {
        if (helper==null) {
            helper = new MarksheetDatabaseOpenHelper(context);
        }
        return getInstance();
    }
    public static MarksheetDatabaseOpenHelper getInstance() {
        return helper;
    }

    private MarksheetDatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MarksheetReaderContract.getCreateEntry());
        db.execSQL(MemberReaderContract.getCreateEntry());
        db.execSQL(QuestionReaderContract.getCreateEntry());
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            if (oldVersion==1) {
                // 1 -> 2: questionテーブル right_flagの削除、right_noの追加
                db.execSQL("alter table " + QuestionReaderContract.QuestionEntry.TABLE_NAME +
                        " rename to temp_" + QuestionReaderContract.QuestionEntry.TABLE_NAME);
                db.execSQL(QuestionReaderContract.getCreateEntry());
                db.execSQL("insert into " + QuestionReaderContract.QuestionEntry.TABLE_NAME +
                        " select _id, member_id, marksheet_id, question_no, choice, null " +
                        " from temp_" + QuestionReaderContract.QuestionEntry.TABLE_NAME);
                db.execSQL("drop table temp_" + QuestionReaderContract.QuestionEntry.TABLE_NAME);
                oldVersion++;
            }

            if (oldVersion==2) {
                // 2 -> 3: questionテーブル choiceをNULL不可からNULL許可に（未選択時はレコード削除ではなくNULLにする）
                db.execSQL("alter table " + QuestionReaderContract.QuestionEntry.TABLE_NAME +
                        " rename to temp_" + QuestionReaderContract.QuestionEntry.TABLE_NAME);
                db.execSQL(QuestionReaderContract.getCreateEntry());
                db.execSQL("insert into " + QuestionReaderContract.QuestionEntry.TABLE_NAME +
                        " select _id, member_id, marksheet_id, question_no, choice, right_no " +
                        " from temp_" + QuestionReaderContract.QuestionEntry.TABLE_NAME);
                db.execSQL("drop table temp_" + QuestionReaderContract.QuestionEntry.TABLE_NAME);
                oldVersion++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    private boolean existsColumn(SQLiteDatabase db, String table, String column) {
        String query = "PRAGMA table_info("+table+")";
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            String name = cursor.getString(1);
            if (column.equals(name)) {
                return true;
            }
        }

        return false;
    }
}
