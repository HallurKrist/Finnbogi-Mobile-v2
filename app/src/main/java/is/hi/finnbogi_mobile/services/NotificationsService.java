package is.hi.finnbogi_mobile.services;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import is.hi.finnbogi_mobile.R;
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
     * Býr til lista af Notification hlutum með niðurstöðunni
     * ef kallið gekk upp.
     *
     * @param callback Fall sem tekur við þegar þetta fall er búið.
     * @param userId Id á user.
     */
    public void getAllNotifications(NetworkCallback<List<Notification>> callback, int userId) {
        mNetworkManager.GET(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, String.valueOf(R.string.service_success));
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Notification>>(){}.getType();
                List<Notification> allNotifications = gson.fromJson(result, listType);
                callback.onSuccess(allNotifications);
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, R.string.service_error + " " + errorString);
                callback.onFailure(errorString);
            }
        }, new String[] {"notifications", "user", String.valueOf(userId)});
    }

    /**
     * Býr til path og kallar á network fall.
     * Býr til Notification hlut með niðurstöðunni
     * ef kallið gekk upp.
     *
     * @param callback Fall sem tekur við þegar þetta fall er búið.
     * @param notificationId Id á Notification.
     */
    public void getNotificationById(NetworkCallback<Notification> callback, int notificationId) {
        mNetworkManager.GET(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, String.valueOf(R.string.service_success));
                Gson gson = new Gson();
                Notification notification = gson.fromJson(result, Notification.class);
                callback.onSuccess(notification);
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, R.string.service_error + " " + errorString);
                callback.onFailure(errorString);
            }
        }, new String[] {"notifications", String.valueOf(notificationId)});
    }

    /**
     * Býr til path og kallar á network fall.
     * Býr til Notification hlut með niðurstöðunni
     * ef kall gekk upp.
     *
     * @param callback Fall sem tekur við þegar þetta fall er búið.
     * @param title Titill á notification.
     * @param text Skilaboð í notification.
     * @param userIds Id á þeim notendum sem eiga að fá notification.
     */
    public void createNotification(NetworkCallback<Notification> callback, String title, String text, int[] userIds) {
        mNetworkManager.POST(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, String.valueOf(R.string.service_success));
                Gson gson = new Gson();
                Notification notificationCreated = gson.fromJson(result, Notification.class);
                callback.onSuccess(notificationCreated);
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, R.string.service_error + " " + errorString);
                callback.onFailure(errorString);
            }
        }, new String[] {"notifications"}, new String[][][] {{{"title"}, {title}}, {{"text"}, {text}}, {{"userIds"}, {String.valueOf(userIds)}}});
    }

    /**
     * Býr til path og kallar á network fall.
     * Lætur vita hvort kallið gekk upp eða ekki.
     *
     * @param notificationId Id á notification sem á að uppfæra.
     * @param userId Id á notanda sem er með þetta notification.
     */
    public void updateNotification(NetworkCallback<String> callback, int notificationId, int userId) {
        mNetworkManager.PATCH(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, String.valueOf(R.string.service_success));
                callback.onSuccess("Gekk upp");
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, R.string.service_error + " " + errorString);
                callback.onFailure(errorString);
            }
        }, new String[] {"notifications", String.valueOf(userId), "read", String.valueOf(notificationId)},
                new String[][] {});
    }
}
