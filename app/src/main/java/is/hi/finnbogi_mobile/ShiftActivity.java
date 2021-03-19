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
import is.hi.finnbogi_mobile.networking.NetworkManager;
import is.hi.finnbogi_mobile.services.ShiftService;

public class ShiftActivity extends AppCompatActivity {

    private static final String TAG = "ShiftActivity";
    private static final String SHIFT_KEY = "currentShift";

    private ShiftService mShiftService;
    private Shift mShift;

    private TextView mDate;
    private TextView mTime;
    private TextView mEmployee;
    private Button mOffer;

    public static Intent newIntent(Context packageContext, int shiftId) {
        Intent intent = new Intent(packageContext, ShiftActivity.class);
        Gson gson = new Gson();
        intent.putExtra(SHIFT_KEY, shiftId);
        return intent;
    }

    //TODO: this class
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

        mShift = mShiftService.getShiftById(shiftId);

        mDate.setText(mShiftService.dateString(mShift.getStartTime()));
        mTime.setText(mShiftService.shiftTimeString(mShift.getStartTime(), mShift.getEndTime()));
        mEmployee.setText(mShiftService.employeeString(mShift.getUser().getUserName()));


    }
}