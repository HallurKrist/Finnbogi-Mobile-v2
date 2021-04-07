package is.hi.finnbogi_mobile.services;

import is.hi.finnbogi_mobile.networking.NetworkManager;

public class UserListService {
    private final String TAG = "UserListService";

    NetworkManager mNetworkManager;

    public UserListService(NetworkManager networkManager) {
        mNetworkManager = networkManager;
    }


}
