package com.example.muhammadfahad.readjs.utils;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class Logger {
    public static void logcat()
    {

        try

        {
            Process process = Runtime.getRuntime().exec("logcat -d");
            InputStreamReader in=new InputStreamReader(process.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(in);
            StringBuilder log = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine()) != null)
            {
                log.append(line);
                log.append("\n");
            }

            //Convert log to string
            final String logString = new String(log.toString());

            //Create txt file in SD Card
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() +File.separator + "Log File");

            if(!dir.exists())
            {
                dir.mkdirs();
            }

            File file = new File(dir, "logcat.txt");


            //To write logcat in text file
            FileOutputStream fout = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fout);

            //Writing the string to file

            osw.write(logString);

            osw.flush();
            osw.close();
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void readLogs(){
        try {
            clearLog();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    try{
                        Process process = Runtime.getRuntime().exec("logcat -d");
                        BufferedReader bufferedReader = new BufferedReader(
                                new InputStreamReader(process.getInputStream()));

                        final StringBuilder log=new StringBuilder();
                        String line = "";
                        while ((line = bufferedReader.readLine()) != null) {
                            log.append(line+"\n");
                        }
                        sendUdp(log.toString());

                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            },0,5000);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void sendUdp(final String msg) throws IOException {
//        BufferedReader inFromUser =
//                new BufferedReader(new InputStreamReader(System.in));
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    DatagramSocket clientSocket = new DatagramSocket();

                    InetAddress IPAddress = InetAddress.getByName("53.53.53.15");
                    byte[] sendData = new byte[1024];
                    String sentence = msg;
                    sendData = sentence.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
                    clientSocket.send(sendPacket);
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }).start();
    }
    public static void clearLog(){
        try {
            Runtime.getRuntime().exec(new String[]{"logcat", "-c"});
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}