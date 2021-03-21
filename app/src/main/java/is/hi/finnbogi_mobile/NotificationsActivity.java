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

public class NotificationsActivity extends AppCompatActivity {
    //TODO: this class

    private ListView list;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        //mock list
        String[] title = {"title1","title2","title3","title4","title5","title1","title2","title3","title4","title5","title1","title2","title3","title4","title5"};
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String date = (LocalDateTime.now()).format(format);
        String[] dateTime = {date,date,date,date,date,date,date,date,date,date,date,date,date,date,date};
        String[] message = {"message1","message2","message3","a long message with alot of words and possible \n some enter and \n\t tabs","message5","message1","message2","message3","message4","message5","message1","message2","message3","message4","message5"};
        NotificationListAdapter adapter = new NotificationListAdapter(this, title, dateTime, message);

        list = (ListView) findViewById(R.id.notification_list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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