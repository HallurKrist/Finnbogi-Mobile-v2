package is.hi.finnbogi_mobile.services;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

import is.hi.finnbogi_mobile.entities.Shift;
import is.hi.finnbogi_mobile.entities.User;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;

public class HomeService {

    private final String TAG = "HomeService";

    NetworkManager mNetworkManager;
    String mUserJson;

    public HomeService(NetworkManager networkManager) {
        mNetworkManager = networkManager;
    }

    /**
     * tekur inn notandalykil og skilar User hlut með upplýsingum um notanda
     * @param userId
     * @return User
     */
    public User getUserById(int userId) {
        String[] path = {"users", ""+userId};
        mNetworkManager.GET(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                mUserJson = result;
            }

            @Override
            public void onFailure(String errorString) {
                mUserJson = null;
                Log.e(TAG, "Failed to find user: " + errorString);
            }
        }, path
        );

        Gson gson = new Gson();
        User user = gson.fromJson(String.valueOf(mUserJson), User.class);

        // TODO: make sure getting user works

        user = new User(
                userId,
                "Hallur Kristinn",
                new String[] {"Kokkur"},
                null,
                null,
                null,
                null,
                true
        );
        return user;
    }

    /**
     * Tekur inn tölu sem skilgreinir númer viku frá núverandi viku og notandalykil.
     * þ.e.a.s. 0 = núverandi vika, 1 = næsta vika, -1 síðasta vika o.s.frv.
     * Skilar þeim vöktum sem eiga við tilgreinda viku, með null ef engin vakt á skilgreindum degi.
     * dæmi: { Shift, Shift, null, null, Shift, Shift, Shift } = í þessari viku er notandi með 5 vaktir.
     * @param weekNr
     * @param userId
     * @return Shift[]
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Shift[] getWeek(int userId, int weekNr) {
        LocalDateTime weekstart = findWeekStartDay(weekNr);

        Shift[] week = new Shift[7];
        for (int i = 0; i < 7; i++) {
            LocalDateTime tmp = weekstart;
            week[i] = new Shift(1,
                    tmp.plusDays(i),
                    tmp.plusHours(8).plusDays(i),
                    null,
                    "Kokkur");
        }

        week[2] = null;
        week[3] = null;

        return week; //TODO: correct user and week
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalDateTime findWeekStartDay(int weekNr) {
        DayOfWeek correctMonday = LocalDateTime.now().getDayOfWeek();
        int offset = 0;

        while (correctMonday.toString() != "MONDAY") {
            offset = offset + 1;
            correctMonday = LocalDateTime.now().minusDays(offset).getDayOfWeek();
        }

        return LocalDateTime.now().minusDays(offset).plusWeeks(weekNr);
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
