package com.oceanstyxx.pddriver.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;

import com.oceanstyxx.pddriver.R;
import com.oceanstyxx.pddriver.helper.SessionManager;
import com.oceanstyxx.pddriver.utils.Const;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.R.attr.password;
import static com.oceanstyxx.pddriver.R.id.driverCode;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText driverCodeTextView;

    private ValidateDriverTask validateDriverTask = null;

    private View loginFormView;

    private static ProgressDialog pleaseWaitDialog;
    private static ProgressDialog pDialog;

    OkHttpClient client;
    MediaType JSON;

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        driverCodeTextView = (EditText) findViewById(driverCode);

        loginFormView = findViewById(R.id.login_form);

        client = new OkHttpClient();
        JSON = MediaType.parse("application/json; charset=utf-8");

        // Session manager
        session = new SessionManager(getApplicationContext());

        Button submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                initLogin();
            }
        });
    }

    public void initLogin() {

        driverCodeTextView.setError(null);

        String driverCode = driverCodeTextView.getText().toString();
        View focusView = null;
        boolean cancelLogin = false;

        if (TextUtils.isEmpty(driverCode)) {
            driverCodeTextView.setError(getString(R.string.field_required));
            focusView = driverCodeTextView;
            cancelLogin = true;
        }

        if (cancelLogin) {
            // error in login
            focusView.requestFocus();
        } else {
            // show progress spinner, and start background task to login
            validateDriverTask = new ValidateDriverTask();
            validateDriverTask.execute(driverCode);
        }
    }


    public class ValidateDriverTask extends AsyncTask<String, Void, String> {
        private Exception exception;


        protected String doInBackground(String... params) {
            try {
                String driverCode = params[0];
                String getResponse = get(Const.BASE_URL+"driver/validateDriver/"+driverCode);
                return getResponse;
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }

        protected void onPostExecute(String getResponse) {
            try {

                session.setLogin(true);
                JSONArray jsonArray = new JSONArray(getResponse);

                int arraySize = jsonArray.length();

                if(arraySize>0){

                    JSONObject jObjDriver = new JSONObject(jsonArray.getString(0));

                    String driverName = jObjDriver.getString("first_name")+" "+jObjDriver.getString("last_name");
                    String driverId = jObjDriver.getString("id");
                    session.setKeyDriverId(driverId);
                    session.setKeyDriverName(driverName);
                    // Launch login activity
                    Intent i = new Intent(getApplicationContext(),
                            MainActivity.class);
                    startActivity(i);
                    finish();
                }
                else {
                    driverCodeTextView.setError(getString(R.string.incorrect_drive_code));
                    driverCodeTextView.requestFocus();
                }

            }
            catch(Exception e ){
                e.printStackTrace();
            }
        }

        public String get(String url) throws IOException {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        }
    }

}
