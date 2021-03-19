package is.hi.finnbogi_mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;

import is.hi.finnbogi_mobile.entities.User;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;
import is.hi.finnbogi_mobile.services.MakeShiftService;

public class MakeShiftActivity extends AppCompatActivity {
    private static final String TAG = "MakeShiftActivity";

    private DatePickerDialog mDatePicker;
    private EditText mDateEditText;
    private TimePickerDialog mStartTimePicker;
    private EditText mStartTimeEditText;
    private TimePickerDialog mEndTimePicker;
    private EditText mEndTimeEditText;

    private Spinner mRoles;
    private Spinner mEmployees;
    private Button mCancel;
    private Button mConfirm;

    private List<User> mAllUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_shift);

        NetworkManager networkManager = NetworkManager.getInstance(this);
        MakeShiftService makeShiftService = new MakeShiftService(networkManager);

        // get all view interactables
        mDateEditText = (EditText) findViewById(R.id.shift_make_date);
        mStartTimeEditText = (EditText) findViewById(R.id.shift_make_startTime);
        mEndTimeEditText = (EditText) findViewById(R.id.shift_make_endTime);
        mRoles = (Spinner) findViewById(R.id.shift_make_role);
        mEmployees = (Spinner) findViewById(R.id.shift_make_employee);
        mCancel = (Button) findViewById(R.id.shift_make_cancel);
        mConfirm = (Button) findViewById(R.id.shift_make_confirm);

        //set lists in spinners
        // TODO: make spinners correct
        // ná í alla users til að geta birt í select boxinu

        makeShiftService.getAllUsers(new NetworkCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> result) {
               mAllUsers = result;
               Log.d(TAG, String.valueOf(mAllUsers));
               String[] users = new String[mAllUsers.size()];
               int i = 0;
               for (User user : mAllUsers) {
                   users[i] = String.valueOf(user.getUserId());
                   i++;
               }
               ArrayAdapter<String> usersAdapter =
                       new ArrayAdapter<String>(MakeShiftActivity.this, android.R.layout.simple_list_item_1, users);
               usersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
               mEmployees.setAdapter(usersAdapter);
            }

            @Override
            public void onFailure(String errorString) {
                mAllUsers = null;
                Log.e(TAG, errorString);
            }
        });

        // Setja select box með roles
        String[] roles = new String[]{"Chef", "Chefs Assistant", "Bartender", "Waiter", "Busboy", "ShiftManager", "Employer"};
        ArrayAdapter<String> rolesAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, roles);
        rolesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRoles.setAdapter(rolesAdapter);


        //set onclicklisteners on EditText and button
        //TODO: make sure onclick events are correct

        mDateEditText.setInputType(InputType.TYPE_NULL);
        mDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                mDatePicker = new DatePickerDialog(MakeShiftActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                mDateEditText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                mDatePicker.show();
            }
        });

        mStartTimeEditText.setInputType(InputType.TYPE_NULL);
        mStartTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                mStartTimePicker = new TimePickerDialog(MakeShiftActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int sHour, int sMinute) {
                                mStartTimeEditText.setText(sHour + ":" + sMinute);
                            }
                        }, hour, minutes, true);
                mStartTimePicker.show();
            }
        });

        mEndTimeEditText.setInputType(InputType.TYPE_NULL);
        mEndTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                mEndTimePicker = new TimePickerDialog(MakeShiftActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int sHour, int sMinute) {
                                mEndTimeEditText.setText(sHour + ":" + sMinute);
                            }
                        }, hour, minutes, true);
                mEndTimePicker.show();
            }
        });

        mCancel = (Button) findViewById(R.id.shift_make_cancel);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MakeShiftActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                Intent intent = HomeActivity.newIntent(MakeShiftActivity.this);
                startActivity(intent);
            }
        });

        mConfirm = (Button) findViewById(R.id.shift_make_confirm);
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String day = mDateEditText.getText().toString();
                String startTime = mStartTimeEditText.getText().toString();
                String endTime = mEndTimeEditText.getText().toString();
                Log.d(TAG, "day: " + day);
                Log.d(TAG, "startTime: " + startTime);
                Log.d(TAG, "endTime: " + endTime);
                // TODO: Búa til LocalDateTime hluti úr þessu, og kalla svo á fall í service með callbacki
            }
        });
    }
}