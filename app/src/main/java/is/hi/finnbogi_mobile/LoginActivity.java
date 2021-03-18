package is.hi.finnbogi_mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import is.hi.finnbogi_mobile.entities.User;
import is.hi.finnbogi_mobile.networking.NetworkCallback;
import is.hi.finnbogi_mobile.networking.NetworkManager;
import is.hi.finnbogi_mobile.services.LoginService;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_CODE_HOME = 0;

    private EditText mEditTextUserName;
    private EditText mEditTextPassword;
    private Button mButtonLogin;

    private String mUserName;
    private String mPassword;
    private User mUserLoggingIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        NetworkManager networkManager = NetworkManager.getInstance(this);
        LoginService loginService = new LoginService(networkManager);

        mEditTextUserName = (EditText) findViewById(R.id.user_name);
        mEditTextPassword = (EditText) findViewById(R.id.user_password);
        mButtonLogin = (Button) findViewById(R.id.button_login);
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserName = mEditTextUserName.getText().toString();
                mPassword = mEditTextPassword.getText().toString();
                // Reynum að logga inn með þessu username og password
                mUserLoggingIn = loginService.login(mUserName, mPassword);
                // Ef user er null þá gekk það ekki og við sýnum toast - annars færum við user í home
                if (mUserLoggingIn == null) {
                    Toast.makeText(LoginActivity.this, "Notendanafn eða lykilorð rangt", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = HomeActivity.newIntent(LoginActivity.this, mUserLoggingIn.getUserId());
                    startActivityForResult(intent, REQUEST_CODE_HOME);
                }
            }
        });
    }
}