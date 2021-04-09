package is.hi.finnbogi_mobile.listAdapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import is.hi.finnbogi_mobile.R;

public class ShiftExchangeListAdapter extends ArrayAdapter<String> {

    private final Activity mContext;
    private final String[] mRole;
    private final String[] mDate;
    private final String[] mStatus;

    public ShiftExchangeListAdapter(Activity context, String[] role, String[] date, String[] status) {
        super(context, R.layout.shiftexchange_list, role);

        this.mContext = context;
        this.mRole = role;
        this.mDate = date;
        this.mStatus = status;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=mContext.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.shiftexchange_list, null,true);

        TextView nameText = (TextView) rowView.findViewById(R.id.shiftexchange_list_role);
        TextView dateText = (TextView) rowView.findViewById(R.id.shiftexchange_list_date);
        TextView statusText = (TextView) rowView.findViewById(R.id.shiftexchange_list_status);

        nameText.setText(mRole[position]);
        dateText.setText(mDate[position]);
        statusText.setText(mStatus[position]);

        return rowView;
    };

}
