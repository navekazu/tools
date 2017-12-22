package tools.marksheetaccumulator.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tools.marksheetaccumulator.MarksheetDatabaseOpenHelper;
import tools.marksheetaccumulator.QuestionOptions;
import tools.marksheetaccumulator.contract.MarksheetReaderContract;
import tools.marksheetaccumulator.contract.QuestionReaderContract;
import tools.marksheetaccumulator.entity.MarksheetEntity;
import tools.marksheetaccumulator.entity.QuestionEntity;

public class MarksheetDao extends BaseDao {
    public MarksheetDao(SQLiteDatabase db) {
        super(db);
    }

    public long insertMarksheet(MarksheetEntity marksheetEntity) {
        ContentValues values = new ContentValues();
        long memberId = 1;
        long marksheetId;

        values.put(MarksheetReaderContract.MarksheetEntry.COLUMN_NAME_MEMBER_ID, memberId );                        // メンバーID
        values.put(MarksheetReaderContract.MarksheetEntry.COLUMN_NAME_TITLE, marksheetEntity.title);                // タイトル
        values.put(MarksheetReaderContract.MarksheetEntry.COLUMN_NAME_QUESTION_NUMBER, marksheetEntity.questionNumber);     // 問題数
        values.put(MarksheetReaderContract.MarksheetEntry.COLUMN_NAME_QUESTION_OPTIONS, marksheetEntity.questionOptions.getValue());   // 選択肢
        values.put(MarksheetReaderContract.MarksheetEntry.COLUMN_NAME_OPTION_NUMBER, marksheetEntity.optionNumber); // 選択肢数
        values.put(MarksheetReaderContract.MarksheetEntry.COLUMN_NAME_CREATE_DATE, System.currentTimeMillis());     // 作成日時
        values.put(MarksheetReaderContract.MarksheetEntry.COLUMN_NAME_UPDATE_DATE, System.currentTimeMillis());     // 更新日時

        marksheetId = db.insert(MarksheetReaderContract.MarksheetEntry.TABLE_NAME, null, values);

        for (int key: marksheetEntity.questionEntityMap.keySet()) {
            QuestionEntity questionEntity = marksheetEntity.questionEntityMap.get(key);
            questionEntity.questionNo = key;

            if (questionEntity.choice==null) {
                continue;
            }

            values = new ContentValues();
            values.put(QuestionReaderContract.QuestionEntry.COLUMN_NAME_MEMBER_ID, memberId );                      // メンバーID
            values.put(QuestionReaderContract.QuestionEntry.COLUMN_NAME_MAKRKSHEET_ID, marksheetId);                // マークシートID
            values.put(QuestionReaderContract.QuestionEntry.COLUMN_NAME_QUESTION_NO, questionEntity.questionNo);    // 質問番号
            values.put(QuestionReaderContract.QuestionEntry.COLUMN_NAME_CHOICE, questionEntity.choice);             // 選択肢
            values.put(QuestionReaderContract.QuestionEntry.COLUMN_NAME_RIGHT_FLAG, questionEntity.rightFlag);      // 正解
            questionEntity.id = db.insert(QuestionReaderContract.QuestionEntry.TABLE_NAME, null, values);
        }

        return marksheetId;
    }

    public MarksheetEntity getMarksheet(long id) {
        Cursor cursor = db.query(MarksheetReaderContract.MarksheetEntry.TABLE_NAME
                , null                              // 結果カラムリスト（nullはすべてのカラム）
                , "_id = ?"                         // 条件
                , new String[]{Long.toString(id)}   // 条件値
                , null                              // group by
                , null                              // having
                , null);                            // order by

        if (!cursor.moveToNext()) {
            throw new IllegalArgumentException();
        }

        MarksheetEntity entity = new MarksheetEntity();

        entity.id = cursor.getLong(0);
        entity.memberId = cursor.getLong(1);
        entity.title = cursor.getString(2);
        entity.questionNumber = cursor.getInt(3);
        entity.questionOptions = QuestionOptions.getQuestionOptions(cursor.getInt(4));
        entity.optionNumber = cursor.getInt(5);
        entity.createDate = new Date(cursor.getLong(6));
        entity.updateDate = new Date(cursor.getLong(7));


        cursor = db.query(QuestionReaderContract.QuestionEntry.TABLE_NAME
                , null                              // 結果カラムリスト（nullはすべてのカラム）
                , "marksheet_id = ?"                // 条件
                , new String[]{Long.toString(id)}   // 条件値
                , null                              // group by
                , null                              // having
                , "question_no asc");               // order by

        while (cursor.moveToNext()) {
            QuestionEntity questionEntity = new QuestionEntity();

            questionEntity.id = cursor.getLong(0);
            questionEntity.memberId = cursor.getLong(1);
            questionEntity.marksheetId = cursor.getLong(2);
            questionEntity.questionNo = cursor.getInt(3);
            questionEntity.choice = cursor.getInt(4);
            questionEntity.rightFlag = cursor.getInt(5)==1;

            entity.questionEntityMap.put(questionEntity.questionNo, questionEntity);
        }

        return entity;
    }

    public List<MarksheetEntity> getMarksheetList() {
        List<MarksheetEntity> list = new ArrayList<>();

        Cursor cursor = db.query(MarksheetReaderContract.MarksheetEntry.TABLE_NAME
                , null                              // 結果カラムリスト（nullはすべてのカラム）
                , null                              // 条件
                , null                              // 条件値
                , null                              // group by
                , null                              // having
                , "update_date desc");              // order by

        while (cursor.moveToNext()) {
            MarksheetEntity entity = new MarksheetEntity();

            entity.id = cursor.getLong(0);
            entity.memberId = cursor.getLong(1);
            entity.title = cursor.getString(2);
            entity.questionNumber = cursor.getInt(3);
            entity.questionOptions = QuestionOptions.getQuestionOptions(cursor.getInt(4));
            entity.optionNumber = cursor.getInt(5);
            entity.createDate = new Date(cursor.getLong(6));
            entity.updateDate = new Date(cursor.getLong(7));

            list.add(entity);
        }

        return list;
    }
}
