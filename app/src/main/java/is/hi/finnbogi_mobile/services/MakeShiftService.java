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
import java.util.Date;
import java.util.List;

import is.hi.finnbogi_mobile.entities.Shift;
import is.hi.finnbogi_mobile.entities.User;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;

public class MakeShiftService {

    private static final String TAG = "MakeShiftService";

    private NetworkManager mNetworkManager;

    public MakeShiftService(NetworkManager networkManager) {
        mNetworkManager = networkManager;
    }

    /**
     * Býr til path og kallar á network fall.
     *
     * @param callback - fall sem tekur við þegar network kall er búið
     */
    public void getAllUsers(NetworkCallback<List<User>> callback) {
        Log.d(TAG, "ná í alla users: ");
        mNetworkManager.GET(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<User>>(){}.getType();
                List<User> allUsers = gson.fromJson(String.valueOf(result), listType);
                callback.onSuccess(allUsers);
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Error when getting users");
            }
        }, new String[] {"users"});
    }

    /**
     * Býr til path og kallar á network fall.
     *
     * @param callback - fall sem tekur við þegar network kall er búið
     * @param startTime - upphafstími fyrir vakt
     * @param endTime - lokatími fyrir vakt
     * @param userId - userId fyrir þann sem á að vinna vaktina
     */
    public void createShift(NetworkCallback<Shift> callback, LocalDateTime startTime, LocalDateTime endTime, int userId, String role) {
        Log.d(TAG, "búa til vakt: ");
        mNetworkManager.POST(new NetworkCallback<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(String result) {
                // Get shift information
                Gson gson = new Gson();
                final Object json = gson.fromJson(result, Object.class);

                // Making localDateTime from string
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                LocalDateTime startTime = null;
                LocalDateTime endTime = null;
                try {
                    Date parsedStart = inputFormat.parse((String)((LinkedTreeMap)json).get("starttime"));
                    startTime = parsedStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    Date parsedEnd = inputFormat.parse((String)((LinkedTreeMap)json).get("endtime"));
                    endTime = parsedEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Could not parse dates from DB");
                }

                // Make shift to return
                Shift shiftCreated = new Shift(
                        ((Double)((LinkedTreeMap)json).get("id")).intValue(),
                        startTime,
                        endTime,
                        userId,
                        ((String)((LinkedTreeMap)json).get("role"))
                );

                //TODO: make network patch call to set userId on shift in API

                callback.onSuccess(shiftCreated);
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Error in creation of shift");
            }
        }, new String[] {"shifts"}, new String[][] {{"startTime", startTime.toString()}, {"endTime", endTime.toString()}, {"role", role}});
    }
}
