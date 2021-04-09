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

    public ShiftListAdapter(Activity context, ShiftListService shiftListService, int[] shiftIds, String[] dateTime, String[] role) {
        super(context, R.layout.user_list, dateTime);

        this.mContext=context;
        this.mShiftIds=shiftIds;
        this.mDateTime=dateTime;
        this.mRole=role;

        this.mShiftListService = shiftListService;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=mContext.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.shift_list, null,true);

        TextView idText = (TextView) rowView.findViewById(R.id.shift_list_shiftID);
        TextView nameText = (TextView) rowView.findViewById(R.id.shift_list_datetime);
        TextView roleText = (TextView) rowView.findViewById(R.id.shift_list_role);

        idText.setText(String.valueOf(mShiftIds[position]));
        nameText.setText(mDateTime[position]);
        roleText.setText(mRole[position]);

        LinearLayout theShift = (LinearLayout) rowView.findViewById(R.id.shift_list_shift);
        theShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView shiftIDTextView = ((TextView) ((LinearLayout) theShift.getParent()).getChildAt(1));
                int shiftID =Integer.parseInt((String)shiftIDTextView.getText());
                Intent shiftIntent = ShiftActivity.newIntent(mContext, shiftID);
                mContext.startActivity(shiftIntent);
            }
        });

        Button delete = (Button) rowView.findViewById(R.id.shift_list_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // hierarchy
                // LinearLayout
                //      LinearLayout
                //          TextView (name)
                //          TextView (role)
                //      TextView (shiftId)
                //      Button (View v)
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(R.string.shift_list_dialog_message)
                        .setTitle(R.string.shift_list_dialog_title)
                        .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
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
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                // Create the AlertDialog object and return it
                AlertDialog dialog = builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
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
