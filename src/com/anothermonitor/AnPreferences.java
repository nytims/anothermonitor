/* Copyright 2010 Antonio Redondo Lopez.
 * Source code published under the GNU GPL v3.
 * For further information visit http://code.google.com/p/anothermonitor
 */

package com.anothermonitor;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;

/**
 * This class has only 2 methods: readPref() and writePref(). ReadPref() reads (or creates the default values if is there is no saved values) the permanently saved values from the mobile for the static parameters of AnotherMonitor through the SharedPreferences Android class. writePref() modifies and save this values when is called from the AnPreferencesWindows class.
 * 
 * @version 1.0.0
 */

public class AnPreferences extends ContextWrapper {

    SharedPreferences mySharedPref;



    // This constructor must be implemented when Context is extended.
    public AnPreferences(Context x) {
	super(x);
	mySharedPref = getSharedPreferences(getString(R.string.app_name)+"Pref", MODE_WORLD_WRITEABLE);
    }




    /**
     * Read the values saved by the Preferences window of AnohterMonitor static fields.
     * If it is the first time AnotherMonitor is run and there is no saved values, it will be used
     * the default values.
     */
    void readPref() {
	AnotherMonitor.READ_INTERVAL = mySharedPref.getInt("READ_INTERVAL", 1000);
	AnotherMonitor.UPDATE_INTERVAL = mySharedPref.getInt("UPDATE_INTERVAL", 4000);
	AnotherMonitor.WIDTH_INTERVAL = mySharedPref.getInt("WIDTH_INTERVAL", 5);
	AnotherMonitor.HAPPYICONS = mySharedPref.getBoolean("HAPPYICONS", false);
	AnotherMonitor.BACKGROUND_COLOR = mySharedPref.getString("BACKGROUND_COLOR", "#000000");
	AnotherMonitor.LINES_COLOR = mySharedPref.getString("LINES_COLOR", "#400000");
	AnotherMonitor.MEMFREE_R = mySharedPref.getBoolean("MEMFREE_R", true);
	AnotherMonitor.BUFFERS_R = mySharedPref.getBoolean("BUFFERS_R", true);
	AnotherMonitor.CACHED_R = mySharedPref.getBoolean("CACHED_R", true);
	AnotherMonitor.ACTIVE_R = mySharedPref.getBoolean("ACTIVE_R", true);
	AnotherMonitor.INACTIVE_R = mySharedPref.getBoolean("INACTIVE_R", true);
	AnotherMonitor.SWAPTOTAL_R = mySharedPref.getBoolean("SWAPTOTAL_R", true);
	AnotherMonitor.DIRTY_R = mySharedPref.getBoolean("DIRTY_R", true);
	AnotherMonitor.CPUAMP_R = mySharedPref.getBoolean("CPUAMP_R", true);
	AnotherMonitor.CPURESTP_R = mySharedPref.getBoolean("CPURESTP_R", true);
	AnotherMonitor.CPUTOTALP_R = mySharedPref.getBoolean("CPUTOTALP_R", true);
	if (!AnotherMonitor.CPUTOTALP_R && !AnotherMonitor.CPUAMP_R && !AnotherMonitor.CPURESTP_R) AnotherMonitor.CPUP_R = false;
	else AnotherMonitor.CPUP_R=true;
	AnotherMonitor.MEMFREE_D = mySharedPref.getBoolean("MEMFREE_D", true);
	AnotherMonitor.BUFFERS_D = mySharedPref.getBoolean("BUFFERS_D", true);
	AnotherMonitor.CACHED_D = mySharedPref.getBoolean("CACHED_D", true);
	AnotherMonitor.ACTIVE_D = mySharedPref.getBoolean("ACTIVE_D", true);
	AnotherMonitor.INACTIVE_D = mySharedPref.getBoolean("INACTIVE_D", true);
	AnotherMonitor.SWAPTOTAL_D = mySharedPref.getBoolean("MEMFREE_R", true);
	AnotherMonitor.DIRTY_D = mySharedPref.getBoolean("DIRTY_D", true);
	AnotherMonitor.CPUTOTALP_D = mySharedPref.getBoolean("CPUTOTALP_D", true);
	AnotherMonitor.CPUAMP_D = mySharedPref.getBoolean("CPUAMP_D", true);
	AnotherMonitor.CPURESTP_D = mySharedPref.getBoolean("CPURESTP_D", true);
    }




