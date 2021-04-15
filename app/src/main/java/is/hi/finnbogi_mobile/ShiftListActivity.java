package is.hi.finnbogi_mobile;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import is.hi.finnbogi_mobile.entities.User;
import is.hi.finnbogi_mobile.listAdapters.NotificationListAdapter;
import is.hi.finnbogi_mobile.listAdapters.ShiftListAdapter;
import is.hi.finnbogi_mobile.listAdapters.UserListAdapter;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;
import is.hi.finnbogi_mobile.services.ShiftListService;
import is.hi.finnbogi_mobile.services.UserListService;

public class ShiftListActivity extends AppCompatActivity {
    private static final String TAG = "ShiftListActivity";

    private ListView mList;

    private ShiftListService mShiftListService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_list);

        // init networkmanager and service
        NetworkManager networkManager = NetworkManager.getInstance(ShiftListActivity.this);
        mShiftListService = new ShiftListService(networkManager);

        // get all shiftIds, shift dateTimes (with dte, startTime and endTime) and shift roles from service
        mShiftListService.getIdNDateTimeNRole(new NetworkCallback<String[][]>() {
            // String[][] result -> {{...shiftIds...}{..shiftDateTimes...}{...shiftRoles...}}
            @Override
            public void onSuccess(String[][] result) {
                int[] shiftIds;
                shiftIds = new int[result[0].length];
                for (int i = 0; i < result[0].length; i++) {
                    shiftIds[i] = (int) Double.parseDouble(result[0][i]);
                }

                // make adapter that handles onclicks
                ShiftListAdapter adapter = new ShiftListAdapter(ShiftListActivity.this, mShiftListService, shiftIds, result[1], result[2]);

                mList = (ListView) findViewById(R.id.shift_list);
                mList.setAdapter(adapter);
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Error while filling out userList " + errorString);
            }
        });
    }
}