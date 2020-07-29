package nerd.tuxmobil.fahrplan.congress.alarms;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cursoradapter.widget.CursorAdapter;

import info.metadude.android.eventfahrplan.database.contract.FahrplanContract.AlarmsTable;
import info.metadude.android.eventfahrplan.database.contract.FahrplanContract.AlarmsTable.Columns;
import nerd.tuxmobil.fahrplan.congress.R;

public class AlarmCursorAdapter extends CursorAdapter {

    protected final LayoutInflater mInflater;

    public AlarmCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.alarmTimeInMin = view.findViewById(R.id.alarm_badge);
            holder.title = view.findViewById(R.id.alarm_title);
            holder.time = view.findViewById(R.id.alarm_start_time);
            view.setTag(holder);
        }
        int alarmTimeInMin = cursor.getInt(cursor.getColumnIndex(Columns.ALARM_TIME_IN_MIN));
        String title = cursor.getString(cursor.getColumnIndex(Columns.SESSION_TITLE));
        String timeText = cursor.getString(cursor.getColumnIndex(Columns.TIME_TEXT));
        if (alarmTimeInMin == AlarmsTable.Defaults.ALARM_TIME_IN_MIN_DEFAULT) {
            holder.alarmTimeInMin.setText("?");
        } else {
            holder.alarmTimeInMin.setText("" + alarmTimeInMin);
        }
        holder.title.setText(title);
        holder.time.setText(timeText);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.alarm_list_item, parent, false);
    }

    private static class ViewHolder {

        TextView alarmTimeInMin;

        TextView title;

        TextView time;
    }

}
