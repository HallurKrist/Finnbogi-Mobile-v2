package is.hi.finnbogi_mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;

import is.hi.finnbogi_mobile.entities.UserInfo;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;
import is.hi.finnbogi_mobile.services.ShiftService;
import is.hi.finnbogi_mobile.services.UserInfoService;

public class UserInfoActivity extends AppCompatActivity {

    private static final String TAG = "UserInfoActivity";
    private static final String USERINFOID_KEY = "is.hi.finnbogi_mobile.userId";

    UserInfoService mUserInfoService; //TODO: make UserInfoService class

    public static Intent newIntent(Context packageContext, int userInfoId) {
        Intent intent = new Intent(packageContext, UserInfoActivity.class);
        intent.putExtra(USERINFOID_KEY, userInfoId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        NetworkManager networkManager = NetworkManager.getInstance(this);
        mUserInfoService = new UserInfoService(networkManager);

        int userId = getIntent().getIntExtra(USERINFOID_KEY, -1);

        mUserInfoService.getUserInfoByUserId(
                new NetworkCallback<UserInfo>(

                ) {
                    @Override
                    public void onSuccess(UserInfo result) {
                        //TODO: put userinfo into view
                    }

                    @Override
                    public void onFailure(String errorString) {
                        Log.e(TAG, "Error when getting userinfo for user");
                    }
                },
                userId
        );
    }
}