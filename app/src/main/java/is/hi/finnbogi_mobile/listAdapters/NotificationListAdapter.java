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
    private final String[] message;

    /**
     * constructor
     * @param context NotificationListActivity
     * @param title list of strings representing notification titles
     * @param message list of strings representing notification messages
     */
    public NotificationListAdapter(Activity context, String[] title, String[] message) {
        super(context, R.layout.notification_list, title);

        this.context = context;
        this.title = title;
        this.message = message;
    }

    /**
     * How NotificationListActivity knows what to display
     * @param position
     * @param view
     * @param parent
     * @return
     */
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.notification_list, null,true);

        // find TextViews
        TextView titleText = (TextView) rowView.findViewById(R.id.notification_list_title);
        TextView messageText = (TextView) rowView.findViewById(R.id.notification_list_message);

        // set TextViews
        titleText.setText(title[position]);
        messageText.setText(message[position]);

        return rowView;
    };
}
