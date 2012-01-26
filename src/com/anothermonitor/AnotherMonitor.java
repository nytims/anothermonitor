/* Copyright 2010 Antonio Redondo Lopez.
 * Source code published under the GNU GPL v3.
 * For further information visit http://code.google.com/p/anothermonitor
 */

package com.anothermonitor;

import java.text.DecimalFormat;
import java.util.Vector;

import com.anothermonitor.R.id;
import com.anothermonitor.R.drawable;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * It is the main activity and the first run code of all the program. In its onCreate() method it loads or creates the preferences, starts the service and loads the GUI from the main.xml file. The interface mConnection() is called as soon as the bind between the activity and the services is created. This implemented interface pass the vectors of each read value of AnReaderService class to AnGraphic class and starts to run the Runnable drawRunnable(). This Runnable is whom updates repetitively every time interval the text labels and the graphic.
 * <p>
 * Another less important features of AnotherMonitor class are the methods related with the management of the main menu: onCreateOptionsMenu(), onPrepareOptionsMenu(), and setMenuIcons().
 * <p>
 * The first one loads the menu from the menu.xml file and check if the flag myAnReaderService.RECORD is true. If it is true it means that this activity was relaunched  when the service was already created because the activity was in the backgroud or it was killed by lack of memory. In this case, the record icon is changed to the stop recording and save icon. The onPrepareOptionsMenu() and setMenuIcons() methods are used to check and change the appearance of the menu icons between the normal icons and the happy ones. Yeah, this option is not very useful... But it is cool!
 * 
 * @author Antonio Redondo
 * @version 1.0.0
 */

public class AnotherMonitor extends Activity {

    static int READ_INTERVAL, UPDATE_INTERVAL;
    static int WIDTH_INTERVAL=1; // This static field should not have valor because it will be loaded from AnPreferences, but it is set to 1 to be drawn when main.xml is seen from Eclipse.
    static int TOTAL_INTERVALS=440; // Default value to initialice the vector. Afterwards will be modified automatically.
    static String BACKGROUND_COLOR, LINES_COLOR;
    static boolean HAPPYICONS, MEMFREE_R, BUFFERS_R, CACHED_R, ACTIVE_R, INACTIVE_R, SWAPTOTAL_R, DIRTY_R, CPUP_R, CPUTOTALP_R, CPUAMP_R, CPURESTP_R, 
    DRAW, MEMFREE_D, BUFFERS_D, CACHED_D, ACTIVE_D, INACTIVE_D, SWAPTOTAL_D, DIRTY_D, CPUTOTALP_D, CPUAMP_D, CPURESTP_D;
    private TextView myTextViewMemTotal, myTextViewMemFree, myTextViewBuffers, myTextViewCached, myTextViewActive,
    myTextViewInactive, myTextViewSwapTotal, myTextViewDirty, myTextViewMemFreeP, myTextViewBuffersP,
    myTextViewCachedP, myTextViewActiveP, myTextViewInactiveP, myTextViewSwapTotalP, myTextViewDirtyP,
    myTextViewCPUTotalP, myTextViewCPUAMP, myTextViewCPURestP, myTextViewMemFreeS, myTextViewBuffersS,
    myTextViewCachedS, myTextViewActiveS, myTextViewInactiveS, myTextViewSwapTotalS, myTextViewDirtyS,
    myTextViewCPUAMPS, myTextViewCPURestPS;
    private Menu myMenu;
    private DecimalFormat myFormat = new DecimalFormat("##,###,##0");
    private DecimalFormat myFormatPercent = new DecimalFormat("##0.0");
    private AnGraphic myAnGraphic; // The customized view to show the graphic.
    private AnReaderService myAnReaderService;
    private Handler myHandler = new Handler();

