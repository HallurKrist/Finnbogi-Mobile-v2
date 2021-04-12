package is.hi.finnbogi_mobile;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import is.hi.finnbogi_mobile.entities.Shift;
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

    private TextView mDate;
    private TextView mTime;
    private TextView mEmployee;
    private Button mOffer;

    /**
     * For other classes to make an intent to call this class
     * @param packageContext
     * @param shiftId
     * @return Intent used to call this activity
     */
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

        // init network manager and service
        NetworkManager networkManager = NetworkManager.getInstance(this);
        mShiftService = new ShiftService(networkManager);

        // get all view interactables
        mDate = (TextView) findViewById(R.id.shift_date);
        mTime = (TextView) findViewById(R.id.shift_time);
        mEmployee = (TextView) findViewById(R.id.shift_employee);
        mOffer = (Button) findViewById(R.id.shift_offer);

        // get shift id from intent
        int shiftId = getIntent().getIntExtra(SHIFT_KEY, -1);

        // get shift with id shiftId from API
        mShiftService.getShiftById(new NetworkCallback<Shift>() {
            @Override
            public void onSuccess(Shift result) {
                mShift = result;

                // get user with id mShift.getUserId()
                mShiftService.getUserById(new NetworkCallback<User>() {
                    @Override
                    public void onSuccess(User result) {
                        // Show shift and user info in view
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
    }
}