package is.hi.finnbogi_mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.util.Calendar;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_shift);

        // get all view interactables
        mDateEditText = (EditText) findViewById(R.id.shift_make_date);
        mStartTimeEditText = (EditText) findViewById(R.id.shift_make_startTime);
        mEndTimeEditText = (EditText) findViewById(R.id.shift_make_endTime);
        mRoles = (Spinner) findViewById(R.id.shift_make_role);
        mEmployees = (Spinner) findViewById(R.id.shift_make_employee);
        mCancel = (Button) findViewById(R.id.shift_make_cancel);
        mConfirm = (Button) findViewById(R.id.shift_make_confirm);

        //set lists in spinners
        //TODO: make spinners correct
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new String[]{"string1", "string2", "string3", "string4", "string5"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRoles.setAdapter(adapter);
        mEmployees.setAdapter(adapter);


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

    }
}