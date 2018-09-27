package com.example.muhammadfahad.readjs.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.example.muhammadfahad.readjs.R;
import com.example.muhammadfahad.readjs.bean.DataBean;




import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * Created by Fahad on 05/05/2018.
 */

public class Helper {
    private static final String TAG = "Helper";

    @SuppressLint("NewApi")
    public static String getConfigValue(Context context, String name) {
        Resources resources = context.getResources();

        try {
            InputStream rawResource = resources.openRawResource(R.raw.config);
            Properties properties = new Properties();
            properties.load(rawResource);
            return properties.getProperty(name);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Unable to find the config file: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "Failed to open config file.");
        }

        return null;
    }
    /*public static void writeStudentsListToExcel(List<DataBean> list,Workbook workbookFile,Sheet sheetFile,FileOutputStream fos,int index){

        // Using XSSF for xlsx format, for xls use HSSF

//        Workbook workbook = workbookFile;
//
//        Sheet sheet = sheetFile;

        int rowIndex = sheetFile.getLastRowNum();
        //for(DataBean dataBean : list){
        for(int i=0;i<list.size();i++){

            Row row = sheetFile.createRow(rowIndex++);
            int cellIndex = 0;
            Log.e("Row", String.valueOf(row.getRowNum()));
            Log.e("Sheet",list.get(i).toString());
            //row.createCell(cellIndex++).setCellValue(rowIndex++);
            //first place in row is name
            row.createCell(cellIndex++).setCellValue(list.get(i).getCatId());

            //second place in row is marks in maths
            row.createCell(cellIndex++).setCellValue(list.get(i).getRecId());

            //third place in row is marks in Science
            row.createCell(cellIndex++).setCellValue(list.get(i).getAttribute());

            //fourth place in row is marks in English
            row.createCell(cellIndex++).setCellValue(list.get(i).getValue());

            row.createCell(cellIndex++).setCellValue(list.get(i).getMobileIMEI());

            row.createCell(cellIndex++).setCellValue(list.get(i).getRecordDate());

            row.createCell(cellIndex++).setCellValue(list.get(i).getInfoBean().getMobileNo());

            row.createCell(cellIndex++).setCellValue(list.get(i).getInfoBean().getCnicNo());

            row.createCell(cellIndex++).setCellValue(list.get(i).getInfoBean().getChannelId());

            row.createCell(cellIndex++).setCellValue(list.get(i).getInfoBean().getIncome());

        }

        //write this workbook in excel file.
        try {

            workbookFile.write(fos);
            workbookFile.close();
            //fos.close();


            //System.out.println(FILE_PATH + " is successfully written");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }*/
/*    public static void setCOnfigValue(Context context, String name){
        Resources resources = context.getResources();

        try {
            InputStream rawResource = resources.openRawResource(R.raw.config);
            Properties properties = new Properties();
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Unable to find the config file: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "Failed to open config file.");
        }
    }*/
}
