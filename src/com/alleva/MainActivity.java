package com.alleva;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.alleva.views.OptionsMenuActivity;

public class MainActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener
{

    private int sleepTimeMillis;
    private Runnable sleepTask;
    private Handler messageHandler;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        messageHandler = new Handler();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        sleepTimeMillis = Integer.parseInt(prefs.getString("sleepTimer","1")) * 60 * 1000;

        sleepTask = new Runnable() {
            public void run() {
                finish();
            }
        };

        messageHandler.postDelayed(sleepTask, sleepTimeMillis);

        setContentView(R.layout.main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.optionMenuButton){

            Intent intent = new Intent(this, OptionsMenuActivity.class);
            startActivity(intent);
            messageHandler.removeCallbacks(sleepTask);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        messageHandler.removeCallbacks(sleepTask);
        sleepTimeMillis = Integer.parseInt(sharedPreferences.getString("sleepTimer","1")) * 60 * 1000;

        messageHandler.postDelayed(sleepTask, sleepTimeMillis);
    }
}
