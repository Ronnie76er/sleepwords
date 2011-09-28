package com.alleva.views;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.alleva.R;

/**
 * User: ronnie
 * Date: 8/9/11
 */
public class OptionsMenuActivity extends PreferenceActivity{
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.preferences);
    }
}