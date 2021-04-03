package is.hi.finnbogi_mobile.services;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

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
                List<ShiftExchange> allShiftExchanges = gson.fromJson(String.valueOf(result), listType);
                callback.onSuccess(allShiftExchanges);
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Gekk ekki að ná í öll ShiftExchange: " + errorString);
                callback.onFailure(errorString);
            }
        }, new String[] {"shiftexchanges"});
    }
}
