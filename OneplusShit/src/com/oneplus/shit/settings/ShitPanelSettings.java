/*
 * Copyright (C) 2015 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oneplus.shit.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings;
import androidx.preference.PreferenceFragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.TwoStatePreference;
import androidx.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.util.Log;

import com.oneplus.shit.settings.utils.FileUtils;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

public class ShitPanelSettings extends PreferenceFragment
             implements Preference.OnPreferenceChangeListener {

    public static final String KEY_VIBSTRENGTH = "vib_strength";
    public static final String KEY_CALL_VIBSTRENGTH = "vib_call_strength";
    public static final String KEY_NOTIF_VIBSTRENGTH = "vib_notif_strength";

    /*public static final String KEY_TAPTOWAKE_SWITCH = "taptowake";*/

    public static final String KEY_SRGB_SWITCH = "srgb";
    public static final String KEY_HBM_SWITCH = "hbm";
    public static final String KEY_HBM_AUTOBRIGHTNESS_SWITCH = "hbm_autobrightness";
    public static final String KEY_DCI_SWITCH = "dci";
    public static final String KEY_DCDIM_SWITCH = "dcdim";
    public static final String KEY_NIGHT_SWITCH = "night";
    public static final String KEY_ONEPLUS_SWITCH = "oneplus";
    public static final String SPECTRUM_KEY = "spectrum";
    public static final String SPECTRUM_SYSTEM_PROPERTY = "persist.spectrum.profile";

    private SharedPreferences mPrefs;
    private VibratorStrengthPreference mVibratorStrength;
    private VibratorCallStrengthPreference mVibratorCallStrength;
    private VibratorNotifStrengthPreference mVibratorNotifStrength;
    private TwoStatePreference mHBMModeSwitch;
    private static TwoStatePreference mHBMAutobrightnessSwitch;
    private TwoStatePreference mDCDimSwitch;
    private ListPreference mSpectrum;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.shit_panel);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        mVibratorStrength = (VibratorStrengthPreference) findPreference(KEY_VIBSTRENGTH);
        if (mVibratorStrength != null) {
            mVibratorStrength.setEnabled(VibratorStrengthPreference.isSupported());
        }

        mVibratorCallStrength = (VibratorCallStrengthPreference) findPreference(KEY_CALL_VIBSTRENGTH);
        if (mVibratorCallStrength != null) {
            mVibratorCallStrength.setEnabled(VibratorCallStrengthPreference.isSupported());
        }

        mVibratorNotifStrength = (VibratorNotifStrengthPreference) findPreference(KEY_NOTIF_VIBSTRENGTH);
        if (mVibratorNotifStrength != null) {
            mVibratorNotifStrength.setEnabled(VibratorNotifStrengthPreference.isSupported());
        }

        /*mTapToWakeSwitch = (TwoStatePreference) findPreference(KEY_TAPTOWAKE_SWITCH);
        mTapToWakeSwitch.setOnPreferenceChangeListener(new TapToWakeSwitch());*/

        mHBMModeSwitch = (TwoStatePreference) findPreference(KEY_HBM_SWITCH);
        mHBMModeSwitch.setOnPreferenceChangeListener(new HBMModeSwitch());

        mHBMAutobrightnessSwitch = (TwoStatePreference) findPreference(KEY_HBM_AUTOBRIGHTNESS_SWITCH);
        mHBMAutobrightnessSwitch.setChecked(mPrefs.getBoolean(ShitPanelSettings.KEY_HBM_AUTOBRIGHTNESS_SWITCH, false));
        mHBMAutobrightnessSwitch.setOnPreferenceChangeListener(this);

        mDCDimSwitch = (TwoStatePreference) findPreference(KEY_DCDIM_SWITCH);
        mDCDimSwitch.setOnPreferenceChangeListener(new DCDimSwitch());

        mSpectrum = (ListPreference) findPreference(SPECTRUM_KEY);
        if( mSpectrum != null ) {
            mSpectrum.setValue(SystemProperties.get(SPECTRUM_SYSTEM_PROPERTY, "0"));
            mSpectrum.setOnPreferenceChangeListener(this);
        }
    }

     @Override
     public boolean onPreferenceChange(Preference preference, Object newValue) {
         mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
         final String key = preference.getKey();
         boolean value;
         String strvalue;
         if (SPECTRUM_KEY.equals(key)) {
            strvalue = (String) newValue;
            SystemProperties.set(SPECTRUM_SYSTEM_PROPERTY, strvalue);
            return true;
        } else if (preference == mHBMAutobrightnessSwitch) {
            Boolean enabled = (Boolean) newValue;
            SharedPreferences.Editor prefChange = mPrefs.edit();
            prefChange.putBoolean(KEY_HBM_AUTOBRIGHTNESS_SWITCH, enabled).commit();
            FileUtils.enableService(getContext());
            return true;
         }
        return false;
     }

    public static boolean isHBMAutobrightnessEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(ShitPanelSettings.KEY_HBM_AUTOBRIGHTNESS_SWITCH, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        // Respond to the action bar's Up/Home button
        case android.R.id.home:
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
