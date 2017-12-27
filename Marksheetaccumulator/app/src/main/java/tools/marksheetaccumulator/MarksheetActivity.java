package tools.marksheetaccumulator;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
        private

        public MarksheetRow(int rowIndex, QuestionEntity questionEntity) {
            this.rowIndex = rowIndex;
            this.questionEntity = questionEntity;
        }

        public View createView(Context context) {
            TableRow row = new TableRow(context);
            TableLayout.LayoutParams layout = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
            layout.weight = 1.0f;
            row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

            row.addView(createTextView(context, Integer.toString(rowIndex+1), false, 0.0f));

            resultArea = createTextView(context, "　", false, 0.0f);
            row.addView(resultArea);

            choices = new TextView[marksheetEntity.optionNumber];
            for (int i=0; i<choices.length; i++) {
                final int choiceIndex = i;
                choices[i] = createTextView(context, marksheetEntity.questionOptions.getOptionValues()[i], true, 1.0f);
                choices[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        markClickEvent(choiceIndex);
                    }
                });
                row.addView(choices[i]);
            }

            return row;
        }
        private TextView createTextView(Context context, String text, boolean clickable, float weight) {
            TextView textView = new TextView(context);
            textView.setText(text);
            textView.setClickable(clickable);

            TableRow.LayoutParams layout = new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            );
            layout.weight = weight;

            layout.leftMargin = 3;
            layout.rightMargin = 3;
            layout.topMargin = 10;
            layout.bottomMargin = 10;

            layout.gravity = Gravity.CENTER;

            textView.setLayoutParams(layout);

            return textView;
        }
        private void markClickEvent(int index) {

        }
    }
}
