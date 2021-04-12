package is.hi.finnbogi_mobile.services;

import android.util.Log;

import com.google.gson.Gson;

import is.hi.finnbogi_mobile.R;
import is.hi.finnbogi_mobile.entities.User;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;

public class LoginService {

    private static final String TAG = "LoginService";

    private NetworkManager mNetworkManager;

    public LoginService(NetworkManager networkManager) {
        mNetworkManager = networkManager;
    }

    /**
     * Býr til path og kallar á network fall.
     * Ef það kall tekst þá er búinn tiul User hlutur
     * með notanda sem verið var að skrá inn.
     *
     * @param userName Notendanafn þess sem er að reyna að logga inn.
     * @param password Lykilorð þess sem er að reyna að logga inn.
     */
    public void login(NetworkCallback<User> callback, String userName, String password) {
        mNetworkManager.POST(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                User user = gson.fromJson(result, User.class);
                callback.onSuccess(user);
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, errorString);
                callback.onFailure(errorString);
            }
        }, new String[] {"users", "login"}, new String[][][] {{{"username"}, {userName}},{{"password"}, {password}}});
    }
}
