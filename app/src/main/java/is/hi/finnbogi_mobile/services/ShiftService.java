package is.hi.finnbogi_mobile.services;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;

import is.hi.finnbogi_mobile.entities.Shift;
import is.hi.finnbogi_mobile.entities.User;
import is.hi.finnbogi_mobile.networking.NetworkManager;

public class ShiftService {
    private final String TAG = "ShiftService";

    NetworkManager mNetworkManager;

    public ShiftService(NetworkManager networkManager) {
        mNetworkManager = networkManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Shift getShiftById(int shiftId) {
        //TODO: make this function
        return new Shift(1, LocalDateTime.now(), LocalDateTime.now().plusHours(8),
                new User(1, "Hallur", null, null, null, null, null, true),
                "kokkur");
    }

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

    public String employeeString(String userName) {
        return "Starfsmaður: " + userName;
    }
}
