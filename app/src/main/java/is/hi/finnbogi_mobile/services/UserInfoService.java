package is.hi.finnbogi_mobile.services;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;

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
                Log.d(TAG, result);

                Gson gson = new Gson();
                final Object json = gson.fromJson(result, Object.class);
                LinkedTreeMap innerJson = (LinkedTreeMap)((ArrayList)json).get(0);

                int id = ((Double)innerJson.get("userinfoid")).intValue();
                String firstName = (String)innerJson.get("firstname");
                String surName = (String)innerJson.get("surname");
                String address = (String)innerJson.get("address");
                String phoneNr = (String)innerJson.get("phonenumber");
                String ssn = (String)innerJson.get("ssn");
                String email = (String)innerJson.get("email");

                // startdate set as null as it is not necessary
                UserInfo info = new UserInfo(id, firstName, surName, address, phoneNr, ssn, null, email);

                callback.onSuccess(info);
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Error getting userInfo");
                callback.onFailure(errorString);
            }
        }, new String[] {"users", "info", String.valueOf(userId)});
    }

    public void patchUserInfo(UserInfo info) {
        // TODO: implement this class
        mNetworkManager.PATCH(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "patch request success");
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Error patching user info");
            }
        }, new String[] {"users", "info", ""+info.getUserInfoId()},
                new String[][] {
                    {"firstname", ""+info.getFirstName()},
                    {"surname", ""+info.getSurName()},
                    {"address", ""+info.getAddress()},
                    {"email", ""+info.getEmail()},
                    {"phonenumber", ""+info.getPhoneNumber()}
                });

    }
}
