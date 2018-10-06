package ke.topcast.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import ke.topcast.Data.ConnectToServer.Api;
import ke.topcast.R;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static ke.topcast.activities.MainActivity.MY_PREFS_NAME;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String SIGN_IN = "SIGN_IN";
    private static final String SIGN_UP = "SIGN_UP";
    private String pageState = SIGN_UP;
    private Context ctx = null;


    private UserLoginTask mAuthTask = null;
    private UserDetailsTask userDetailsTask = null;

    // UI references.
    private EditText mNameView, mPhoneView, mPasswordView, cPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        MainActivity.RegisterPage = true;
        ctx = this;

        getSupportActionBar().setTitle("ایجاد حساب");
        mNameView = (EditText) findViewById(R.id.name);
        mPhoneView = (EditText) findViewById(R.id.phone);
        cPasswordView = (EditText) findViewById(R.id.c_password);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attempt();
                    return true;
                }
                return false;
            }
        });

        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pageState == SIGN_IN){
                    attempt();
                }else {
                    mNameView.setVisibility(View.GONE);
                    cPasswordView.setVisibility(View.GONE);
                    mNameView.setText("");
                    cPasswordView.setText("");
                    pageState = SIGN_IN;
                    getSupportActionBar().setTitle("ورود");
                }

            }
        });
        Button mSignUpButton = (Button) findViewById(R.id.sign_up_button);
        mSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pageState == SIGN_UP){
                    attempt();
                }else {
                    mNameView.setVisibility(View.VISIBLE);
                    cPasswordView.setVisibility(View.VISIBLE);
                    pageState = SIGN_UP;
                    getSupportActionBar().setTitle("ایجاد حساب");
                }
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }


    @Override
    public void onBackPressed() {
        finish();
    }


    private void attempt() {
        if (mAuthTask != null) {
            return;
        }


        mPhoneView.setError(null);
        mPasswordView.setError(null);
        if (pageState == SIGN_UP){
            mNameView.setError(null);
            cPasswordView.setError(null);
        }

        String phone = mPhoneView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (pageState == SIGN_UP){
            if (mNameView.getText().toString().isEmpty()){
                mNameView.setError("این فیلد مورد نیاز است");
                focusView = mNameView;
                cancel = true;
            }
            if (mNameView.getText().toString().startsWith(" ")){
                mNameView.setError("نام با فاصله شروع نمی شود");
                focusView = mNameView;
                cancel = true;
            }
            if (!mPasswordView.getText().toString().equals(cPasswordView.getText().toString())){
                cPasswordView.setError("مطابقت ندارد");
                focusView = cPasswordView;
                cancel = true;
            }
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) | !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(phone)) {
            mPhoneView.setError("این فیلد مورد نیاز است");
            focusView = mPhoneView;
            cancel = true;
        } else if (!isPhoneValid(phone)) {
            mPhoneView.setError("شماره تلفن معتبر نمی باشد");
            focusView = mPhoneView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            if (pageState == SIGN_IN) {
                mAuthTask = new UserLoginTask(null, phone, password, null);
                mAuthTask.execute((Void) null);
            }else {
                mAuthTask = new UserLoginTask(mNameView.getText().toString(), phone, password, cPasswordView.getText().toString());
                mAuthTask.execute((Void) null);
            }
        }
    }

    private boolean isPhoneValid(String phone) {
        return phone.length() == 11;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        private final String name;
        private final String phone;
        private final String mPassword;
        private final String cPassword;
        private String token = null;

        UserLoginTask(String name, String phone, String password, String cPassword) {
            this.name = name;
            this.phone = phone;
            this.mPassword = password;
            this.cPassword = cPassword;
        }
    String er;
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                OkHttpClient client = new OkHttpClient();
                if (pageState == SIGN_IN){
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("phoneNumber", phone)
                            .addFormDataPart("password", mPassword)
                            .build();
                    Request request = new Request.Builder()
                            .url(Api.URL_LOGIN)
                            .post(requestBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    JSONObject object = new JSONObject(response.body().string());
                    JSONObject object1 = new JSONObject(object.getString("success"));

                    if (response.isSuccessful()){

                        token = object1.getString("token");
                        if (token.isEmpty())
                            return false;
                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                        token = "Bearer " + token;
                        editor.putString("token", token);
                        editor.commit();
                    }else {
                        return false;
                    }
                }else {

                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("name", name)
                            .addFormDataPart("phoneNumber", phone)
                            .addFormDataPart("password", mPassword)
                            .addFormDataPart("c_password", cPassword)
                            .build();

                    Request request = new Request.Builder()
                            .url(Api.URL_REGISTER)
                            .post(requestBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    JSONObject object = new JSONObject(response.body().string());
                    JSONObject object2 = object.getJSONObject("success");

                    if (response.isSuccessful()){
                        token = object2.getString("token");
                        if (token.isEmpty())
                            return false;
                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                        token = "Bearer " + token;
                        editor.putString("token", token);
                        editor.putString("name", name);
                        editor.putString("phoneNumber", phone);
                        editor.commit();
                        return true;
                    }else {
                        return false;
                    }
                }
            } catch (Exception e) {
                er = e.toString()+er;
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);
            mAuthTask = null;
            showProgress(false);

            Toast.makeText(ctx, er, Toast.LENGTH_LONG).show();

            if (success) {
                if (pageState == SIGN_IN){
                    userDetailsTask = new UserDetailsTask(token);
                    userDetailsTask.execute();
                }else {
                    Intent mIntent = new Intent(ctx, MainActivity.class);
                    startActivity(mIntent);
                    finish();
                }
            } else {
                if (pageState == SIGN_IN){
                    Toast.makeText(getApplicationContext(), "شماره تلفن یا رمز اشتباه است", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(ctx, "اگر پیش تر  حساب خود را ایجاد کرده اید از گزینه ورود استفاده نمایید", Toast.LENGTH_LONG).show();
                    mPhoneView.setError("یک حساب با این شماره تلفن قبلا ایجاد شده است");
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }


    public class UserDetailsTask extends AsyncTask<Void, Void, Boolean> {
        private String token;
        String error;
        UserDetailsTask(String token) {
            this.token = token;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

                OkHttpClient client = new OkHttpClient();

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("Authorization", token)
                        .build();

                Request request = new Request.Builder()
                        .url(Api.URL_USER_DETAILS)
                        .post(requestBody)
                        .addHeader("Authorization", token)
                        .build();

                Response response = client.newCall(request).execute();
                JSONObject objectDetails = new JSONObject(response.body().string());
                JSONObject objectDetails1 = objectDetails.getJSONObject("success");
                if (response.isSuccessful()){
                    String name = "";
                    String phone = "";

                    if (objectDetails1 == null)
                        return false;
                    name = objectDetails1.getString("name");
                    phone = objectDetails1.getString("phoneNumber");


                    editor.putString("name", name);
                    editor.putString("phoneNumber", phone);
                    editor.commit();
                }else {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);
            mAuthTask = null;
            showProgress(false);
            if (success) {
                Intent mIntent = new Intent(ctx, MainActivity.class);
                startActivity(mIntent);
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }
}

