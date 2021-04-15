package is.hi.finnbogi_mobile.services;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import is.hi.finnbogi_mobile.entities.Notification;
import is.hi.finnbogi_mobile.entities.Shift;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;

public class ShiftListService {
    private final String TAG = "ShiftListService";

    NetworkManager mNetworkManager;

    /**
     * constructor
     * @param networkManager
     */
    public ShiftListService(NetworkManager networkManager) {
        mNetworkManager=networkManager;
    }

    /**
     * get all ids, datetimes and roles for all shifts
     * @param callback
     */
    public void getIdNDateTimeNRole(NetworkCallback<String[][]> callback) {
        mNetworkManager.GET(new NetworkCallback<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(String result) {
                // work with string response
                Gson gson = new Gson();
                Object object = gson.fromJson(result, Object.class);
                ArrayList shiftObject = (ArrayList) object;

                List<Shift> shifts = new ArrayList<Shift>();

                //make a list of shift objects from result
                for (int i = 0; i < shiftObject.size(); i++) {
                    LinkedTreeMap LTMShift = (LinkedTreeMap) shiftObject.get(i);

                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    LocalDateTime startTime = null;
                    LocalDateTime endTime = null;
                    try {
                        Date parsedStart = inputFormat.parse((String)((LinkedTreeMap)LTMShift).get("starttime"));
                        startTime = parsedStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                        Date parsedEnd = inputFormat.parse((String)((LinkedTreeMap)LTMShift).get("endtime"));
                        endTime = parsedEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Could not parse dates from DB");
                    }

                    Shift shift = new Shift(
                            ((Double)((LinkedTreeMap)LTMShift).get("id")).intValue(),
                            startTime,
                            endTime,
                            ((Double)((LinkedTreeMap)LTMShift).get("userid")).intValue(),
                            ((String)((LinkedTreeMap)LTMShift).get("role"))
                    );

                    shifts.add(shift);
                }

                // sort the list
                shifts = sortByDatetime(shifts);

                String[][] idDatetimeRole = new String[3][shifts.size()];

                for (int i = 0; i < shifts.size(); i++) {
                    Shift shift = shifts.get(i);
                    idDatetimeRole[0][i] = String.valueOf(shift.getShiftId());
                    idDatetimeRole[1][i] = prettyDateTime(shift.getStartTime().toString(), shift.getEndTime().toString());
                    idDatetimeRole[2][i] = shift.getRole();
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

    /**
     * delete shift by shiftId
     * @param callback
     * @param shiftId
     */
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

    /**
     * make a "pretty" string from 2 DateTime strings
     * @param startTime
     * @param endTime
     * @return "beautyfied" string
     */
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


    /**
     * sorts a list of shifts and returns it
     * @param shifts
     * @return sorted list of shifts from input list of shifts
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<Shift> sortByDatetime(List<Shift> shifts) {
        shifts.sort(new Comparator<Shift>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public int compare(Shift s1, Shift s2) {
                if (s1.getStartTime().equals(s2.getStartTime())) {
                    return 0;
                }
                if (s2.getStartTime().isAfter(s1.getStartTime())) {
                    return -1;
                }
                return 1;
            }
        });
        return shifts;
    }
}
