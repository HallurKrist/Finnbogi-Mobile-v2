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
     *
     * @param userName - Notendanafn þess sem er að reyna að logga inn
     * @param password - Lykilorð þess sem er að reyna að logga inn
     * @return User ef tókst að logga inn - Null ef ekki tókst að logga inn
     */
    public User login(String userName, String password) {
        Log.d(TAG, "inn í login falli: ");
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
        }, "users/login", userName, password);

        return mUserLoggingIn;
    }
}
