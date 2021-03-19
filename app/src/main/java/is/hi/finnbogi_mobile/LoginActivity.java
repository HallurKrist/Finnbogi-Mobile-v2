package is.hi.finnbogi_mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private static final String MY_PREFERENCES = "Session";

    private EditText mEditTextUserName;
    private EditText mEditTextPassword;
    private Button mButtonLogin;

    private String mUserName;
    private String mPassword;
    private User mUserLoggingIn;

    private SharedPreferences mSharedPreferences;

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, LoginActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        NetworkManager networkManager = NetworkManager.getInstance(this);
        LoginService loginService = new LoginService(networkManager);

        mSharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        mEditTextUserName = (EditText) findViewById(R.id.user_name);
        mEditTextPassword = (EditText) findViewById(R.id.user_password);
        mButtonLogin = (Button) findViewById(R.id.button_login);
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            /**
             * Þegar ýtt er á innskráningartakka.
             * Reynir að innskrá notanda, ef tekst þá er HomeActivity opnað,
             * annars eru birt skilaboð um að innskráning hafi ekki tekist.
             * @param v
             */
            @Override
            public void onClick(View v) {
                mUserName = mEditTextUserName.getText().toString();
                mPassword = mEditTextPassword.getText().toString();
                // Reynum að logga inn með þessu username og password
                loginService.login(new NetworkCallback<User>() {
                    @Override
                    public void onSuccess(User result) {
                        mUserLoggingIn = result;
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString("user", String.valueOf(mUserLoggingIn));
                        //editor.putInt("userId", mUserLoggingIn.getUserId());
                        editor.commit();
                        Intent intent = HomeActivity.newIntent(LoginActivity.this, mUserLoggingIn.getUserId());
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(String errorString) {
                        Toast.makeText(LoginActivity.this, "Notendanafn eða lykilorð rangt", Toast.LENGTH_SHORT).show();
                    }
                }, mUserName, mPassword);
            }
        });
    }
}