    /**
     * Save on the SharedPreferences preferences system the values of the static fields,
     * that is it, it saves permanently on the mobile memory the AnotherMonitor preferences.
     */
    void writePref() {
	SharedPreferences.Editor mySharedPrefEditor = mySharedPref.edit();
	mySharedPrefEditor.putInt("READ_INTERVAL", AnotherMonitor.READ_INTERVAL);
	mySharedPrefEditor.putInt("UPDATE_INTERVAL", AnotherMonitor.UPDATE_INTERVAL);
	mySharedPrefEditor.putInt("WIDTH_INTERVAL", AnotherMonitor.WIDTH_INTERVAL);
	mySharedPrefEditor.putBoolean("HAPPYICONS", AnotherMonitor.HAPPYICONS);
	mySharedPrefEditor.putString("BACKGROUND_COLOR", AnotherMonitor.BACKGROUND_COLOR);
	mySharedPrefEditor.putString("LINES_COLOR", AnotherMonitor.LINES_COLOR);
	mySharedPrefEditor.putBoolean("MEMFREE_R", AnotherMonitor.MEMFREE_R);
	mySharedPrefEditor.putBoolean("BUFFERS_R", AnotherMonitor.BUFFERS_R);
	mySharedPrefEditor.putBoolean("CACHED_R", AnotherMonitor.CACHED_R);
	mySharedPrefEditor.putBoolean("ACTIVE_R", AnotherMonitor.ACTIVE_R);
	mySharedPrefEditor.putBoolean("INACTIVE_R", AnotherMonitor.INACTIVE_R);
	mySharedPrefEditor.putBoolean("SWAPTOTAL_R", AnotherMonitor.SWAPTOTAL_R);
	mySharedPrefEditor.putBoolean("DIRTY_R", AnotherMonitor.DIRTY_R);
	mySharedPrefEditor.putBoolean("CPUTOTALP_R", AnotherMonitor.CPUTOTALP_R);
	mySharedPrefEditor.putBoolean("CPUAMP_R", AnotherMonitor.CPUAMP_R);
	mySharedPrefEditor.putBoolean("CPURESTP_R", AnotherMonitor.CPURESTP_R);
	mySharedPrefEditor.putBoolean("MEMFREE_D", AnotherMonitor.MEMFREE_D);
	mySharedPrefEditor.putBoolean("BUFFERS_D", AnotherMonitor.BUFFERS_D);
	mySharedPrefEditor.putBoolean("CACHED_D", AnotherMonitor.CACHED_D);
	mySharedPrefEditor.putBoolean("ACTIVE_D", AnotherMonitor.ACTIVE_D);
	mySharedPrefEditor.putBoolean("INACTIVE_D", AnotherMonitor.INACTIVE_D);
	mySharedPrefEditor.putBoolean("SWAPTOTAL_D", AnotherMonitor.SWAPTOTAL_D);
	mySharedPrefEditor.putBoolean("DIRTY_D", AnotherMonitor.DIRTY_D);
	mySharedPrefEditor.putBoolean("CPUTOTALP_D", AnotherMonitor.CPUTOTALP_D);
	mySharedPrefEditor.putBoolean("CPUAMP_D", AnotherMonitor.CPUAMP_D);
	mySharedPrefEditor.putBoolean("CPURESTP_D", AnotherMonitor.CPURESTP_D);
	mySharedPrefEditor.commit();
    }
}
