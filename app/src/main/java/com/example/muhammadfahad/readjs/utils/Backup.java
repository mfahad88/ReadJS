package com.example.muhammadfahad.readjs.utils;

import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.util.Log;


import com.example.muhammadfahad.readjs.bean.DataBean;
import com.example.muhammadfahad.readjs.bean.InfoBean;

import com.example.muhammadfahad.readjs.bean.Records;
import com.example.muhammadfahad.readjs.dao.DBHelper;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Fahad on 22/03/2018.
 */

public class Backup {


    public static int ContactList(Context context, DBHelper dbHelper, InfoBean infoBean) {
        int recId=0;
        int catId=1;

        List<DataBean> list = new ArrayList<>();
        Cursor rs = dbHelper.getCategory("Contact");

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String device_id = tm.getDeviceId();
        if (rs.getCount() == 0) {
            dbHelper.insertCategory("Contact");
        }


        /*if(rs.moveToFirst()){
            do{
                for(int j=0;j<rs.getCount();j++){
                    catId=rs.getInt(j);
                }
            }while (rs.moveToNext());
        }*/
        Log.i("Backup>>>>>","Fetching contact....");
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (cursor.moveToFirst()) { // must check the result to prevent exception

            do {
                recId++;
                for (int i = 0; i < cursor.getColumnCount(); i++) {
//                        Log.d("Object-->",cursor.getColumnName(i)+" ---- "+cursor.getString(i));
                    DataBean dataBean = new DataBean(catId,recId, cursor.getColumnName(i), cursor.getString(i), 0, device_id, new Date().toString(),infoBean);
                    list.add(dataBean);
                }
            } while (cursor.moveToNext());
        } else {
            // empty box, no SMS
        }
        Log.i("Backup>>>>>","Fetched contact....");
        if (list.size() > 0) {
            /*for (int i = 0; i < list.size(); i++) {
                  Log.e("List>>>",list.toString());
            }*/
            if (dbHelper.insertDetail(list)) {

                return list.size();
            }
        }

        cursor.close();
        return 0;
    }





    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static int SmsList(Context context, DBHelper dbHelper,InfoBean infoBean)  {
        int recId=0;
        int catId=2;
        List<DataBean> list = new ArrayList<>();
        Log.i("Backup>>>>>","Fetching Sms....");
        //try{
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        @SuppressLint("MissingPermission") String device_id = tm.getDeviceId();
        Cursor rs = dbHelper.getCategory("Sms");
        String dateString = "01/01/2018";
        String endString = "31/01/2018";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");


        if (rs.getCount() == 0) {
            dbHelper.insertCategory("Sms");
        }

        /*if(rs.moveToFirst()){
            do{
                for(int j=0;j<rs.getCount();j++){
                    catId=rs.getInt(j);
                }
            }while (rs.moveToNext());
        }*/

        Cursor cursor = context.getContentResolver().query(Telephony.Sms.CONTENT_URI/*Uri.parse("content://sms/inbox")*/, null/*new String[]{"body", "error_code", "rcs_message_id", "rcs_message_type"}*/,
                /*"date BETWEEN "+startDate+" AND "+endDate*/ null, null, null);
        if (cursor.moveToFirst()) { // must check the result to prevent exception

            do {
                recId++;

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    DataBean dataBean;
                    if (cursor.getColumnName(i).equalsIgnoreCase("body")) {
                        //String patternToMatch = "[\\\\!\"#$%&()*+/:;<=>?@\\[\\]^_{|}~|\\s]+";
                        String patternToMatch = "[\\\\!\"#$%&()*+/;<=>?@\\[\\]^_{|}~|\\s]+";
                        Pattern p = Pattern.compile(patternToMatch);
                        Matcher m = p.matcher(cursor.getString(i));
                        if (m.find()) {
                            dataBean = new DataBean(catId, recId, cursor.getColumnName(i), m.replaceAll(" "), 0, device_id, new Date().toString(), infoBean);
                        } else {
                            dataBean = new DataBean(catId, recId, cursor.getColumnName(i), cursor.getString(i), 0, device_id, new Date().toString(), infoBean);
                        }
                    } else {
                        dataBean = new DataBean(catId, recId, cursor.getColumnName(i), cursor.getString(i), 0, device_id, new Date().toString(), infoBean);
                    }
                    list.add(dataBean);
                    //  dbHelper.insertDetail(2,cursor.getColumnName(i),cursor.getString(i),0,device_id,new Date().toString());
                }
            } while (cursor.moveToNext());
        }
        Log.i("Backup>>>>>","Fetched Sms....");
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                //Log.e("List>>>",list.toString());
            }
            if (dbHelper.insertDetail(list)) {
                return list.size();
            }
        }
        rs.close();
        cursor.close();
        /*}catch (Exception e){
            Log.d("Error--->",e.getMessage());
        }*/
        return 0;
    }

    public static int CallList(Context context, DBHelper dbHelper,InfoBean infoBean) {
        int recId=0;
        int catId=3;
        List<DataBean> list = new ArrayList<>();
        Cursor rs = dbHelper.getCategory("Call");
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Log.i("Backup>>>>>","Fetching Call....");

        @SuppressLint("MissingPermission") String device_id = tm.getDeviceId();
        Log.e("Count>>>>>", String.valueOf(rs.getCount()));
        if (rs.getCount() == 0) {
            dbHelper.insertCategory("Call");
        }
        /*if(rs.moveToFirst()){
            do{
                for(int j=0;j<rs.getCount();j++){
                    catId=rs.getInt(j);
                }
            }while (rs.moveToNext());
        }*/

        @SuppressWarnings("MissingPermission") Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI/*Uri.parse("content://call_log/calls")*/, null /*new String[]{"ANSWERED_EXTERNALLY_TYPE","BLOCKED_TYPE ","COUNTRY_ISO ","DATA_USAGE ","DATE ","DURATION ","FEATURES_HD_CALL ","FEATURES_VIDEO ","FEATURES_WIFI ","GEOCODED_LOCATION ","TYPE"}*/,
                null, null, null);
        if (cursor.moveToFirst()) { // must check the result to prevent exception

            do {
                recId++;

                for (int i = 0; i < cursor.getColumnCount(); i++) {

                    DataBean dataBean = new DataBean(catId, recId, cursor.getColumnName(i), cursor.getString(i), 0, device_id, new Date().toString(), infoBean);
                    list.add(dataBean);
                }
            } while (cursor.moveToNext());

        }
        Log.i("Backup>>>>>","Fetched Call....");
        if (list.size() > 0) {
            if (dbHelper.insertDetail(list)) {
                return list.size();
            }
        }
        rs.close();
        cursor.close();
        return 0;
    }

    public static int SensorList(Context context, DBHelper dbHelper,InfoBean infoBean) {
        int recId=0;
        int catId=4;
        List<DataBean> list = new ArrayList<>();
        Cursor rs = dbHelper.getCategory("Sensor");
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Log.i("Backup>>>>>","Fetching Sensor....");

        @SuppressLint("MissingPermission") String device_id = tm.getDeviceId();
        Log.e("Count>>>>>", String.valueOf(rs.getCount()));
        if (rs.getCount() == 0) {
            dbHelper.insertCategory("Sensor");
        }
        /*if(rs.moveToFirst()){
            do{
                for(int j=0;j<rs.getCount();j++){
                    catId=rs.getInt(j);
                }
            }while (rs.moveToNext());
        }*/
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        List<android.hardware.Sensor> sensors = sensorManager.getSensorList(android.hardware.Sensor.TYPE_ALL);
        recId++;
        for (int i = 0; i < sensors.size(); i++) {

            DataBean dataBean = new DataBean(catId, recId, "Sensor", sensors.toString(), 0, device_id, new Date().toString(), infoBean);
            list.add(dataBean);

            //tvSensor.append(sensors.toString() + "\n");
        }
        Log.i("Backup>>>>>","Fetched Sensor....");
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                //Log.e("List>>>",list.toString());
            }
            if (dbHelper.insertDetail(list)) {
                return list.size();
            }
        }
        rs.close();
        return 0;

    }

    public static int DeviceInfoList(Context context, DBHelper dbHelper,InfoBean infoBean) {
        int recId=0;
        int catId=5;
        List<DataBean> list = new ArrayList<>();
        Cursor rs = dbHelper.getCategory("DeviceInfo");
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Log.i("Backup>>>>>","Fetching DeviceInfo....");
        @SuppressLint("MissingPermission") String device_id = tm.getDeviceId();
        if (rs.getCount() == 0) {
            dbHelper.insertCategory("DeviceInfo");
        }
        /*if(rs.moveToFirst()){
            do{
                for(int j=0;j<rs.getCount();j++){
                    catId=rs.getInt(j);
                }
            }while (rs.moveToNext());
        }*/
        Map<String, String> map = new HashMap<>();
        recId++;
        map.put("SERIAL", Build.SERIAL);
        list.add(new DataBean(catId, recId,"SERIAL", Build.SERIAL, 0, device_id, new Date().toString(),infoBean));
        map.put("MODEL", Build.MODEL);
        list.add(new DataBean(catId, recId,"MODEL", Build.MODEL, 0, device_id, new Date().toString(),infoBean));
        map.put("ID", Build.ID);
        list.add(new DataBean(catId, recId,"ID", Build.ID, 0, device_id, new Date().toString(),infoBean));
        map.put("Manufacture", Build.MANUFACTURER);
        list.add(new DataBean(catId, recId,"SERIAL", Build.MANUFACTURER, 0, device_id, new Date().toString(),infoBean));
        map.put("brand", Build.BRAND);
        list.add(new DataBean(catId, recId,"brand", Build.BRAND, 0, device_id, new Date().toString(),infoBean));
        map.put("type", Build.TYPE);
        list.add(new DataBean(catId, recId,"type", Build.TYPE, 0, device_id, new Date().toString(),infoBean));
        map.put("user", Build.USER);
        list.add(new DataBean(catId, recId,"user", Build.USER, 0, device_id, new Date().toString(),infoBean));
        map.put("BASE", String.valueOf(Build.VERSION_CODES.BASE));
        list.add(new DataBean(catId, recId,"BASE", String.valueOf(Build.VERSION_CODES.BASE), 0, device_id, new Date().toString(),infoBean));
        map.put("INCREMENTAL", Build.VERSION.INCREMENTAL);
        list.add(new DataBean(catId, recId,"INCREMENTAL", Build.VERSION.INCREMENTAL, 0, device_id, new Date().toString(),infoBean));
        map.put("SDK", Build.VERSION.SDK);
        list.add(new DataBean(catId, recId,"SDK", Build.VERSION.SDK, 0, device_id, new Date().toString(),infoBean));
        map.put("BOARD", Build.BOARD);
        list.add(new DataBean(catId, recId,"BOARD", Build.BOARD, 0, device_id, new Date().toString(),infoBean));
        map.put("BRAND", Build.BRAND);
        list.add(new DataBean(catId, recId,"BRAND", Build.BRAND, 0, device_id, new Date().toString(),infoBean));
        map.put("HOST", Build.HOST);
        list.add(new DataBean(catId, recId,"HOST", Build.HOST, 0, device_id, new Date().toString(),infoBean));
        map.put("FINGERPRINT", Build.FINGERPRINT);
        list.add(new DataBean(catId, recId,"FINGERPRINT", Build.FINGERPRINT, 0, device_id, new Date().toString(),infoBean));
        map.put("VersionCode", Build.VERSION.RELEASE);
        Log.i("Backup>>>>>","Fetched DeviceInfo....");
        list.add(new DataBean(catId, recId,"VersionCode", Build.VERSION.RELEASE, 0, device_id, new Date().toString(),infoBean));

        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                //  Log.e("List>>>",list.toString());
            }
            if (dbHelper.insertDetail(list)) {
                return list.size();
            }
        }
        rs.close();
        return 0;
    }

    public static int AccountList(Context context, DBHelper dbHelper,InfoBean infoBean) {
        int recId=0;
        int catId=6;
        List<DataBean> list = new ArrayList<>();
        Cursor rs = dbHelper.getCategory("Account");
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Log.i("Backup>>>>>","Fetching Account....");
        @SuppressLint("MissingPermission") String device_id = tm.getDeviceId();
        if (rs.getCount() == 0) {
            dbHelper.insertCategory("Account");
        }

        /*if(rs.moveToFirst()){
            do{
                for(int j=0;j<rs.getCount();j++){
                    catId=rs.getInt(j);
                }
            }while (rs.moveToNext());
        }*/
        AccountManager accountManager = AccountManager.get(context);
        android.accounts.Account[] acc = accountManager.getAccounts();
        recId++;
        for (int i = 0; i < acc.length; i++) {
            //acc[i].name.toString();
            DataBean dataBean = new DataBean(6, recId,acc[i].name, acc[i].toString(), 0, device_id, new Date().toString(),infoBean);
            list.add(dataBean);
        }
        Log.i("Backup>>>>>","Fetched Account....");
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                //  Log.e("List>>>",list.toString());
            }
            if (dbHelper.insertDetail(list)) {
                return list.size();
            }
        }
        rs.close();

        return 0;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int CalendarContractEventsList(Context context, DBHelper dbHelper,InfoBean infoBean) {
        int recId=0;
        int catId=7;
        List<DataBean> list = new ArrayList<>();
        Cursor rs = dbHelper.getCategory("CalendarContractEvents");
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Log.i("Backup>>>>>","Fetching CalendarContractEvents....");


        @SuppressLint("MissingPermission") String device_id = tm.getDeviceId();
        Log.e("Count>>>>>", String.valueOf(rs.getCount()));
        if (rs.getCount() == 0) {
            dbHelper.insertCategory("CalendarContractEvents");
        }


        /*if(rs.moveToFirst()){
            do{
                for(int j=0;j<rs.getCount();j++){
                    catId=rs.getInt(j);
                }
            }while (rs.moveToNext());
        }*/

        @SuppressWarnings("MissingPermission") Cursor cursor = context.getContentResolver().query(CalendarContract.Events.CONTENT_URI, null,
                null, null, null);
        if (cursor.moveToFirst()) { // must check the result to prevent exception

            do {
                recId++;

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    DataBean dataBean = new DataBean(catId,recId, cursor.getColumnName(i), cursor.getString(i), 0, device_id, new Date().toString(),infoBean);
                    list.add(dataBean);
                }
            } while (cursor.moveToNext());

        }
        Log.i("Backup>>>>>","Fetched CalendarContractEvents....");
        if (list.size() > 0) {
            if (dbHelper.insertDetail(list)) {
                return list.size();
            }
        }
        rs.close();
        cursor.close();
        return 0;
    }



    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int BatteryList(final Context context, DBHelper dbHelper,InfoBean infoBean) {
        int recId=0;
        int catId=8;
        final List<DataBean> list = new ArrayList<>();
        try{
            Cursor rs = dbHelper.getCategory("Battery");
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Log.i("Backup>>>>>","Fetching Battery....");

            @SuppressLint("MissingPermission") final String device_id = tm.getDeviceId();
            Log.e("Count>>>>>", String.valueOf(rs.getCount()));
            if (rs.getCount() == 0) {
                dbHelper.insertCategory("Battery");
            }
            /*if(rs.moveToFirst()){
                do{
                    for(int j=0;j<rs.getCount();j++){
                        catId=rs.getInt(j);
                    }
                }while (rs.moveToNext());
            }*/
            recId++;
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            for(int i=0;i<batteryStatus.getExtras().keySet().toArray().length;i++) {
                list.add(new DataBean(catId,recId,batteryStatus.getExtras().keySet().toArray()[i].toString()
                        ,batteryStatus.getExtras().get(batteryStatus.getExtras().keySet().toArray()[i].toString()).toString()
                        ,0,device_id,new Date().toString(),infoBean));
            }
            Log.i("Backup>>>>>","Fetched Battery....");

            if (list.size() > 0) {
                if(dbHelper.insertDetail(list)){
                    return list.size();
                }
            }
            rs.close();

        }catch (Exception e){
            e.printStackTrace();
            Log.d("Error",e.getMessage());
        }
        return 0;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int CalendarContractRemindersList(Context context, DBHelper dbHelper,InfoBean infoBean) {
        int recId=0;
        int catId=9;
        List<DataBean> list = new ArrayList<>();
        Cursor rs = dbHelper.getCategory("CalendarContractReminders");
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        Log.i("Backup>>>>>","Fetching CalendarContractReminders....");
        @SuppressLint("MissingPermission") String device_id = tm.getDeviceId();
        Log.e("Count>>>>>", String.valueOf(rs.getCount()));
        if (rs.getCount() == 0) {
            dbHelper.insertCategory("CalendarContractReminders");
        }
        /*if(rs.moveToFirst()){
            do{
                for(int j=0;j<rs.getCount();j++){
                    catId=rs.getInt(j);
                }
            }while (rs.moveToNext());
        }*/

        @SuppressWarnings("MissingPermission") Cursor cursor = context.getContentResolver().query(CalendarContract.Reminders.CONTENT_URI, null,
                null, null, null);
        if (cursor.moveToFirst()) { // must check the result to prevent exception

            do {
                recId++;

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    DataBean dataBean = new DataBean(catId, recId,cursor.getColumnName(i), cursor.getString(i), 0, device_id, new Date().toString(),infoBean);
                    list.add(dataBean);
                }
            } while (cursor.moveToNext());

        }
        Log.i("Backup>>>>>","Fetched CalendarContractReminders....");
        if (list.size() > 0) {
            if (dbHelper.insertDetail(list)) {
                return list.size();
            }
        }
        rs.close();
        cursor.close();
        return 0;
    }

    @SuppressLint({"MissingPermission", "NewApi"})
    public static int LocationList(final Context context, final DBHelper dbHelper, List<DataBean> beanList, InfoBean infoBean) {
        int recId=0;
        int catId=1;
        final List<DataBean> list = beanList;
        try{
            Cursor rs = dbHelper.getCategory("Location");
//            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Log.i("Backup>>>>>","Fetching Location....");
            @SuppressLint("MissingPermission") final String device_id = tm.getDeviceId();
            Log.e("Count>>>>>", String.valueOf(rs.getCount()));
            if (rs.getCount() == 0) {
                dbHelper.insertCategory("Location");
            }
            /*if(rs.moveToFirst()){
                do{
                    for(int j=0;j<rs.getCount();j++){
                        catId=rs.getInt(j);
                    }
                }while (rs.moveToNext());
            }*/
            recId++;
            Log.i("Backup>>>>>","Fetched Location....");

            if (list.size() > 0) {
                if(dbHelper.insertDetail(list)){
                    return list.size();
                }
            }
            rs.close();

        }catch (Exception e){
            e.printStackTrace();
            Log.d("Error",e.getMessage());
        }
        return 0;
    }

    @SuppressLint({"MissingPermission", "NewApi"})
    public static int LocationList(final Context context, final DBHelper dbHelper, final Location location, final int recId, final InfoBean infoBean) {

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.setTimeZone(TimeZone.getDefault());

        final int catId=1;
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        Geocoder geocoder=new Geocoder(context, Locale.getDefault());
        final List<DataBean> beanList = new ArrayList<>();
        try{
            Cursor rs = dbHelper.getCategory("Location");
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Log.i("Backup>>>>>","Fetching Location....");
            @SuppressLint("MissingPermission") final String device_id = tm.getDeviceId();
            Log.e("Count>>>>>", String.valueOf(rs.getCount()));
            if (rs.getCount() == 0) {
                dbHelper.insertCategory("Location");
            }
            /*if(rs.moveToFirst()){
                do{
                    for(int j=0;j<rs.getCount();j++){
                        catId=rs.getInt(j);
                    }
                }while (rs.moveToNext());
            }*/
            Log.e("Location--->",String.valueOf(location.getLatitude())+","+String.valueOf(location.getLongitude()));
            if(location!=null){
                if(isConnected) {
                    OkHttpClient client = new OkHttpClient();
                    /*Request request_radius=new Request.Builder()
                            .url("https://mfahad88.000webhostapp.com/radius.php")
                            .build();
                    Response response_radius=client.newCall(request_radius).execute();
                    JSONArray array_radius=new JSONArray(response_radius.body().string());
                    JSONObject object_radius=array_radius.getJSONObject(0);*/

                    Request request_place = new Request.Builder()
                            //.url("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+String.valueOf(location.getLatitude())+","+String.valueOf(location.getLongitude())+"&radius="+object_radius.getString("value")+"&key=AIzaSyAMly2uKnHT14gr3sYXOKSrytvw25SlcsA")
                            .url("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+String.valueOf(location.getLatitude())+","+String.valueOf(location.getLongitude())+"&radius=10&key=AIzaSyAMly2uKnHT14gr3sYXOKSrytvw25SlcsA")
                            .build();

                    Response response_place = client.newCall(request_place).execute();
                    JSONObject object_place=new JSONObject(response_place.body().string());
                    JSONArray array_place=object_place.getJSONArray("results");
                    final List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    beanList.add(new DataBean(catId,recId,"Accuracy",String.valueOf(location.getAccuracy()),0,tm.getDeviceId(),new Date().toString(),infoBean));
                    beanList.add(new DataBean(catId,recId,"Altitude",String.valueOf(location.getAltitude()),0,tm.getDeviceId(),new Date().toString(),infoBean));
                    beanList.add(new DataBean(catId,recId,"Bearing",String.valueOf(location.getBearing()),0,tm.getDeviceId(),new Date().toString(),infoBean));
                    beanList.add(new DataBean(catId,recId,"ElapsedRealtimeNanos",String.valueOf(location.getElapsedRealtimeNanos()),0,new Date().toString(),tm.getDeviceId(),infoBean));
                    beanList.add(new DataBean(catId,recId,"Latitude",String.valueOf(location.getLatitude()),0,tm.getDeviceId(),new Date().toString(),infoBean));
                    beanList.add(new DataBean(catId,recId,"Longitude",String.valueOf(location.getLongitude()),0,tm.getDeviceId(),new Date().toString(),infoBean));
                    beanList.add(new DataBean(catId,recId,"Provider",String.valueOf(location.getProvider()),0,tm.getDeviceId(),new Date().toString(),infoBean));
                    beanList.add(new DataBean(catId,recId,"Speed",String.valueOf(((location.getSpeed()*3600)/1000)),0,tm.getDeviceId(),new Date().toString(),infoBean));
                    beanList.add(new DataBean(catId,recId,"Time",simpleDateFormat.format(new Date(Long.parseLong(String.valueOf(location.getTime())))),0,new Date().toString(),tm.getDeviceId(),infoBean));
                    beanList.add(new DataBean(catId,recId,"Address",String.valueOf(addresses.get(0).getAddressLine(0)),0,tm.getDeviceId(),new Date().toString(),infoBean));
                    beanList.add(new DataBean(catId,recId,"Known Name",String.valueOf(addresses.get(0).getFeatureName()),0,tm.getDeviceId(),new Date().toString(),infoBean));
//                    beanList.add(new DataBean(catId,recId,"Radius",object_radius.getString("value"),0,tm.getDeviceId(),new Date().toString(),infoBean));
                    beanList.add(new DataBean(catId,recId,"Radius","10",0,tm.getDeviceId(),new Date().toString(),infoBean));
                    beanList.add(new DataBean(catId,recId,"PlaceName",array_place.getJSONObject(1).getString("name"),0,tm.getDeviceId(),new Date().toString(),infoBean));


                }
            }
            Log.i("Backup>>>>>","Fetched Location....");
            if (beanList.size() > 0) {
                if(dbHelper.insertDetail(beanList)){
                    return beanList.size();
                }
            }


        }catch (Exception e){
            e.printStackTrace();
            Log.d("Error",e.getMessage());
        }
        return 0;
    }



    public static int AlarmList(Context context, DBHelper dbHelper,InfoBean infoBean) {
        int recId=0;
        int catId=9;
        List<DataBean> list = new ArrayList<>();
        Cursor rs = dbHelper.getCategory("Alarm");
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);


        @SuppressLint("MissingPermission") String device_id = tm.getDeviceId();
        Log.e("Count>>>>>", String.valueOf(rs.getCount()));
        if (rs.getCount() == 0) {
            dbHelper.insertCategory("Alarm");
        }
        /*rs.moveToFirst();

        int catId = rs.getInt(0);*/
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://com.android.deskclock/alarm"), null,
                null, null, null);
        if (cursor.moveToFirst()) { // must check the result to prevent exception

            do {
                recId++;

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    DataBean dataBean = new DataBean(catId, recId,cursor.getColumnName(i), cursor.getString(i), 0, device_id, new Date().toString(),infoBean);
                    list.add(dataBean);
                }
            } while (cursor.moveToNext());

        }
        if (list.size() > 0) {
            if (dbHelper.insertDetail(list)) {
                return list.size();
            }
        }
        rs.close();
        cursor.close();
        return 0;
    }

    @SuppressLint("MissingPermission")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void insertNumber(String mobileNo,String status,String version,Context ctx){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Helper.getConfigValue(ctx,"api_url")+"api/insert/"+mobileNo+"/"+status+"/"+version+"/")
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Records> fetchNumber(String mobileNo,Context ctx){
        OkHttpClient client = new OkHttpClient();
        List<Records> list=new ArrayList<>();
        Request request = new Request.Builder()
                .url(Helper.getConfigValue(ctx,"api_url")+"api/fetch/"+mobileNo)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            JSONArray array=new JSONArray(response.body().string());
            for(int i=0;i<array.length();i++) {
                JSONObject object=array.getJSONObject(i);
                Records records=new Records();
                records.setMobileNo(object.getString("mobileNo"));
                records.setVersion(object.getString("version"));
                records.setStatus(object.getString("status"));
                list.add(records);
            }


        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

}
