package is.hi.finnbogi_mobile;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import is.hi.finnbogi_mobile.entities.Notification;
import is.hi.finnbogi_mobile.listAdapters.NotificationListAdapter;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;
import is.hi.finnbogi_mobile.services.NotificationsService;

public class NotificationsActivity extends AppCompatActivity {

    private static final String TAG = "NotificationsActivity";
    private static final String MY_PREFERENCES = "Session";

    private ListView mList;

    private List<Notification> mAllNotifications;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        mList = (ListView) findViewById(R.id.notification_list);

        SharedPreferences sharedPref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);

        NetworkManager networkManager = NetworkManager.getInstance(this);
        NotificationsService notificationsService = new NotificationsService(networkManager);

        notificationsService.getAllNotifications(new NetworkCallback<List<Notification>>() {
            @Override
            public void onSuccess(List<Notification> result) {
                mAllNotifications = result;
                Log.d(TAG, "Gekk að ná í lista af notifications: " + String.valueOf(mAllNotifications));
                int n = mAllNotifications.size();
                String[] title = new String[n];
                String[] message = new String[n];
                // Setjum upplýsingar inn í fylkin fyrir hvert notification fyrir ListView
                int i = 0;
                for (Notification notification : mAllNotifications) {
                    title[i] = notification.getTitle();
                    message[i] = notification.getText();
                    i++;
                }
                NotificationListAdapter adapter = new NotificationListAdapter(NotificationsActivity.this, title, message);
                mList.setAdapter(adapter);
            }

            @Override
            public void onFailure(String errorString) {
                mAllNotifications = null;
                Log.e(TAG, "Villa að ná í allar tilkynningar: " + errorString);
            }
        }, sharedPref.getInt("userId", -1));

        // TODO: Viljum við geta klikkað á notification?
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