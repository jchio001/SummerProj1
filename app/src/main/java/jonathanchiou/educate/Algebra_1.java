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
import android.widget.Button;
import android.widget.Toast;


public class Algebra_1 extends ActionBarActivity {

    private static final int DL_All = 5;
    private static final String[] ALG1_NAME_ARRAY = {"Variables", "PEMDAS", "Equations_With_Variables"};
    private static final String DOWNLOAD_TAG = "dl_Id";
    boolean wifi_Only = false;
    long dl_Id = 0;
    DownloadManager manager;

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
        switch (id) {
            case DL_All:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to download all Algebra1 files?");
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
            //Toast.makeText(getApplicationContext(), "Downloading file....", Toast.LENGTH_LONG).show();
            download_All();
        }
    }

    private final class CancelOnClickListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
            //MainActivity.this.finish(); THIS KILLS THE APP
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

        if (buttonText.matches("Variables"))
            startActivity(new Intent(Algebra_1.this, Algebra_1_Variables.class));
        else if (buttonText.matches("PEMDAS"))
            startActivity(new Intent(Algebra_1.this, Algebra_1_PEMDAS.class));
        else if (buttonText.matches("Equations With Variables"))
            startActivity(new Intent(Algebra_1.this, Algebra_1_Equation_With_Variables.class));
        else if (buttonText.matches("Balancing Equations"))
            startActivity(new Intent(Algebra_1.this, Algebra_1_Balancing_Equations.class));
        else if (buttonText.matches("System of Equations"))
            startActivity(new Intent(Algebra_1.this, Algebra_1_System_Of_Equations.class));
    }

}
