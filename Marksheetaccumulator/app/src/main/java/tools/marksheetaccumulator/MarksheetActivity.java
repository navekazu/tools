package tools.marksheetaccumulator;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import tools.marksheetaccumulator.dao.MarksheetDao;
import tools.marksheetaccumulator.entity.MarksheetEntity;
import tools.marksheetaccumulator.entity.QuestionEntity;

public class MarksheetActivity extends AppCompatActivity {

    private MarksheetEntity marksheetEntity;
    public static final long NEW_MARKSHEET_ID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marksheet);
        setTitle(getString(R.string.marksheet_avtivity_name));
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        Long marksheetId = intent.getLongExtra("marksheet_id", -1L);

        if (marksheetId==NEW_MARKSHEET_ID) {
            DialogFragment marksheetConfigDialog = new MarksheetConfigDialog();
            marksheetConfigDialog.show(getFragmentManager(), "MarksheetConfigDialog");
        } else {
            initialMarksheet(marksheetId);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void initialMarksheet(long id) {

        MarksheetDatabaseOpenHelper dbHelper = MarksheetDatabaseOpenHelper.getInstance();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.beginTransaction();
            MarksheetDao dao = new MarksheetDao(db);
            marksheetEntity = dao.getMarksheet(id);
            setTitle(marksheetEntity.title);

            TableLayout marksheetTable = (TableLayout) findViewById(R.id.questionTable);
            for (int i=0; i<marksheetEntity.questionNumber; i++) {
                MarksheetRow marksheetRow = new MarksheetRow(i, marksheetEntity.questionEntityMap.get(i));
                marksheetTable.addView(marksheetRow.createView(this), new TableLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

        } catch(Exception e) {
            e.printStackTrace();

        } finally {
            db.endTransaction();
        }
    }

    private class MarksheetRow {
        private int rowIndex;
        private QuestionEntity questionEntity;
        private TextView resultArea;
        private TextView[] choices;

        public MarksheetRow(int rowIndex, QuestionEntity questionEntity) {
            this.rowIndex = rowIndex;
            this.questionEntity = questionEntity;
        }

        public View createView(Context context) {
            TableRow row = new TableRow(context);
            row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

            TextView textView = new TextView(context);
            textView.setText(Integer.toString(rowIndex+1));
            row.addView(textView);

            resultArea = new TextView(context);
            resultArea.setText("  ");
            row.addView(resultArea);

            choices = new TextView[marksheetEntity.optionNumber];
            for (int i=0; i<choices.length; i++) {
                choices[i] = new TextView(context);
                choices[i].setText(marksheetEntity.questionOptions.getOptionValues()[i]);
                row.addView(choices[i]);
            }

            return row;
        }
    }
}
