package is.hi.finnbogi_mobile.services;

import android.util.Log;

import java.time.LocalDateTime;
import java.util.List;

import is.hi.finnbogi_mobile.entities.Shift;
import is.hi.finnbogi_mobile.entities.User;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;

public class MakeShiftService {

    private static final String TAG = "MakeShiftService";

    private NetworkManager mNetworkManager;

    public MakeShiftService(NetworkManager networkManager) {
        mNetworkManager = networkManager;
    }

    /**
     * Býr til path og kallar á network fall.
     *
     * @param callback - fall sem tekur við þegar network kall er búið
     */
    public void getAllUsers(NetworkCallback<List<User>> callback) {
        Log.d(TAG, "ná í alla users: ");
        String path = new String("users");
        mNetworkManager.getUsers(new NetworkCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> result) {
                Log.d(TAG, "Successfully fetched all users: ");
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(String errorString) {
                callback.onFailure(errorString);
                Log.e(TAG, "Failed to fetch all users: " + errorString);
            }
        }, path);
    }

    /**
     * Býr til path og kallar á network fall.
     *
     * @param callback - fall sem tekur við þegar network kall er búið
     * @param startTime - upphafstími fyrir vakt
     * @param endTime - lokatími fyrir vakt
     * @param userId - userId fyrir þann sem á að vinna vaktina
     */
    public void createShift(NetworkCallback<Shift> callback, LocalDateTime startTime, LocalDateTime endTime, int userId) {
        Log.d(TAG, "búa til vakt: ");
        String path = new String("shifts");
        mNetworkManager.createShift(new NetworkCallback<Shift>() {
            @Override
            public void onSuccess(Shift result) {
                Log.d(TAG, "Successfully created shift: ");
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(String errorString) {
                callback.onFailure(errorString);
                Log.e(TAG, "Failed to create shift: " + errorString);
            }
        }, path, startTime, endTime, userId);
    }
}
