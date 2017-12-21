package tools.marksheetaccumulator.dao;

import android.database.sqlite.SQLiteDatabase;

public abstract class BaseDao {
    protected SQLiteDatabase db;
    public BaseDao(SQLiteDatabase db) {
        this.db = db;
    }
}
