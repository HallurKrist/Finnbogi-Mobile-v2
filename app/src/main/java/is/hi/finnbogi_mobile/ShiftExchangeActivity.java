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
    private static final String MY_PREFERENCES = "Session";

    private TextView mTextViewShiftRole;
    private TextView mTextViewShiftDate;
    private TextView mTextViewShiftTime;
    private Spinner mSpinnerShiftsToOffer;
    private TextView mTextViewSpecializedMessage;
    private Button mButton;

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
    public static Intent newIntent(Context packageContext, int shiftExchangeId) {
        Intent intent = new Intent(packageContext, ShiftExchangeActivity.class);
        intent.putExtra(SHIFT_EXCHANGE_KEY, shiftExchangeId);
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

        //TODO: check what state the shiftexchange is in through the intent, show correct view for that state.

        mTextViewShiftRole = (TextView) findViewById(R.id.shiftexchange_upforgrabs_shift_role);
        mTextViewShiftDate = (TextView) findViewById(R.id.shiftexchange_upforgrabs_shift_date);
        mTextViewShiftTime = (TextView) findViewById(R.id.shiftexchange_upforgrabs_shift_time);
        mSpinnerShiftsToOffer = (Spinner) findViewById(R.id.shiftexchange_upforgrabs_shifts_to_offer);
        mTextViewSpecializedMessage = (TextView) findViewById(R.id.shiftexchange_upforgrabs_message);
        mButton = (Button) findViewById(R.id.shiftexchange_upforgrabs_button);

        int shiftExchangeId = getIntent().getIntExtra(SHIFT_EXCHANGE_KEY, -1);
        int userId = sharedPref.getInt("userId", -1);

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
                        mTextViewShiftRole.setText(mShiftForExchange.getRole());
                        mTextViewShiftDate.setText(mShiftForExchange.getStartTime().format(dateFormat));
                        mTextViewShiftTime.setText(
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
                mSpinnerShiftsToOffer.setAdapter(adapter);
                mSpinnerShiftsToOffer.setBackgroundResource(R.drawable.black_white_border);
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Villa að ná í vaktir notanda: " + errorString);
            }
        }, userId);

        // Heldur utan um shift sem er valin í lista
        mSpinnerShiftsToOffer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mShiftForOffer = mUserShifts.get(mSpinnerShiftsToOffer.getSelectedItemPosition());
                Log.d(TAG, "shift valin úr lista: " + mShiftForOffer.getShiftId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "Ekkert valið úr lista");
            }
        });

        // Bregst við þegar ýtt er á takka til að bjóða vakt á móti
        mButton.setOnClickListener(new View.OnClickListener() {
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
                        Toast.makeText(ShiftExchangeActivity.this, "Ekki gekk að bjóða vakt á móti", Toast.LENGTH_SHORT).show();
                    }
                }, shiftExchangeId, mShiftForOffer.getShiftId());
            }
        });

        /*
        boolean UFG = true;

        if (UFG) {
            Log.d(TAG, "onCreate: in if");
            //mock list
            String[] title = {"title1","title2","title3","title4","title5",
                    "title1","title2","title3","title4","title5",
                    "title1","title2","title3","title4","title5"};

            ArrayAdapter<String> adapter =
                    new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, title);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpinnerShiftsToOffer.setAdapter(adapter);
            mSpinnerShiftsToOffer.setBackgroundResource(R.drawable.black_white_border);

            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: shift " + mSpinnerShiftsToOffer.getSelectedItem().toString() + " chosen");
                }
            });
        } else {
            Log.d(TAG, "onCreate: in else");
            mSpinnerShiftsToOffer.setAdapter(null);
            mSpinnerShiftsToOffer.setVisibility(View.INVISIBLE);
            mSpinnerShiftsToOffer.getParent().bringChildToFront(mSpinnerShiftsToOffer);

            mLinearLayoutOffer.setVisibility(View.VISIBLE);
            mLinearLayoutOffer.setBackgroundResource(R.drawable.black_white_border);

            mTextViewOfferShiftDate.setText("02.02.2022");
            mTextViewOfferShiftTime.setText("08:00 - 16:00");

            boolean employeeAccepts = true;
            boolean managerAccepts = false;

            if (!employeeAccepts ) {
                Log.d(TAG, "onCreate: !employeeAccepts");
                mTextViewSpecializedMessage.setText("Beðið eftir að Jón samþykki");
                mTextViewSpecializedMessage.setVisibility(View.VISIBLE);
            } else if (employeeAccepts && !managerAccepts) {
                Log.d(TAG, "onCreate: !employeeAccepts // !managerAccepts");
                mTextViewSpecializedMessage.setText("Beðið eftir að yfirmaður samþykki");
                mTextViewSpecializedMessage.setVisibility(View.VISIBLE);
            } else  {
                Log.d(TAG, "onCreate: á ekki að fara hingað");
                //TODO: eyða shiftexchange þar sem að búið að samþykkja (það þarf að vera búið að skipta um vaktir og þannig)
                // shiftexhange er aldrei í þessu state.
            }

        }

         */


    }
}