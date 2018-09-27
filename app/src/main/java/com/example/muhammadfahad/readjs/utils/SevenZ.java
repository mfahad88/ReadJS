package com.example.muhammadfahad.readjs.utils;

import android.os.Environment;
import android.util.Log;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;



public class SevenZ {

    public static void Compression(ArrayList<File> filesToArchive) {
        SevenZOutputFile sevenZOutput = null;
        String sevenZarchiveFileName = null;
        for (int i = 0; i < filesToArchive.size(); i++) {
            try {
                sevenZarchiveFileName = filesToArchive.get(i).getName()+".7z";
                File sevenZFile=new File(Environment.getExternalStorageDirectory()+File.separator+sevenZarchiveFileName);
                sevenZOutput = new SevenZOutputFile(sevenZFile);
                System.out.println("Attempting to create " + sevenZarchiveFileName
                        + ".......");
                SevenZArchiveEntry entry = sevenZOutput.createArchiveEntry(
                        filesToArchive.get(i), filesToArchive.get(i).getName());
                sevenZOutput.putArchiveEntry(entry);
                FileInputStream in = new FileInputStream(filesToArchive.get(i));
                byte[] b = new byte[1024];
                int count = 0;
                while ((count = in.read(b)) > 0) {
                    sevenZOutput.write(b, 0, count);
                }
                sevenZOutput.closeArchiveEntry();
                in.close();
                sevenZOutput.close();
                System.out.println("Archive " + sevenZarchiveFileName
                        + " created successfully.......");

            } catch (IOException e) {
                Log.e("Error",e.getMessage());
                System.out.println(e.getMessage());
                //System.exit(0);
            }

        }
    }

    public static void decompress(String... file) throws IOException {
        for(int i=0;i<file.length;i++) {
            SevenZFile sevenZFile = new SevenZFile(new File(file[i]));
            SevenZArchiveEntry entry = sevenZFile.getNextEntry();
            while(entry!=null){
                System.out.println("Attempting to extract "+entry.getName()+"...");
                FileOutputStream out = new FileOutputStream(entry.getName());
                byte[] content = new byte[(int) entry.getSize()];
                sevenZFile.read(content, 0, content.length);
                out.write(content);
                out.close();
                entry = sevenZFile.getNextEntry();

            }
            sevenZFile.close();
            System.out.println("Created successfully.......");
        }
    }
}
