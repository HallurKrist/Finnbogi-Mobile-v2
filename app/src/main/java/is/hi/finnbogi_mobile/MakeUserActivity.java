package is.hi.finnbogi_mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import is.hi.finnbogi_mobile.entities.User;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;
import is.hi.finnbogi_mobile.services.MakeUserService;

public class MakeUserActivity extends AppCompatActivity {

    private static final String TAG = "MakeUserActivity";

    // Viðmótshlutir
    private EditText mEditTextUserName;
    private Spinner mSpinnerRole;
    private EditText mEditTextPassword;
    private EditText mEditTextSsn;
    private CheckBox mCheckBoxAdmin;
    private Button mButtonConfirm;
    private Button mButtonCancel;

    // Global breytur
    private String mUserName;
    private String mRole;
    private String mPassword;
    private String mSsn;
    private boolean mIsAdmin;

    /**
     * Upphafsstillir alla viðmótshluti, nær í gögn og setur hlustara.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_user);

        NetworkManager networkManager = NetworkManager.getInstance(this);
        MakeUserService makeUserService = new MakeUserService(networkManager);

        mEditTextUserName = (EditText) findViewById(R.id.user_make_userName);
        mSpinnerRole = (Spinner) findViewById(R.id.user_make_role);
        mEditTextPassword = (EditText) findViewById(R.id.user_make_password);
        mEditTextSsn = (EditText) findViewById(R.id.user_make_ssn);
        mCheckBoxAdmin = (CheckBox) findViewById(R.id.user_make_admin);
        mButtonCancel = (Button) findViewById(R.id.user_make_cancel);
        mButtonConfirm = (Button) findViewById(R.id.user_make_confirm);

        // Setja roles í spinner fyrir starfsheiti
        String[] roles = new String[]{"Chef", "Bartender", "Waiter", "Busboy", "ShiftManager", "Employer"};
        ArrayAdapter<String> rolesAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, roles);
        rolesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerRole.setAdapter(rolesAdapter);

        /**
         * Event listener fyrir checkbox, setur mIsAdmin tilviksbreytu
         * í samræmi við checkboxið.
         *
         */
        mCheckBoxAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox) v).isChecked();
                mIsAdmin = checked;
            }
        });

        /**
         * Event listener fyrir cancel takka, sendir
         * notanda aftur í HomeActivity.
         *
         */
        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /**
         * Event listener fyrir Staðfesta takka.
         * Reynt er að búa til nýjan notanda, ef það tekst
         * er notandi sendur í UserInfoActivity, annars eru
         * birt villuskilaboð.
         *
         */
        mButtonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ná í texta úr viðmóti
                mUserName = mEditTextUserName.getText().toString();
                mRole = mSpinnerRole.getSelectedItem().toString();
                mPassword = mEditTextPassword.getText().toString();
                mSsn = mEditTextSsn.getText().toString();
                // Reyna að búa til user með þessum upplýsingum
                makeUserService.createUser(new NetworkCallback<User>() {
                    @Override
                    public void onSuccess(User result) {
                        Toast.makeText(MakeUserActivity.this, getString(R.string.makeuser_activity_success), Toast.LENGTH_SHORT).show();
                        Intent intent = UserInfoActivity.newIntent(MakeUserActivity.this, result.getUserId());
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(String errorString) {
                        Toast.makeText(MakeUserActivity.this, getString(R.string.makeuser_activity_fail), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, errorString);
                    }
                }, mUserName, mRole, mPassword, mSsn, mIsAdmin);
            }
        });

    }
}