    // The Runnable used to update every time interval the text labels and the graphic.
    private Runnable drawRunnable = new Runnable() {
	public void run() {
	    setTextLabel(AnotherMonitor.MEMFREE_R, AnotherMonitor.MEMFREE_D, myTextViewMemFree, myTextViewMemFreeP, myTextViewMemFreeS, myAnReaderService.memFree);
	    setTextLabel(AnotherMonitor.BUFFERS_R, AnotherMonitor.BUFFERS_D, myTextViewBuffers, myTextViewBuffersP, myTextViewBuffersS, myAnReaderService.buffers);
	    setTextLabel(AnotherMonitor.CACHED_R, AnotherMonitor.CACHED_D, myTextViewCached, myTextViewCachedP, myTextViewCachedS, myAnReaderService.cached);
	    setTextLabel(AnotherMonitor.ACTIVE_R, AnotherMonitor.ACTIVE_D, myTextViewActive, myTextViewActiveP, myTextViewActiveS, myAnReaderService.active);
	    setTextLabel(AnotherMonitor.INACTIVE_R, AnotherMonitor.INACTIVE_D, myTextViewInactive, myTextViewInactiveP, myTextViewInactiveS, myAnReaderService.inactive);
	    setTextLabel(AnotherMonitor.SWAPTOTAL_R, AnotherMonitor.SWAPTOTAL_D, myTextViewSwapTotal, myTextViewSwapTotalP, myTextViewSwapTotalS, myAnReaderService.swapTotal);
	    setTextLabel(AnotherMonitor.DIRTY_R, AnotherMonitor.DIRTY_D, myTextViewDirty, myTextViewDirtyP, myTextViewDirtyS, myAnReaderService.dirty);

	    if (AnotherMonitor.CPUTOTALP_R) {
		if (!myAnReaderService.cPUTotalP.isEmpty()) myTextViewCPUTotalP.setText(myFormatPercent.format((double)myAnReaderService.cPUTotalP.firstElement())+"%");
		else myTextViewCPUTotalP.setText(getString(R.string.main_reading));
	    }
	    else myTextViewCPUTotalP.setText(getString(R.string.main_not_reading));

	    setTextLabel(AnotherMonitor.CPUAMP_R, AnotherMonitor.CPUAMP_D, myTextViewCPUAMP, myTextViewCPUAMPS, myAnReaderService.cPUAMP);
	    setTextLabel(AnotherMonitor.CPURESTP_R, AnotherMonitor.CPURESTP_D, myTextViewCPURestP, myTextViewCPURestPS, myAnReaderService.cPURestP);

	    if (AnotherMonitor.DRAW) myAnGraphic.invalidate();
	    myHandler.postDelayed(this, UPDATE_INTERVAL);
	}
    };

    // This interface must be implemented when an Activity connect to a Service. AnotherMonitor.class connects to AnReaderService.class.
    private ServiceConnection mConnection = new ServiceConnection() {

	/* when the connection is established this method will be called. Then we will pass the vectors
	 * with each read value	from the service to AnGraphic and will start to update the text labels
	 * and the graphic. */
	public void onServiceConnected(ComponentName className, IBinder service) {
	    myAnReaderService = ((AnReaderService.AnReadDataBinder)service).getService();
	    myAnGraphic.setVectors(myAnReaderService.memTotal, myAnReaderService.memFree, myAnReaderService.buffers, myAnReaderService.cached,
		    myAnReaderService.active, myAnReaderService.inactive, myAnReaderService.swapTotal, myAnReaderService.dirty,
		    myAnReaderService.cPUAMP, myAnReaderService.cPURestP, AnotherMonitor.this);
	    myTextViewMemTotal.setText(myFormat.format(myAnReaderService.memTotal)+" kB");
	    myHandler.post(drawRunnable);
	}
	
	// Called because some reason we have disconnect from the service. We hope this method does not be call.
	public void onServiceDisconnected(ComponentName className) {
	    myAnReaderService = null;
	}
    };




    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	(new AnPreferences(this)).readPref(); // We load the default or saved values for the AnotherMonitor static fields.
	startService(new Intent(this, AnReaderService.class)); // We initialize the service, but does not still connect to it.
	setContentView(R.layout.main);
	
