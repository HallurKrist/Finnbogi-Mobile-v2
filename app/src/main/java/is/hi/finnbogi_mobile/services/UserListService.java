package is.hi.finnbogi_mobile.services;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;

import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;

public class UserListService {

    private static final String TAG = "UserListService";

    NetworkManager mNetworkManager;

    private String[] mNames;
    private String[][] mNamesNRoles;

    public UserListService(NetworkManager networkManager) {
        mNetworkManager = networkManager;
    }

    public void deleteUserByName(NetworkCallback<Boolean> callback, String userName) {
        // Ná í alla users og bera saman við nafn
        mNetworkManager.GET(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                Object json = gson.fromJson(result, Object.class);

                // bera saman við nöfn í gangagrunni
                ArrayList jsonArray = (ArrayList) json;
                int userId = -1;
                for (int i = 0; i < jsonArray.size(); i++) {
                    LinkedTreeMap user = (LinkedTreeMap) jsonArray.get(i);
                    if (user.get("username").equals(userName)) {
                        userId = ((Double) user.get("id")).intValue();
                    }
                }

                // Ef engin með þetta nafn í gagnagrunni skila villu
                if (userId == -1) {
                    callback.onFailure("No user with that name in DB");
                }

                // Ef er til í gagnagrunni þá eyða
                mNetworkManager.DELETE(new NetworkCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        // tókst að eyða
                        callback.onSuccess(true);
                    }

                    @Override
                    public void onFailure(String errorString) {
                        callback.onFailure(errorString);
                    }
                }, new String[] {"users", ""+userId}, new String [][] {});
            }

            @Override
            public void onFailure(String errorString) {
                callback.onFailure(errorString);
            }
        }, new String[] {"users"});
    }

    public void getUserIdByName(NetworkCallback<Integer> callback, String userName) {
        // Ná í alla users og bera saman við nafn
        mNetworkManager.GET(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                Object json = gson.fromJson(result, Object.class);

                // bera saman við nöfn í gangagrunni
                ArrayList jsonArray = (ArrayList) json;
                int userId = -1;
                for (int i = 0; i < jsonArray.size(); i++) {
                    LinkedTreeMap user = (LinkedTreeMap) jsonArray.get(i);
                    for (int j = 0; j < mNames.length; j++) {
                        // gotta check both mNames and mNamesNRoles because mNamesNRoles might have a shortened names
                        // as can be seen in the getNamesAndRoles function
                        if (mNamesNRoles[0][j].equals(userName) && user.get("username").equals(mNames[j])) {
                            userId = ((Double) user.get("id")).intValue();
                        }
                    }
                }

                // Ef engin með þetta nafn í gagnagrunni skila villu
                if (userId == -1) {
                    callback.onFailure("No user with that name in DB");
                } else {
                    callback.onSuccess(userId);
                }
            }

            @Override
            public void onFailure(String errorString) {
                callback.onFailure(errorString);
            }
        }, new String[] {"users"});
    }

    public void getNamesAndRoles(NetworkCallback<String[][]> callback) {

        mNetworkManager.GET(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                Object json = gson.fromJson(result, Object.class);

                ArrayList jsonArray = (ArrayList) json;
                int arrayLength = jsonArray.size();

                String[][] namesNRoles = new String[][] {new String[arrayLength], new String[arrayLength]};

                mNames = new String[arrayLength];

                for (int i = 0; i < arrayLength; i++) {
                    LinkedTreeMap user = (LinkedTreeMap) jsonArray.get(i);
                    // if name is to long split it so that the view is correct
//                    String[] splitName = ;
                    String name = ((String) user.get("username"));
                    mNames[i] = name;
                    if (name.length() > 16) {
                        namesNRoles[0][i] =  name.substring(0, 16);
                    } else {
                        namesNRoles[0][i] = name;
                    }
                    namesNRoles[1][i] =  (String) user.get("role");
                }

                mNamesNRoles = namesNRoles;
                callback.onSuccess(namesNRoles);
            }

            @Override
            public void onFailure(String errorString) {
                callback.onFailure(errorString);
            }
        }, new String[] {"users"});
    }
}
