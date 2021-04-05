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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import is.hi.finnbogi_mobile.networking.NetworkManager;
import is.hi.finnbogi_mobile.services.ShiftExchangeService;

public class ShiftExchangeActivity extends AppCompatActivity {

    private static final String TAG = "ShiftExchangeActivity";
    private static final String SHIFT_EXCHANGE_KEY = "currentShiftExchange";
    private static final String MY_PREFERENCES = "Session";

    private ListView mList;

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

        Log.d(TAG, "onCreate: creating");
        //TODO: check what state the shiftexchange is in through the intent, show correct view for that state.

        Spinner shifts = (Spinner) findViewById(R.id.shiftexchange_shifts_to_offer);
        Button offerOrAccept = (Button) findViewById(R.id.shiftexchange_button);
        LinearLayout theOffer = (LinearLayout) findViewById(R.id.shiftexchange_offer);

        TextView employeeName = (TextView) findViewById(R.id.shiftexchange_name);
        TextView originShiftDate = (TextView) findViewById(R.id.shiftexchange_shift_date);
        TextView originShiftTime = (TextView) findViewById(R.id.shiftexchange_shift_time);
        TextView offerShiftDate = (TextView) findViewById(R.id.shiftexchange_offer_shift_date);
        TextView offerShiftTime = (TextView) findViewById(R.id.shiftexchange_offer_shift_time);

        TextView specializedMessage = (TextView) findViewById(R.id.shiftexchange_message);

        employeeName.setText("Jón jónsson");
        originShiftDate.setText("01.01.2021");
        originShiftTime.setText("08:00 - 16:00");

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
            shifts.setAdapter(adapter);
            shifts.setBackgroundResource(R.drawable.black_white_border);

            offerOrAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: shift " + shifts.getSelectedItem().toString() + " chosen");
                }
            });
        } else {
            Log.d(TAG, "onCreate: in else");
            shifts.setAdapter(null);
            shifts.setVisibility(View.INVISIBLE);
            shifts.getParent().bringChildToFront(shifts);

            theOffer.setVisibility(View.VISIBLE);
            theOffer.setBackgroundResource(R.drawable.black_white_border);

            offerShiftDate.setText("02.02.2022");
            offerShiftTime.setText("08:00 - 16:00");

            boolean employeeAccepts = true;
            boolean managerAccepts = false;

            if (!employeeAccepts ) {
                Log.d(TAG, "onCreate: !employeeAccepts");
                specializedMessage.setText("Beðið eftir að Jón samþykki");
                specializedMessage.setVisibility(View.VISIBLE);
            } else if (employeeAccepts && !managerAccepts) {
                Log.d(TAG, "onCreate: !employeeAccepts // !managerAccepts");
                specializedMessage.setText("Beðið eftir að yfirmaður samþykki");
                specializedMessage.setVisibility(View.VISIBLE);
            } else  {
                Log.d(TAG, "onCreate: á ekki að fara hingað");
                //TODO: eyða shiftexchange þar sem að búið að samþykkja (það þarf að vera búið að skipta um vaktir og þannig)
                // shiftexhange er aldrei í þessu state.
            }

        }


    }
}