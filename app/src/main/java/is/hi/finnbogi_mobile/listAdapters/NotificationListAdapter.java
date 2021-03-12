package is.hi.finnbogi_mobile.listAdapters;

import is.hi.finnbogi_mobile.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NotificationListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] title;
    private final String[] dateTime;
    private final String[] message;

    public NotificationListAdapter(Activity context, String[] title,String[] dateTime, String[] message) {
        super(context, R.layout.notification_list, title);

        this.context=context;
        this.title=title;
        this.dateTime=dateTime;
        this.message=message;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.notification_list, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.notification_list_title);
        TextView dateTimeText = (TextView) rowView.findViewById(R.id.notification_list_dateTime);
        TextView messageText = (TextView) rowView.findViewById(R.id.notification_list_message);

        titleText.setText(title[position]);
        dateTimeText.setText(dateTime[position]);
        messageText.setText(message[position]);

        return rowView;

    };
}
