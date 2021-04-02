package is.hi.finnbogi_mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import is.hi.finnbogi_mobile.entities.Notification;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;
import is.hi.finnbogi_mobile.services.NotificationsService;

public class OneNotificationActivity extends AppCompatActivity {

    private static final String TAG = "OneNotificationActivity";
    private static final String NOTIFICATION_KEY = "currentNotification";
    private static final String MY_PREFERENCES = "Session";

    private TextView mTextViewTitle;
    private TextView mTextViewText;

    private Notification mNotification;

    public static Intent newIntent(Context packageContext, int notificationId) {
        Intent intent = new Intent(packageContext, OneNotificationActivity.class);
        intent.putExtra(NOTIFICATION_KEY, notificationId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_notification);

        NetworkManager networkManager = NetworkManager.getInstance(this);
        NotificationsService notificationsService = new NotificationsService(networkManager);

        SharedPreferences sharedPref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);

        mTextViewTitle = (TextView) findViewById(R.id.one_notification_title);
        mTextViewText = (TextView) findViewById(R.id.one_notification_text);

        int notificationId = getIntent().getIntExtra(NOTIFICATION_KEY, -1);

        notificationsService.getNotificationById(new NetworkCallback<Notification>() {
            @Override
            public void onSuccess(Notification result) {
                Log.d(TAG, "Gekk að ná í notification: " + notificationId);
                mNotification = result;
                mTextViewTitle.setText(mNotification.getTitle());
                mTextViewText.setText(mNotification.getText());
                // Setja notification sem 'read'
                notificationsService.updateNotification(notificationId, sharedPref.getInt("userId", -1));
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Villa við að ná í notification með id: " + notificationId + ": " + errorString);
            }
        }, notificationId);
    }
}