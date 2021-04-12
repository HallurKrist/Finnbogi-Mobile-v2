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

    // Up for grabs viðmót hlutir
    private LinearLayout mLinearLayoutUFG;
    private TextView mTextViewShiftRoleUFG;
    private TextView mTextViewShiftDateUFG;
    private TextView mTextViewShiftTimeUFG;
    private Spinner mSpinnerShiftsToOfferUFG;
    private Button mButtonOfferUFG;

    // Pending viðmót hlutir
    private LinearLayout mLinearLayoutPending;
    private TextView mTextViewShiftRolePending;
    private TextView mTextViewShiftDatePending;
    private TextView mTextViewShiftTimePending;
    private TextView mTextViewOfferedShiftDatePending;
    private TextView mTextViewOfferedShiftTimePending;
    private Button mButtonDeclinePending;
    private Button mButtonAcceptPending;

    // Confirmable viðmót hlutir
    private LinearLayout mLinearLayoutConfirmable;
    private TextView mTextViewShiftRoleConfirmable;
    private TextView mTextViewShiftDateConfirmable;
    private TextView mTextViewShiftTimeConfirmable;
    private TextView mTextViewOfferedShiftDateConfirmable;
    private TextView mTextViewOfferedShiftTimeConfirmable;
    private Button mButtonDeclineConfirmable;
    private Button mButtonAcceptConfirmable;

    // Aðrar global breytur
    private ShiftExchange mShiftExchange;
    private Shift mShiftForExchange;
    private Shift mShiftForOffer;
    private List<Shift> mUserShifts;

    /**
     * Aðferð fyrir aðra klasa að búa til nýtt intent fyrir þetta activity.
     *
     * @param packageContext Gamli activity klasinn.
     * @param shiftExchangeId Id fyrir ShiftExchange sem er verið að opna.
     * @param shiftExchangeStatus Status á ShiftExchange sem er verið að opna.
     * @return intent
     */
    public static Intent newIntent(Context packageContext, int shiftExchangeId, String shiftExchangeStatus) {
        Intent intent = new Intent(packageContext, ShiftExchangeActivity.class);
        intent.putExtra(SHIFT_EXCHANGE_KEY, shiftExchangeId);
        intent.putExtra(SHIFT_EXCHANGE_STATUS, shiftExchangeStatus);
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
        setContentView(R.layout.activity_shiftexchange);

        NetworkManager networkManager = NetworkManager.getInstance(this);
        ShiftExchangeService shiftExchangeService = new ShiftExchangeService(networkManager);

        SharedPreferences sharedPref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);

        int shiftExchangeId = getIntent().getIntExtra(SHIFT_EXCHANGE_KEY, -1);
        String shiftExchangeStatus = getIntent().getStringExtra(SHIFT_EXCHANGE_STATUS);
        int userId = sharedPref.getInt("userId", -1);

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

            /**
             * Nær í ShiftExchange hlutinn sem á að skoða og setur viðmótshluti.
             *
             */
            shiftExchangeService.getShiftExchangeById(new NetworkCallback<ShiftExchange>() {
                @Override
                public void onSuccess(ShiftExchange result) {
                    mShiftExchange = result;
                    /**
                     * Nær í vakt sem er í boði og setur viðmótshluti.
                     *
                     */
                    shiftExchangeService.getShiftById(new NetworkCallback<Shift>() {
                        @Override
                        public void onSuccess(Shift result) {
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
                            Log.e(TAG, errorString);
                        }
                    }, mShiftExchange.getShiftForExchangeId());

                }

                @Override
                public void onFailure(String errorString) {
                    Log.e(TAG, errorString);
                }
            }, shiftExchangeId);

            /**
             * Nær í allar vaktir notanda og setur spinner-inn með vöktunum.
             *
             */
            shiftExchangeService.getUserShifts(new NetworkCallback<List<Shift>>() {
                @Override
                public void onSuccess(List<Shift> result) {
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
                    Log.e(TAG, errorString);
                }
            }, userId);

            /**
             * Event listener fyrir spinner. Heldur utan um hvaða vakt er valin.
             *
             */
            mSpinnerShiftsToOfferUFG.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mShiftForOffer = mUserShifts.get(mSpinnerShiftsToOfferUFG.getSelectedItemPosition());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });

            /**
             * Event listener fyrir takkann. Býður aðra vakt á móti
             * og fer til baka í ShiftExchangeListActivity.
             *
             */
            mButtonOfferUFG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shiftExchangeService.offerShiftForExchange(new NetworkCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Toast.makeText(ShiftExchangeActivity.this, getString(R.string.shiftexchange_activity_offer_success), Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onFailure(String errorString) {
                            Log.e(TAG, errorString);
                            Toast.makeText(ShiftExchangeActivity.this, getString(R.string.shiftexchange_activity_offer_fail), Toast.LENGTH_SHORT).show();
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

            /**
             * Nær í ShiftExchange hlutinn sem á að skoða og setur viðmótshluti.
             *
             */
            shiftExchangeService.getShiftExchangeById(new NetworkCallback<ShiftExchange>() {
                @Override
                public void onSuccess(ShiftExchange result) {
                    mShiftExchange = result;
                    /**
                     * Nær í vakt sem er í boði og setur viðmótshluti.
                     *
                     */
                    shiftExchangeService.getShiftById(new NetworkCallback<Shift>() {
                        @Override
                        public void onSuccess(Shift result) {
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
                            Log.e(TAG, errorString);
                        }
                    }, mShiftExchange.getShiftForExchangeId());

                    /**
                     * Nær í vakt sem er boðið á móti og setur viðmótshluti.
                     *
                     */
                    shiftExchangeService.getShiftById(new NetworkCallback<Shift>() {
                        @Override
                        public void onSuccess(Shift result) {
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
                            Log.e(TAG, errorString);
                        }
                    }, mShiftExchange.getCoworkerShiftId());

                }

                @Override
                public void onFailure(String errorString) {
                    Log.e(TAG, errorString);
                }
            }, shiftExchangeId);

            /**
             * Event listener fyrir hafna takkann. Hafnar boði og
             * fer til baka í ShiftExchangeListActivity.
             *
             */
            mButtonDeclinePending.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shiftExchangeService.declinePendingOffer(new NetworkCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Toast.makeText(ShiftExchangeActivity.this, getString(R.string.shiftexchange_activity_offer_declined_success), Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onFailure(String errorString) {
                            Log.e(TAG, errorString);
                            Toast.makeText(ShiftExchangeActivity.this, getString(R.string.shiftexchange_activity_offer_declined_fail), Toast.LENGTH_SHORT).show();
                        }
                    }, shiftExchangeId);
                }
            });

            /**
             * Event listener fyrir samþykkja takkann. Samþykkir boð og
             * fer til baka í ShiftExchangeListActivity.
             *
             */
            mButtonAcceptPending.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shiftExchangeService.acceptPendingOffer(new NetworkCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Toast.makeText(ShiftExchangeActivity.this, getString(R.string.shiftexchange_activity_offer_accept_success), Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onFailure(String errorString) {
                            Log.e(TAG, errorString);
                            Toast.makeText(ShiftExchangeActivity.this, getString(R.string.shiftexchange_activity_offer_accept_fail), Toast.LENGTH_SHORT).show();
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

            /**
             * Nær í ShiftExchange hlutinn sem á að skoða og setur viðmótshluti.
             *
             */
            shiftExchangeService.getShiftExchangeById(new NetworkCallback<ShiftExchange>() {
                @Override
                public void onSuccess(ShiftExchange result) {
                    mShiftExchange = result;
                    /**
                     * Nær í vakt sem er í boði og setur viðmótshluti.
                     *
                     */
                    shiftExchangeService.getShiftById(new NetworkCallback<Shift>() {
                        @Override
                        public void onSuccess(Shift result) {
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
                            Log.e(TAG, errorString);
                        }
                    }, mShiftExchange.getShiftForExchangeId());

                    /**
                     * Nær í vakt sem er boðið á móti og setur viðmótshluti.
                     *
                     */
                    shiftExchangeService.getShiftById(new NetworkCallback<Shift>() {
                        @Override
                        public void onSuccess(Shift result) {
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
                            Log.e(TAG, errorString);
                        }
                    }, mShiftExchange.getCoworkerShiftId());

                }

                @Override
                public void onFailure(String errorString) {
                    Log.e(TAG, errorString);
                }
            }, shiftExchangeId);

            /**
             * Event listener fyrir hafna takkann. Hafnar vaktaskiptum og
             * fer til baka í ShiftExchangeListActivity.
             *
             */
            mButtonDeclineConfirmable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shiftExchangeService.declineConfirmableOffer(new NetworkCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Toast.makeText(ShiftExchangeActivity.this, getString(R.string.shiftexchange_activity_declined_success), Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onFailure(String errorString) {
                            Log.e(TAG, errorString);
                            Toast.makeText(ShiftExchangeActivity.this, getString(R.string.shiftexchange_activity_declined_fail), Toast.LENGTH_SHORT).show();
                        }
                    }, shiftExchangeId);
                }
            });

            /**
             * Event listener fyrir samþykkja takkann. Samþykkir
             * vaktaskipti og fer til baka í ShiftExchangeListActivity.
             *
             */
            mButtonAcceptConfirmable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shiftExchangeService.acceptConfirmableOffer(new NetworkCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Toast.makeText(ShiftExchangeActivity.this, getString(R.string.shiftexchange_activity_confirm_success), Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onFailure(String errorString) {
                            Log.e(TAG, errorString);
                            Toast.makeText(ShiftExchangeActivity.this, getString(R.string.shiftexchange_activity_confirm_fail), Toast.LENGTH_SHORT).show();
                        }
                    }, shiftExchangeId);
                }
            });
        }
    }
}