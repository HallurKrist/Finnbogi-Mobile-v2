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

// TODO: Setja harðkóðaða strengi í strings.xml

public class ShiftExchangeListActivity extends AppCompatActivity {

    private static final String TAG = "ShiftExchangesActivity";
    private static final String MY_PREFERENCES = "Session";

    private SharedPreferences mSharedPreferences;

    // Viðmótshlutir
    private ListView mListUser;
    private ListView mListAll;
    private ListView mListAdmin;
    private LinearLayout mAdminTitle;
    private LinearLayout mAdminLinear;

    // Aðrar global breytur
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

    /**
     * Upphafsstillir alla viðmótshluti, nær í gögn og setur hlustara.
     *
     * @param savedInstanceState
     */
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

        /**
         * Nær í user sem er skráður inn.
         *
         */
        shiftExchangeService.getUserById(new NetworkCallback<User>() {
            @Override
            public void onSuccess(User result) {
                Log.d(TAG, "User logged in: " + result);
                mUser = result;
                // Ef user er admin þá náum við líka í öll confirmable shiftExchange
                if (mUser.getAdmin()) {
                    mAdminTitle.setVisibility(View.VISIBLE);
                    mAdminLinear.setVisibility(View.VISIBLE);
                    /**
                     * Nær í öll confirmable shiftExchange og setur lista með þeim.
                     *
                     */
                    shiftExchangeService.getConfirmableShiftExchanges(new NetworkCallback<List<ShiftExchange>>() {
                        @Override
                        public void onSuccess(List<ShiftExchange> result) {
                            mShiftExchangesListAdmin = result;
                            Log.d(TAG, "Gekk að ná í lista af confirmable shiftExchanges: " + String.valueOf(mShiftExchangesListAdmin));
                            Log.d(TAG, "Næ í lista af shifts");
                            /**
                             * Nær í allar vaktirnar sem eru venslaðar við þessi shiftExchange
                             * og setur viðeigandi upplýsingar í viðmótshluti.
                             *
                             */
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
                                    mListAdmin.setAdapter(adapterAdmin);
                                    /**
                                     * Event listener fyrir stök í lista. Opnar ShiftExchangeActivity
                                     * með þeim ShiftExchange hlut sem smellt var á úr lista.
                                     *
                                     */
                                    mListAdmin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            ShiftExchange shiftExchange = mShiftExchangesListAdmin.get(position);
                                            Intent intent = ShiftExchangeActivity.newIntent(ShiftExchangeListActivity.this, shiftExchange.getShiftExchangeId(), shiftExchange.getStatus());
                                            startActivity(intent);
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(String errorString) {
                                    Log.e(TAG, errorString);
                                }
                            });
                        }

                        @Override
                        public void onFailure(String errorString) {
                            Log.e(TAG, errorString);
                        }
                    });
                }
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, errorString);
            }
        }, userId);

        /**
         * Nær í öll shiftExchange sem innskráður notandi
         * er venslaður við og setur lista með þeim.
         *
         */
        shiftExchangeService.getShiftExchangesForUser(new NetworkCallback<List<ShiftExchange>>() {
            @Override
            public void onSuccess(List<ShiftExchange> result) {
                mShiftExchangesListUser = result;
                Log.d(TAG, "Gekk að ná í lista af shiftExchanges fyrir user: " + String.valueOf(mShiftExchangesListUser));
                /**
                 * Nær í allar vaktirnar sem eru venslaðar við þessi shiftExchange
                 * og setur viðeigandi upplýsingar í viðmótshluti.
                 *
                 */
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
                        Log.e(TAG, errorString);
                    }
                }, userId);
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, errorString);
            }
        }, userId);

        /**
         * Nær í öll shiftExchange og setur lista með þeim.
         *
         */
        shiftExchangeService.getAllShiftExchanges(new NetworkCallback<List<ShiftExchange>>() {
            @Override
            public void onSuccess(List<ShiftExchange> result) {
                mShiftExchangesListAll = result;
                Log.d(TAG, "Gekk að ná í lista af shiftExchanges: " + String.valueOf(mShiftExchangesListAll));
                /**
                 * Nær í allar vaktirnar sem eru venslaðar við þessi shiftExchange
                 * og setur viðeigandi upplýsingar í viðmótshluti.
                 *
                 */
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
                        Log.e(TAG, errorString);
                    }
                });
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, errorString);
            }
        });

        /**
         * Event listener fyrir stök í lista með vaktaskiptum notanda.
         * Opnar ShiftExchangeActivity með þeim ShiftExchange hlut
         * sem smellt var á úr lista.
         *
         */
        mListUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShiftExchange shiftExchange = mShiftExchangesListUser.get(position);
                Intent intent = ShiftExchangeActivity.newIntent(ShiftExchangeListActivity.this, shiftExchange.getShiftExchangeId(), shiftExchange.getStatus());
                startActivity(intent);
            }
        });

        /**
         * Event listener fyrir stök í lista með öllum vaktaskiptum.
         * Opnar ShiftExchangeActivity með þeim ShiftExchange hlut
         * sem smellt var á úr lista ef notandi hefur leyfi til þess.
         * Birtir annars villuskilaboð.
         *
         */
        mListAll.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShiftExchange shiftExchange = mShiftExchangesListAll.get(position);
                // Admin getur opnað öll shiftexchanges
                if (mUser.getAdmin()) {
                    Intent intent = ShiftExchangeActivity.newIntent(ShiftExchangeListActivity.this, shiftExchange.getShiftExchangeId(), shiftExchange.getStatus());
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
                            Intent intent = ShiftExchangeActivity.newIntent(ShiftExchangeListActivity.this, shiftExchange.getShiftExchangeId(), shiftExchange.getStatus());
                            startActivity(intent);
                        }
                    }
                }
            }
        });
    }
}