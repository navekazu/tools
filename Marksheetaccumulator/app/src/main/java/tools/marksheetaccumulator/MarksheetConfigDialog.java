package tools.marksheetaccumulator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.DialogFragment;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import org.w3c.dom.Text;

import tools.marksheetaccumulator.dao.MarksheetDao;
import tools.marksheetaccumulator.entity.MarksheetEntity;

public class MarksheetConfigDialog extends DialogFragment {
    private boolean positiveButtonClicked = false;
    private ConfigMode configMode = ConfigMode.NEW;

    public enum ConfigMode {
        NEW,
        EDIT,
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.dialog_marksheet_config, null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder .setView(dialogView)
        // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MarksheetEntity entity = new MarksheetEntity();
                        EditText editText;
                        Spinner spinner;

                        editText = (EditText)dialogView.findViewById(R.id.marksheetTitle);
                        entity.title = editText.getText().toString();

                        editText = (EditText)dialogView.findViewById(R.id.questionNumber);
                        entity.questionNumber = optionValue(editText.getText().toString(), 30);

                        spinner = (Spinner)dialogView.findViewById(R.id.questionOptions);
                        entity.questionOptions = QuestionOptions.getQuestionOptions(spinner.getSelectedItemId());

                        editText = (EditText)dialogView.findViewById(R.id.optionNumber);
                        entity.optionNumber = optionValue(editText.getText().toString(), 5);

                        MarksheetDatabaseOpenHelper dbHelper = MarksheetDatabaseOpenHelper.getInstance();
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        long insertedId = -1;
                        try {
                            db.beginTransaction();
                            MarksheetDao dao = new MarksheetDao(db);
                            insertedId = dao.insertMarksheet(entity);
                            db.setTransactionSuccessful();

                        } finally {
                            db.endTransaction();
                        }
                        if (insertedId!=-1) {
                            positiveButtonClicked = true;
                            MarksheetActivity marksheetActivity = (MarksheetActivity) getActivity();
                            marksheetActivity.initialMarksheet(insertedId);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        getActivity().finish();
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (!positiveButtonClicked && configMode==ConfigMode.NEW) {
            getActivity().finish();
        }
    }

    private int optionValue(String stringValue, int defaultValue) {
        if (stringValue==null || stringValue.trim().isEmpty()) {
            return defaultValue;
        }
        return Integer.parseInt(stringValue);
    }
}
