package is.hi.finnbogi_mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import is.hi.finnbogi_mobile.entities.UserInfo;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;
import is.hi.finnbogi_mobile.services.ShiftService;
import is.hi.finnbogi_mobile.services.UserInfoService;

public class UserInfoActivity extends AppCompatActivity {

    private static final String TAG = "UserInfoActivity";
    private static final String USERID_KEY = "is.hi.finnbogi_mobile.userId";

    private EditText mFirstName;
    private EditText mSurName;
    private EditText mAddress;
    private EditText mPhoneNr;
    private EditText mEmail;
    private TextView mSSN;
    private Button mSubmitChanges;

    private int mUserId;

    UserInfoService mUserInfoService;

    public static Intent newIntent(Context packageContext, int userId) {
        Intent intent = new Intent(packageContext, UserInfoActivity.class);
        intent.putExtra(USERID_KEY, userId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        NetworkManager networkManager = NetworkManager.getInstance(this);
        mUserInfoService = new UserInfoService(networkManager);

        mFirstName = (EditText) findViewById(R.id.user_info_firstname);
        mSurName = (EditText) findViewById(R.id.user_info_surname);
        mAddress = (EditText) findViewById(R.id.user_info_address);
        mPhoneNr = (EditText) findViewById(R.id.user_info_phone);
        mEmail = (EditText) findViewById(R.id.user_info_email);
        mSSN = (TextView) findViewById(R.id.user_info_ssn);
        mSubmitChanges = (Button) findViewById(R.id.user_info_applyChanges);
        mSubmitChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changesSubmited();
            }
        });

        mUserId = getIntent().getIntExtra(USERID_KEY, -1);
        Log.d(TAG, "userId is " + mUserId);

        loadUserInfo();
    }

    private void loadUserInfo() {
        mUserInfoService.getUserInfoByUserId(
                new NetworkCallback<UserInfo>() {
                    @Override
                    public void onSuccess(UserInfo result) {
                        mFirstName.setText(result.getFirstName());
                        mSurName.setText(result.getSurName());
                        mAddress.setText(result.getAddress());
                        mPhoneNr.setText(result.getPhoneNumber());
                        mEmail.setText(result.getEmail());
                        mSSN.setText(result.getSSN());
                    }

                    @Override
                    public void onFailure(String errorString) {
                        Log.e(TAG, "Error when getting userinfo for user");
                    }
                },
                mUserId
        );
    }

    private void changesSubmited() {
        String FN = mFirstName.getText().toString();
        String SN = mSurName.getText().toString();
        String ADD = mAddress.getText().toString();
        String PH = mPhoneNr.getText().toString();
        String EM = mEmail.getText().toString();
        String SSN = mSSN.getText().toString();

        UserInfo info = new UserInfo(mUserId, FN, SN, ADD, PH, SSN, null, EM);

        mUserInfoService.patchUserInfo(info);
    }
}