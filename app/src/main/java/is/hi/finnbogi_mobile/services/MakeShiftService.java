package is.hi.finnbogi_mobile.services;

import android.util.Log;

import java.util.List;

import is.hi.finnbogi_mobile.entities.User;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;

public class MakeShiftService {

    private static final String TAG = "MakeShiftService";

    private NetworkManager mNetworkManager;

    public MakeShiftService(NetworkManager networkManager) {
        mNetworkManager = networkManager;
    }

    public void getAllUsers(NetworkCallback<List<User>> callback) {
        Log.d(TAG, "ná í alla users: ");
        String path = new String("users");
        mNetworkManager.getUsers(new NetworkCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> result) {
                Log.d(TAG, "Successfully fetched all users: ");
                callback.onSuccess(result);
                Log.d(TAG, String.valueOf(result));
            }

            @Override
            public void onFailure(String errorString) {
                callback.onFailure(errorString);
                Log.e(TAG, "Failed to fetch all users: " + errorString);
            }
        }, path);
    }
}