	// We link all components from the main.xml file to their programatic objects.
	myTextViewMemTotal = (TextView) findViewById(id.textViewMemTotal);
	myTextViewMemFree = (TextView) findViewById(id.textViewMemFree);
	myTextViewMemFreeP = (TextView) findViewById(id.textViewMemFreeP);
	myTextViewMemFreeS = (TextView) findViewById(id.textViewMemFreeS);
	myTextViewBuffers = (TextView) findViewById(id.textViewBuffers);
	myTextViewBuffersP = (TextView) findViewById(id.textViewBuffersP);
	myTextViewBuffersS = (TextView) findViewById(id.textViewBuffersS);
	myTextViewCached = (TextView) findViewById(id.textViewCached);
	myTextViewCachedP = (TextView) findViewById(id.textViewCachedP);
	myTextViewCachedS = (TextView) findViewById(id.textViewCachedS);
	myTextViewActive = (TextView) findViewById(id.textViewActive);
	myTextViewActiveP = (TextView) findViewById(id.textViewActiveP);
	myTextViewActiveS = (TextView) findViewById(id.textViewActiveS);
	myTextViewInactive = (TextView) findViewById(id.textViewInactive);
	myTextViewInactiveP = (TextView) findViewById(id.textViewInactiveP);
	myTextViewInactiveS = (TextView) findViewById(id.textViewInactiveS);
	myTextViewSwapTotal = (TextView) findViewById(id.textViewSwapTotal);
	myTextViewSwapTotalP = (TextView) findViewById(id.textViewSwapTotalP);
	myTextViewSwapTotalS = (TextView) findViewById(id.textViewSwapTotalS);
	myTextViewDirty = (TextView) findViewById(id.textViewDirty);
	myTextViewDirtyP = (TextView) findViewById(id.textViewDirtyP);
	myTextViewDirtyS = (TextView) findViewById(id.textViewDirtyS);
	myTextViewCPUTotalP = (TextView) findViewById(id.textViewCPUTotalP);
	myTextViewCPUAMP = (TextView) findViewById(id.textViewCPUAMP);
	myTextViewCPUAMPS = (TextView) findViewById(id.textViewCPUAMPS);
	myTextViewCPURestP = (TextView) findViewById(id.textViewCPURestP);
	myTextViewCPURestPS = (TextView) findViewById(id.textViewCPURestPS);
	myAnGraphic = (AnGraphic) findViewById(id.myAnGraphic);
	DRAW=true; // If this flag is false, the graphic does not be updated.
    }




    @Override
    public void onResume() {
	super.onResume();
	
	// We connect with the service.
	bindService(new Intent(this, AnReaderService.class), mConnection, 0);
    }




    @Override
    public void onStop() {
	super.onStop();
	unbindService(mConnection); // We disconnect from te service.
    }



    
    // We override this method exclusively to update the main menu icons.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	switch (resultCode) {
	case 10:
	    setMenuIcons(false);
	    myAnGraphic.invalidate();
	    break;
	case 20:
	    setMenuIcons(true);
	    myAnGraphic.invalidate();
	    break;
	case 30: myAnGraphic.invalidate(); break;
	}
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.menu, menu);
	myMenu = menu;
	
	// If the mobile has QWERTY keyboard we set alphabetic shortcuts to every menu option.
	if (getResources().getConfiguration().keyboard == Configuration.KEYBOARD_QWERTY) {
	    myMenu.findItem(id.menu_pause).setAlphabeticShortcut('r').setTitle(getString(R.string.menu_pause)+" (R)");
	    myMenu.findItem(id.menu_play).setAlphabeticShortcut('r').setTitle(getString(R.string.menu_play)+" (R)");
	    myMenu.findItem(id.menu_record).setAlphabeticShortcut('s').setTitle(getString(R.string.menu_record)+" (S)");
	    myMenu.findItem(id.menu_stop_record).setAlphabeticShortcut('s').setTitle(getString(R.string.menu_stop_record)+" (S)");
	    myMenu.findItem(id.menu_pref).setAlphabeticShortcut('p').setTitle(getString(R.string.menu_pref)+" (P)");
	    myMenu.findItem(id.menu_about).setAlphabeticShortcut('a').setTitle(getString(R.string.menu_about)+" (A)");
	    myMenu.findItem(id.menu_exit).setAlphabeticShortcut('e').setTitle(getString(R.string.menu_exit)+" (E)");
	}
	
	// If the mobile has numeric-12keys keyboard we set numeric shortcuts to every menu option.
	if (getResources().getConfiguration().keyboard == Configuration.KEYBOARD_12KEY) {
	    myMenu.findItem(id.menu_pause).setNumericShortcut('1').setTitle(getString(R.string.menu_pause)+" (1)");
	    myMenu.findItem(id.menu_play).setAlphabeticShortcut('1').setTitle(getString(R.string.menu_play)+" (1)");
	    myMenu.findItem(id.menu_record).setAlphabeticShortcut('5').setTitle(getString(R.string.menu_record)+" (5)");
	    myMenu.findItem(id.menu_stop_record).setAlphabeticShortcut('5').setTitle(getString(R.string.menu_stop_record)+" (5)");
	    myMenu.findItem(id.menu_pref).setAlphabeticShortcut('7').setTitle(getString(R.string.menu_pref)+" (7)");
	    myMenu.findItem(id.menu_about).setAlphabeticShortcut('8').setTitle(getString(R.string.menu_about)+" (8)");
	    myMenu.findItem(id.menu_exit).setAlphabeticShortcut('9').setTitle(getString(R.string.menu_exit)+" (9)");
	}
	
	// We check if a record is permorming.
	if (AnReaderService.RECORD) {
	    myMenu.findItem(id.menu_record).setVisible(false).setEnabled(false);
	    myMenu.findItem(id.menu_stop_record).setVisible(true).setEnabled(true);
	}
	if (HAPPYICONS) setMenuIcons(true);
	return super.onCreateOptionsMenu(menu);
    }




    /* We override this method to change the default Menu by our customized one.
    This is necessary to implement the Happy menu icons option. */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
	return super.onPrepareOptionsMenu(myMenu);
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	switch(item.getItemId()) {
	case id.menu_pause:
	    DRAW = false;
	    myAnGraphic.invalidate();
	    myMenu.findItem(id.menu_pause).setVisible(false).setEnabled(false);
	    myMenu.findItem(id.menu_play).setVisible(true).setEnabled(true);
	    break;
	case id.menu_play:
	    DRAW = true;
	    myAnGraphic.invalidate();
	    myMenu.findItem(id.menu_pause).setVisible(true).setEnabled(true);
	    myMenu.findItem(id.menu_play).setVisible(false).setEnabled(false);
	    break;
	case id.menu_record:
	    AnReaderService.RECORD = true;
	    myAnGraphic.invalidate();
	    myMenu.findItem(id.menu_record).setVisible(false).setEnabled(false);
	    myMenu.findItem(id.menu_stop_record).setVisible(true).setEnabled(true);
	    break;
	case id.menu_stop_record:
	    myAnReaderService.stopRecord();
	    myAnGraphic.invalidate();
	    myMenu.findItem(id.menu_record).setVisible(true).setEnabled(true);
	    myMenu.findItem(id.menu_stop_record).setVisible(false).setEnabled(false);
	    break;
	case id.menu_pref:
	    startActivityForResult(new Intent(this, AnPreferencesWindow.class), 1);
	    break;
	case id.menu_about:
	    startActivity(new Intent(this, AnAbout.class));
	    break;
	case id.menu_exit:
	    myHandler.removeCallbacks(drawRunnable);
	    myAnReaderService.stopSelf();
	    finish();
	    break;
	}
	return super.onOptionsItemSelected(item);
    }


    

    /**
     * It updates the different text labels of a memory value.
     * This method is only called from the drawRunnable Runnable object.
     */
    private void setTextLabel(boolean read, boolean draw, TextView textView, TextView textViewP, TextView textViewS, Vector<String> y) {
	if (read) {
	    if (!draw) textViewS.setText(getString(R.string.main_stopped));
	    else textViewS.setText(getString(R.string.main_drawing));
	    if (!y.isEmpty()) {
		textView.setText(myFormat.format(Integer.parseInt(y.firstElement()))+" kB");
		textViewP.setText(myFormatPercent.format(Integer.parseInt(y.firstElement())*100/(float)myAnReaderService.memTotal)+"%");
	    } else textViewP.setText(getString(R.string.main_reading));
	} else {
	    textView.setText("");
	    textViewP.setText(getString(R.string.main_not_reading));
	    textViewS.setText("");
	}		
    }




    /**
     * It updates the different text labels of a CPU usage.
     * 
     * This method is only called from the drawRunnable Runnable object.
     */
    private void setTextLabel(boolean read, boolean draw, TextView textViewP, TextView textViewS, Vector<Float> y) {
	if (read) {
	    if (!draw) textViewS.setText(getString(R.string.main_stopped));
	    else textViewS.setText(getString(R.string.main_drawing));
	    if (!y.isEmpty()) textViewP.setText(myFormatPercent.format((double)y.firstElement())+"%");
	    else textViewP.setText(getString(R.string.main_reading));
	} else {
	    textViewP.setText(getString(R.string.main_not_reading));
	    textViewS.setText("");
	}		
    }




    /**
     * It changes the appearance of the main menu icons betwein the normal ones and the Happy ones.
     */
    private void setMenuIcons(boolean happy) {
	if (happy) {
	    myMenu.findItem(id.menu_pause).setIcon(getResources().getDrawable(drawable.pause));
	    myMenu.findItem(id.menu_play).setIcon(getResources().getDrawable(drawable.play));
	    myMenu.findItem(id.menu_record).setIcon(getResources().getDrawable(drawable.record));
	    myMenu.findItem(id.menu_stop_record).setIcon(getResources().getDrawable(drawable.stoprecord));
	    myMenu.findItem(id.menu_pref).setIcon(getResources().getDrawable(drawable.pref));
	    myMenu.findItem(id.menu_about).setIcon(getResources().getDrawable(drawable.about));
	    myMenu.findItem(id.menu_exit).setIcon(getResources().getDrawable(drawable.exit));
	} else {
	    myMenu.findItem(id.menu_pause).setIcon(android.R.drawable.ic_media_pause);
	    myMenu.findItem(id.menu_play).setIcon(android.R.drawable.ic_media_play);
	    myMenu.findItem(id.menu_record).setIcon(android.R.drawable.ic_menu_edit);
	    myMenu.findItem(id.menu_stop_record).setIcon(android.R.drawable.ic_menu_save);
	    myMenu.findItem(id.menu_pref).setIcon(android.R.drawable.ic_menu_preferences);
	    myMenu.findItem(id.menu_about).setIcon(android.R.drawable.ic_menu_info_details);
	    myMenu.findItem(id.menu_exit).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
	}		
    }
}