package tools.marksheetaccumulator;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import tools.marksheetaccumulator.entity.MarksheetEntity;

public class MarksheetAdapter extends ArrayAdapter<MarksheetEntity> {
//    private Context context;
//    private MarksheetEntity[] values;

    public MarksheetAdapter(Context context, List<MarksheetEntity> values) {
        super(context, R.layout.main_row, values);
//        this.context = context;
//        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = getContext();
        MarksheetEntity value = getItem(position);
        SimpleDateFormat sdf = new SimpleDateFormat(Constant.DATE_PATTERN);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.main_row, parent, false);
        TextView mainRowTitle = (TextView) rowView.findViewById(R.id.mainRowTitle);
        TextView mainRowUpdateDate = (TextView) rowView.findViewById(R.id.mainRowUpdateDate);

        mainRowTitle.setTextAppearance(getContext(), R.style.TextAppearance_AppCompat_Medium);

        mainRowTitle.setText(value.title);
        mainRowUpdateDate.setText(sdf.format(value.updateDate));

        mainRowTitle.setTextColor(Color.BLACK);
        mainRowUpdateDate.setTextColor(Color.BLACK);

        return rowView;
    }
}
