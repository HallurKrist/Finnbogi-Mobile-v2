package is.hi.finnbogi_mobile.listAdapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import is.hi.finnbogi_mobile.R;

public class UserListAdapter extends ArrayAdapter<String> {

    private final Activity mContext;
    private final String[] mName;
    private final String[] mRole;

    public UserListAdapter(Activity context, String[] name, String[] role) {
        super(context, R.layout.user_list, name);

        this.mContext=context;
        this.mName=name;
        this.mRole=role;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=mContext.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.user_list, null,true);

        TextView nameText = (TextView) rowView.findViewById(R.id.user_list_name);
        TextView roleText = (TextView) rowView.findViewById(R.id.user_list_role);

        nameText.setText(mName[position]);
        roleText.setText(mRole[position]);

        Button delete = (Button) rowView.findViewById()

        return rowView;
    };
}
