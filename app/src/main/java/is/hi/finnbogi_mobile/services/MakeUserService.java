package is.hi.finnbogi_mobile.services;

import android.util.Log;

import com.google.gson.Gson;

import is.hi.finnbogi_mobile.R;
import is.hi.finnbogi_mobile.entities.User;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;

public class MakeUserService {

    private static final String TAG = "MakeUserService";

    private NetworkManager mNetworkManager;

    public MakeUserService(NetworkManager networkManager) {
        mNetworkManager = networkManager;
    }

    /**
     * Býr til path og kallar á network fall.
     * Býr til User hlut með niðurstöðunni ef kall gekk upp.
     *
     * @param callback - fall sem tekur við þegar network kall er búið
     * @param userName - notendanafn fyrir user
     * @param role - role fyrir user
     * @param password - lykilorð fyrir user
     * @param ssn - kennitala fyrir user
     * @param admin - hvort user sé admin eða ekki
     */
    public void createUser(NetworkCallback<User> callback, String userName, String role,
                           String password, String ssn, boolean admin) {
        mNetworkManager.POST(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, String.valueOf(R.string.service_success));
                Gson gson = new Gson();
                User userCreated = gson.fromJson(result, User.class);
                callback.onSuccess(userCreated);
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, R.string.service_error + " " + errorString);
                callback.onFailure(errorString);
            }
        }, new String[] {"users", "register"},
           new String[][][] {{{"username"}, {userName}}, {{"password"}, {password}}, {{"role"}, {role}},
                   {{"ssn"}, {ssn}}, {{"admin"}, {String.valueOf(admin)}}}
        );
    }
}
