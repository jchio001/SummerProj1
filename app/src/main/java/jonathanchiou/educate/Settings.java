package jonathanchiou.educate;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

public class Settings extends ActionBarActivity {

    private static final String SETTING_CHECK_BOX1 = "WIFI_ONLY";
    private static CheckBoxPreference cb1;
    private static CheckBoxPreference cb2;
    private static CheckBoxPreference cb3;
    private static CheckBoxPreference cb4;
    boolean resetAlg1 = false;
    boolean resetAlg2 = false;
    boolean resetPrecalc = false;
    private static final String DOWNLOAD_TAG = "dl_Id";
    long dl_Id = 0;
    DownloadManager manager;
    //arrays for resetting all information on DL'd files
    private static final String[] ALG1_SETTINGS_ARRAY = {"Duped_Vars", "Duped_PEMDAS", "Duped_EWV", "Duped_BE", "Duped_SOQ"};
    //private static final String[] ALG2_SETTINGS_ARRAY = new String[0];
    //private static final String[] PRECALC_SETTINGS_ARRAY = new String[0];
    private static final String[] SUBJECTS = {"Reset download data on Alg1", "Reset download data on Alg2", "Reset download data on Precalc"};
    private ListView lv;

    //creating SettingsFragment class inside of the Settings Class because they both exist together
    //it also allows me an easier time to access elements within the fragment if I can access it
    //through the fragment rather than the setting class
    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settinglayout);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            cb1 = (CheckBoxPreference) findPreference("checkBoxA");
            cb1.setChecked(false);
            cb2 = (CheckBoxPreference) findPreference("checkBoxB");
            cb2.setChecked(false);
            cb3 = (CheckBoxPreference) findPreference("checkBoxC");
            cb3.setChecked(false);
            cb4 = (CheckBoxPreference) findPreference("checkBoxD");
            cb4.setChecked(sp.getBoolean(SETTING_CHECK_BOX1, false));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
        //PUT ALL RELEVANT CODE AFTER THE ABOVE 2 LINES OR ELSE EVERYTHING BREAKS!!!
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        dl_Id = sp.getLong(DOWNLOAD_TAG, 0);
        manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
    }

        public void onResume() {
        super.onResume();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        dl_Id = sp.getLong(DOWNLOAD_TAG, 0);

        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(myReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        if (cb1.isChecked()) {
            Toast.makeText(getApplicationContext(), "Resetting data on Algebra1....", Toast.LENGTH_LONG).show();
            do_resetting(ALG1_SETTINGS_ARRAY);
        }
        super.onDestroy();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putLong(DOWNLOAD_TAG, dl_Id);
        sp.edit().putBoolean(SETTING_CHECK_BOX1, cb4.isChecked()).apply();
        //unregisterReceiver(myReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    //ON PAUSE HERE!
    public void onPause() {
        super.onPause();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putBoolean(SETTING_CHECK_BOX1, cb4.isChecked()).apply();
        sp.edit().putLong(DOWNLOAD_TAG, dl_Id).apply();
        unregisterReceiver(myReceiver);
    }

    public void do_resetting(String[] SETTINGS_ARRAY) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        int i = 0;
        for (i = 0; i < SETTINGS_ARRAY.length; ++i) {
            sp.edit().putBoolean(SETTINGS_ARRAY[i], false).apply();
        }
    }

    private final BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MainActivity.download_Status(dl_Id, manager, getApplicationContext());
        }
    };

    @Override
    //handles selecting things on the action bar.
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
