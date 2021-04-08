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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.time.format.DateTimeFormatter;
import java.util.List;

import is.hi.finnbogi_mobile.entities.Shift;
import is.hi.finnbogi_mobile.entities.ShiftExchange;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;
import is.hi.finnbogi_mobile.services.ShiftExchangeService;

public class ShiftExchangeActivity extends AppCompatActivity {

    private static final String TAG = "ShiftExchangeActivity";
    private static final String SHIFT_EXCHANGE_KEY = "currentShiftExchange";
    private static final String SHIFT_EXCHANGE_STATUS = "currentShiftExchangeStatus";
    private static final String MY_PREFERENCES = "Session";

    // Up for grabs viðmót
    private LinearLayout mLinearLayoutUFG;
    private TextView mTextViewShiftRoleUFG;
    private TextView mTextViewShiftDateUFG;
    private TextView mTextViewShiftTimeUFG;
    private Spinner mSpinnerShiftsToOfferUFG;
    private Button mButtonOfferUFG;

    // Pending viðmót
    private LinearLayout mLinearLayoutPending;
    private TextView mTextViewShiftRolePending;
    private TextView mTextViewShiftDatePending;
    private TextView mTextViewShiftTimePending;
    private TextView mTextViewOfferedShiftDatePending;
    private TextView mTextViewOfferedShiftTimePending;
    private Button mButtonDeclinePending;
    private Button mButtonAcceptPending;

    // Confirmable viðmót
    private LinearLayout mLinearLayoutConfirmable;
    private TextView mTextViewShiftRoleConfirmable;
    private TextView mTextViewShiftDateConfirmable;
    private TextView mTextViewShiftTimeConfirmable;
    private TextView mTextViewOfferedShiftDateConfirmable;
    private TextView mTextViewOfferedShiftTimeConfirmable;
    private Button mButtonDeclineConfirmable;
    private Button mButtonAcceptConfirmable;


    private ShiftExchange mShiftExchange;
    private Shift mShiftForExchange;
    private Shift mShiftForOffer;
    private List<Shift> mUserShifts;

    /**
     * Aðferð fyrir aðra klasa að búa til nýtt intent fyrir þetta activity.
     *
     * @param packageContext - Gamli activity klasinn
     * @param shiftExchangeId - Id fyrir shiftexchange sem er verið að opna
     * @return intent
     */
    public static Intent newIntent(Context packageContext, int shiftExchangeId, String shiftExchangeStatus) {
        Intent intent = new Intent(packageContext, ShiftExchangeActivity.class);
        intent.putExtra(SHIFT_EXCHANGE_KEY, shiftExchangeId);
        intent.putExtra(SHIFT_EXCHANGE_STATUS, shiftExchangeStatus);
        return intent;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shiftexchange);

        NetworkManager networkManager = NetworkManager.getInstance(this);
        ShiftExchangeService shiftExchangeService = new ShiftExchangeService(networkManager);

