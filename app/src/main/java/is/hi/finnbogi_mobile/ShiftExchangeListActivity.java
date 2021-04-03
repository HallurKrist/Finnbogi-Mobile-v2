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
import android.widget.ListView;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import is.hi.finnbogi_mobile.entities.Shift;
import is.hi.finnbogi_mobile.entities.ShiftExchange;
import is.hi.finnbogi_mobile.entities.UserInfo;
import is.hi.finnbogi_mobile.listAdapters.ShiftExchangeListAdapter;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;
import is.hi.finnbogi_mobile.services.ShiftExchangeService;
import is.hi.finnbogi_mobile.services.ShiftService;
import is.hi.finnbogi_mobile.services.UserInfoService;

public class ShiftExchangeListActivity extends AppCompatActivity {

    private static final String TAG = "ShiftExchangesActivity";
    private static final String MY_PREFERENCES = "Session";

    private ListView mList;

    private List<ShiftExchange> mShiftExchangesList;
    private List<Shift> mShiftsUpForGrabs;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shiftexchange_list);

        mList = (ListView) findViewById(R.id.shiftexchange_list);

        SharedPreferences sharedPref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);

        NetworkManager networkManager = NetworkManager.getInstance(this);
        ShiftExchangeService shiftExchangeService = new ShiftExchangeService(networkManager);
        ShiftService shiftService = new ShiftService(networkManager);
        UserInfoService userInfoService = new UserInfoService(networkManager);

        // Get all shiftexchanges up for grabs and show them
        shiftExchangeService.getAllShiftExchanges(new NetworkCallback<List<ShiftExchange>>() {
            @Override
            public void onSuccess(List<ShiftExchange> result) {
                mShiftExchangesList = result;
                Log.d(TAG, "Gekk að ná í lista af shiftexchanges: " + String.valueOf(mShiftExchangesList));
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Villa við að ná í shiftexchanges: " + errorString);
            }
        });


        /*
        //mock list
        String[] names = {"name1","name2","name3","name4","name5","name1","name2","name3","name4","name5","name1","name2","name3","name4","name5",
                "name1","name2","name3","name4","name5","name1","name2","name3","name4","name5","name1","name2","name3","name4","name5"};
        String[] roles = {"Waiter","cook","Bartender","Waiter","cook","Waiter","cook","Bartender","Waiter","cook","Waiter","cook","Bartender","Waiter","cook",
                "Waiter","cook","Bartender","Waiter","cook","Waiter","cook","Bartender","Waiter","cook","Waiter","cook","Bartender","Waiter","cook"};
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String date = (LocalDateTime.now()).format(dateFormat);
        String[] dates = {date,date,date,date,date,date,date,date,date,date,date,date,date,date,date,date,date,date,date,date,date,date,date,date,date,date,date,date,date,date};
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
        String startTime = (LocalDateTime.now()).format(timeFormat);
        String endTime = (LocalDateTime.now()).plusHours(8).format(timeFormat);
        String time = startTime + " - " + endTime;
        String[] times = {time,time,time,time,time,time,time,time,time,time,time,time,time,time,time,time,time,time,time,time,time,time,time,time,time,time,time,time,time,time};
        int[] statuses = {0,1,0,1,1,0,1,0,1,1,0,1,0,1,1,0,1,0,1,1,0,1,0,1,1,0,1,0,1,1};

        ShiftExchangeListAdapter adapter = new ShiftExchangeListAdapter(this, names, roles, dates, times, statuses);

        mList = (ListView) findViewById(R.id.shiftexchange_list);
        mList.setAdapter(adapter);

         */

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    //code specific to first list item
                    Toast.makeText(getApplicationContext(),"Place Your First Option Code",Toast.LENGTH_SHORT).show();
                    Intent shiftexchangeIntent = new Intent(ShiftExchangeListActivity.this, ShiftExchangeActivity.class);
                    startActivity(shiftexchangeIntent);
                }

                else if(position == 1) {
                    //code specific to 2nd list item
                    Toast.makeText(getApplicationContext(),"Place Your Second Option Code",Toast.LENGTH_SHORT).show();
                }

                else if(position == 2) {

                    Toast.makeText(getApplicationContext(),"Place Your Third Option Code",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}