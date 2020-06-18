package com.example.b10709008hw2;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

public class SettingFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_visualizer);
        SharedPreferences sharedPreferences= getPreferenceScreen().getSharedPreferences();
        PreferenceScreen preferenceScreen=getPreferenceScreen();
        int count=preferenceScreen.getPreferenceCount();
        for(int i=0;i<count;i++){
            Preference p= preferenceScreen.getPreference(i);
            if(p instanceof ListPreference){
                String value=sharedPreferences.getString(p.getKey(),"");
                setPreferenceSumary(p,value);
            }
        }
    }

    private void setPreferenceSumary(Preference p, String value) {
        if(p instanceof ListPreference){
            ListPreference listPreference= (ListPreference) p;
            int prefIndex= listPreference.findIndexOfValue(value);
            if(prefIndex>=0){
                listPreference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Preference preference= findPreference(s);
        if(null!=preference){
            if(preference instanceof ListPreference){
                String value=sharedPreferences.getString(preference.getKey(),"");
                setPreferenceSumary(preference,value);
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}