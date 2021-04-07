package is.hi.finnbogi_mobile.listAdapters;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import is.hi.finnbogi_mobile.R;
import is.hi.finnbogi_mobile.UserInfoActivity;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;
import is.hi.finnbogi_mobile.services.UserInfoService;
import is.hi.finnbogi_mobile.services.UserListService;

public class UserListAdapter extends ArrayAdapter<String> {

    private static final String TAG = "UserListAdapter";

    private final Activity mContext;
    private final String[] mName;
    private final String[] mRole;

    private final UserListService mUserListService;

    public UserListAdapter(Activity context, UserListService userListService, String[] name, String[] role) {
        super(context, R.layout.user_list, name);

        this.mContext=context;
        this.mName=name;
        this.mRole=role;

        this.mUserListService = userListService;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=mContext.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.user_list, null,true);

        TextView nameText = (TextView) rowView.findViewById(R.id.user_list_name);
        TextView roleText = (TextView) rowView.findViewById(R.id.user_list_role);

        nameText.setText(mName[position]);
        roleText.setText(mRole[position]);

        LinearLayout theUser = (LinearLayout) rowView.findViewById(R.id.user_list_user);
        theUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserListService.getUserIdByName(new NetworkCallback<Integer>() {
                    @Override
                    public void onSuccess(Integer result) {
                        Intent userInfoIntent = UserInfoActivity.newIntent(mContext, result);
                        mContext.startActivity(userInfoIntent);
                    }

                    @Override
                    public void onFailure(String errorString) {
                        Log.e(TAG, "Error finding user");
                    }
                }, (String) ((TextView) theUser.getChildAt(0)).getText());
            }
        });

        Button delete = (Button) rowView.findViewById(R.id.user_list_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "on eyða: " + ((TextView)((LinearLayout)((LinearLayout) v.getParent()).getChildAt(0)).getChildAt(0)).getText());
                // hierarchy
                // LinearLayout
                //      LinearLayout
                //          TextView (name)
                //          TextView (role)
                //      Button (View v)
                String userName = (String) ((TextView) ((LinearLayout) ((LinearLayout) v.getParent()).getChildAt(0)).getChildAt(0)).getText();
                mUserListService.deleteUserByName(new NetworkCallback<Boolean>() {

                    @Override
                    public void onSuccess(Boolean result) {
                        Log.d(TAG, "Successfully deleted user" + userName);
                        Toast.makeText(mContext,"Successfully deleted user "+userName, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String errorString) {
                        Log.e(TAG, "Error while deleting user: " + errorString);
                        Toast.makeText(mContext,"Error deleting user " + userName, Toast.LENGTH_SHORT).show();
                    }
                }, userName);
            }
        });



        return rowView;
    };
}
