package jonathanchiou.educate.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.app.DownloadManager;
import android.widget.Button;
import android.widget.Toast;
import jonathanchiou.educate.Algebra1_Lessons.Algebra_1;
import jonathanchiou.educate.Classes.LessonMapper;
import jonathanchiou.educate.R;

//I put all my shared functionality here
public class MainActivity extends AppCompatActivity {

    private static final String DOWNLOAD_TAG = "dl_Id";
    private static final String FIRST_TIME_TAG = "not_first_time";
    private static final int OPEN_BROWSER = 11;
    long dl_Id = 0;
    boolean not_first_time = false;
    String url;
    DownloadManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        dl_Id = sp.getLong(DOWNLOAD_TAG, 0);
        not_first_time = sp.getBoolean(FIRST_TIME_TAG, false);
        if (!not_first_time) {
            not_first_time = true;
            sp.edit().putBoolean(FIRST_TIME_TAG, not_first_time).apply();
            startActivity(new Intent(MainActivity.this, Help.class));
        }
        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Loading lessons list...");
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        LessonMapper.getInstance().setUpMap();
        progress.dismiss();
        manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        dl_Id = sp.getLong(DOWNLOAD_TAG, 0);
        not_first_time = sp.getBoolean(FIRST_TIME_TAG, false);

        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(myReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putLong(DOWNLOAD_TAG, dl_Id).apply();
        sp.edit().putBoolean(FIRST_TIME_TAG, not_first_time).apply();
        unregisterReceiver(myReceiver);
    }

    public void onDestroy() {
        super.onDestroy();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putLong(DOWNLOAD_TAG, dl_Id).apply();
        sp.edit().putBoolean(FIRST_TIME_TAG, not_first_time).apply();
    }

    private final BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            download_Status(dl_Id, manager, getApplicationContext());
        }
    };

    static public void download_Status(long dl_Id, DownloadManager manager, Context context) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(dl_Id);
        Cursor cursor = manager.query(query);
        if(cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
            int status = cursor.getInt(columnIndex);
            MainActivity.check_Status(status, context);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder;
        switch (id) {
            case OPEN_BROWSER:
                builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to leave the app and open the external link?");
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", new BrowserListener());
                builder.setNegativeButton("No", new CancelOnClickListener());
                AlertDialog dialogB = builder.create();
                dialogB.show();
                return super.onCreateDialog(id);
        }
        return super.onCreateDialog(id);
    }

    private final class BrowserListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
    }

    public final class CancelOnClickListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
            return;
        }
    }

    static public boolean resetDL(Context context) {
        Toast.makeText(context, "Resetting haveDLd to false.", Toast.LENGTH_LONG).show();
        return false;
    }

    static public boolean isConnected(ConnectivityManager cm) {
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }

    static public boolean isWifiConnected(ConnectivityManager cm) {
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, Settings.class));
                return true;
            case R.id.action_help:
                startActivity(new Intent(MainActivity.this, Help.class));
                return true;
            case R.id.search:
                onSearchRequested();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void to_havingtrouble(View v) {
        startActivity(new Intent(MainActivity.this, havingtrouble.class));
    }

    static public void check_Status(int status, Context context) {
        switch(status) {
            case DownloadManager.STATUS_FAILED:
                Toast.makeText(context, "Download failed.", Toast.LENGTH_LONG).show();
                return;
            case DownloadManager.STATUS_PAUSED:
                Toast.makeText(context, "Download paused", Toast.LENGTH_LONG).show();
                return;
            case DownloadManager.STATUS_SUCCESSFUL:
                Toast.makeText(context, "Download sucessful.", Toast.LENGTH_LONG).show();
                return;
            default:
                Toast.makeText(context, "Failed for unknown reasons.", Toast.LENGTH_LONG).show();
                return;
        }
    }

    static public boolean check_connection(ConnectivityManager cm, boolean haveDLd, Context context, String connectionType) {
        boolean isConnected;
        if (connectionType.equals("WiFi"))
            isConnected = isWifiConnected(cm);
        else
            isConnected = isConnected(cm);

        if (!isConnected) {
            if (connectionType.equals("WiFi"))
                Toast.makeText(context, "Error: No connection to WiFi.", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context, "Error: No connection to anything.", Toast.LENGTH_LONG).show();

            return false;
        }
        else
            Toast.makeText(context, "Downloading file....", Toast.LENGTH_LONG).show();

        return true;
    }


    public static long download_file(DownloadManager manager, DownloadManager.Request request, String file_name) {
        //Establish what do we allow the user to DL the file on
        request.setTitle(file_name);
        request.setDescription("EL TUCAN HA LLEGADO");
        //set description. In order for this if to run, you must use the android 3.2 to compile your app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, file_name + ".pdf");
        // get download service and enqueue file
        return manager.enqueue(request);
    }


    public void to_lessons(View v) {
        Button button = (Button) v;
        String buttonText = (String) button.getText();
        if (buttonText.matches("Algebra1"))
            startActivity(new Intent(MainActivity.this, Algebra_1.class));
        else
            Toast.makeText(getApplicationContext(), "Unavailable for now.", Toast.LENGTH_LONG).show();
    }

    public void main_onClick(View v) {
        int id = v.getId();
        if (id == R.id.mainKA)
            url = "https://www.khanacademy.org/math/algebra";
        else
            url = "http://www.wolframalpha.com/";
        showDialog(OPEN_BROWSER);
    }

}
