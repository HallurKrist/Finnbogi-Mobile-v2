package is.hi.finnbogi_mobile.services;

import android.util.Log;

import is.hi.finnbogi_mobile.entities.User;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;

public class LoginService {

    private static final String TAG = "LoginService";

    private NetworkManager mNetworkManager;
    private User mUserLoggingIn;

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
    public User login(String userName, String password) {
        Log.d(TAG, "inn í login falli: ");
        String path = new String("users/login");
        mNetworkManager.loginPost(new NetworkCallback<User>() {
            @Override
            public void onSuccess(User result) {
                // TODO: Bæta við að setja user í session
                mUserLoggingIn = result;
            }

            @Override
            public void onFailure(String errorString) {
                mUserLoggingIn = null;
                Log.e(TAG, "Failed to log in: " + errorString);
            }
        }, "users\\/login", userName, password);

        return mUserLoggingIn;
    }
}
