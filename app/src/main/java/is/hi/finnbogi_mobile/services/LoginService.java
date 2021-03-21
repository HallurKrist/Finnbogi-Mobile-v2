package is.hi.finnbogi_mobile.services;

import android.util.Log;

import com.google.gson.Gson;

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
     * Kallar á network fall til að reyna að innskrá notanda, ef það tekst
     * þá er skilað User hlut með notanda sem verið var að skrá inn, annars
     * er skilað Null.
     *
     * @param userName - Notendanafn þess sem er að reyna að logga inn
     * @param password - Lykilorð þess sem er að reyna að logga inn
     * @return User ef tókst að logga inn - Null ef ekki tókst að logga inn
     */
    public void login(NetworkCallback<User> callback, String userName, String password) {
        Log.d(TAG, "inn í login falli: ");
        mNetworkManager.POST(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "Successfully logged in user: ");

                Gson gson = new Gson();
                User user = gson.fromJson(result, User.class);

                callback.onSuccess(user);
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Failed to log in: " + errorString);
                callback.onFailure(errorString);
            }
        }, new String[] {"users", "login"}, new String[][] {{"username", userName},{"password", password}});
    }
}
