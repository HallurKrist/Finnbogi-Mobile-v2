package is.hi.finnbogi_mobile;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.time.format.DateTimeFormatter;
import java.util.List;

import is.hi.finnbogi_mobile.entities.Shift;
import is.hi.finnbogi_mobile.entities.ShiftExchange;
import is.hi.finnbogi_mobile.entities.User;
import is.hi.finnbogi_mobile.listAdapters.ShiftExchangeListAdapter;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;
import is.hi.finnbogi_mobile.services.ShiftExchangeService;

public class ShiftExchangeListActivity extends AppCompatActivity {

    private static final String TAG = "ShiftExchangesActivity";
    private static final String MY_PREFERENCES = "Session";

    private SharedPreferences mSharedPreferences;

    private ListView mListUser;
    private ListView mListAll;
    private ListView mListAdmin;
    private LinearLayout mAdminTitle;
    private LinearLayout mAdminLinear;

    private List<ShiftExchange> mShiftExchangesListUser;
    private List<ShiftExchange> mShiftExchangesListAll;
    private List<ShiftExchange> mShiftExchangesListAdmin;
    private List<Shift> mShiftsUser;
    private List<Shift> mShiftsAll;
    private List<Shift> mShiftsAdmin;
    private User mUser;

    /**
     * Aðferð fyrir aðra klasa að búa til nýtt intent fyrir þetta activity.
     *
     * @param packageContext - Gamli activity klasinn
     * @return intent
     */
    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, ShiftExchangeListActivity.class);
        return intent;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shiftexchange_list);

        mListUser = (ListView) findViewById(R.id.shiftexchange_list_mylist);
        mListAll = (ListView) findViewById(R.id.shiftexchange_list_list);
        mListAdmin = (ListView) findViewById(R.id.shiftexchange_list_adminlist);
        mAdminTitle = (LinearLayout) findViewById(R.id.shiftexchange_list_admintitle);
        mAdminLinear = (LinearLayout) findViewById(R.id.shiftexchange_list_adminlinear);

        NetworkManager networkManager = NetworkManager.getInstance(this);
        ShiftExchangeService shiftExchangeService = new ShiftExchangeService(networkManager);

        mSharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);

        int userId = mSharedPreferences.getInt("userId", -1);

        // Ná í user sem er loggaður inn
        shiftExchangeService.getUserById(new NetworkCallback<User>() {
            @Override
            public void onSuccess(User result) {
                Log.d(TAG, "User logged in: " + result);
                mUser = result;
                // Ef user er admin þá náum við líka í confirmable vaktir
                if (mUser.getAdmin()) {
                    mAdminTitle.setVisibility(View.VISIBLE);
                    mAdminLinear.setVisibility(View.VISIBLE);
                    // Ná í öll confirmable shiftexchange
                    shiftExchangeService.getConfirmableShiftExchanges(new NetworkCallback<List<ShiftExchange>>() {
                        @Override
                        public void onSuccess(List<ShiftExchange> result) {
                            mShiftExchangesListAdmin = result;
                            Log.d(TAG, "Gekk að ná í lista af confirmable shiftExchanges: " + String.valueOf(mShiftExchangesListAdmin));
                            Log.d(TAG, "Næ í lista af shifts");
                            // Ná í allar vaktir sem eru í confirmable shiftexchange hlutum
                    /*
                    shiftExchangeService.getShiftsForExchangeForConfirmable(new NetworkCallback<List<Shift>>() {
                        @Override
                        public void onSuccess(List<Shift> result) {
                            mShiftsAdmin = result;
                            Log.d(TAG, "Gekk að ná í lista af shifts for exchange fyrir confirmable: " + String.valueOf(mShiftsAdmin));
                            int n = mShiftExchangesListAdmin.size();
                            String[] role = new String[n];
                            String[] date = new String[n];
                            String[] status = new String[n];
                            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH:mm");
                            for (int i = 0; i < n; i++) {
                                role[i] = mShiftsAdmin.get(i).getRole();
                                date[i] = mShiftsAdmin.get(i).getStartTime().format(dateFormat) + " - " + mShiftsAdmin.get(i).getEndTime().format(dateFormat);
                                status[i] = mShiftExchangesListAdmin.get(i).getStatus();
                            }
                            ShiftExchangeListAdapter adapterAdmin = new ShiftExchangeListAdapter(
                                    ShiftExchangeListActivity.this, role, date, status);
                            mListAll.setAdapter(adapterAdmin);
                        }

                        @Override
                        public void onFailure(String errorString) {
                            Log.e(TAG, "Villa að ná í lista af shifts for exchange fyrir confirmable: " + errorString);
                        }
                    });

                     */
                        }

                        @Override
                        public void onFailure(String errorString) {
                            Log.e(TAG, "Villa að ná í lista af confirmable shiftExchanges: " + errorString);
                        }
                    });
                }
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Villa að ná í logged in user: " + errorString);
            }
        }, userId);

        // Ná í shiftexchange fyrir user
        shiftExchangeService.getShiftExchangesForUser(new NetworkCallback<List<ShiftExchange>>() {
            @Override
            public void onSuccess(List<ShiftExchange> result) {
                mShiftExchangesListUser = result;
                Log.d(TAG, "Gekk að ná í lista af shiftExchanges fyrir user: " + String.valueOf(mShiftExchangesListUser));
                Log.d(TAG, "Næ í lista af shifts fyrir user");
                // Ná í allar vaktir sem eru í shiftexchange hlutunum
                shiftExchangeService.getShiftsForExchangeForUser(new NetworkCallback<List<Shift>>() {
                    @Override
                    public void onSuccess(List<Shift> result) {
                        mShiftsUser = result;
                        Log.d(TAG, "Gekk að ná í lista af shifts for exchange fyrir user: " + String.valueOf(mShiftsUser));
                        int n = mShiftExchangesListUser.size();
                        String[] role = new String[n];
                        String[] date = new String[n];
                        String[] status = new String[n];
                        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH:mm");
                        for (int i = 0; i < n; i++) {
                            role[i] = mShiftsUser.get(i).getRole();
                            date[i] = mShiftsUser.get(i).getStartTime().format(dateFormat) + " - " + mShiftsUser.get(i).getEndTime().format(dateFormat);
                            status[i] = mShiftExchangesListUser.get(i).getStatus();
                        }
                        ShiftExchangeListAdapter adapterUser = new ShiftExchangeListAdapter(
                                ShiftExchangeListActivity.this, role, date, status);
                        mListUser.setAdapter(adapterUser);
                    }

                    @Override
                    public void onFailure(String errorString) {
                        Log.e(TAG, "Villa að ná í lista af shifts for exchange fyrir user: " + errorString);
                    }
                }, userId);
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Villa að ná í lista af shiftExchanges fyrir user: " + errorString);
            }
        }, userId);

        // Ná í öll shiftexchange - bara ef user er ekki admin (breyta í sækja bara upforgrabs?)
        shiftExchangeService.getAllShiftExchanges(new NetworkCallback<List<ShiftExchange>>() {
            @Override
            public void onSuccess(List<ShiftExchange> result) {
                mShiftExchangesListAll = result;
                Log.d(TAG, "Gekk að ná í lista af shiftExchanges: " + String.valueOf(mShiftExchangesListAll));
                Log.d(TAG, "Næ í lista af shifts");
                // Ná í allar vaktir sem eru í shiftexchange hlutum
                shiftExchangeService.getShiftsForExchange(new NetworkCallback<List<Shift>>() {
                    @Override
                    public void onSuccess(List<Shift> result) {
                        mShiftsAll = result;
                        Log.d(TAG, "Gekk að ná í lista af shifts for exchange: " + String.valueOf(mShiftsAll));
                        int n = mShiftExchangesListAll.size();
                        String[] role = new String[n];
                        String[] date = new String[n];
                        String[] status = new String[n];
                        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH:mm");
                        for (int i = 0; i < n; i++) {
                            role[i] = mShiftsAll.get(i).getRole();
                            date[i] = mShiftsAll.get(i).getStartTime().format(dateFormat) + " - " + mShiftsAll.get(i).getEndTime().format(dateFormat);
                            status[i] = mShiftExchangesListAll.get(i).getStatus();
                        }
                        ShiftExchangeListAdapter adapterAll = new ShiftExchangeListAdapter(
                                ShiftExchangeListActivity.this, role, date, status);
                        mListAll.setAdapter(adapterAll);
                    }

                    @Override
                    public void onFailure(String errorString) {
                        Log.e(TAG, "Villa að ná í lista af shifts for exchange: " + errorString);
                    }
                });
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Villa að ná í lista af shiftExchanges: " + errorString);
            }
        });

        // TODO: onclicklistener fyrir stök í mListUser

        // Opnar nýtt activity með shift exchange hlutnum sem smellt var á
        mListAll.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShiftExchange shiftExchange = mShiftExchangesListAll.get(position);
                Log.d(TAG, "ShiftExchange nr. " + position + " í lista");
                Log.d(TAG, "id: " + shiftExchange.getShiftExchangeId());
                // Admin getur opnað öll shiftexchanges
                if (mUser.getAdmin()) {
                    Intent intent = ShiftExchangeActivity.newIntent(ShiftExchangeListActivity.this, shiftExchange.getShiftExchangeId());
                    startActivity(intent);
                }
                // Aðrir geta bara opnað þau sem eru upforgrabs og hafa sama role og notandi
                else {
                    if (!shiftExchange.getStatus().equals("upforgrabs")) {
                        Toast.makeText(ShiftExchangeListActivity.this, "Búið hefur verið að bjóða vakt á móti þessari vakt", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!mShiftsAll.get(position).getRole().equals(mUser.getRole())) {
                            Toast.makeText(ShiftExchangeListActivity.this, "Þú hefur ekki sama role og þessi vakt", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = ShiftExchangeActivity.newIntent(ShiftExchangeListActivity.this, shiftExchange.getShiftExchangeId());
                            startActivity(intent);
                        }
                    }
                }
            }
        });

        // TODO: onclicklistener fyrir stök í confirmable mListAdmin
    }
}