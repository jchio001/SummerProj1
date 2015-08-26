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
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import jonathanchiou.educate.Help;
import jonathanchiou.educate.MainActivity;
import jonathanchiou.educate.R;
import jonathanchiou.educate.Settings;

public class Algebra_1_PEMDAS extends AppCompatActivity {

    private static final String DUPED_BOOL = "Duped_PEMDAS";
    private static final String DOWNLOAD_TAG = "dl_Id";
    boolean wifi_Only = false;
    boolean haveDLd = false;
    boolean afterCreate = false;
    private static final int DupeDL = 10;
    long dl_Id = 0;
    DownloadManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_algebra_1__pemdas);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        haveDLd = sp.getBoolean(DUPED_BOOL, false);
        wifi_Only = sp.getBoolean("WIFI_ONLY", false);
        dl_Id = sp.getLong(DOWNLOAD_TAG, 0);
        afterCreate = true;
        manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
    }

    @Override
    public void onResume() {
        if (!afterCreate) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            haveDLd = sp.getBoolean(DUPED_BOOL, false);
            wifi_Only = sp.getBoolean("WIFI_ONLY", false);
            dl_Id = sp.getLong(DOWNLOAD_TAG, 0);
        }
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(myReceiver, intentFilter);
        afterCreate = false;
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putBoolean(DUPED_BOOL, haveDLd).apply();
        sp.edit().putLong(DOWNLOAD_TAG, dl_Id).apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_algebra_1__pemda, menu);
        return true;
    }

    public void onClick_PEMDAS(View v) {
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
        String url = "https://github.com/jchio001/EducateFiles/raw/master/Algebra1_PEMDAS.pdf";
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //Create a string that contains a link to the file, then turn it into a request
        if (wifi_Only) {
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (!MainActivity.check_connection(cm, haveDLd, getApplicationContext(), "WiFi"))
                return;
            else
                haveDLd = true;
        }
        else {
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (!MainActivity.check_connection(cm, haveDLd, getApplicationContext(), "Not_WiFi"))
                return;
            else
                haveDLd = true;
        }
        //Establish what do we allow the user to DL the file on
        manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        dl_Id = MainActivity.download_file(manager, request, "Algebra1_PEMDAS");
    }

    private final BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MainActivity.download_Status(dl_Id, manager, getApplicationContext());
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putLong(DOWNLOAD_TAG, dl_Id).apply();
        sp.edit().putBoolean(DUPED_BOOL, haveDLd).apply();
        unregisterReceiver(myReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                sp.edit().putBoolean(DUPED_BOOL, haveDLd).apply();
                sp.edit().putLong(DOWNLOAD_TAG, dl_Id).apply();
                finish();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(Algebra_1_PEMDAS.this, Settings.class));
                return true;
            case R.id.action_help:
                startActivity(new Intent(Algebra_1_PEMDAS.this, Help.class));
                return true;
            case R.id.action_resetDL:
                haveDLd = MainActivity.resetDL(getApplicationContext());
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
