package io.github.hamzaikine.myserviceex;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    EditText displayText;


    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String string = bundle.getString(FileService.FILEPATH);
                int resultCode = bundle.getInt(FileService.RESULT);

                if (resultCode == RESULT_OK) {
                    Toast.makeText(MainActivity.this,
                            "Download complete. Download URI: " + string,
                            Toast.LENGTH_LONG).show();

                   File output = new File(string);
                   StringBuilder sb = new StringBuilder();
                   displayText.setText(string+"\n");


                   try {
                       List<String> text = FileUtils.readLines(output,"UTF-8");
                       for(int i=0; i < text.size(); i++) {
                           sb.append(text.get(i)).append("\n");
                       }
                       displayText.append(sb.toString());
                       Log.d("uri", output.toString());

                       // open file to view with an external application NEEDS FILEPROVIDER NOW AND ON
                       Uri path = FileProvider.getUriForFile(MainActivity.this,BuildConfig.APPLICATION_ID+".provider",output);
                       Intent viewDoc = new Intent();
                       viewDoc.setAction(Intent.ACTION_VIEW);
                       viewDoc.setDataAndType(path,"text/*");
                       viewDoc.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                       // Verify that the intent will resolve to an activity
                       if (viewDoc.resolveActivity(getPackageManager()) != null) {
                           startActivity(Intent.createChooser(viewDoc,"Open with"));
                       }

                   }catch (Exception e){
                       e.printStackTrace();
                   }

                } else {
                    Toast.makeText(MainActivity.this, "Download failed",
                            Toast.LENGTH_LONG).show();
                    displayText.setText("Download failed");
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayText = findViewById(R.id.displayText);
    }

    public void startFileService(View view) {


        // Create an intent to run the IntentService in the background
        Intent intent = new Intent(this, FileService.class);

        // Pass the URL that the IntentService will download from
        intent.putExtra(FileService.URL, "https://www.newthinktank.com/wordpress/lotr.txt");

        //pass the filename to save data
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = timeStamp+"~.txt";
        intent.putExtra(FileService.FILENAME,filename);
        // Start the intent service
        this.startService(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(receiver, new IntentFilter(FileService.TRANSACTION_DONE));
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }





}
