package is.hi.finnbogi_mobile.listAdapters;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import is.hi.finnbogi_mobile.R;
import is.hi.finnbogi_mobile.ShiftActivity;
import is.hi.finnbogi_mobile.UserInfoActivity;
import is.hi.finnbogi_mobile.entities.Shift;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;
import is.hi.finnbogi_mobile.services.ShiftListService;
import is.hi.finnbogi_mobile.services.UserInfoService;
import is.hi.finnbogi_mobile.services.UserListService;

public class ShiftListAdapter extends ArrayAdapter<String> {

    private static final String TAG = "ShiftListAdapter";

    private final Activity mContext;
    private final String[] mDateTime;
    private final String[] mRole;
    private final int[] mShiftIds;

    private final ShiftListService mShiftListService;

    /**
     * constructor
     * @param context ShiftListActivity
     * @param shiftListService ShiftListService from ShiftListActivity
     * @param shiftIds List of shiftId's that will be hiddren in the view
     * @param dateTime list of strings representing the date and time for given shift
     * @param role list og strings representing what kind of role given shift has
     */
    public ShiftListAdapter(Activity context, ShiftListService shiftListService, int[] shiftIds, String[] dateTime, String[] role) {
        super(context, R.layout.user_list, dateTime);

        this.mContext=context;
        this.mShiftIds=shiftIds;
        this.mDateTime=dateTime;
        this.mRole=role;

        this.mShiftListService = shiftListService;
    }

    /**
     * How ShiftListActivity knows what to display
     * @param position
     * @param view
     * @param parent
     * @return
     */
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=mContext.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.shift_list, null,true);

        // find TextViews
        TextView idText = (TextView) rowView.findViewById(R.id.shift_list_shiftID);
        TextView nameText = (TextView) rowView.findViewById(R.id.shift_list_datetime);
        TextView roleText = (TextView) rowView.findViewById(R.id.shift_list_role);

        // set TextViews
        idText.setText(String.valueOf(mShiftIds[position]));
        nameText.setText(mDateTime[position]);
        roleText.setText(mRole[position]);

        // Make onclick for entire shift
        LinearLayout theShift = (LinearLayout) rowView.findViewById(R.id.shift_list_shift);
        theShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // find textView with string id for clicked shift
                TextView shiftIDTextView = ((TextView) ((LinearLayout) theShift.getParent()).getChildAt(1));
                // extract int from string
                int shiftID =Integer.parseInt((String)shiftIDTextView.getText());
                // navigate to ShiftActivity with correct id
                Intent shiftIntent = ShiftActivity.newIntent(mContext, shiftID);
                mContext.startActivity(shiftIntent);
            }
        });

        // make onclick for deleting shift
        Button delete = (Button) rowView.findViewById(R.id.shift_list_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // hierarchy:
                // LinearLayout
                //      LinearLayout
                //          TextView (name)
                //          TextView (role)
                //      TextView (shiftId)
                //      Button (View v)

                // Ask user if sure they want to delete shift
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(R.string.shift_list_dialog_message)
                        .setTitle(R.string.shift_list_dialog_title)
                        .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if user chooses positively (want to delete) then delete
                                int shiftId = Integer.parseInt((String)((TextView) ((LinearLayout) delete.getParent()).getChildAt(1)).getText());
                                mShiftListService.deleteShiftById(new NetworkCallback<Boolean>() {

                                    @Override
                                    public void onSuccess(Boolean result) {
                                        Log.d(TAG, "Successfully deleted shift" + shiftId);
                                        Toast.makeText(mContext,"Successfully deleted shift", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(String errorString) {
                                        Log.e(TAG, "Error while deleting shift: " + errorString);
                                        Toast.makeText(mContext,"Error deleting shift", Toast.LENGTH_SHORT).show();
                                    }
                                }, shiftId);
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
