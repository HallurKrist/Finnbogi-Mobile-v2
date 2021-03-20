package is.hi.finnbogi_mobile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import is.hi.finnbogi_mobile.entities.Shift;
import is.hi.finnbogi_mobile.entities.User;
import is.hi.finnbogi_mobile.entities.UserInfo;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;
import is.hi.finnbogi_mobile.services.HomeService;
import is.hi.finnbogi_mobile.services.LoginService;

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
//        if (!sharedPref.contains("userId")) {
//            Log.d(TAG, "No user logged in");
//            Intent loginIntent = LoginActivity.newIntent(HomeActivity.this);
//            startActivity(loginIntent);
//        }

        setContentView(R.layout.activity_home);

        // Búa til HomeService hlut
        NetworkManager networkManager = NetworkManager.getInstance(this);
        mHomeService = new HomeService(networkManager);

//        CookieManager cookieManager = new CookieManager();
//        CookieHandler.setDefault(cookieManager);

        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));

//// this should be done at the beginning of an HTTP session
//        CookieHandler.setDefault(new CookieManager());
//// this can be done at any point of an HTTP session
//        ((CookieManager)CookieHandler.getDefault()).setCookiePolicy(CookiePolicy.ACCEPT_ALL);


        networkManager.POST(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {

                networkManager.GET(new NetworkCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d(TAG, "onSuccess: " + result);
                    }

                    @Override
                    public void onFailure(String errorString) {
                        Log.e(TAG, "onFailure: " + errorString );
                    }
                }, new String[] {"users", "me"});
//                try {
////                    Map<String, List<String>> tmp = cookieManager.get(new URI("https://finnbogi-api.herokuapp.com/users/login"), new Map<String, List<String>>() {
////                        @Override
////                        public int size() {
////                            return 0;
////                        }
////
////                        @Override
////                        public boolean isEmpty() {
////                            return false;
////                        }
////
////                        @Override
////                        public boolean containsKey(@Nullable Object key) {
////                            return false;
////                        }
////
////                        @Override
////                        public boolean containsValue(@Nullable Object value) {
////                            return false;
////                        }
////
////                        @Nullable
////                        @Override
////                        public List<String> get(@Nullable Object key) {
////                            return null;
////                        }
////
////                        @Nullable
////                        @Override
////                        public List<String> put(String key, List<String> value) {
////                            return null;
////                        }
////
////                        @Nullable
////                        @Override
////                        public List<String> remove(@Nullable Object key) {
////                            return null;
////                        }
////
////                        @Override
////                        public void putAll(@NonNull Map<? extends String, ? extends List<String>> m) {
////
////                        }
////
////                        @Override
////                        public void clear() {
////
////                        }
////
////                        @NonNull
////                        @Override
////                        public Set<String> keySet() {
////                            return null;
////                        }
////
////                        @NonNull
////                        @Override
////                        public Collection<List<String>> values() {
////                            return null;
////                        }
////
////                        @NonNull
////                        @Override
////                        public Set<Entry<String, List<String>>> entrySet() {
////                            return null;
////                        }
////                    });
////                    List<HttpCookie> tmp2 = cookieManager.getCookieStore().getCookies();
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (URISyntaxException e) {
//                    e.printStackTrace();
//                }
                    Log.d(TAG, "onSuccess: " + result);

            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "onFailure: " + errorString);
            }
        }, new String[] {"users", "login"}, "username=admin&password=123");

        networkManager.GET(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "onSuccess: " + result);
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "onFailure: " + errorString );
            }
        }, new String[] {"users", "me"});

        // Ná í rétta notanda og réttu viku miðað við hann
        int currUserId = sharedPref.getInt("userId", -1);
        currUserId = 1; //TODO: take this out

        mCurrentUser = mHomeService.getUserById(currUserId);
        mCurrWeek = mHomeService.getWeek(currUserId,mCurrWeekNr);
        updateDays();

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
                mCurrWeek = mHomeService.getWeek(mCurrentUser.getUserId(),mCurrWeekNr);
                updateDays();
            }
        });

        mLastWeek = (Button) findViewById(R.id.button_week_last);
        mLastWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrWeekNr = mCurrWeekNr - 1;
                mCurrWeek = mHomeService.getWeek(mCurrentUser.getUserId(),mCurrWeekNr);
                updateDays();
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

    // Passar að rétt menubar sé sýnt miðað við hvort það sé almennur notandi eða admin innskráður.
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
                Intent notificationIntent = new Intent(HomeActivity.this, NotificationsActivity.class);
                startActivity(notificationIntent);
                return true;
            case R.id.menu_shift:
                Intent shiftIntent = new Intent(HomeActivity.this, ShiftActivity.class);
                startActivity(shiftIntent);
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