package tools.marksheetaccumulator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import tools.marksheetaccumulator.contract.BaseContract;
import tools.marksheetaccumulator.contract.MarksheetReaderContract;
import tools.marksheetaccumulator.contract.MemberReaderContract;
import tools.marksheetaccumulator.contract.QuestionReaderContract;

public class MarksheetDatabaseOpenHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
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
        db.execSQL(MarksheetReaderContract.getDropEntry());
        db.execSQL(MemberReaderContract.getDropEntry());
        db.execSQL(QuestionReaderContract.getDropEntry());
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
