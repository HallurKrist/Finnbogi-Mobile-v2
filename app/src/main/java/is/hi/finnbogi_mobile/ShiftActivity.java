package is.hi.finnbogi_mobile;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import is.hi.finnbogi_mobile.entities.Notification;
import is.hi.finnbogi_mobile.entities.Shift;
import is.hi.finnbogi_mobile.entities.ShiftExchange;
import is.hi.finnbogi_mobile.entities.User;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;
import is.hi.finnbogi_mobile.services.ShiftService;

public class ShiftActivity extends AppCompatActivity {

    private static final String TAG = "ShiftActivity";
    private static final String SHIFT_KEY = "currentShift";

    private ShiftService mShiftService;
    private Shift mShift;
    private User mUser;
    private List<User> mUsersWithSameRole;

    private TextView mDate;
    private TextView mTime;
    private TextView mEmployee;
    private Button mOffer;

    public static Intent newIntent(Context packageContext, int shiftId) {
        Intent intent = new Intent(packageContext, ShiftActivity.class);
        intent.putExtra(SHIFT_KEY, shiftId);
        return intent;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift);

        NetworkManager networkManager = NetworkManager.getInstance(this);
        mShiftService = new ShiftService(networkManager);

        mDate = (TextView) findViewById(R.id.shift_date);
        mTime = (TextView) findViewById(R.id.shift_time);
        mEmployee = (TextView) findViewById(R.id.shift_employee);
        mOffer = (Button) findViewById(R.id.shift_offer);

        int shiftId = getIntent().getIntExtra(SHIFT_KEY, -1);

        mShiftService.getShiftById(new NetworkCallback<Shift>() {
            @Override
            public void onSuccess(Shift result) {
                mShift = result;
                mShiftService.getUserById(new NetworkCallback<User>() {
                    @Override
                    public void onSuccess(User result) {
                        mUser = result;
                        mDate.setText(mShiftService.dateString(mShift.getStartTime()));
                        mTime.setText(mShiftService.shiftTimeString(mShift.getStartTime(), mShift.getEndTime()));
                        mEmployee.setText(mShiftService.employeeString(mUser.getUserName()));
                    }

                    @Override
                    public void onFailure(String errorString) {
                        Log.e(TAG, "Error in getting user by id");
                    }
                }, mShift.getUserId());
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Error in finding shift by id");
            }
        }, shiftId);

        /**
         * Event listener fyrir bjóða takkann. Býr til ný vaktaskipti
         * fyrir þessa vakt og sendir notification á alla notendur með
         * sama role og vaktin.
         *
         */
        mOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Búa til ShiftExchange-ið sjálft
                mShiftService.createShiftExchange(new NetworkCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(ShiftActivity.this, String.valueOf(R.string.shift_activity_createexchange_success), Toast.LENGTH_SHORT).show();
                        // Búa til Notification og senda út á þá notendur sem eiga að fá tilkynningu
                        mShiftService.getAllUsersWithSameRole(new NetworkCallback<List<User>>() {
                            @Override
                            public void onSuccess(List<User> result) {
                                mUsersWithSameRole = result;
                                String title = getString(R.string.shift_activity_notification_title);
                                String text = getString(R.string.shift_activity_notification_text);
                                int[] userIds = new int[mUsersWithSameRole.size()];
                                int i = 0;
                                for (User user : mUsersWithSameRole) {
                                    userIds[i] = user.getUserId();
                                    i++;
                                }
                                mShiftService.createNotification(new NetworkCallback<String>() {
                                    @Override
                                    public void onSuccess(String result) {
                                        finish();
                                    }

                                    @Override
                                    public void onFailure(String errorString) {
                                        Log.e(TAG, errorString);
                                    }
                                }, title, text, userIds);
                            }

                            @Override
                            public void onFailure(String errorString) {
                                Log.e(TAG, errorString);
                            }
                        }, mShift.getRole(), mUser.getUserId());
                    }

                    @Override
                    public void onFailure(String errorString) {
                        Toast.makeText(ShiftActivity.this, String.valueOf(R.string.shift_activity_createexchange_fail), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, errorString);
                    }
                }, mUser.getUserId(), shiftId);
            }
        });
    }
}