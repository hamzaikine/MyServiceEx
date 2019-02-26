package io.github.hamzaikine.myserviceex;

import android.Manifest;
import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;


public class FileService extends IntentService {

    public static final String TRANSACTION_DONE = "io.github.hamzaikine.Transaction_Done";
    public static final String FILEPATH = "filepath";
    public static final String FILENAME = "filename";
    public static final String RESULT = "result";
    public static final String URL = "urlpath";
    private int result = Activity.RESULT_CANCELED;


    public FileService() {

        super("FileService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.e("FileService","Service_start");
            String passedURL = intent.getStringExtra(URL);
            String passedFileName = intent.getStringExtra(FILENAME);
            downloadFile(passedURL,passedFileName);
            Log.e("FileService","Service_done");

        }
    }

    protected void downloadFile(String theURL, String theFileName){

        File output = null;

        try{


            output = new File(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), theFileName);

            //connectionTimeout, readTimeout = 10 seconds
            FileUtils.copyURLToFile(new URL(theURL), output, 10000, 10000);

            // successfully finished
            result = Activity.RESULT_OK;

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }

        publishResults(output.getAbsolutePath(), result);
    }


    private void publishResults(String outputPath, int result) {
        Intent intent = new Intent(TRANSACTION_DONE);
        intent.putExtra(FILEPATH, outputPath);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }




}
