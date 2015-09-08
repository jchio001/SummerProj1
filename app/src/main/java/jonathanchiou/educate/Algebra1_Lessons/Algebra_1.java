package jonathanchiou.educate.Algebra1_Lessons;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import jonathanchiou.educate.Classes.LessonMapper;
import jonathanchiou.educate.Activities.Help;
import jonathanchiou.educate.Activities.MainActivity;
import jonathanchiou.educate.R;
import jonathanchiou.educate.Activities.Settings;

public class Algebra_1 extends AppCompatActivity {

    private static final int DL_All = 5;
    boolean wifi_Only = false;
    long dl_Id = 0;

    private static final String[] ALG1_NAME_ARRAY = {"Variables", "PEMDAS", "Equations_With_Variables", "Balancing_Equations",
    "Equations_with_Zero_or_Infinite_Answers", "Linear_Equations_and_Word_Problems", "System_of_Equations", "Inequalities"};
    private static final String DOWNLOAD_TAG = "dl_Id";

    DownloadManager manager;

    LessonMapper myLessons = LessonMapper.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_algebra_1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        wifi_Only = sp.getBoolean("WIFI_ONLY", false);
        dl_Id = sp.getLong(DOWNLOAD_TAG, 0);
        manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        wifi_Only = sp.getBoolean("WIFI_ONLY", false);
        dl_Id = sp.getLong(DOWNLOAD_TAG, 0);

        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(myReceiver, intentFilter);
    }

    public void onDestroy() {
        super.onDestroy();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putLong(DOWNLOAD_TAG, dl_Id);
        //unregisterReceiver(myReceiver);
    }

    private final BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MainActivity.download_Status(dl_Id, manager, getApplicationContext());
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_algebra_1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                sp.edit().putLong(DOWNLOAD_TAG, dl_Id);
                finish();
                return true;
            case R.id.search:
                onSearchRequested();
                return true;
            case R.id.action_settings:
                //Toast.makeText(getApplicationContext(), "I clicked this!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Algebra_1.this, Settings.class));
                return true;
            case R.id.action_help:
                startActivity(new Intent(Algebra_1.this, Help.class));
                return true;
            case R.id.action_downloadall:
                showDialog(DL_All);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder;
        switch (id) {
            case DL_All:
                builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to download all Algebra1 files?");
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", new OkOnClickListener());
                builder.setNegativeButton("No", new CancelOnClickListener());
                AlertDialog dialogA = builder.create();
                dialogA.show();
                return super.onCreateDialog(id);
        }
        return super.onCreateDialog(id);
    }


    private final class OkOnClickListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
            download_All();
        }
    }

    public final class CancelOnClickListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
            return;
        }
    }

    public void download_All() {
        unregisterReceiver(myReceiver);
        String base_url = "https://github.com/jchio001/EducateFiles/raw/master/Algebra1_";
        String cur_url;
        int i;
        DownloadManager.Request request;
        for (i = 0; i < ALG1_NAME_ARRAY.length; ++i) {
            cur_url = base_url + ALG1_NAME_ARRAY[i] + ".pdf";
            request = new DownloadManager.Request(Uri.parse(cur_url));
            if (wifi_Only) {
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (!MainActivity.isWifiConnected(cm)) {
                    Toast.makeText(getApplicationContext(), "Error: No connection to Wifi.", Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    Toast.makeText(getApplicationContext(), "Downloading file....", Toast.LENGTH_LONG).show();
                }
            }
            else {
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (!MainActivity.isConnected(cm)) {
                    Toast.makeText(getApplicationContext(), "Error: No connection to anything.", Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    Toast.makeText(getApplicationContext(), "Downloading file....", Toast.LENGTH_LONG).show();
                }
            }
            //Create a string that contains a link to the file, then turn it into a request
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            //Establish what do we allow the user to DL the file on
            request.setTitle(ALG1_NAME_ARRAY[i]);
            request.setDescription("EL TUCAN HA LLEGADO");
            //set description
            // in order for this if to run, you must use the android 3.2 to compile your app
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            }
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Algebra1_" + ALG1_NAME_ARRAY[i] + ".pdf");

            // get download service and enqueue file
            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
        }
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(myReceiver, intentFilter);
        Toast.makeText(getApplicationContext(), "Done downloading all Algebra1 lessons.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putLong(DOWNLOAD_TAG, dl_Id).apply();
        unregisterReceiver(myReceiver);
    }

    //doing all the transitions to lessons in 1 function!
    public void to_Lessons(View v) {
        Button button = (Button) v;
        String buttonText = (String) button.getText();
        Class lesson = myLessons.getLesson(buttonText);
        if (lesson != null)
            startActivity(new Intent(Algebra_1.this, lesson));
    }

}
