package is.hi.finnbogi_mobile.services;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import is.hi.finnbogi_mobile.entities.Notification;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;

public class NotificationsService {

    private static final String TAG = "NotificationsService";

    NetworkManager mNetworkManager;

    public NotificationsService(NetworkManager networkManager) {
        mNetworkManager = networkManager;
    }

    /**
     * Býr til path og kallar á network fall.
     *
     * @param callback - Fall sem tekur við þegar network kall er búið.
     * @param userId - Id á notanda.
     */
    public void getAllNotifications(NetworkCallback<List<Notification>> callback, int userId) {
        Log.d(TAG, "næ í öll notification fyrir user: " + userId);
        mNetworkManager.GET(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "Gson-a lista af notifications");
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Notification>>(){}.getType();
                List<Notification> allNotifications = gson.fromJson(String.valueOf(result), listType);
                callback.onSuccess(allNotifications);
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Gekk ekki að ná í öll notifications: " + errorString);
                callback.onFailure(errorString);
            }
        }, new String[] {"notifications", "user", String.valueOf(userId)});
    }

    /**
     * Býr til path og kallar á network fall.
     *
     * @param callback - Fall sem tekur við þegar network kall er búið.
     * @param title - Titill á notification.
     * @param text - Skilaboð í notification.
     * @param userIds - Id á þeim notendum sem eiga að fá notification.
     */
    public void createNotification(NetworkCallback<Notification> callback, String title, String text, int[] userIds) {
        Log.d(TAG, "bý til nýtt notification");
        mNetworkManager.POST(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "Gson-a notification");
                Gson gson = new Gson();
                Notification notificationCreated = gson.fromJson(result, Notification.class);
                callback.onSuccess(notificationCreated);
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Failed to create notification: " + errorString);
                callback.onFailure(errorString);
            }
        }, new String[] {"notifications"}, new String[][] {{"title", title}, {"text", text}, {"userIds", String.valueOf(userIds)}});
    }

    /**
     * Býr til path og kallar á network fall.
     *
     * @param notificationId - Id á notification sem á að uppfæra.
     * @param userId - Id á notanda sem er með þetta notification.
     */
    public void updateNotification(int notificationId, int userId) {
        Log.d(TAG, "update-a notification " + notificationId);
        mNetworkManager.PATCH(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "Notification var update-að");
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Ekki gekk að update-a notification: " + errorString);
            }
        }, new String[] {"notifications", String.valueOf(userId), "read", String.valueOf(notificationId)},
                new String[][] {});
    }
}
