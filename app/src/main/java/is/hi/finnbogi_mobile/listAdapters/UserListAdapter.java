package is.hi.finnbogi_mobile.listAdapters;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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

    /**
     * constructor
     * @param context UserListActiviy
     * @param userListService UserListSerivce from UserListActivity
     * @param name list of strings representing names of users
     * @param role list of strings representing roles of users
     */
    public UserListAdapter(Activity context, UserListService userListService, String[] name, String[] role) {
        super(context, R.layout.user_list, name);

        this.mContext=context;
        this.mName=name;
        this.mRole=role;

        this.mUserListService = userListService;
    }

    /**
     * How UserListActivity knows what to display
     * @param position
     * @param view
     * @param parent
     * @return
     */
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=mContext.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.user_list, null,true);

        // find textViews
        TextView nameText = (TextView) rowView.findViewById(R.id.user_list_name);
        TextView roleText = (TextView) rowView.findViewById(R.id.user_list_role);

        // set TextViews
        nameText.setText(mName[position]);
        roleText.setText(mRole[position]);

        // Set onclick for user in the list
        LinearLayout theUser = (LinearLayout) rowView.findViewById(R.id.user_list_user);
        theUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get userId and navigate to UserInfoActivity for correct user
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

        // Set onclick for deleting user
        Button delete = (Button) rowView.findViewById(R.id.user_list_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // hierarchy
                // LinearLayout
                //      LinearLayout
                //          TextView (name)
                //          TextView (role)
                //      Button (View v)

                // Ask user if sure they want to delete user
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(R.string.user_list_dialog_message)
                        .setTitle(R.string.user_list_dialog_title)
                        .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if user chooses positively (want to delete) then delete
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
                        })
                        .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                            // if user chooses negatively (does not want to delete) then do nothing
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                // Create the AlertDialog object and return it
                AlertDialog dialog = builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        // Changes alert style for buttons
                        Button positiveButton = ((AlertDialog) dialog)
                                .getButton(AlertDialog.BUTTON_POSITIVE);
                        positiveButton.setBackgroundColor(Color.WHITE);
                        positiveButton.setTextColor(Color.BLACK);

                        Button negativeButton = ((AlertDialog) dialog)
                                .getButton(AlertDialog.BUTTON_NEGATIVE);
                        negativeButton.setBackgroundColor(Color.WHITE);
                        negativeButton.setTextColor(Color.BLACK);
                    }
                });
                dialog.show();
            }
        });
        return rowView;
    };
}
