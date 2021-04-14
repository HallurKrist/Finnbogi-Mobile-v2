package is.hi.finnbogi_mobile;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
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

import com.google.gson.Gson;

import java.time.LocalDateTime;

import is.hi.finnbogi_mobile.NotificationSpecifics.NotificationReciever;
import is.hi.finnbogi_mobile.entities.Shift;
import is.hi.finnbogi_mobile.entities.User;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;
import is.hi.finnbogi_mobile.services.HomeService;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private static final String EXTRA_USER_ID = "is.hi.finnbogi_mobile.userId";
    private static final String MY_PREFERENCES = "Session";
    private static final String KEY_INDEX = "currWeekIndex";

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

        if (savedInstanceState != null) {
            mCurrWeekNr = savedInstanceState.getInt(KEY_INDEX, 0);
        }

        // Ef engin notandi er skráður in þá er sent notanda á login síðuna
        SharedPreferences sharedPref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        Log.d(TAG, "checking if logged in: " + sharedPref.contains("userId"));
        if (!sharedPref.contains("userId")) {
            Log.d(TAG, "No user logged in");
            cancelNotificationSchedule();
            Intent loginIntent = LoginActivity.newIntent(HomeActivity.this);
            startActivity(loginIntent);
            return;
        }

        setContentView(R.layout.activity_home);

        // Starta notification updater-inn sem athugar á 15min fresti hvort nýtt notification sé fyrir notanda
        scheduleNotification(sharedPref);

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
     * kickstartar sceduler sem athugar á 15min fresti hvort nýtt notification sé fyrir notanda
     */
    private void scheduleNotification(SharedPreferences sharedPref) {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), NotificationReciever.class);
        // Set extra
        intent.putExtra(NotificationReciever.USERID_ID, sharedPref.getInt("userId", -1));
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, NotificationReciever.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every every half hour from this point onwards
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES, pIntent);
    }

    public void cancelNotificationSchedule() {
        Intent intent = new Intent(getApplicationContext(), NotificationReciever.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, NotificationReciever.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
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
        mTuesday = (LinearLayout) findViewById(R.id.tuesday);
        mWednesday = (LinearLayout) findViewById(R.id.wednesday);
        mThursday = (LinearLayout) findViewById(R.id.thursday);
        mFriday = (LinearLayout) findViewById(R.id.friday);
        mSaturday = (LinearLayout) findViewById(R.id.saturday);
        mSunday = (LinearLayout) findViewById(R.id.sunday);

        LinearLayout[] theWeekView = {mMonday, mTuesday, mWednesday, mThursday, mFriday, mSaturday, mSunday};

        // Setja onclicklistener fyrir alla daga vikunar
        for (int i = 0; i < 7; i++) {
            int finalI = i;
            theWeekView[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCurrWeek[finalI] != null) {
                        Intent mondayIntent = ShiftActivity.newIntent(HomeActivity.this, mCurrWeek[finalI].getShiftId());
                        startActivity(mondayIntent);
                    }
                }
            });
        }

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
        // TODO: navigation to notifications
        switch (item.getItemId()) {
            case R.id.menu_notifications:
                Intent notificationIntent = new Intent(HomeActivity.this, NotificationsActivity.class);
                startActivity(notificationIntent);
                return true;
            case R.id.menu_shift_list:
                Intent shiftListIntent = new Intent(HomeActivity.this, ShiftListActivity.class);
                startActivity(shiftListIntent);
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
                Intent userInfoIntent = UserInfoActivity.newIntent(HomeActivity.this, mCurrentUser.getUserId());
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

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(KEY_INDEX, mCurrWeekNr);
    }
}