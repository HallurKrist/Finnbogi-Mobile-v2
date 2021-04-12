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
import java.util.Date;
import java.util.List;

import is.hi.finnbogi_mobile.R;
import is.hi.finnbogi_mobile.entities.Notification;
import is.hi.finnbogi_mobile.entities.Shift;
import is.hi.finnbogi_mobile.entities.ShiftExchange;
import is.hi.finnbogi_mobile.entities.User;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;

public class ShiftService {
    private final String TAG = "ShiftService";

    NetworkManager mNetworkManager;

    /**
     * constructor
     * @param networkManager
     */
    public ShiftService(NetworkManager networkManager) {
        mNetworkManager = networkManager;
    }

    /**
     * get a shift from API with ShiftId
     * @param callback
     * @param shiftId
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getShiftById(NetworkCallback<Shift> callback, int shiftId) {
        mNetworkManager.GET(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                // make shift object from result string
                Gson gson = new Gson();
                final Object json = gson.fromJson(result, Object.class);
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

                Shift shift = new Shift(
                        ((Double)((LinkedTreeMap)json).get("id")).intValue(),
                        startTime,
                        endTime,
                        ((Double)((LinkedTreeMap)json).get("userid")).intValue(),
                        ((String)((LinkedTreeMap)json).get("role"))
                );

                callback.onSuccess(shift);
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Error in finding shift by id");
                callback.onFailure(errorString);
            }
        }, new String[] {"shifts", String.valueOf(shiftId)});
    }

    /**
     * Tekur inn notandalykil og skilar User hlut með upplýsingum um notanda.
     * @param callback fallðið sem er notað til að skila user tilbaka.
     * @param userId
     */
    public void getUserById(final NetworkCallback<User> callback, int userId) {
        String[] path = {"users", String.valueOf(userId)};
        mNetworkManager.GET(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                User user = gson.fromJson(result, User.class);
                callback.onSuccess(user);
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Failed to find user: " + errorString);
                callback.onFailure(errorString);
            }
        }, path );
    }

    /**
     * Býr til date strenginn sem er birtur í viðmótinu útfrá LocalDateTime hlut
     * @param date
     * @return String
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String dateString(LocalDateTime date) {
        int day = date.getDayOfMonth();
        String month = date.getMonth().toString();
        int year = date.getYear();

        String dString = "" + day;
        if (day < 10) { dString = "0" + day; }
        String mString = month.substring(0,3).toLowerCase();
        String yString = ("" + year).substring(2) + "'";

        return "Dags: " + dString + "." + mString + "." + yString;
    }

    /**
     * Býr til tímabils strenginn sem er birtur í viðmótinu útfrá tveimur LocalDateTime hlutum
     * @param startTime
     * @param endTime
     * @return String
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String shiftTimeString(LocalDateTime startTime, LocalDateTime endTime) {
        int sH = startTime.getHour();
        int sM = startTime.getMinute();
        int eH = endTime.getHour();
        int eM = endTime.getMinute();
        String sHString = "" + sH;
        String sMString = "" + sM;
        String eHString = "" + eH;
        String eMString = "" + eM;

        if (sH < 10) { sHString = "0" + sH; }
        if (sM < 10) { sMString = "0" + sM; }
        if (eH < 10) { eHString = "0" + eH; }
        if (eM < 10) { eMString = "0" + eM; }

        return sHString + ":" + sMString + " - " + eHString + ":" + eMString;
    }

    /**
     * Býr til starfsmanna strenginn sem er birtur í viðmótinu útfrá nafni notanda.
     * @param userName
     * @return
     */
    public String employeeString(String userName) {
        return "Starfsmaður: " + userName;
    }

    /**
     * Býr til path og kallar á network fall.
     * Býr til lista af User hlutum með niðurstöðunni
     * ef kall gekk upp.
     *
     * @param callback Fall sem tekur við þegar þetta fall er búið.
     * @param role Role á notendum.
     * @param userId Id á notandanum sem á ekki að fá notification.
     */
    public void getAllUsersWithSameRole(NetworkCallback<List<User>> callback, String role, int userId) {
        mNetworkManager.GET(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, String.valueOf(R.string.service_success));
                Gson gson = new Gson();
                Type listType = new TypeToken<List<User>>(){}.getType();
                List<User> allUsers = gson.fromJson(result, listType);
                List<User> usersWithSameRole = new ArrayList<>();
                for (User user : allUsers) {
                    if (user.getRole().equals(role) && user.getUserId() != userId) {
                        usersWithSameRole.add(user);
                    }
                }
                callback.onSuccess(usersWithSameRole);
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, R.string.service_error + " " + errorString);
                callback.onFailure(errorString);
            }
        }, new String[] {"users"});
    }

    /**
     * Býr til path og kallar á network fall.
     * Býr til ShiftExchange hlut með niðurstöðunni
     * ef kall gekk upp.
     *
     * @param callback Fall sem tekur við þegar þetta fall er búið.
     * @param employeeId Id á user sem á vaktina sem er í boði.
     * @param shiftId Id á vaktinni sem er í boði.
     */
    public void createShiftExchange(NetworkCallback<String> callback, int employeeId, int shiftId) {
        mNetworkManager.POST(new NetworkCallback<String>() {
             @Override
             public void onSuccess(String result) {
                 Log.d(TAG, "Gekk upp");
                 callback.onSuccess(result);
             }

             @Override
             public void onFailure(String errorString) {
                 Log.e(TAG, errorString);
                 callback.onFailure(errorString);
             }
        }, new String[] {"shiftexchanges"},
                new String[][] {{"employeeid", String.valueOf(employeeId)}, {"shiftid", String.valueOf(shiftId)}});
    }

    /**
     * Býr til path og kallar á network fall.
     * Býr til Notification hlut með niðurstöðunni
     * ef kall gekk upp.
     *
     * @param callback Fall sem tekur við þegar þetta fall er búið.
     * @param title Titill á notification.
     * @param text Skilaboð í notification.
     * @param userIds Id á þeim notendum sem eiga að fá notification.
     */
    public void createNotification(NetworkCallback<String> callback, String title, String text, int[] userIds) {
        mNetworkManager.POST(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, String.valueOf(R.string.service_success));
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, R.string.service_error + " " + errorString);
                callback.onFailure(errorString);
            }
        }, new String[] {"notifications"}, new String[][][] {{{"title"}, {title}}, {{"text"}, {text}}, {{"userIds"}, {String.valueOf(userIds)}}});
    }
}
