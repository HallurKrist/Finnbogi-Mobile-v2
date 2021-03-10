package is.hi.finnbogi_mobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    private TextView mLoggedInUser;
    private LinearLayout mMonday;
    private LinearLayout mTuesday;
    private LinearLayout mWednesday;
    private LinearLayout mThursday;
    private LinearLayout mFriday;
    private LinearLayout mSaturday;
    private LinearLayout mSunday;
    private Button mLastWeek;
    private Button mNextWeek;



    private boolean adminUser;
    private boolean mUserLoggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: make sure user is logged in, relocate to login if not logged in.
        setContentView(R.layout.activity_home);

        mLoggedInUser = (TextView) findViewById(R.id.logged_in_user);
        mLoggedInUser.setText("Jon Jonsson");  //TODO: set correct username

        mMonday = (LinearLayout) findViewById(R.id.monday);
        mMonday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: redirect to correct shift according to clicked layout
                Toast.makeText(HomeActivity.this, "monday clicked", Toast.LENGTH_SHORT).show();
            }
        });

        mTuesday = (LinearLayout) findViewById(R.id.tuesday);
        mTuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: redirect to correct shift according to clicked layout
                Toast.makeText(HomeActivity.this, "tuesday clicked", Toast.LENGTH_SHORT).show();
            }
        });

        mWednesday = (LinearLayout) findViewById(R.id.wednesday);
        mWednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: redirect to correct shift according to clicked layout
                Toast.makeText(HomeActivity.this, "wednesday clicked", Toast.LENGTH_SHORT).show();
            }
        });

        mThursday = (LinearLayout) findViewById(R.id.thursday);
        mThursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: redirect to correct shift according to clicked layout
                Toast.makeText(HomeActivity.this, "thursday clicked", Toast.LENGTH_SHORT).show();
            }
        });

        mFriday = (LinearLayout) findViewById(R.id.friday);
        mFriday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: redirect to correct shift according to clicked layout
                Toast.makeText(HomeActivity.this, "friday clicked", Toast.LENGTH_SHORT).show();
            }
        });

        mSaturday = (LinearLayout) findViewById(R.id.saturday);
        mSaturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: redirect to correct shift according to clicked layout
                Toast.makeText(HomeActivity.this, "saturday clicked", Toast.LENGTH_SHORT).show();
            }
        });

        mSunday = (LinearLayout) findViewById(R.id.sunday);
        mSunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: redirect to correct shift according to clicked layout
                Toast.makeText(HomeActivity.this, "sunday clicked", Toast.LENGTH_SHORT).show();
            }
        });

        mNextWeek = (Button) findViewById(R.id.button_week_next);
        mNextWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: change week
            }
        });

        mLastWeek = (Button) findViewById(R.id.button_week_last);
        mLastWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: change week
            }
        });

        //TODO: get correct week
        //TODO: write correct days in the linearLayouts
        // filling monday
        ((TextView) mMonday.getChildAt(0)).setText("Date");
        ((TextView) mMonday.getChildAt(1)).setText("Time");
        ((TextView) mMonday.getChildAt(2)).setText("Role");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        //TODO: make different menu for regular user and admin
        inflater.inflate(R.menu.home_menu_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_notifications:
                //TODO: changeActivity
                Toast.makeText(this, "notifications", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_shift:
                //TODO: changeActivity
                Toast.makeText(this, "Shift", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_make_shift:
                //TODO: changeActivity
                Toast.makeText(this, "Make Shift", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_user_list:
                //TODO: changeActivity
                Toast.makeText(this, "User List", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_user_info:
                //TODO: changeActivity
                Toast.makeText(this, "My Info", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_make_user:
                //TODO: changeActivity
                Toast.makeText(this, "Make New User", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_shiftexchange_list:
                //TODO: changeActivity
                Toast.makeText(this, "Shift Exchange List", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}