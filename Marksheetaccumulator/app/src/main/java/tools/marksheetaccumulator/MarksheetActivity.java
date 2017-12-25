package tools.marksheetaccumulator;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;

import org.w3c.dom.Text;

import tools.marksheetaccumulator.dao.MarksheetDao;
import tools.marksheetaccumulator.entity.MarksheetEntity;

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
                LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.marksheet_row, null);
                marksheetTable.addView(rowView, new TableLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

        } catch(Exception e) {
            e.printStackTrace();

        } finally {
            db.endTransaction();
        }

    }
}
