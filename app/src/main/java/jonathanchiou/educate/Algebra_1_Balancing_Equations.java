package jonathanchiou.educate;

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
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class Algebra_1_Balancing_Equations extends ActionBarActivity {

    private static final String DUPED_BOOL = "Duped_BE";
    private static final String DOWNLOAD_TAG = "dl_Id";
    private static final int DupeDL = 10;
    boolean wifi_Only = false;
    boolean haveDLd = false;
    long dl_Id = 0;
    DownloadManager manager;

    private final BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MainActivity.download_Status(dl_Id, manager, getApplicationContext());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_algebra_1__balancing__equations);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        haveDLd = sp.getBoolean(DUPED_BOOL, false);
        wifi_Only = sp.getBoolean("WIFI_ONLY", false);
        dl_Id = sp.getLong(DOWNLOAD_TAG, 0);
        manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        haveDLd = MainActivity.onResume_helper(sp, DUPED_BOOL);
        wifi_Only = sp.getBoolean("WIFI_ONLY", false);
        dl_Id = sp.getLong(DOWNLOAD_TAG, 0);

        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(myReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putBoolean(DUPED_BOOL, haveDLd).apply();
        sp.edit().putLong(DOWNLOAD_TAG, dl_Id).apply();
        unregisterReceiver(myReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putBoolean(DUPED_BOOL, haveDLd).apply();
        sp.edit().putLong(DOWNLOAD_TAG, dl_Id).apply();
        //unregisterReceiver(myReceiver);
    }

    public void onClick_BE(View v) {
        if (haveDLd) {
            showDialog(DupeDL);
        }
        else {
            doDownloading();
        }
    }

    //displays a dialog for repeat downloads
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DupeDL:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to download this file again?");
                builder.setCancelable(true);
                //not working atm
                builder.setPositiveButton("Yes", new OkOnClickListener());
                builder.setNegativeButton("No", new CancelOnClickListener());
                AlertDialog dialog = builder.create();
                dialog.show();
        }
        return super.onCreateDialog(id);
    }

    private final class OkOnClickListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
            doDownloading();
        }
    }

    private final class CancelOnClickListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
            //MainActivity.this.finish(); THIS KILLS THE APP
            return;
        }
    }

    //works
    //Why it didn't work initally; No permissions set. Permissions are improtant.
    public void doDownloading() {
        String url = "https://github.com/jchio001/EducateFiles/raw/master/Algebra1_Balancing_Equations.pdf";
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //Create a string that contains a link to the file, then turn it into a request
        if (wifi_Only) {
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (!MainActivity.isWifiConnected(cm)) {
                Toast.makeText(getApplicationContext(), "Error: No connection to WiFi.", Toast.LENGTH_LONG).show();
                return;
            }
            else {
                haveDLd = true;
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
                haveDLd = true;
                Toast.makeText(getApplicationContext(), "Downloading file....", Toast.LENGTH_LONG).show();
            }
        }
        //Establish what do we allow the user to DL the file on
        request.setTitle("Balancing Equations");
        request.setDescription("EL TUCAN HA LLEGADO");
        //set description
        // in order for this if to run, you must use the android 3.2 to compile your app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Algebra1_Balancing_Equations.pdf");

        // get download service and enqueue file
        manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        dl_Id = manager.enqueue(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_algebra_1__balancing__equations, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(Algebra_1_Balancing_Equations.this, Settings.class));
                return true;
            case R.id.action_help:
                startActivity(new Intent(Algebra_1_Balancing_Equations.this, Help.class));
                return true;
            case R.id.action_resetDL:
                haveDLd = MainActivity.resetDL(getApplicationContext());
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
