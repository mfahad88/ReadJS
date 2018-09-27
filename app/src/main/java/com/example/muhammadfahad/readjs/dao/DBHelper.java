package com.example.muhammadfahad.readjs.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.muhammadfahad.readjs.bean.DataBean;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

  // public static final String DATABASE_NAME = "/sdcard/MyDBName.db";


   public DBHelper(Context context,String DATABASE_NAME) {
      super(context, DATABASE_NAME , null, 1);
      Log.i("DbHelper>>>>>>>>","Object created....");
   }

   @Override
   public void onCreate(SQLiteDatabase db) {
      // TODO Auto-generated method stub
      db.execSQL("create table Category (catId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name text)");
      db.execSQL(
              "create table Detail " +
                      "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, catId INTEGER ,recId integer,attribute text, value text,status integer,mobileIMEI text,recordDate text,mobileNo text,cnicNo text,channelId text,income text)"
      );
      Log.i("DbHelper>>>>>>>>","Database Created...");
   }

   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      // TODO Auto-generated method stub
      db.execSQL("DROP TABLE IF EXISTS Category");
      db.execSQL("DROP TABLE IF EXISTS Detail");
      onCreate(db);
   }

   public boolean insertDetail (List<DataBean> list) {
      SQLiteDatabase db = this.getWritableDatabase();
      db.beginTransaction();
      try{
         ContentValues contentValues = new ContentValues();
         Log.i("Details>>>>>>>>>>","Inserting data.....");
         for(DataBean dataBean:list){
            contentValues.put("catId", dataBean.getCatId());
            contentValues.put("recId", dataBean.getRecId());
            contentValues.put("attribute", dataBean.getAttribute());
            contentValues.put("value", dataBean.getValue());
            contentValues.put("status", dataBean.getStatus());
            contentValues.put("mobileIMEI", dataBean.getMobileIMEI());
            contentValues.put("recordDate", dataBean.getRecordDate());
            contentValues.put("mobileNo", dataBean.getInfoBean().getMobileNo());
            contentValues.put("cnicNo", dataBean.getInfoBean().getCnicNo());
            contentValues.put("channelId", dataBean.getInfoBean().getChannelId());
            contentValues.put("income",dataBean.getInfoBean().getIncome());
            db.insert("Detail", null, contentValues);

            //Log.d("Details>>",contentValues.toString());
         }
         contentValues.clear();
         db.setTransactionSuccessful();
      }catch (SQLException e){
         e.printStackTrace();
         return false;
      }
      finally {
         db.endTransaction();
         Log.i("Details>>>>>>>>>>","Data insertion complete.....");
      }
      return true;
   }

   /*public void insertMySql (List<DataBean> list) {
      try{
         int count=0;
         int batchSize=50000;
         Class.forName("com.mysql.jdbc.Driver");
         Connection con= DriverManager.getConnection(
                 "jdbc:mysql://53.53.53.33:3306/mobile_data","root","");

         Statement stmt=con.createStatement();
         String sql="INSERT INTO detail(catId, recId, attribute, value, status, mobileIMEI, recordDate, mobileNo, cnicNo, channelId, income) " +
                 "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
         PreparedStatement ps=con.prepareStatement(sql);
         Log.d("Query: ",sql);
         for(int i=0;i<list.size();i++){
            ps.setInt(1,list.get(i).getCatId());
            ps.setInt(2,list.get(i).getRecId());
            ps.setString(3,list.get(i).getAttribute());
            ps.setString(4,list.get(i).getValue());
            ps.setInt(5,list.get(i).getStatus());
            ps.setString(6,list.get(i).getMobileIMEI());
            ps.setString(7,list.get(i).getRecordDate());
            ps.setString(8,list.get(i).getInfoBean().getMobileNo());
            ps.setString(9,list.get(i).getInfoBean().getCnicNo());
            ps.setString(10,list.get(i).getInfoBean().getChannelId());
            ps.setString(11,list.get(i).getInfoBean().getIncome());
            ps.addBatch();
            count++;
            System.out.println(list.get(i).toString());
            if(count % batchSize==0) {
               ps.executeBatch();
            }
         }
         ps.executeBatch();
         ps.close();
         con.close();
      }catch(Exception e){
         Log.d("Sql",e.getMessage());
         e.printStackTrace();
      }
   }*/



   /*public boolean insertDetail (int catId, String attribute, String value,int status,String mobileIEMI,String recordDate) {
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues contentValues = new ContentValues();
      contentValues.put("catId", catId);
      contentValues.put("attribute", attribute);
      contentValues.put("value", value);
      contentValues.put("status", status);
      contentValues.put("mobileIMEI", mobileIEMI);
      contentValues.put("recordDate", recordDate);

      db.insert("Detail", null, contentValues);
       Log.e("Details>>",contentValues.toString());
      return true;
   }*/

   public boolean insertCategory ( String name) {
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues contentValues = new ContentValues();

      contentValues.put("name", name);
      //db.insertWithOnConflict("Category",null,contentValues,SQLiteDatabase.CONFLICT_REPLACE);
      db.insert("Category", null, contentValues);
      contentValues.clear();
       //Log.e("Category>>",contentValues.toString());
       db.close();
      return true;
   }
   
   public Cursor getCategory(String name) {
      SQLiteDatabase db = this.getReadableDatabase();
      Cursor res=db.query("Category",null/*new String[]{"catId"}*/,"name='"+name+"'",null, null, null, null);
      return res;
   }



}