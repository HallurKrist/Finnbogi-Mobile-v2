package is.hi.finnbogi_mobile;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
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

import is.hi.finnbogi_mobile.entities.Shift;
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

        // init networkmanager and service
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

        // Hér erum við að ná í alla users til að setja í selectlist
        makeShiftService.getAllUsers(new NetworkCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> result) {
               mAllUsers = result;
               Log.d(TAG, String.valueOf(mAllUsers));
               String[] users = new String[mAllUsers.size()];
               int i = 0;
               for (User user : mAllUsers) {
                   users[i] = String.valueOf(user.getUserName());
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

        // Setja selectlist fyrir roles
        String[] roles = new String[]{"Chef", "Bartender", "Waiter", "Busboy", "ShiftManager", "Employer"}; // Harðkóðað fyrir þetta verkefni
        ArrayAdapter<String> rolesAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, roles);
        rolesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRoles.setAdapter(rolesAdapter);

        // onclick til að velja dagsetningu vaktar
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

        // onclick til að velja upphafstíma vaktar
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

        // onclick til að velja lokatíma vaktar
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
            /**
             * Þegar ýtt er á hætta við takka er farið til baka í HomeActivity.
             *
             * @param v
             */
            @Override
            public void onClick(View v) {
                Toast.makeText(MakeShiftActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                Intent intent = HomeActivity.newIntent(MakeShiftActivity.this);
                startActivity(intent);
            }
        });

        mConfirm = (Button) findViewById(R.id.shift_make_confirm);
        mConfirm.setOnClickListener(new View.OnClickListener() {
            /**
             * Þegar ýtt er á staðfesta takka er búin til ný vakt.
             *
             * @param v
             */
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String[] day = mDateEditText.getText().toString().split("/");
                String[] startTimeString = mStartTimeEditText.getText().toString().split(":");
                String[] endTimeString = mEndTimeEditText.getText().toString().split(":");
                String role = mRoles.getSelectedItem().toString();

                LocalDateTime startTime = LocalDateTime.of(
                        Integer.parseInt(day[2]),
                        Integer.parseInt(day[1]),
                        Integer.parseInt(day[0]),
                        Integer.parseInt(startTimeString[0]),
                        Integer.parseInt(startTimeString[1]),
                        0,
                        0);
                LocalDateTime endTime = LocalDateTime.of(
                        Integer.parseInt(day[2]),
                        Integer.parseInt(day[1]),
                        Integer.parseInt(day[0]),
                        Integer.parseInt(endTimeString[0]),
                        Integer.parseInt(endTimeString[1]),
                        0,
                        0);

                // sækja alla notendur til að finna notendalykilinn hans
                makeShiftService.getAllUsers(new NetworkCallback<List<User>>() {
                    @Override
                    public void onSuccess(List<User> result) {
                        int userId = -1;
                        for (int i = 0; i < result.size(); i++) {
                            String selectedEmployee = mEmployees.getSelectedItem().toString();
                            String userNameI = result.get(i).getUserName();
                            if (userNameI.equals(selectedEmployee)) {
                                userId = result.get(i).getUserId();
                            }
                        }

                        // nota notendalykilinn til að búa til nýa vakt
                        makeShiftService.createShift(new NetworkCallback<Shift>() {
                            @Override
                            public void onSuccess(Shift result) {
                                Toast.makeText(MakeShiftActivity.this, "Tókst að búa til vakt", Toast.LENGTH_SHORT).show();
                                mDateEditText.setText("");
                                mStartTimeEditText.setText("");
                                mEndTimeEditText.setText("");
                            }

                            @Override
                            public void onFailure(String errorString) {
                                Toast.makeText(MakeShiftActivity.this, "Villa við að búa til vakt", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Failed to create shift: " + errorString);
                            }
                        }, startTime, endTime, userId, role);
                    }

                    @Override
                    public void onFailure(String errorString) {
                        Log.e(TAG, "Error getting all users");
                    }
                });
            }
        });
    }
}