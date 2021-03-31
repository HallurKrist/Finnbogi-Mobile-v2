package is.hi.finnbogi_mobile.services;

import android.util.Log;

import com.google.gson.Gson;

import is.hi.finnbogi_mobile.entities.User;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;

public class MakeUserService {

    private static final String TAG = "MakeShiftService";

    private NetworkManager mNetworkManager;

    public MakeUserService(NetworkManager networkManager) {
        mNetworkManager = networkManager;
    }

    /**
     * Býr til path og kallar á network fall.
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
        Log.d(TAG, "búa til user");
        mNetworkManager.POST(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                // Get user information
                Gson gson = new Gson();
                User userCreated = gson.fromJson(result, User.class);
                callback.onSuccess(userCreated);
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Failed to create user: " + errorString);
                callback.onFailure(errorString);
            }
        }, new String[] {"users", "register"},
           new String[][] {{"username", userName}, {"password", password}, {"role", role},
                   {"ssn", ssn}, {"admin", String.valueOf(admin)}}
        );
    }
}
