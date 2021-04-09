package is.hi.finnbogi_mobile.services;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;

import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;

public class ShiftListService {
    private final String TAG = "ShiftListService";

    NetworkManager mNetworkManager;

    public ShiftListService(NetworkManager networkManager) {
        mNetworkManager=networkManager;
    }

    public void getIdNDateTimeNRole(NetworkCallback<String[][]> callback) {
        mNetworkManager.GET(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                Object json = gson.fromJson(result, Object.class);
                ArrayList jsonArray = (ArrayList) json;

                String[][] idDatetimeRole = new String[3][jsonArray.size()];

                for (int i = 0; i < jsonArray.size(); i++) {
                    LinkedTreeMap shift = (LinkedTreeMap) jsonArray.get(i);
                    idDatetimeRole[0][i] = ((Double) shift.get("id")).toString();
                    idDatetimeRole[1][i] = prettyDateTime((String) shift.get("starttime"), (String) shift.get("endtime"));
                    idDatetimeRole[2][i] = (String) shift.get("role");
                }

                callback.onSuccess(idDatetimeRole);
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Error getting shifts from API");
                callback.onFailure(errorString);
            }
        }, new String[] {"shifts"});
    }

    public void deleteShiftById(NetworkCallback<Boolean> callback, int shiftId) {
        mNetworkManager.DELETE(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                callback.onSuccess(true);
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Error deleting shift");
                callback.onFailure(errorString);
            }
        },new String[] {"shifts", ""+shiftId}, new String[][] {});
    }

    private String prettyDateTime(String startTime, String endTime) {
        String[] datePlusTime = startTime.split("T");
        String[] yearMonthDay = datePlusTime[0].split("-");
        String dayMonth = yearMonthDay[2] + "/" + yearMonthDay[1];

        String[] sHourMinSec = datePlusTime[1].split(":");
        String sTime = sHourMinSec[0] + ":" + sHourMinSec[1];

        String[] eHourMinSec = endTime.split("T")[1].split(":");
        String eTime = eHourMinSec[0] + ":" + eHourMinSec[1];

        String returnDateTime = dayMonth + " - " + sTime + "-" + eTime;

        return returnDateTime;
    }
}
