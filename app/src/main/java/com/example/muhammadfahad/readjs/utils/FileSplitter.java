package com.example.muhammadfahad.readjs.utils;

import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Fahad on 13/03/2018.
 */

public class FileSplitter {

    ArrayList<String> mFilePathList=new ArrayList<>();



    public void split(String filename) throws FileNotFoundException, IOException
    {
        // final long floppySize = (long)(1.4 * 1024 * 1024);
        /** the maximum size of each file "chunk" generated, in bytes */
        long chunkSize = (long)(0.5 * 1024 * 1024);
        // open the file
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(filename));

        // get the file length
        File f = new File(filename);
        long fileSize = f.length();

        // loop for each full chunk
        int subfile;
        for (subfile = 0; subfile < fileSize / chunkSize; subfile++)
        {
            // open the output file
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filename + "." + subfile));

            // write the right amount of bytes
            for (int currentByte = 0; currentByte < chunkSize; currentByte++)
            {
                // load one byte from the input file and write it to the output file
                out.write(in.read());
            }

            // close the file
            out.close();
        }

        // loop for the last chunk (which may be smaller than the chunk size)
        if (fileSize != chunkSize * (subfile - 1))
        {
            // open the output file
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filename + "." + subfile));

            // write the rest of the file
            int b;
            while ((b = in.read()) != -1)
                out.write(b);

            // close the file
            out.close();
        }

        // close the file
        in.close();

    }

    public int getNumberParts(String baseFilename) throws IOException
    {
        // list all files in the same directory
        File directory = new File(baseFilename).getAbsoluteFile().getParentFile();
        final String justFilename = new File(baseFilename).getName();
        String[] matchingFiles = directory.list(new FilenameFilter()
        {
            public boolean accept(File dir, String name)
            {
                return name.startsWith(justFilename) && name.substring(justFilename.length()).matches("^\\.\\d+$");
            }
        });
        return matchingFiles.length;
    }

    public boolean zip(String zipFilePath,int BUFFER) {
        try {
            BufferedInputStream origin;
            FileOutputStream dest = new FileOutputStream(zipFilePath);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

            byte data[] = new byte[BUFFER];

            for (int i = 0; i < mFilePathList.size(); i++) {


                FileInputStream fi = new FileInputStream(mFilePathList.get(i));
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(mFilePathList.get(i).substring(mFilePathList.get(i).lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();

        } catch (Exception e) {
            e.printStackTrace();
            return false;

        }
        return true;
    }

    public String getMimeType(String path) {

        String extension = MimeTypeMap.getFileExtensionFromUrl(path.substring(0,path.lastIndexOf(".")));
        Log.e("extension>>",extension);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }
}
