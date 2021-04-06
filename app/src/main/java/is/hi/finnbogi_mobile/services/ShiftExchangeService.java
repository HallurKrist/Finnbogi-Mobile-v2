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

import is.hi.finnbogi_mobile.entities.Shift;
import is.hi.finnbogi_mobile.entities.ShiftExchange;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;

public class ShiftExchangeService {

    private static final String TAG = "ShiftExchangeService";

    NetworkManager mNetworkManager;

    public ShiftExchangeService(NetworkManager networkManager) {
        mNetworkManager = networkManager;
    }

    /**
     * Býr til path og kallar á network fall.
     *
     * @param callback Fall sem tekur við þegar network kall er búið.
     */
    public void getAllShiftExchanges(NetworkCallback<List<ShiftExchange>> callback) {
        Log.d(TAG, "næ í öll shiftexchanges");
        mNetworkManager.GET(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "Gson-a lista af ShiftExchange");
                Gson gson = new Gson();
                Type listType = new TypeToken<List<ShiftExchange>>(){}.getType();
                List<ShiftExchange> allShiftExchanges = gson.fromJson(result, listType);
                callback.onSuccess(allShiftExchanges);
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Gekk ekki að ná í öll ShiftExchange: " + errorString);
                callback.onFailure(errorString);
            }
        }, new String[] {"shiftexchanges"});
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getShiftsForExchange(NetworkCallback<List<Shift>> callback) {
        Log.d(TAG, "næ í öll shifts for exchange");
        mNetworkManager.GET(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "Gson-a lista af shifts");
                Gson gson = new Gson();
                final ArrayList<?> jsonArray = gson.fromJson(result, ArrayList.class);

                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

                List<Shift> shifts = new ArrayList<>(jsonArray.size());
                for (int i = 0; i < jsonArray.size(); i++) {
                    int shiftId = ((Double)((LinkedTreeMap)jsonArray.get(i)).get("id")).intValue();

                    LocalDateTime startTime = null;
                    LocalDateTime endTime = null;
                    try {
                        Date parsedStart = inputFormat.parse((String)((LinkedTreeMap)jsonArray.get(i)).get("starttime"));
                        startTime = parsedStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                        Date parsedEnd = inputFormat.parse((String)((LinkedTreeMap)jsonArray.get(i)).get("endtime"));
                        endTime = parsedEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Could not parse dates from DB");
                    }

                    int userId = ((Double)((LinkedTreeMap)jsonArray.get(i)).get("userid")).intValue();
                    String role = ((String)((LinkedTreeMap)jsonArray.get(i)).get("role"));
                    shifts.add(new Shift(shiftId, startTime, endTime, userId, role));
                }
                callback.onSuccess(shifts);
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Gekk ekki að ná í öll shifts for exchange: " + errorString);
                callback.onFailure(errorString);
            }
        }, new String[] {"shiftexchanges", "shiftsforexchange"});
    }
}
