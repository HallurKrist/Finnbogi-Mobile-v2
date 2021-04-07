package is.hi.finnbogi_mobile;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import is.hi.finnbogi_mobile.entities.User;
import is.hi.finnbogi_mobile.listAdapters.NotificationListAdapter;
import is.hi.finnbogi_mobile.listAdapters.UserListAdapter;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;
import is.hi.finnbogi_mobile.services.UserListService;

public class UserListActivity extends AppCompatActivity {
    private final String TAG = "UserListActivity";

    private ListView mList;

    private UserListService mUserListService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        NetworkManager networkManager = NetworkManager.getInstance(this);
        mUserListService = new UserListService(networkManager);

        mUserListService.getUsers(new NetworkCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> result) {

                String[] names = new String[result.size()];
                String[] roles = new String[result.size()];

                for (int i = 0; i < result.size(); i++) {
                    names[i] = result.get(i).getUserName();
                    roles[i] = result.get(i).getRole();
                }

                UserListAdapter adapter = new UserListAdapter(UserListActivity.this, names, roles);

                mList = (ListView) findViewById(R.id.user_list);
                mList.setAdapter(adapter);
            }

            @Override
            public void onFailure(String errorString) {
                Log.e(TAG, "Error getting users");
            }
        });



        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    //code specific to first list item
                    Toast.makeText(getApplicationContext(),"Place Your First Option Code",Toast.LENGTH_SHORT).show();
                }

                else if(position == 1) {
                    //code specific to 2nd list item
                    Toast.makeText(getApplicationContext(),"Place Your Second Option Code",Toast.LENGTH_SHORT).show();
                }

                else if(position == 2) {

                    Toast.makeText(getApplicationContext(),"Place Your Third Option Code",Toast.LENGTH_SHORT).show();
                }
                else if(position == 3) {

                    Toast.makeText(getApplicationContext(),"Place Your Forth Option Code",Toast.LENGTH_SHORT).show();
                }
                else if(position == 4) {

                    Toast.makeText(getApplicationContext(),"Place Your Fifth Option Code",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}