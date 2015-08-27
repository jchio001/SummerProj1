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
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class havingtrouble extends AppCompatActivity {

    private static final String DUPED_BOOL = "Duped_Vars";
    private static final String DOWNLOAD_TAG = "dl_Id";
    private static final int DupeDL = 10;
    boolean wifi_Only = false;
    boolean haveDLd = false;
    long dl_Id = 0;
    DownloadManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_havingtrouble);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        haveDLd = sp.getBoolean(DUPED_BOOL, false);
        dl_Id = sp.getLong(DOWNLOAD_TAG, 0);
        wifi_Only = sp.getBoolean("WIFI_ONLY", false);

        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(myReceiver, intentFilter);
    }

    public void onDestroy() {
        super.onDestroy();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putBoolean(DUPED_BOOL, haveDLd).apply();
        sp.edit().putLong(DOWNLOAD_TAG, dl_Id).apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_havingtrouble, menu);
        return true;
    }

    public void onClick_Help(View v) {
        if (haveDLd)
            showDialog(DupeDL);
        else
            doDownloading();
    }

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
            Toast.makeText(getApplicationContext(), "Downloading file....", Toast.LENGTH_LONG).show();
            doDownloading();
        }
    }

    private final class CancelOnClickListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
            //MainActivity.this.finish(); THIS KILLS THE APP
            return;
        }
    }

    public void doDownloading() {
        String url = "https://github.com/jchio001/EducateFiles/raw/master/havingtrouble.pdf";
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //Create a string that contains a link to the file, then turn it into a request
        if (wifi_Only) {
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (!MainActivity.isWifiConnected(cm)) {
                Toast.makeText(getApplicationContext(), "Error: No connection to Wifi.", Toast.LENGTH_LONG).show();
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
        dl_Id = MainActivity.download_file(manager, request, "havingtrouble.");
    }

    private final BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MainActivity.download_Status(dl_Id, manager, getApplicationContext());
        }
    };

    public void onPause() {
        super.onPause();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putBoolean(DUPED_BOOL, haveDLd).apply();
        sp.edit().putLong(DOWNLOAD_TAG, dl_Id).apply();
        unregisterReceiver(myReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(havingtrouble.this, Settings.class));
                return true;
            case R.id.action_help:
                startActivity(new Intent(havingtrouble.this, Help.class));
                return true;
            case R.id.action_resetDL:
                Context context = getApplicationContext();
                haveDLd = MainActivity.resetDL(context);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
