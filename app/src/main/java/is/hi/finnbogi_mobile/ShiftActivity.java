package is.hi.finnbogi_mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class ShiftActivity extends AppCompatActivity {

    private static final String TAG = "ShiftActivity";

    private TextView mDate;
    private TextView mTime;
    private TextView mEmployee;
    private Button mOffer;

    //TODO: this class
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift);

        mDate = (TextView) findViewById(R.id.shift_date);
        mTime = (TextView) findViewById(R.id.shift_time);
        mEmployee = (TextView) findViewById(R.id.shift_employee);
        mOffer = (Button) findViewById(R.id.shift_offer);

        // mock shift
        mDate.setText("Dags: 01.01.2021");
        mTime.setText("08:00 - 16:00");
        mEmployee.setText("Starfsmaður: Jón Jónsson");


    }
}