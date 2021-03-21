package is.hi.finnbogi_mobile;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import is.hi.finnbogi_mobile.listAdapters.ShiftexchangeListAdapter;
import is.hi.finnbogi_mobile.listAdapters.UserListAdapter;

public class ShiftExchangeListActivity extends AppCompatActivity {
    //TODO: this class

    private ListView mList;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shiftexchange_list);

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

        ShiftexchangeListAdapter adapter = new ShiftexchangeListAdapter(this, names, roles, dates, times, statuses);

        mList = (ListView) findViewById(R.id.shiftexchange_list);
        mList.setAdapter(adapter);

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    //code specific to first list item
                    Toast.makeText(getApplicationContext(),"Place Your First Option Code",Toast.LENGTH_SHORT).show();
                    Intent shiftexchangeIntent = new Intent(ShiftExchangeListActivity.this, ShiftexchangeActivity.class);
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