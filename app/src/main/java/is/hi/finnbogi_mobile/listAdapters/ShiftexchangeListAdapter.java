package is.hi.finnbogi_mobile.listAdapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import is.hi.finnbogi_mobile.R;

public class ShiftexchangeListAdapter extends ArrayAdapter<String> {

    private final Activity mContext;
    private final String[] mName;
    private final String[] mRole;
    private final String[] mDate;
    private final String[] mTime;
    private final int[] mStatus;

    public ShiftexchangeListAdapter(Activity context, String[] name, String[] role, String[] date, String[] time, int[] status) {
        super(context, R.layout.shiftexchange_list, name);

        this.mContext=context;
        this.mName=name;
        this.mRole=role;
        this.mDate=date;
        this.mTime=time;
        this.mStatus=status;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=mContext.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.shiftexchange_list, null,true);

        TextView nameText = (TextView) rowView.findViewById(R.id.shiftexchange_list_name);
        TextView roleText = (TextView) rowView.findViewById(R.id.shiftexchange_list_role);
        TextView dateText = (TextView) rowView.findViewById(R.id.shiftexchange_list_date);
        TextView timeText = (TextView) rowView.findViewById(R.id.shiftexchange_list_time);
        TextView statusText = (TextView) rowView.findViewById(R.id.shiftexchange_list_status);

        nameText.setText(mName[position]);
        roleText.setText(mRole[position]);
        dateText.setText(mDate[position]);
        timeText.setText(mTime[position]);

        if (mStatus[position] == 0) { statusText.setText(R.string.shiftexchange_list_status_0); }
        else { statusText.setText(R.string.shiftexchange_list_status_1); }

        return rowView;
    };

}
