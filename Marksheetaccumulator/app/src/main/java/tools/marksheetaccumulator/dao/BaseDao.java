package tools.marksheetaccumulator.dao;

import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class BaseDao {
    protected SQLiteDatabase db;
    public BaseDao(SQLiteDatabase db) {
        this.db = db;
    }
}
