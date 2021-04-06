package is.hi.finnbogi_mobile;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import is.hi.finnbogi_mobile.entities.Shift;
import is.hi.finnbogi_mobile.entities.ShiftExchange;
import is.hi.finnbogi_mobile.listAdapters.ShiftExchangeListAdapter;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;
import is.hi.finnbogi_mobile.services.ShiftExchangeService;

public class ShiftExchangeListActivity extends AppCompatActivity {

    private static final String TAG = "ShiftExchangesActivity";

    private ListView mList;

    private List<ShiftExchange> mShiftExchangesList;
    private List<Shift> mShifts;
    private List<String> roles = new ArrayList<>();
    private List<String> dates = new ArrayList<>();
    private List<String> statuses = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shiftexchange_list);

        mList = (ListView) findViewById(R.id.shiftexchange_list);

        NetworkManager networkManager = NetworkManager.getInstance(this);
        ShiftExchangeService shiftExchangeService = new ShiftExchangeService(networkManager);

        shiftExchangeService.getAllShiftExchanges(new NetworkCallback<List<ShiftExchange>>() {
            @Override
            public void onSuccess(List<ShiftExchange> result) {
                mShiftExchangesList = result;
                Log.d(TAG, "Gekk að ná í lista af shiftExchanges: " + String.valueOf(mShiftExchangesList));
                Log.d(TAG, "Næ í lista af shifts");
                shiftExchangeService.getShiftsForExchange(new NetworkCallback<List<Shift>>() {
                    @Override
                    public void onSuccess(List<Shift> result) {
                        mShifts = result;
                        Log.d(TAG, "Gekk að ná í lista af shifts for exchange: " + String.valueOf(mShifts));
                        int n = mShiftExchangesList.size();
                        String[] role = new String[n];
                        String[] date = new String[n];
                        String[] status = new String[n];
                        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH:mm");
                        for (int i = 0; i < n; i++) {
                            role[i] = mShifts.get(i).getRole();
                            date[i] = mShifts.get(i).getStartTime().format(dateFormat) + " - " + mShifts.get(i).getEndTime().format(dateFormat);
                            status[i] = mShiftExchangesList.get(i).getStatus();
                        }
                        ShiftExchangeListAdapter adapter = new ShiftExchangeListAdapter(
                                ShiftExchangeListActivity.this, role, date, status);
                        mList.setAdapter(adapter);
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
                Log.d(TAG, "ShiftExchange nr. " + position + " í lista");
                ShiftExchange shiftExchange = mShiftExchangesList.get(position);
                Log.d(TAG, "id: " + shiftExchange.getShiftExchangeId());
                Intent intent = ShiftExchangeActivity.newIntent(ShiftExchangeListActivity.this, shiftExchange.getShiftExchangeId());
                startActivity(intent);
            }
        });
    }
}