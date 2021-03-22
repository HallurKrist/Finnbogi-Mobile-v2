package is.hi.finnbogi_mobile;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import is.hi.finnbogi_mobile.listAdapters.NotificationListAdapter;
import is.hi.finnbogi_mobile.listAdapters.UserListAdapter;

public class UserListActivity extends AppCompatActivity {
    //TODO: this class

    private ListView mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        //mock list
        String[] names = {"name1","name2","name3","name4","name5","name1","name2","name3","name4","name5","name1","name2","name3","name4","name5",
                "name1","name2","name3","name4","name5","name1","name2","name3","name4","name5","name1","name2","name3","name4","name5"};
        String[] roles = {"Waiter","cook","Bartender","Waiter","cook","Waiter","cook","Bartender","Waiter","cook","Waiter","cook","Bartender","Waiter","cook",
                "Waiter","cook","Bartender","Waiter","cook","Waiter","cook","Bartender","Waiter","cook","Waiter","cook","Bartender","Waiter","cook"};
        UserListAdapter adapter = new UserListAdapter(this, names, roles);

        mList = (ListView) findViewById(R.id.user_list);
        mList.setAdapter(adapter);

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    //code specific to first list item
                    Toast.makeText(getApplicationContext(),"Place Your First Option Code",Toast.LENGTH_SHORT).show();
                }

                else if(position == 1) {
                    //code specific to 2nd list item
                    Toast.makeText(getApplicationContext(),"Place Your Second Option Code",Toast.LENGTH_SHORT).show();
                }

                else if(position == 2) {

                    Toast.makeText(getApplicationContext(),"Place Your Third Option Code",Toast.LENGTH_SHORT).show();
                }
                else if(position == 3) {

                    Toast.makeText(getApplicationContext(),"Place Your Forth Option Code",Toast.LENGTH_SHORT).show();
                }
                else if(position == 4) {

                    Toast.makeText(getApplicationContext(),"Place Your Fifth Option Code",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}