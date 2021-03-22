package is.hi.finnbogi_mobile.services;

import android.util.Log;

import is.hi.finnbogi_mobile.entities.UserInfo;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;

public class UserInfoService {

    private static final String TAG = "UserInfoService";

    NetworkManager mNetworkManager;

    public UserInfoService(NetworkManager networkManager) {
        mNetworkManager = networkManager;
    }

    public void getUserInfoByUserId(NetworkCallback<UserInfo> callback, int userId) {
        mNetworkManager.GET(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //TODO: make USerInfo from result
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Error getting userInfo");
            }
        }, new String[] {"users", "info", String.valueOf(userId)});
    }
}