        SharedPreferences sharedPref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);

        int shiftExchangeId = getIntent().getIntExtra(SHIFT_EXCHANGE_KEY, -1);
        String shiftExchangeStatus = getIntent().getStringExtra(SHIFT_EXCHANGE_STATUS);
        int userId = sharedPref.getInt("userId", -1);

        //TODO: check what state the shiftexchange is in through the intent, show correct view for that state.

        // Up for grabs view
        if (shiftExchangeStatus.equals("upforgrabs")) {
            mLinearLayoutUFG = (LinearLayout) findViewById(R.id.shiftexchange_upforgrabs);
            mTextViewShiftRoleUFG = (TextView) findViewById(R.id.shiftexchange_upforgrabs_shift_role);
            mTextViewShiftDateUFG = (TextView) findViewById(R.id.shiftexchange_upforgrabs_shift_date);
            mTextViewShiftTimeUFG = (TextView) findViewById(R.id.shiftexchange_upforgrabs_shift_time);
            mSpinnerShiftsToOfferUFG = (Spinner) findViewById(R.id.shiftexchange_upforgrabs_shifts_to_offer);
            mButtonOfferUFG = (Button) findViewById(R.id.shiftexchange_upforgrabs_button);

            // Gera þetta view visible
            mLinearLayoutUFG.setVisibility(View.VISIBLE);

            shiftExchangeService.getShiftExchangeById(new NetworkCallback<ShiftExchange>() {
                @Override
                public void onSuccess(ShiftExchange result) {
                    Log.d(TAG, "Gekk að ná í shiftexchange: " + shiftExchangeId);
                    mShiftExchange = result;
                    shiftExchangeService.getShiftById(new NetworkCallback<Shift>() {
                        @Override
                        public void onSuccess(Shift result) {
                            Log.d(TAG, "Gekk að ná í shift for exchange: " + result);
                            mShiftForExchange = result;
                            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
                            mTextViewShiftRoleUFG.setText(mShiftForExchange.getRole());
                            mTextViewShiftDateUFG.setText(mShiftForExchange.getStartTime().format(dateFormat));
                            mTextViewShiftTimeUFG.setText(
                                    mShiftForExchange.getStartTime().format(timeFormat) + " - "
                                            + mShiftForExchange.getEndTime().format(timeFormat));
                        }

                        @Override
                        public void onFailure(String errorString) {
                            Log.e(TAG, "Villa að ná í shift for exchange: " + errorString);
                        }
                    }, mShiftExchange.getShiftForExchangeId());

                }

                @Override
                public void onFailure(String errorString) {
                    Log.e(TAG, "Villa við að ná í shiftexchange með id: " + shiftExchangeId + ": " + errorString);
                }
            }, shiftExchangeId);

            // Þetta er ef notandi getur boðið vakt á móti
            shiftExchangeService.getUserShifts(new NetworkCallback<List<Shift>>() {
                @Override
                public void onSuccess(List<Shift> result) {
                    Log.d(TAG, "Gekk að ná í vaktir notanda");
                    mUserShifts = result;
                    String[] dates = new String[result.size()];
                    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-mm-yyyy");
                    DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
                    for (int i = 0; i < result.size(); i++) {
                        dates[i] = result.get(i).getStartTime().format(dateFormat) +
                                ": " + result.get(i).getStartTime().format(timeFormat) +
                                " - " + result.get(i).getEndTime().format(timeFormat);
                    }
                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<String>(ShiftExchangeActivity.this, android.R.layout.simple_list_item_1, dates);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinnerShiftsToOfferUFG.setAdapter(adapter);
                    mSpinnerShiftsToOfferUFG.setBackgroundResource(R.drawable.black_white_border);
                }

                @Override
                public void onFailure(String errorString) {
                    Log.e(TAG, "Villa að ná í vaktir notanda: " + errorString);
                }
            }, userId);

            // Heldur utan um shift sem er valin í lista
            mSpinnerShiftsToOfferUFG.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mShiftForOffer = mUserShifts.get(mSpinnerShiftsToOfferUFG.getSelectedItemPosition());
                    Log.d(TAG, "shift valin úr lista: " + mShiftForOffer.getShiftId());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Log.d(TAG, "Ekkert valið úr lista");
                }
            });

            // Bregst við þegar ýtt er á takka til að bjóða vakt á móti
            mButtonOfferUFG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Ýtti á bjóða takka");
                    shiftExchangeService.offerShiftForExchange(new NetworkCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d(TAG, "Gekk að update-a shiftexchange með vakt til að bjóða á móti");
                            Toast.makeText(ShiftExchangeActivity.this, "Vakt var boðið á móti", Toast.LENGTH_SHORT).show();
                            Intent intent = ShiftExchangeListActivity.newIntent(ShiftExchangeActivity.this);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(String errorString) {
                            Log.e(TAG, "Ekki gekk að bjóða vakt á móti: " + errorString);
                            Toast.makeText(ShiftExchangeActivity.this, "Ekki gekk að bjóða vakt á móti", Toast.LENGTH_SHORT).show();
                        }
                    }, shiftExchangeId, mShiftForOffer.getShiftId());
                }
            });
        }
        // Pending view
        else if (shiftExchangeStatus.equals("pending")) {
            mLinearLayoutPending = (LinearLayout) findViewById(R.id.shiftexchange_pending);
            mTextViewShiftRolePending = (TextView) findViewById(R.id.shiftexchange_pending_shift_role);
            mTextViewShiftDatePending = (TextView) findViewById(R.id.shiftexchange_pending_shift_date);
            mTextViewShiftTimePending = (TextView) findViewById(R.id.shiftexchange_pending_shift_time);
            mTextViewOfferedShiftDatePending = (TextView) findViewById(R.id.shiftexchange_pending_offeredshift_date);
            mTextViewOfferedShiftTimePending = (TextView) findViewById(R.id.shiftexchange_pending_offeredshift_time);
            mButtonDeclinePending = (Button) findViewById(R.id.shiftexchange_pending_button_decline);
            mButtonAcceptPending = (Button) findViewById(R.id.shiftexchange_pending_button_accept);

            // Gera þetta view visible
            mLinearLayoutPending.setVisibility(View.VISIBLE);

            shiftExchangeService.getShiftExchangeById(new NetworkCallback<ShiftExchange>() {
                @Override
                public void onSuccess(ShiftExchange result) {
                    Log.d(TAG, "Gekk að ná í shiftexchange: " + shiftExchangeId);
                    mShiftExchange = result;
                    shiftExchangeService.getShiftById(new NetworkCallback<Shift>() {
                        @Override
                        public void onSuccess(Shift result) {
                            Log.d(TAG, "Gekk að ná í shift for exchange: " + result);
                            mShiftForExchange = result;
                            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
                            mTextViewShiftRolePending.setText(mShiftForExchange.getRole());
                            mTextViewShiftDatePending.setText(mShiftForExchange.getStartTime().format(dateFormat));
                            mTextViewShiftTimePending.setText(
                                    mShiftForExchange.getStartTime().format(timeFormat) + " - "
                                            + mShiftForExchange.getEndTime().format(timeFormat));
                        }

                        @Override
                        public void onFailure(String errorString) {
                            Log.e(TAG, "Villa að ná í shift for exchange: " + errorString);
                        }
                    }, mShiftExchange.getShiftForExchangeId());

                    shiftExchangeService.getShiftById(new NetworkCallback<Shift>() {
                        @Override
                        public void onSuccess(Shift result) {
                            Log.d(TAG, "Gekk að ná í shift for exchange: " + result);
                            mShiftForOffer = result;
                            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
                            mTextViewOfferedShiftDatePending.setText(mShiftForOffer.getStartTime().format(dateFormat));
                            mTextViewOfferedShiftTimePending.setText(
                                    mShiftForOffer.getStartTime().format(timeFormat) + " - "
                                            + mShiftForOffer.getEndTime().format(timeFormat));
                        }

                        @Override
                        public void onFailure(String errorString) {
                            Log.e(TAG, "Villa að ná í shift for offer: " + errorString);
                        }
                    }, mShiftExchange.getCoworkerShiftId());

                }

                @Override
                public void onFailure(String errorString) {
                    Log.e(TAG, "Villa við að ná í shiftexchange með id: " + shiftExchangeId + ": " + errorString);
                }
            }, shiftExchangeId);

            // Listener fyrir decline takka
            mButtonDeclinePending.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Ýtti á decline takka");
                    shiftExchangeService.declinePendingOffer(new NetworkCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d(TAG, "Gekk að decline-a pending offer");
                            Toast.makeText(ShiftExchangeActivity.this, "Boði hafnað", Toast.LENGTH_SHORT).show();
                            Intent intent = ShiftExchangeListActivity.newIntent(ShiftExchangeActivity.this);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(String errorString) {
                            Log.e(TAG, "Ekki gekk að hafna boði: " + errorString);
                            Toast.makeText(ShiftExchangeActivity.this, "Ekki gekk að hafna boði", Toast.LENGTH_SHORT).show();
                        }
                    }, shiftExchangeId);
                }
            });

            // Listener fyrir accept takka
            mButtonAcceptPending.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Ýtti á accept takka");
                    shiftExchangeService.acceptPendingOffer(new NetworkCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d(TAG, "Gekk að accepta-a pending offer");
                            Toast.makeText(ShiftExchangeActivity.this, "Boð samþykkt", Toast.LENGTH_SHORT).show();
                            Intent intent = ShiftExchangeListActivity.newIntent(ShiftExchangeActivity.this);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(String errorString) {
                            Log.e(TAG, "Ekki gekk að samþykkja boð: " + errorString);
                            Toast.makeText(ShiftExchangeActivity.this, "Ekki gekk að samþykkja boð", Toast.LENGTH_SHORT).show();
                        }
                    }, shiftExchangeId);
                }
            });
        }
        // Confirmable view
        else {
            mLinearLayoutConfirmable = (LinearLayout) findViewById(R.id.shiftexchange_confirmable);
            mTextViewShiftRoleConfirmable = (TextView) findViewById(R.id.shiftexchange_confirmable_shift_role);
            mTextViewShiftDateConfirmable = (TextView) findViewById(R.id.shiftexchange_confirmable_shift_date);
            mTextViewShiftTimeConfirmable = (TextView) findViewById(R.id.shiftexchange_confirmable_shift_time);
            mTextViewOfferedShiftDateConfirmable = (TextView) findViewById(R.id.shiftexchange_confirmable_offeredshift_date);
            mTextViewOfferedShiftTimeConfirmable = (TextView) findViewById(R.id.shiftexchange_confirmable_offeredshift_time);
            mButtonDeclineConfirmable = (Button) findViewById(R.id.shiftexchange_confirmable_button_decline);
            mButtonAcceptConfirmable = (Button) findViewById(R.id.shiftexchange_confirmable_button_accept);

            // Gera þetta view visible
            mLinearLayoutConfirmable.setVisibility(View.VISIBLE);

            shiftExchangeService.getShiftExchangeById(new NetworkCallback<ShiftExchange>() {
                @Override
                public void onSuccess(ShiftExchange result) {
                    Log.d(TAG, "Gekk að ná í shiftexchange: " + shiftExchangeId);
                    mShiftExchange = result;
                    shiftExchangeService.getShiftById(new NetworkCallback<Shift>() {
                        @Override
                        public void onSuccess(Shift result) {
                            Log.d(TAG, "Gekk að ná í shift for exchange: " + result);
                            mShiftForExchange = result;
                            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
                            mTextViewShiftRoleConfirmable.setText(mShiftForExchange.getRole());
                            mTextViewShiftDateConfirmable.setText(mShiftForExchange.getStartTime().format(dateFormat));
                            mTextViewShiftTimeConfirmable.setText(
                                    mShiftForExchange.getStartTime().format(timeFormat) + " - "
                                            + mShiftForExchange.getEndTime().format(timeFormat));
                        }

                        @Override
                        public void onFailure(String errorString) {
                            Log.e(TAG, "Villa að ná í shift for exchange: " + errorString);
                        }
                    }, mShiftExchange.getShiftForExchangeId());

                    shiftExchangeService.getShiftById(new NetworkCallback<Shift>() {
                        @Override
                        public void onSuccess(Shift result) {
                            Log.d(TAG, "Gekk að ná í shift for exchange: " + result);
                            mShiftForOffer = result;
                            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
                            mTextViewOfferedShiftDateConfirmable.setText(mShiftForOffer.getStartTime().format(dateFormat));
                            mTextViewOfferedShiftTimeConfirmable.setText(
                                    mShiftForOffer.getStartTime().format(timeFormat) + " - "
                                            + mShiftForOffer.getEndTime().format(timeFormat));
                        }

                        @Override
                        public void onFailure(String errorString) {
                            Log.e(TAG, "Villa að ná í shift for offer: " + errorString);
                        }
                    }, mShiftExchange.getCoworkerShiftId());

                }

                @Override
                public void onFailure(String errorString) {
                    Log.e(TAG, "Villa við að ná í shiftexchange með id: " + shiftExchangeId + ": " + errorString);
                }
            }, shiftExchangeId);

            // Listener fyrir decline takka
            mButtonDeclineConfirmable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Ýtti á decline takka");
                    shiftExchangeService.declineConfirmableOffer(new NetworkCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d(TAG, "Gekk að decline-a confirmable offer");
                            Toast.makeText(ShiftExchangeActivity.this, "Vaktaskiptum hafnað", Toast.LENGTH_SHORT).show();
                            Intent intent = ShiftExchangeListActivity.newIntent(ShiftExchangeActivity.this);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(String errorString) {
                            Log.e(TAG, "Ekki gekk að hafna vaktaskiptum: " + errorString);
                            Toast.makeText(ShiftExchangeActivity.this, "Ekki gekk að hafna vaktaskiptum", Toast.LENGTH_SHORT).show();
                        }
                    }, shiftExchangeId);
                }
            });

            // Listener fyrir accept takka
            mButtonAcceptConfirmable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Ýtti á accept takka");
                    shiftExchangeService.acceptConfirmableOffer(new NetworkCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d(TAG, "Gekk að accepta-a vaktaskipti");
                            Toast.makeText(ShiftExchangeActivity.this, "Vaktaskipti samþykkt", Toast.LENGTH_SHORT).show();
                            Intent intent = ShiftExchangeListActivity.newIntent(ShiftExchangeActivity.this);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(String errorString) {
                            Log.e(TAG, "Ekki gekk að samþykkja vaktaskipti: " + errorString);
                            Toast.makeText(ShiftExchangeActivity.this, "Ekki gekk að samþykkja vaktaskipti", Toast.LENGTH_SHORT).show();
                        }
                    }, shiftExchangeId);
                }
            });
        }
    }
}