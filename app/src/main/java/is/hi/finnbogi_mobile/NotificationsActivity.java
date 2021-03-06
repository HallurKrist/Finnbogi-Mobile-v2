package is.hi.finnbogi_mobile;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

    // Viðmótshlutir
    private ListView mList;

    // Global breytur
    private List<Notification> mAllNotifications;

    /**
     * Upphafsstillir alla viðmótshluti, nær í gögn og setur hlustara.
     *
     * @param savedInstanceState
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        mList = (ListView) findViewById(R.id.notification_list);

        SharedPreferences sharedPref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);

        NetworkManager networkManager = NetworkManager.getInstance(this);
        NotificationsService notificationsService = new NotificationsService(networkManager);

        /**
         * Nær í öll Notification sem innskráður notandi
         * er venslaður við og setur lista með þeim.
         *
         */
        notificationsService.getAllNotifications(new NetworkCallback<List<Notification>>() {
            @Override
            public void onSuccess(List<Notification> result) {
                mAllNotifications = result;
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
                Log.e(TAG, errorString);
            }
        }, sharedPref.getInt("userId", -1));

        /**
         * Event listener fyrir lista af Notifiction.
         * Hlustar á hvaða stak er smellt á í lista og opnar
         * OneNotificationActivity með þeim Notification hlut
         * sem smellt er á.
         *
         */
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Notification notification = mAllNotifications.get(position);
                Intent intent = OneNotificationActivity.newIntent(NotificationsActivity.this, notification.getNotificationId());
                startActivity(intent);
            }
        });
    }
}