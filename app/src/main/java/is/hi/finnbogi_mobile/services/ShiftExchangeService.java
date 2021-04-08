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
import is.hi.finnbogi_mobile.entities.User;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;

// TODO: Setja harðkóðaða strengi í strings.xml

public class ShiftExchangeService {

    private static final String TAG = "ShiftExchangeService";

    NetworkManager mNetworkManager;

    public ShiftExchangeService(NetworkManager networkManager) {
        mNetworkManager = networkManager;
    }

    /**
     * Býr til path og kallar á network fall.
     * Býr til lista af ShiftExchange hlutum með niðurstöðunni
     * ef kallið gekk upp.
     *
     * @param callback Fall sem tekur við þegar þetta fall er búið.
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

    /**
     * Býr til path og kallar á network fall.
     * Býr til lista af ShiftExchange hlutum með niðurstöðunni
     * ef kallið gekk upp.
     *
     * @param callback Fall sem tekur við þegar þetta fall er búið.
     */
    public void getConfirmableShiftExchanges(NetworkCallback<List<ShiftExchange>> callback) {
        Log.d(TAG, "næ í öll confirmable shiftexchanges");
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
        }, new String[] {"shiftexchanges", "confirmable"});
    }

    /**
     * Býr til path og kallar á network fall.
     * Býr til lista af ShiftExchange hlutum með niðurstöðunni
     * ef kallið gekk upp.
     *
     * @param callback Fall sem tekur við þegar þetta fall er búið.
     * @param userId Id fyrir user.
     */
    public void getShiftExchangesForUser(NetworkCallback<List<ShiftExchange>> callback, int userId) {
        Log.d(TAG, "Næ í shiftexchanges fyrir user: " + userId);
        mNetworkManager.GET(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "Gson-a lista af ShiftExchange");
                Gson gson = new Gson();
                Type listType = new TypeToken<List<ShiftExchange>>(){}.getType();
                List<ShiftExchange> allShiftExchanges = gson.fromJson(result, listType);
                // Ná bara í shiftexchange með userId
                List<ShiftExchange> userShiftExchanges = new ArrayList<>();
                for (ShiftExchange shiftExchange : allShiftExchanges) {
                    if (shiftExchange.getEmployeeId() == userId) {
                        userShiftExchanges.add(shiftExchange);
                    }
                }
                callback.onSuccess(userShiftExchanges);
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Gekk ekki að ná í öll ShiftExchange fyrir user: " + errorString);
                callback.onFailure(errorString);
            }
        }, new String[] {"shiftexchanges"});
    }

    /**
     * Býr til path og kallar á network fall.
     * Býr til ShiftExchange hlut með niðurstöðunni
     * ef kallið gekk upp.
     *
     * @param callback Fall sem tekur við þegar þetta fall er búið.
     * @param shiftExchangeId Id á ShiftExchange.
     */
    public void getShiftExchangeById(NetworkCallback<ShiftExchange> callback, int shiftExchangeId) {
        Log.d(TAG, "næ í shiftexchange: " + shiftExchangeId);
        mNetworkManager.GET(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "Gson-a shiftexchange");
                Gson gson = new Gson();
                ShiftExchange shiftExchange = gson.fromJson(result, ShiftExchange.class);
                callback.onSuccess(shiftExchange);
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Villa við að ná í shiftexchange: " + errorString);
                callback.onFailure(errorString);
            }
        }, new String[] {"shiftexchanges", String.valueOf(shiftExchangeId)});
    }

    /**
     * Býr til path og kallar á network fall.
     * Býr til lista af Shift hlutum með niðurstöðunni
     * ef kall gekk upp.
     *
     * @param callback Fall sem tekur við þegar þetta fall er búið.
     */
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

    /**
     * Býr til path og kallar á network fall.
     * Býr til lista af Shift hlutum með niðurstöðunni
     * ef kall gekk upp.
     *
     * @param callback Fall sem tekur við þegar þetta fall er búið.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getShiftsForExchangeForConfirmable(NetworkCallback<List<Shift>> callback) {
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
        }, new String[] {"shiftexchanges", "confirmable", "shiftsforexchange"});
    }

    /**
     * Býr til path og kallar á network fall.
     * Býr til lista af Shift hlutum með niðurstöðunni
     * ef kall gekk upp.
     *
     * @param callback Fall sem tekur við þegar þetta fall er búið.
     * @param userId Id á user.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getShiftsForExchangeForUser(NetworkCallback<List<Shift>> callback, int userId) {
        Log.d(TAG, "næ í öll shifts for exchange fyrir user");
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
                // Ná bara í vaktirnar fyrir user með userId
                List<Shift> userShifts = new ArrayList<>();
                for (Shift shift : shifts) {
                    if (shift.getUserId() == userId) {
                        userShifts.add(shift);
                    }
                }
                callback.onSuccess(userShifts);
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Gekk ekki að ná í öll shifts for exchange fyrir user: " + errorString);
                callback.onFailure(errorString);
            }
        }, new String[] {"shiftexchanges", "shiftsforexchange"});
    }

    /**
     * Býr til path og kallar á network fall.
     * Býr til Shift hlut með niðurstöðunni ef
     * kall gekk upp.
     *
     * @param callback Fall sem tekur við þegar þetta fall er búið.
     * @param shiftId Id á shift sem á að sækja.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getShiftById(NetworkCallback<Shift> callback, int shiftId) {
        Log.d(TAG, "Næ í shift: " + shiftId);
        mNetworkManager.GET(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "Gson-a shift");
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
                Shift shift = new Shift(
                        shiftId,
                        startTime,
                        endTime,
                        ((Double)((LinkedTreeMap)json).get("userid")).intValue(),
                        ((String)((LinkedTreeMap)json).get("role"))
                );
                callback.onSuccess(shift);
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Villa að ná í shift: " + errorString);
                callback.onFailure(errorString);
            }
        }, new String[] {"shifts", String.valueOf(shiftId)});
    }

    /**
     * Býr til path og kallar á network fall.
     * Býr til lista af Shift hlutum með niðurstöðunni
     * ef kall gekk upp.
     *
     * @param callback Fall sem tekur við þegar þetta fall er búið.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getUserShifts(NetworkCallback<List<Shift>> callback, int userId) {
        Log.d(TAG, "Næ í allar vaktir fyrir user: " + userId);
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
                Log.e(TAG, "Villa að ná í vaktir notanda: " + errorString);
                callback.onFailure(errorString);
            }
        }, new String[] {"shifts", "user", String.valueOf(userId)});
    }

    /**
     * Býr til path og kallar á network fall.
     * Býr til User hlut með niðurstöðunni ef
     * kall gekk upp.
     *
     * @param callback Fall sem tekur við þegar þetta fall er búið.
     * @param userId Id á user sem á að sækja.
     */
    public void getUserById(NetworkCallback<User> callback, int userId) {
        Log.d(TAG, "Næ í user: " + userId);
        mNetworkManager.GET(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "Náði í user: " + result);
                Gson gson = new Gson();
                User user = gson.fromJson(result, User.class);
                callback.onSuccess(user);
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Villa að ná í user: " + errorString);
                callback.onFailure(errorString);
            }
        }, new String[] {"users", String.valueOf(userId)});
    }

    /**
     * Býr til path og kallar á network fall.
     * Lætur vita hvort kallið gekk upp eða ekki.
     *
     * @param callback Fall sem tekur við þegar þetta fall er búið.
     * @param shiftExchangeId Id á shiftexchange sem á að uppfæra.
     * @param shiftId Id á vakt sem er boðið á móti.
     */
    public void offerShiftForExchange(NetworkCallback<String> callback, int shiftExchangeId, int shiftId) {
        Log.d(TAG, "update-a shiftexchange með offeredshift í pending");
        mNetworkManager.PATCH(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "Gekk að update-a shiftexchange með vakt til að bjóða á móti");
                callback.onSuccess("Gekk upp");
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Villa að update-a shiftexchange með vakt til að bjóða á móti: " + errorString);
                callback.onFailure(errorString);
            }
        }, new String[] {"shiftexchanges", "setpending", String.valueOf(shiftExchangeId)},
                new String[][]{{"coworkerShiftId", String.valueOf(shiftId)}});
    }

    /**
     * Býr til path og kallar á network fall.
     * Lætur vita hvort kallið gekk upp eða ekki.
     *
     * @param callback Fall sem tekur við þegar þetta fall er búið.
     * @param shiftExchangeId Id á shiftexchange sem á að uppfæra.
     */
    public void declinePendingOffer(NetworkCallback<String> callback, int shiftExchangeId) {
        Log.d(TAG, "decline-a pending offer");
        mNetworkManager.PATCH(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "Gekk að decline-a pending offer");
                callback.onSuccess("Gekk upp");
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Villa að decline-a pending offer: " + errorString);
                callback.onFailure(errorString);
            }
        }, new String[] {"shiftexchanges", "declinepending", String.valueOf(shiftExchangeId)},
                new String[][] {});
    }

    /**
     * Býr til path og kallar á network fall.
     * Lætur vita hvort kallið gekk upp eða ekki.
     *
     * @param callback Fall sem tekur við þegar þetta fall er búið.
     * @param shiftExchangeId Id á shiftexchange sem á að uppfæra.
     */
    public void acceptPendingOffer(NetworkCallback<String> callback, int shiftExchangeId) {
        Log.d(TAG, "accepta-a pending offer");
        mNetworkManager.PATCH(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "Gekk að accepta-a pending offer");
                callback.onSuccess("Gekk upp");
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Villa að decline-a pending offer: " + errorString);
                callback.onFailure(errorString);
            }
        }, new String[] {"shiftexchanges", "approvepending", String.valueOf(shiftExchangeId)},
                new String[][] {});
    }

    /**
     * Býr til path og kallar á network fall.
     * Lætur vita hvort kallið gekk upp eða ekki.
     *
     * @param callback Fall sem tekur við þegar þetta fall er búið.
     * @param shiftExchangeId Id á shiftexchange sem á að uppfæra.
     */
    public void declineConfirmableOffer(NetworkCallback<String> callback, int shiftExchangeId) {
        Log.d(TAG, "decline-a vaktaskipti");
        // kalla á delete fall í network manager
    }

    /**
     * Býr til path og kallar á network fall.
     * Lætur vita hvort kallið gekk upp eða ekki.
     *
     * @param callback Fall sem tekur við þegar þetta fall er búið.
     * @param shiftExchangeId Id á shiftexchange sem á að uppfæra.
     */
    public void acceptConfirmableOffer(NetworkCallback<String> callback, int shiftExchangeId) {
        Log.d(TAG, "accepta-a vaktaskipti");
        mNetworkManager.PATCH(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "Gekk að accepta-a vaktaskipti");
                callback.onSuccess("Gekk upp");
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Villa að accepta-a vaktaskipti: " + errorString);
                callback.onFailure(errorString);
            }
        }, new String[] {"shiftexchanges", "confirm", String.valueOf(shiftExchangeId)},
                new String[][] {});
    }
}
