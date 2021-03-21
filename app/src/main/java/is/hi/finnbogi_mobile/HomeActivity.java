package is.hi.finnbogi_mobile;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.gson.Gson;

import java.time.LocalDateTime;

import is.hi.finnbogi_mobile.entities.Shift;
import is.hi.finnbogi_mobile.entities.User;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;
import is.hi.finnbogi_mobile.services.HomeService;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private static final String EXTRA_USER_ID = "is.hi.finnbogi_mobile.userId";
    private static final String MY_PREFERENCES = "Session";


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

    private Shift[] mCurrWeek;
    private int mCurrWeekNr = 0;
    private User mCurrentUser;
    private HomeService mHomeService;

    /**
     * Aðferð fyrir aðra klasa að búa til nýtt intent fyrir þetta activity.
     *
     * @param packageContext - Gamli activity klasinn
     * @return intent
     */
    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, HomeActivity.class);
        return intent;
    }

    /**
     * Aðferð fyrir aðra klasa að búa til nýtt intent fyrir þetta activity,
     * með extra fyrir userId.
     *
     * @param packageContext - Gamli activity klasinn
     * @param userId - userId fyrir extra
     * @return intent
     */
    public static Intent newIntentWithExtra(Context packageContext, int userId) {
        Intent intent = new Intent(packageContext, HomeActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        return intent;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ef engin notandi er skráður in þá er sent notanda á login síðuna
        SharedPreferences sharedPref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        Log.d(TAG, "checking if logged in: " + sharedPref.contains("userId"));
        if (!sharedPref.contains("userId")) {
            Log.d(TAG, "No user logged in");
            Intent loginIntent = LoginActivity.newIntent(HomeActivity.this);
            startActivity(loginIntent);
            return;
        }

        setContentView(R.layout.activity_home);

        // Búa til HomeService hlut
        NetworkManager networkManager = NetworkManager.getInstance(this);
        mHomeService = new HomeService(networkManager);

        // Ná í rétta notanda og réttu viku miðað við hann
        Gson gson = new Gson();
        mHomeService.getUserById(new NetworkCallback<User>() {
            @Override
            public void onSuccess(User result) {
                mCurrentUser = result;
                mHomeService.getWeek(new NetworkCallback<Shift[]>() {
                    @Override
                    public void onSuccess(Shift[] result) {
                        mCurrWeek = result;
                        // uppfæra viðmót þegar búið er að sækja notanda og vikuna
                        initView();
                        updateDays();
                    }

                    @Override
                    public void onFailure(String errorString) {
                        Log.e(TAG, "onFailure: " + errorString);
                    }
                }, mCurrentUser.getUserId(), mCurrWeekNr);
            }
            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Error in finding signed inn user");
            }
        }, sharedPref.getInt("userId", -1));
    }

    /**
     * Upphafsstillr viðmót og setur onclick listener-a
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initView() {
        // Upphafsstilla alla viðeigandi hluti í viðmóti
        mLoggedInUser = (TextView) findViewById(R.id.logged_in_user);
        mLoggedInUser.setText(mCurrentUser.getUserName());

        mMonday = (LinearLayout) findViewById(R.id.monday);
        mMonday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrWeek[0] != null) {
                    Intent mondayIntent = ShiftActivity.newIntent(HomeActivity.this, mCurrWeek[0].getShiftId());
                    startActivity(mondayIntent);
                }
            }
        });

        mTuesday = (LinearLayout) findViewById(R.id.tuesday);
        mTuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrWeek[1] != null) {
                    Intent tuesdayIntent = ShiftActivity.newIntent(HomeActivity.this, mCurrWeek[1].getShiftId());
                    startActivity(tuesdayIntent);
                }
            }
        });

        mWednesday = (LinearLayout) findViewById(R.id.wednesday);
        mWednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { ;
                if (mCurrWeek[2] != null) {
                    Intent wednesdayIntent = ShiftActivity.newIntent(HomeActivity.this, mCurrWeek[2].getShiftId());
                    startActivity(wednesdayIntent);
                }
            }
        });

        mThursday = (LinearLayout) findViewById(R.id.thursday);
        mThursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrWeek[3] != null) {
                    Intent thursdayIntent = ShiftActivity.newIntent(HomeActivity.this, mCurrWeek[3].getShiftId());
                    startActivity(thursdayIntent);
                }
            }
        });

        mFriday = (LinearLayout) findViewById(R.id.friday);
        mFriday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrWeek[4] != null) {
                    Intent fridayIntent = ShiftActivity.newIntent(HomeActivity.this, mCurrWeek[4].getShiftId());
                    startActivity(fridayIntent);
                }
            }
        });

        mSaturday = (LinearLayout) findViewById(R.id.saturday);
        mSaturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrWeek[5] != null) {
                    Intent saturdayIntent = ShiftActivity.newIntent(HomeActivity.this, mCurrWeek[5].getShiftId());
                    startActivity(saturdayIntent);
                }
            }
        });

        mSunday = (LinearLayout) findViewById(R.id.sunday);
        mSunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrWeek[6] != null) {
                    Intent sundayIntent = ShiftActivity.newIntent(HomeActivity.this, mCurrWeek[6].getShiftId());
                    startActivity(sundayIntent);
                }
            }
        });

        mNextWeek = (Button) findViewById(R.id.button_week_next);
        mNextWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrWeekNr = mCurrWeekNr + 1;
                mHomeService.getWeek(new NetworkCallback<Shift[]>() {
                    @Override
                    public void onSuccess(Shift[] result) {
                        mCurrWeek = result;
                        updateDays();
                    }

                    @Override
                    public void onFailure(String errorString) {
                        Log.e(TAG, "error in udating week");
                    }
                }, mCurrentUser.getUserId(), mCurrWeekNr);
            }
        });

        mLastWeek = (Button) findViewById(R.id.button_week_last);
        mLastWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrWeekNr = mCurrWeekNr - 1;
                mHomeService.getWeek(new NetworkCallback<Shift[]>() {
                    @Override
                    public void onSuccess(Shift[] result) {
                        mCurrWeek = result;
                        updateDays();
                    }

                    @Override
                    public void onFailure(String errorString) {
                        Log.e(TAG, "error in udating week");
                    }
                }, mCurrentUser.getUserId(), mCurrWeekNr);
            }
        });
    }

    /**
     * Breytir viku sem birt er í viðmóti eftir því hvað mCurrWeekNr er.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateDays() {
        int[] idList = {R.id.monday,
                R.id.tuesday,
                R.id.wednesday,
                R.id.thursday,
                R.id.friday,
                R.id.saturday,
                R.id.sunday};

        // Fer í gegnum alla vikuna og stillir hvern dag
        for (int i = 0; i < 7; i++) {
            Shift shift = mCurrWeek[i];
            LinearLayout day = (LinearLayout) findViewById(idList[i]);

            // Athugar hvort það sé vakt á tilteknum degi og birtir viðeigandi í viðmót
            if (shift != null) {
                LocalDateTime startTime = shift.getStartTime();
                LocalDateTime endTime = shift.getEndTime();

                ((TextView) day.getChildAt(0)).setText(mHomeService.prettyDate(
                        startTime.getDayOfMonth(),
                        startTime.getMonth().toString(),
                        startTime.getYear()
                ));
                ((TextView) day.getChildAt(1)).setText(mHomeService.prettyTime(
                        startTime.getHour(), startTime.getMinute(), endTime.getHour(), endTime.getMinute()
                ));
                ((TextView) day.getChildAt(2)).setText(shift.getRole());
            } else {
                LocalDateTime weekStart = mHomeService.findWeekStartDay(mCurrWeekNr);
                LocalDateTime dayDateTime = weekStart.plusDays(i);

                ((TextView) day.getChildAt(0)).setText(mHomeService.prettyDate(
                        dayDateTime.getDayOfMonth(),
                        dayDateTime.getMonth().toString(),
                        dayDateTime.getYear()
                ));
                ((TextView) day.getChildAt(1)).setText("Engin vakt");
                ((TextView) day.getChildAt(2)).setText("______");
            }
        }
    }

    /**
     * Passar að rétt menubar sé sýnt miðað við hvort það sé almennur notandi eða admin innskráður.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        SharedPreferences sharedPref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        NetworkManager networkManager = NetworkManager.getInstance(this);
        HomeService tmpHomeService = new HomeService(networkManager);

        if (sharedPref.contains("userId")) {
            tmpHomeService.getUserById(new NetworkCallback<User>() {
                @Override
                public void onSuccess(User result) {
                    if (result.getAdmin() == true) {
                        inflater.inflate(R.menu.home_menu_admin, menu);
                    } else {
                        inflater.inflate(R.menu.home_menu_user, menu);
                    }
                }

                @Override
                public void onFailure(String errorString) {
                    Log.e(TAG, "Error in getting logged in user");
                }
            }, sharedPref.getInt("userId", -1));
        }
        return true;
    }

    /**
     * onclick listener fyrir menubar-ið
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //TODO: take out Shift and put inn shifts list view for admin
        //TODO: make sure all navigation is correct
        switch (item.getItemId()) {
            case R.id.menu_notifications:
                Intent notificationIntent = new Intent(HomeActivity.this, NotificationsActivity.class);
                startActivity(notificationIntent);
                return true;
            case R.id.menu_shift:
                Toast.makeText(HomeActivity.this, "TODO", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_make_shift:
                Intent makeShiftIntent = new Intent(HomeActivity.this, MakeShiftActivity.class);
                startActivity(makeShiftIntent);
                return true;
            case R.id.menu_user_list:
                Intent userListIntent = new Intent(HomeActivity.this, UserListActivity.class);
                startActivity(userListIntent);
                return true;
            case R.id.menu_user_info:
                Intent userInfoIntent = new Intent(HomeActivity.this, UserInfoActivity.class);
                startActivity(userInfoIntent);
                return true;
            case R.id.menu_make_user:
                Intent makeUserIntent = new Intent(HomeActivity.this, MakeUserActivity.class);
                startActivity(makeUserIntent);
                return true;
            case R.id.menu_shiftexchange_list:
                Intent shiftexchangeListIntent = new Intent(HomeActivity.this, ShiftExchangeListActivity.class);
                startActivity(shiftexchangeListIntent);
                return true;
            case R.id.menu_logout:
                // Tek hér user úr session
                SharedPreferences sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                Intent logoutIntent = LoginActivity.newIntent(HomeActivity.this);
                startActivity(logoutIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}