package com.example.muhammadfahad.readjs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


import com.example.muhammadfahad.readjs.R;
import com.example.muhammadfahad.readjs.bean.DataBean;
import com.example.muhammadfahad.readjs.dao.DBHelper;

import com.example.muhammadfahad.readjs.service.MyService;
import com.example.muhammadfahad.readjs.utils.Backup;
import com.example.muhammadfahad.readjs.utils.Compress;
import com.example.muhammadfahad.readjs.utils.FileSplitter;
import com.example.muhammadfahad.readjs.utils.Logger;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.muhammadfahad.readjs.utils.Backup.LocationList;

public class MainActivity extends AppCompatActivity{
    TextView tv;
    BroadcastReceiver broadcastReceiver;
    private Intent i;
    ProgressDialog pd;

    SharedPreferences sharedPreferences;
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //requestPermission();
        sharedPreferences = getSharedPreferences(StartActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        tv = (TextView) findViewById(R.id.textview);
        tv.setText("");

        i=new Intent(getApplicationContext(), MyService.class);
        startService(i);


    }

    @Override
    protected void onResume() {
        super.onResume();
        /*if(broadcastReceiver==null){

            broadcastReceiver=new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int total = 0;
                    if(pd.isShowing()){
                        pd.dismiss();
                        String status="";
                        Toast.makeText(MainActivity.this, intent.getExtras().getString("count"), Toast.LENGTH_SHORT).show();
                        for(int i=0;i<intent.getExtras().keySet().toArray().length;i++) {
                            tv.append(intent.getExtras().keySet().toArray()[i].toString().toUpperCase() + ": " + intent.getExtras().get(intent.getExtras().keySet().toArray()[i].toString()).toString() + "\n");
                            *//*if(intent.getExtras().keySet().toArray()[i].toString().equalsIgnoreCase("status")) {
                                status=intent.getExtras().get(intent.getExtras().keySet().toArray()[i].toString()).toString();
                            }else{
                                tv.append(intent.getExtras().keySet().toArray()[i].toString().toUpperCase() + ": " + intent.getExtras().get(intent.getExtras().keySet().toArray()[i].toString()).toString() + "\n");
                                total=total+Integer.parseInt(intent.getExtras().get(intent.getExtras().keySet().toArray()[i].toString()).toString());
                            }*//*



                        }
                        *//*if(status.equalsIgnoreCase("done")){
                            tv.append("STATUS: "+status+"\n");
                            tv.append("-------------------------------------------\n");
                            tv.append("TOTAL: "+total);

                        }else{
                            tv.append("STATUS: "+status+"\n");
                            tv.append("-------------------------------------------\n");
                            tv.append("TOTAL: "+total);
                        }*//*
                    }



                }
            };

        }
        registerReceiver(broadcastReceiver,new IntentFilter("Data"));*/
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    public boolean deleteFile(String filename) {
        File file = new File(filename);
        if (file.delete()) {
            Log.e("Deleting....", file.getName());
            return true;
        }
        return false;
    }


    @SuppressLint("MissingPermission")
    public void deviceId(TelephonyManager tMgr) {

        tv.append("IMEI: " + tMgr.getDeviceId() + "\n");

    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Exit Application?");
        alertDialogBuilder
                .setMessage("Click yes to exit!")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //unregisterReceiver(broadcastReceiver);
                                //stopService(i);
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


}
