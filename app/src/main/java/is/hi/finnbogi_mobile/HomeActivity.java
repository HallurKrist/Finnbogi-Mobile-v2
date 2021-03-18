package is.hi.finnbogi_mobile;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.net.CookieHandler;
import java.net.CookieManager;

import is.hi.finnbogi_mobile.entities.Shift;
import is.hi.finnbogi_mobile.entities.User;
import is.hi.finnbogi_mobile.entities.UserInfo;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private static final String EXTRA_USER_ID = "is.hi.finnbogi_mobile.userId";

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



    private boolean adminUser = false;
    private boolean mUserLoggedIn = false;

    private Shift[] mThisWeek;

    public static Intent newIntent(Context packageContext, int userId) {
        Intent intent = new Intent(packageContext, HomeActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        return intent;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: make sure user is logged in, relocate to login if not logged in.
        setContentView(R.layout.activity_home);

        //trying networking
        Log.d(TAG, "starting networking test");

        CookieHandler.setDefault(new CookieManager());

        NetworkManager networkManager = NetworkManager.getInstance(this);
//        networkManager.GET(new NetworkCallback<String>() {
//            @Override
//            public void onSuccess(String result) {
//                Log.d(TAG, "GET: " + result);
//            }
//
//            @Override
//            public void onFailure(String errorString) {
//                Log.e(TAG, "GET: " + errorString);
//            }
//        }, "users");

        networkManager.POST(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "POST login: " + result);
                networkManager.GET(new NetworkCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d(TAG, "GET me2: " + result);
                    }

                    @Override
                    public void onFailure(String errorString) {
                        Log.e(TAG, "GET me2: " + errorString);
                    }
                }, "users/me");
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "POST login: " + errorString);
            }
        }, "/user/login", "username=admin&password=123");

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
                Intent notificationIntent = new Intent(HomeActivity.this, NotificationsActivity.class);
                startActivity(notificationIntent);
                Toast.makeText(this, "notifications", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_shift:
                //TODO: changeActivity
                Intent shiftIntent = new Intent(HomeActivity.this, ShiftActivity.class);
                startActivity(shiftIntent);
                Toast.makeText(this, "Shift", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_make_shift:
                //TODO: changeActivity
                Intent makeShiftIntent = new Intent(HomeActivity.this, MakeShiftActivity.class);
                startActivity(makeShiftIntent);
                Toast.makeText(this, "Make Shift", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_user_list:
                //TODO: changeActivity
                Intent userListIntent = new Intent(HomeActivity.this, UserListActivity.class);
                startActivity(userListIntent);
                Toast.makeText(this, "User List", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_user_info:
                //TODO: changeActivity
                Intent userInfoIntent = new Intent(HomeActivity.this, UserInfoActivity.class);
                startActivity(userInfoIntent);
                Toast.makeText(this, "My Info", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_make_user:
                //TODO: changeActivity
                Intent makeUserIntent = new Intent(HomeActivity.this, MakeUserActivity.class);
                startActivity(makeUserIntent);
                Toast.makeText(this, "Make New User", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_shiftexchange_list:
                //TODO: changeActivity
                Intent shiftexchangeListIntent = new Intent(HomeActivity.this, ShiftExchangeListActivity.class);
                startActivity(shiftexchangeListIntent);
                Toast.makeText(this, "Shift Exchange List", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_logout:
                //TODO: changeActivity
                Intent logoutIntent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(logoutIntent);
                Toast.makeText(this, "Log out", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getMe() {
        NetworkManager networkManager = NetworkManager.getInstance(this);
        networkManager.GET(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "GET me: " + result);
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "GET me: " + errorString);
            }
        }, "users/me");
    }
}