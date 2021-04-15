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
import is.hi.finnbogi_mobile.listAdapters.UserListAdapter;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;
import is.hi.finnbogi_mobile.services.UserListService;

public class UserListActivity extends AppCompatActivity {
    private static final String TAG = "UserListActivity";

    private ListView mList;
    private Button mDelete;

    private UserListService mUserListService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        //init networkmanager and service
        NetworkManager networkManager = NetworkManager.getInstance(UserListActivity.this);
        mUserListService = new UserListService(networkManager);

        // get names and roles for all users and put it in the view
        mUserListService.getNamesAndRoles(new NetworkCallback<String[][]>() {
            @Override
            public void onSuccess(String[][] result) {
                //make adapter that handles onclicks
                UserListAdapter adapter = new UserListAdapter(UserListActivity.this, mUserListService, result[0], result[1]);

                mList = (ListView) findViewById(R.id.user_list);
                mList.setAdapter(adapter);
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Error while filling out userList " + errorString);
            }
        });
    }
}