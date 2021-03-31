package is.hi.finnbogi_mobile.services;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import is.hi.finnbogi_mobile.entities.Shift;
import is.hi.finnbogi_mobile.entities.User;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;

public class HomeService {

    private final String TAG = "HomeService";

    NetworkManager mNetworkManager;
    User mUser;

    public HomeService(NetworkManager networkManager) {
        mNetworkManager = networkManager;
    }

    /**
     * tekur inn notandalykil og skilar User hlut með upplýsingum um notanda.
     * @param callback fallðið sem er notað til að skila user tilbaka.
     * @param userId
     */
    public void getUserById(final NetworkCallback<User> callback, int userId) {
        String[] path = {"users", String.valueOf(userId)};
        mNetworkManager.GET(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                mUser = gson.fromJson(result, User.class);
                callback.onSuccess(mUser);
            }

            @Override
            public void onFailure(String errorString) {
                mUser = null;
                Log.e(TAG, "Failed to find user: " + errorString);
                callback.onFailure(errorString);
            }
        }, path
        );
    }

    /**
     * Tekur inn tölu sem skilgreinir númer viku frá núverandi viku og notandalykil.
     * þ.e.a.s. 0 = núverandi vika, 1 = næsta vika, -1 síðasta vika o.s.frv.
     * Skilar þeim vöktum sem eiga við tilgreinda viku, með null ef engin vakt á skilgreindum degi.
     * dæmi: { Shift, Shift, null, null, Shift, Shift, Shift } <- í þessari viku er notandi með 5 vaktir.
     * @param callback fallið sem er notað til að skila vikunni tilbaka.
     * @param weekNr
     * @param userId
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getWeek(final NetworkCallback<Shift[]> callback, int userId, int weekNr) {
        LocalDateTime weekstart = findWeekStartDay(weekNr);
        Shift[] week = new Shift[] {null, null, null, null, null, null, null};

        mNetworkManager.GET(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                final ArrayList<?> jsonArray = gson.fromJson(result, ArrayList.class);

                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

                Shift[] shifts = new Shift[jsonArray.size()];
                for ( int i = 0; i < jsonArray.size(); i++) {
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
                    shifts[i] = new Shift(shiftId, startTime, endTime, userId, role);
                }

                for (int i = 0; i < shifts.length; i++) {
                    LocalDateTime shiftDate = shifts[i].getStartTime();
                    if ((weekstart.isBefore(shiftDate) || weekstart.isEqual(shiftDate)) && weekstart.plusDays(7).isAfter(shiftDate)) {
                        DayOfWeek dayOfWeek = shiftDate.getDayOfWeek();

                        int dayOfWeekInt;
                        switch(dayOfWeek) {
                            case MONDAY:
                                dayOfWeekInt = 0;
                                break;
                            case TUESDAY:
                                dayOfWeekInt = 1;
                                break;
                            case WEDNESDAY:
                                dayOfWeekInt = 2;
                                break;
                            case THURSDAY:
                                dayOfWeekInt = 3;
                                break;
                            case FRIDAY:
                                dayOfWeekInt = 4;
                                break;
                            case SATURDAY:
                                dayOfWeekInt = 5;
                                break;
                            case SUNDAY:
                                dayOfWeekInt = 6;
                                break;
                            default:
                                dayOfWeekInt = -1;
                        }

                        week[dayOfWeekInt] = shifts[i];
                    }
                }
                callback.onSuccess(week);
            }

            @Override
            public void onFailure(String errorString) {
                callback.onFailure(errorString);
            }
        }, new String[] {"shifts", "user", String.valueOf(userId)});
    }

    /**
     * Finnur hvaða dagur er mánudagurinn sem passar við núverandi vika + weekNr.
     * @param weekNr int sem segir til um hvaða viku verið er að leita í.
     * @return LocalDateTime sem er mánudagurinn í viðeigandi viku.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalDateTime findWeekStartDay(int weekNr) {
        DayOfWeek correctMonday = LocalDateTime.now().getDayOfWeek();
        int offset = 0;

        while (correctMonday.toString() != "MONDAY") {
            offset = offset + 1;
            correctMonday = LocalDateTime.now().minusDays(offset).getDayOfWeek();
        }

        return LocalDateTime.now()
                .minusDays(offset)
                .plusWeeks(weekNr)
                .minusHours(LocalDateTime.now().getHour())
                .minusMinutes(LocalDateTime.now().getMinute());
    }

    /**
     * tekur við dasetningar upplýsingum og skila "fallega" uppsetum streng með gildunum
     * @param day
     * @param month
     * @param year
     * @return prettyDate string
     */
    public String prettyDate(int day, String month, int year) {
        String dayString = "" + day;
        String monthString = month.substring(0,3).toLowerCase();
        String yearString = ("" + year).substring(2) + "'";
        if (day < 10) { dayString = "0" + day; }

        String finalString = dayString + "." + monthString + "." + yearString;
        return finalString;
    }

    /**
     * Tekur við tveimur tímasetningum og skilar "fallegum" streng sem táknar tímabilið milli þeirra
     * @param startHour
     * @param startMinute
     * @param endHour
     * @param endMinute
     * @return
     */
    public String prettyTime(int startHour, int startMinute, int endHour, int endMinute) {
        String sHourString = "" + startHour;
        String sMinuteString = "" + startMinute;
        String eHourString = "" + endHour;
        String eMinuteString = "" + endMinute;
        if (startHour < 10) { sHourString = "0" + startHour; }
        if (startMinute < 10) { sMinuteString = "0" + sMinuteString; }
        if (endHour < 10) { eHourString = "0" + eHourString; }
        if (endMinute < 10) { eMinuteString = "0" + eMinuteString; }

        String finalString = sHourString + ":" + sMinuteString + " - " + eHourString + ":" + eMinuteString;
        return finalString;
    }
}
