package tools.marksheetaccumulator;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
                MarksheetRow marksheetRow = new MarksheetRow(i);
                marksheetTable.addView(marksheetRow.createView(this), new TableLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

        } catch(Exception e) {
            e.printStackTrace();

        } finally {
            db.endTransaction();
        }
    }

    private static final int UNSELECTED_COLOR = Color.argb(0, 0, 0, 0);   // 透明
    private static final int SELECTED_COLOR = Color.argb(20, 255, 0, 0);   // 若干赤（透明度のある赤）

    private class MarksheetRow {
        private int rowIndex;
        private TextView resultArea;
        private TextView[] choices;
        private int selectedIndex = -1;

        public MarksheetRow(int rowIndex) {
            this.rowIndex = rowIndex;
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
            textView.setBackgroundColor(UNSELECTED_COLOR);

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
            if (selectedIndex==index) {
                // 選択中のものをクリックしたら未選択に
                setSelectedIndex(-1);
                return ;
            }
            setSelectedIndex(index);
        }

        public void setSelectedIndex(int index) {
            // UI更新
            this.selectedIndex = index;

            for (TextView text: choices) {
                text.setBackgroundColor(UNSELECTED_COLOR);
            }

            if (index!=-1) {
                choices[index].setBackgroundColor(SELECTED_COLOR);
            }

            // DB更新
            MarksheetDatabaseOpenHelper dbHelper = MarksheetDatabaseOpenHelper.getInstance();
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            try {
                db.beginTransaction();
                MarksheetDao dao = new MarksheetDao(db);

                if (index == -1) {
                    dao.deleteQuestion(rowIndex);
                    marksheetEntity.questionEntityMap.remove(rowIndex);

                } else {
                    QuestionEntity questionEntity = marksheetEntity.questionEntityMap.get(rowIndex);

                    if (questionEntity==null) {
                        questionEntity = new QuestionEntity();
                        questionEntity.questionNo = rowIndex;
                        questionEntity.memberId = marksheetEntity.memberId;
                        questionEntity.marksheetId = marksheetEntity.id;
                        questionEntity.rightFlag = false;
                    }

                    questionEntity.choice = index;

                    // 更新件数0ならinsert
                    if (dao.updateQuestion(questionEntity)==0) {
                        dao.insertQuestion(questionEntity);
                    }

                }


            } catch(Exception e) {
                e.printStackTrace();

            } finally {
                db.endTransaction();
            }
        }
    }
}
