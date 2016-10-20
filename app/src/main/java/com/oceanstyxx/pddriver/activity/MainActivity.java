package com.oceanstyxx.pddriver.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.oceanstyxx.pddriver.R;
import com.oceanstyxx.pddriver.helper.SessionManager;
import com.oceanstyxx.pddriver.model.DriveRequest;
import com.oceanstyxx.pddriver.model.InvoiceData;
import com.oceanstyxx.pddriver.model.Invoices;
import com.oceanstyxx.pddriver.utils.Const;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.R.attr.name;
import static android.view.View.GONE;
import static com.oceanstyxx.pddriver.R.id.bookingDate;
import static com.oceanstyxx.pddriver.R.id.bookingFrom;
import static com.oceanstyxx.pddriver.R.id.bookingNumber;
import static com.oceanstyxx.pddriver.R.id.bookingStartTime;
import static com.oceanstyxx.pddriver.R.id.bookingStartTimeTitle;
import static com.oceanstyxx.pddriver.R.id.bookingTotal;
import static com.oceanstyxx.pddriver.R.id.bookingTotalTitle;
import static com.oceanstyxx.pddriver.R.id.bookingTravelTime;
import static com.oceanstyxx.pddriver.R.id.bookingTravelTimeTitle;
import static com.oceanstyxx.pddriver.R.id.driverCode;
import static com.oceanstyxx.pddriver.R.id.ll2;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SessionManager session;

    private View linearLayout1;
    private View linearLayout2;

    TimerTask mTimerTask;
    final Handler handler = new Handler();
    Timer t = new Timer();


    private View ll3;
    private View ll6;

    OkHttpClient client;
    MediaType JSON;

    private BookStatusTask bookStatusTask = null;
    private StartDriveTask startDriveTask = null;
    private EndDriveTask endDriveTask = null;
    private SettleDriveDriveTask settleDriveDriveTask = null;
    private SignOutDriverTask signOutDriverTask = null;

    private TextView textViewBookingNumber;
    private TextView textViewBookingFrom;
    private TextView textViewBookingDate;
    private TextView bookingTravelTimeTitle;
    private TextView bookingTravelTime;
    private TextView bookingStartTimeTitle;
    private TextView bookingStartTime;
    private TextView bookingEndTimeTitle;
    private TextView bookingEndTime;
    private TextView bookingTotalTravelTimeTitle;
    private TextView bookingTotalTravelTime;
    private TextView bookingTotalTitle;
    private TextView bookingTotal;


    private Button btnAction;

    private TableLayout mTableLayout;
    ProgressDialog mProgressBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Session manager
        session = new SessionManager(getApplicationContext());
        getSupportActionBar().setTitle(session.getDriverName());

        client = new OkHttpClient();
        JSON = MediaType.parse("application/json; charset=utf-8");

        btnAction = (Button) findViewById(R.id.btnAction);
        btnAction.setTag(1);
        btnAction.setText("START DRIVE");

        linearLayout1 = findViewById(R.id.ll1);
        linearLayout2 = findViewById(R.id.ll2);
        ll3 = findViewById(R.id.ll3);
        ll6 = findViewById(R.id.ll6);

        doTimerTask();

        textViewBookingNumber = (TextView)findViewById(R.id.bookingNumber);
        textViewBookingFrom = (TextView)findViewById(R.id.bookingFrom);
        textViewBookingDate = (TextView)findViewById(R.id.bookingDate);
        bookingTravelTimeTitle = (TextView)findViewById(R.id.bookingTravelTimeTitle);
        bookingTravelTime = (TextView)findViewById(R.id.bookingTravelTime);
        bookingStartTimeTitle = (TextView)findViewById(R.id.bookingStartTimeTitle);
        bookingStartTime = (TextView)findViewById(R.id.bookingStartTime);
        bookingEndTimeTitle = (TextView)findViewById(R.id.bookingEndTimeTitle);
        bookingEndTime = (TextView)findViewById(R.id.bookingEndTime);
        bookingTotalTravelTimeTitle = (TextView)findViewById(R.id.bookingTotalTravelTimeTitle);
        bookingTotalTravelTime = (TextView)findViewById(R.id.bookingTotalTravelTime);
        bookingTotalTitle = (TextView)findViewById(R.id.bookingTotalTitle);
        bookingTotal = (TextView)findViewById(R.id.bookingTotal);


        mProgressBar = new ProgressDialog(this);

        // setup the table
        mTableLayout = (TableLayout) findViewById(R.id.tableInvoices);

        mTableLayout.setStretchAllColumns(true);

        btnAction = (Button) findViewById(R.id.btnAction);

        btnAction.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                final int status =(Integer) view.getTag();
                String driverId = session.getDriverId();
                if(status == 1) {
                    startDriveTask = new StartDriveTask();
                    startDriveTask.execute(driverId);
                } else if(status == 2){

                    endDriveTask = new EndDriveTask();
                    endDriveTask.execute(driverId);
                    loadData();

                }
                else if(status == 3){
                    settleDriveDriveTask = new SettleDriveDriveTask();
                    settleDriveDriveTask.execute(driverId);
                }

            }
        });



    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {

        signOutDriverTask = new SignOutDriverTask();
        String driverCode = session.getDriverCode();
        signOutDriverTask.execute(driverCode);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            logoutUser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class BookStatusTask extends AsyncTask<String, Void, String> {
        private Exception exception;


        protected String doInBackground(String... params) {
            try {
                String driverId = params[0];
                String getResponse = get(Const.BASE_URL+"driver/bookingStatus/"+driverId);
                return getResponse;
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }

        protected void onPostExecute(String getResponse) {
            try {

                session.setLogin(true);
                JSONObject jObjBookingStatus = new JSONObject(getResponse);

                String code = jObjBookingStatus.getString("code");
                //boolean error = jObj.getBoolean("error");
                if (code.equals("200") ) {

                    stopTask();
                    JSONArray jsonArray =  jObjBookingStatus.getJSONArray("data");
                    DriveRequest driveRequest = null;
                    for (int i=0; i<jsonArray.length(); i++) {
                        JSONObject jObj = new JSONObject(jsonArray.getString(i));
                        driveRequest = new Gson().fromJson(jsonArray.getString(i), DriveRequest.class);
                        System.out.println(" driveRequest "+driveRequest.getDrive_code());

                    }
                    btnAction.setVisibility(View.VISIBLE);
                    linearLayout1.setVisibility(GONE);
                    linearLayout2.setVisibility(View.VISIBLE);
                    textViewBookingNumber.setText(driveRequest.getDrive_code());
                    textViewBookingFrom.setText(driveRequest.getPub().getAddress());
                    textViewBookingDate.setText(driveRequest.getBooking_date_time());

                }
                else {
                    linearLayout1.setVisibility(View.VISIBLE);
                    linearLayout2.setVisibility(GONE);
                    btnAction.setVisibility(GONE);
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


    public class StartDriveTask extends AsyncTask<String, Void, String> {
        private Exception exception;


        protected String doInBackground(String... params) {
            try {

                String driverId = params[0];

                JSONObject json = new JSONObject();
                JSONObject manJson = new JSONObject();
                manJson.put("driveId", driverId);
                json.put("data",manJson);

                String getResponse = post(Const.BASE_URL+"driver/startDrive", json.toString());

                return getResponse;

            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }

        protected void onPostExecute(String getResponse) {
            try {
                JSONObject jObjBookingStatus = new JSONObject(getResponse);

                String code = jObjBookingStatus.getString("code");
                //boolean error = jObj.getBoolean("error");
                if (code.equals("200") ) {

                    DriveRequest driveRequest = null;
                    String strData =jObjBookingStatus.getString("data");

                    driveRequest = new Gson().fromJson(strData, DriveRequest.class);

                    bookingTravelTimeTitle.setVisibility(View.VISIBLE);
                    bookingTravelTime.setVisibility(View.VISIBLE);
                    bookingStartTimeTitle.setVisibility(View.VISIBLE);
                    bookingStartTime.setVisibility(View.VISIBLE);
                    bookingStartTime.setText(driveRequest.getDrive_start_time());
                    btnAction.setTag(2);
                    btnAction.setText("END DRIVE");

                }
                else {

                }


            }
            catch(Exception e ){
                e.printStackTrace();
            }
        }

        private String post(String url, String json) throws IOException {

            Log.w("pubdriver", "json request "+json);

            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }
    }

    public class EndDriveTask extends AsyncTask<String, Void, String> {
        private Exception exception;


        protected String doInBackground(String... params) {
            try {
                String driverId = params[0];

                JSONObject json = new JSONObject();
                JSONObject manJson = new JSONObject();
                manJson.put("driveId", driverId);
                json.put("data",manJson);

                String getResponse = post(Const.BASE_URL+"driver/endDrive", json.toString());

                return getResponse;
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }

        protected void onPostExecute(String getResponse) {
            try {
                JSONObject jObjBookingStatus = new JSONObject(getResponse);

                String code = jObjBookingStatus.getString("code");
                //boolean error = jObj.getBoolean("error");
                if (code.equals("200") ) {

                    DriveRequest driveRequest = null;
                    String strData =jObjBookingStatus.getString("data");

                    driveRequest = new Gson().fromJson(strData, DriveRequest.class);

                    ll3.setVisibility(View.VISIBLE);
                    ll6.setVisibility(View.VISIBLE);
                    bookingTotalTitle.setVisibility(View.VISIBLE);
                    bookingTotal.setVisibility(View.VISIBLE);
                    //bookingTotal.setText(driveRequest.getTotal_travel_time());
                    bookingEndTimeTitle.setVisibility(View.VISIBLE);
                    bookingEndTime.setVisibility(View.VISIBLE);
                    bookingEndTime.setText(driveRequest.getDrive_end_time());
                    bookingTotalTravelTimeTitle.setVisibility(View.VISIBLE);
                    bookingTotalTravelTime.setVisibility(View.VISIBLE);
                    bookingTotalTravelTime.setText(driveRequest.getTotal_travel_time());
                    btnAction.setTag(3);
                    btnAction.setText("SETTLE AMOUNT");

                }
                else {

                }


            }
            catch(Exception e ){
                e.printStackTrace();
            }
        }

        private String post(String url, String json) throws IOException {

            Log.w("pubdriver", "json request "+json);

            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }
    }


    public class SettleDriveDriveTask extends AsyncTask<String, Void, String> {
        private Exception exception;


        protected String doInBackground(String... params) {
            try {
                String driverId = params[0];

                JSONObject json = new JSONObject();
                JSONObject manJson = new JSONObject();
                manJson.put("driveId", driverId);
                json.put("data",manJson);

                String getResponse = post(Const.BASE_URL+"driver/settleDrive", json.toString());

                return getResponse;
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }

        protected void onPostExecute(String getResponse) {
            try {
                JSONObject jObjBookingStatus = new JSONObject(getResponse);

                String code = jObjBookingStatus.getString("code");
                //boolean error = jObj.getBoolean("error");
                if (code.equals("200") ) {

                    /*DriveRequest driveRequest = null;
                    String strData =jObjBookingStatus.getString("data");

                    driveRequest = new Gson().fromJson(strData, DriveRequest.class);*/
                    ll3.setVisibility(GONE);
                    ll6.setVisibility(GONE);
                    linearLayout1.setVisibility(View.VISIBLE);
                    linearLayout2.setVisibility(GONE);

                    bookingTravelTimeTitle.setVisibility(View.GONE);
                    bookingTravelTime.setVisibility(View.GONE);
                    bookingStartTimeTitle.setVisibility(View.GONE);
                    bookingStartTime.setVisibility(View.GONE);
                    bookingStartTime.setText("");

                    bookingTotalTitle.setVisibility(GONE);
                    bookingTotal.setVisibility(GONE);
                    bookingEndTimeTitle.setVisibility(GONE);
                    bookingEndTime.setVisibility(GONE);
                    bookingEndTime.setText("");
                    bookingTotalTravelTimeTitle.setVisibility(GONE);
                    bookingTotalTravelTime.setVisibility(GONE);
                    bookingTotalTravelTime.setText("");



                    btnAction.setVisibility(GONE);
                    btnAction.setTag(1);
                    btnAction.setText("START DRIVE");
                    doTimerTask();


                }
                else {

                }


            }
            catch(Exception e ){
                e.printStackTrace();
            }
        }

        private String post(String url, String json) throws IOException {

            Log.w("pubdriver", "json request "+json);

            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }
    }

    public void startLoadData() {

        /*mProgressBar.setCancelable(false);
        mProgressBar.setMessage("Fetching Invoices..");
        mProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressBar.show();*/
        new LoadDataTask().execute(0);

    }

    public void loadData() {

        int leftRowMargin=0;
        int topRowMargin=0;
        int rightRowMargin=0;
        int bottomRowMargin = 0;
        int textSize = 0, smallTextSize =0, mediumTextSize = 0;

        textSize = (int) getResources().getDimension(R.dimen.font_size_verysmall);
        smallTextSize = (int) getResources().getDimension(R.dimen.font_size_small);
        mediumTextSize = (int) getResources().getDimension(R.dimen.font_size_medium);

        Invoices invoices = new Invoices();
        InvoiceData[] data = invoices.getInvoices();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy");
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        int rows = data.length;
        //getSupportActionBar().setTitle("Invoices (" + String.valueOf(rows) + ")");
        TextView textSpacer = null;

        mTableLayout.removeAllViews();

        // -1 means heading row
        for(int i = -1; i < rows; i ++) {
            InvoiceData row = null;
            if (i > -1)
                row = data[i];
            else {
                textSpacer = new TextView(this);
                textSpacer.setText("");

            }
            // data columns
            final TextView tv = new TextView(this);
            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            tv.setGravity(Gravity.LEFT);

            tv.setPadding(5, 15, 0, 15);
            if (i == -1) {
                tv.setText("CHARGE DETAILS");
                tv.setBackgroundColor(Color.parseColor("#f0f0f0"));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
                tv.setTypeface(tv.getTypeface(), Typeface.BOLD);

            } else {
                tv.setBackgroundColor(Color.parseColor("#f8f8f8"));
                tv.setText(row.chargeDetails);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }

            final TextView tv2 = new TextView(this);
            if (i == -1) {
                tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            } else {
                tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.MATCH_PARENT));
                tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }

            tv2.setGravity(Gravity.LEFT);

            tv2.setPadding(5, 15, 0, 15);
            if (i == -1) {
                tv2.setText("QTY");
                tv2.setBackgroundColor(Color.parseColor("#f7f7f7"));
                tv2.setTypeface(tv.getTypeface(), Typeface.BOLD);
            }else {
                tv2.setBackgroundColor(Color.parseColor("#ffffff"));
                tv2.setTextColor(Color.parseColor("#000000"));
                tv2.setText(String.valueOf(row.qty));
            }


            final LinearLayout layCustomer = new LinearLayout(this);
            layCustomer.setOrientation(LinearLayout.VERTICAL);
            layCustomer.setPadding(0, 10, 0, 10);
            layCustomer.setBackgroundColor(Color.parseColor("#f8f8f8"));

            final TextView tv3 = new TextView(this);
            if (i == -1) {
                tv3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT));
                tv3.setPadding(5, 5, 0, 5);
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            } else {
                tv3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT));
                tv3.setPadding(5, 0, 0, 5);
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }

            tv3.setGravity(Gravity.TOP);


            if (i == -1) {
                tv3.setText("UNIT RATE");
                tv3.setBackgroundColor(Color.parseColor("#f0f0f0"));
                tv3.setTypeface(tv.getTypeface(), Typeface.BOLD);
            } else {
                tv3.setBackgroundColor(Color.parseColor("#f8f8f8"));
                tv3.setTextColor(Color.parseColor("#000000"));
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
                tv3.setText(String.valueOf(row.unitRate));
            }
            layCustomer.addView(tv3);


            /*if (i > -1) {
                final TextView tv3b = new TextView(this);
                tv3b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));

                tv3b.setGravity(Gravity.RIGHT);
                tv3b.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                tv3b.setPadding(5, 1, 0, 5);
                tv3b.setTextColor(Color.parseColor("#aaaaaa"));
                tv3b.setBackgroundColor(Color.parseColor("#f8f8f8"));
                tv3b.setText(row.customerAddress);
                layCustomer.addView(tv3b);
            }*/

            final LinearLayout layAmounts = new LinearLayout(this);
            layAmounts.setOrientation(LinearLayout.VERTICAL);
            layAmounts.setGravity(Gravity.RIGHT);
            layAmounts.setPadding(0, 10, 0, 10);
            layAmounts.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT));



            final TextView tv4 = new TextView(this);
            if (i == -1) {
                tv4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT));
                tv4.setPadding(5, 5, 1, 5);
                layAmounts.setBackgroundColor(Color.parseColor("#f7f7f7"));
            } else {
                tv4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tv4.setPadding(5, 0, 1, 5);
                layAmounts.setBackgroundColor(Color.parseColor("#ffffff"));
            }

            tv4.setGravity(Gravity.RIGHT);

            if (i == -1) {
                tv4.setText("TOTAL");
                tv4.setBackgroundColor(Color.parseColor("#f7f7f7"));
                tv4.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
                tv4.setTypeface(tv.getTypeface(), Typeface.BOLD);
            } else {
                tv4.setBackgroundColor(Color.parseColor("#ffffff"));
                tv4.setTextColor(Color.parseColor("#000000"));
                tv4.setText(decimalFormat.format(row.total));
                tv4.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }

            layAmounts.addView(tv4);


            /*if (i > -1) {
                final TextView tv4b = new TextView(this);
                tv4b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));

                tv4b.setGravity(Gravity.RIGHT);
                tv4b.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                tv4b.setPadding(2, 2, 1, 5);
                tv4b.setTextColor(Color.parseColor("#00afff"));
                tv4b.setBackgroundColor(Color.parseColor("#ffffff"));

                String due = "";
                if (row.amountDue.compareTo(new BigDecimal(0.01)) == 1) {
                    due = "Due:" + decimalFormat.format(row.amountDue);
                    due = due.trim();
                }
                tv4b.setText(due);
                layAmounts.addView(tv4b);
            }*/


            // add table row
            final TableRow tr = new TableRow(this);
            tr.setId(i + 1);
            TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT);
            trParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin);
            tr.setPadding(0,0,0,0);
            tr.setLayoutParams(trParams);



            tr.addView(tv);
            tr.addView(tv2);
            tr.addView(layCustomer);
            tr.addView(layAmounts);

            if (i > -1) {

                tr.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        TableRow tr = (TableRow) v;
                        //do whatever action is needed

                    }
                });


            }
            mTableLayout.addView(tr, trParams);

            if (i > -1) {

                // add separator row
                final TableRow trSep = new TableRow(this);
                TableLayout.LayoutParams trParamsSep = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT);
                trParamsSep.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin);

                trSep.setLayoutParams(trParamsSep);
                TextView tvSep = new TextView(this);
                TableRow.LayoutParams tvSepLay = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT);
                tvSepLay.span = 4;
                tvSep.setLayoutParams(tvSepLay);
                tvSep.setBackgroundColor(Color.parseColor("#d9d9d9"));
                tvSep.setHeight(1);

                trSep.addView(tvSep);
                mTableLayout.addView(trSep, trParamsSep);
            }


        }
    }


    public class SignOutDriverTask extends AsyncTask<String, Void, String> {
        private Exception exception;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {
            try {
                String driverCode = params[0];
                String getResponse = get(Const.BASE_URL+"driver/"+driverCode+"/signout");
                return getResponse;
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }

        protected void onPostExecute(String getResponse) {
            System.out.println(getResponse);

            try {

                JSONObject jObjDriver = new JSONObject(getResponse);

                String code = jObjDriver.getString("code");
                Log.d(TAG, "Register status: " + code);
                //boolean error = jObj.getBoolean("error");
                if (code.equals("200") ) {
                    session.setLogin(false);

                    // Launching the login activity
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
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

    //
    // The params are dummy and not used
    //
    class LoadDataTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {

            try {
                Thread.sleep(2000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return "Task Completed.";
        }
        @Override
        protected void onPostExecute(String result) {
            mProgressBar.hide();
            loadData();
        }
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    public void doTimerTask(){

        mTimerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        Log.d("TIMER", "TimerTask run");

                        bookStatusTask = new BookStatusTask();
                        String driverId = session.getDriverId();
                        bookStatusTask.execute(driverId);
                    }
                });
            }};

        // public void schedule (TimerTask task, long delay, long period)
        t.schedule(mTimerTask, 0, 15000);  //

    }

    public void stopTask(){

        if(mTimerTask!=null){

            Log.d("TIMER", "timer canceled");
            mTimerTask.cancel();
        }

    }


}
