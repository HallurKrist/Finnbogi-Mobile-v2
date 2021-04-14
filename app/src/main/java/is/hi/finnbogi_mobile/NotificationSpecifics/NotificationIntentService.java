package is.hi.finnbogi_mobile.NotificationSpecifics;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import is.hi.finnbogi_mobile.NotificationsActivity;
import is.hi.finnbogi_mobile.R;
import is.hi.finnbogi_mobile.ShiftActivity;
import is.hi.finnbogi_mobile.entities.ShiftExchange;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;

public class NotificationIntentService extends IntentService {
    private final String TAG = "NotificationIntentS";
    public static final int NOTIFICATION_ID = 888;
    public static final String CHANNEL_ID = "NotificationIntentService";
    public static final String USERID_ID = "NotificationIntentService_userid";

    public NotificationIntentService() {
        super("NotificationIntentService");
    }

    public static Intent newIntent(Context packageContext, int userId) {
        Intent intent = new Intent(packageContext, NotificationIntentService.class);
        intent.putExtra(USERID_ID, userId);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        createNotificationChannel();

        NetworkManager.getInstance(this).GET(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<is.hi.finnbogi_mobile.entities.Notification>>(){}.getType();
                List<is.hi.finnbogi_mobile.entities.Notification> allNotifications = gson.fromJson(result, listType);

                for (int i = 0; i < allNotifications.size(); i++) {
                    is.hi.finnbogi_mobile.entities.Notification notif = allNotifications.get(i);

                    if (!notif.getRead()) {

                        Intent notifIntent = new Intent(NotificationIntentService.this, NotificationsActivity.class);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(NotificationIntentService.this);
                        stackBuilder.addNextIntentWithParentStack(notifIntent);
                        // Get the PendingIntent containing the entire back stack
                        PendingIntent pendingIntent =
                                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                        Notification notification = new NotificationCompat.Builder(NotificationIntentService.this, CHANNEL_ID)
                                .setSmallIcon(R.mipmap.sm_icon_foreground)
                                .setContentTitle(notif.getTitle())
                                .setContentText(notif.getText())
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true)
                                .build();

                        NotificationManagerCompat.from(getApplicationContext()).notify(notif.getNotificationId(), notification);
                    }

                }

                Log.i(TAG, "All new notifications sent to phone");
            }

            @Override
            public void onFailure(String errorString) {
                Log.d(TAG, "error getting notifications from API");
            }
        }, new String[] {"notifications", "user", ""+intent.getIntExtra(USERID_ID, -1)});

        Log.i(TAG, "Notification Intent Service running");
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
