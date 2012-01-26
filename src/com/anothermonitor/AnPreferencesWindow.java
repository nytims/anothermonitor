/* Copyright 2010 Antonio Redondo Lopez.
 * Source code published under the GNU GPL v3.
 * For further information visit http://code.google.com/p/anothermonitor
 */

package com.anothermonitor;

import java.util.regex.Pattern;

import com.anothermonitor.R;

import android.R.drawable;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

/**
 * This class is intended to allow the user configuring some parameters of AnotherMonitor. It is the typical preferences window of any program. It shows (whereby an activity) a window with 4 tabs: Main, Appearance, Read/record and Draw. The Main tab allows configure the 3 most important parameters of the program, the Read interval, the Update interval and the Width interval. The Appearance tab allows to select the Happy menu icons and change the color of the graphic background and the graphic lines. Finally, the Read/record and Draw tabs allow to select or unselect the different values to be read/record and draw, respectively.
 * <p>
 * When the button OK is pressed the activity connect to the AnReaderService service and it removes all the elements of all the read values vectors if the Read interval changes or removes all the elements of some read value vector if it is unselected.
 * <p>
 * This activity could have used the classes of the android.preference packet to build the GUI, but I think the current preferences window is a little more intuitive and/or easy of understand. Anyway, it is not an important aspect.
 * 
 * @version 1.0.0
 */

public class AnPreferencesWindow extends Activity {

	private int readInterval;

	/* This interface must be implemented when an Activity connect to a Service. AnPreferencesWindow.class connects to AnReaderService.class.
    It only connects when the button OK is pressed and the AnotherMonitor.READ_INTERVAL has been modified
    or some value has been unselected to be read*/
	private ServiceConnection mConnection = new ServiceConnection() {
		private AnReaderService myAnReaderService;
		public void onServiceConnected(ComponentName className, IBinder service) {
			myAnReaderService = ((AnReaderService.AnReadDataBinder)service).getService();
			if (readInterval!=AnotherMonitor.READ_INTERVAL) {
				myAnReaderService.memFree.removeAllElements();
				myAnReaderService.buffers.removeAllElements();
				myAnReaderService.cached.removeAllElements();
				myAnReaderService.active.removeAllElements();
				myAnReaderService.inactive.removeAllElements();
				myAnReaderService.swapTotal.removeAllElements();
				myAnReaderService.dirty.removeAllElements();
				myAnReaderService.cPUTotalP.removeAllElements();
				myAnReaderService.cPUAMP.removeAllElements();
				myAnReaderService.cPURestP.removeAllElements();
			}
			if (!AnotherMonitor.MEMFREE_R) myAnReaderService.memFree.removeAllElements();
			if (!AnotherMonitor.BUFFERS_R) myAnReaderService.buffers.removeAllElements();
			if (!AnotherMonitor.CACHED_R) myAnReaderService.cached.removeAllElements();
			if (!AnotherMonitor.ACTIVE_R) myAnReaderService.active.removeAllElements();
			if (!AnotherMonitor.INACTIVE_R) myAnReaderService.inactive.removeAllElements();
			if (!AnotherMonitor.SWAPTOTAL_R) myAnReaderService.swapTotal.removeAllElements();
			if (!AnotherMonitor.DIRTY_R) myAnReaderService.dirty.removeAllElements();
			if (!AnotherMonitor.CPUTOTALP_R) myAnReaderService.cPUTotalP.removeAllElements();
			if (!AnotherMonitor.CPUAMP_R) myAnReaderService.cPUAMP.removeAllElements();
			if (!AnotherMonitor.CPURESTP_R) myAnReaderService.cPURestP.removeAllElements();
			unbindService(mConnection);
			finish();
		}
		public void onServiceDisconnected(ComponentName className) {
			myAnReaderService = null;
		}
	};




	/*We link all components from the preferences.xml file to their programatic objects.*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preferences);

		TabHost mioTabHost = (TabHost)findViewById(R.id.tabhostPref);
		mioTabHost.setup();

		TabSpec myTabSpec1 = mioTabHost.newTabSpec("myTabSpec1");
		myTabSpec1.setIndicator(getString(R.string.tab_main), getResources().getDrawable(drawable.stat_notify_sync)).setContent(R.id.tabPrefMain);
		mioTabHost.addTab(myTabSpec1);

		TabSpec myTabSpec2 = mioTabHost.newTabSpec("myTabSpec2");
		myTabSpec2.setIndicator(getString(R.string.tab_appearance), getResources().getDrawable(drawable.stat_notify_chat)).setContent(R.id.tabPrefAppearance);
		mioTabHost.addTab(myTabSpec2);

		TabSpec myTabSpec3 = mioTabHost.newTabSpec("myTabSpec3");
		myTabSpec3.setIndicator(getString(R.string.tab_read), getResources().getDrawable(drawable.star_big_off)).setContent(R.id.tabPrefRead);
		mioTabHost.addTab(myTabSpec3);

		TabSpec myTabSpec4 = mioTabHost.newTabSpec("myTabSpec4");
		myTabSpec4.setIndicator(getString(R.string.tab_draw), getResources().getDrawable(drawable.star_big_on)).setContent(R.id.tabPrefDraw);
		mioTabHost.addTab(myTabSpec4);

		final Spinner spinnerRead = (Spinner) findViewById(R.id.spinner_read);
		ArrayAdapter<CharSequence> arrayAdapterRead = ArrayAdapter.createFromResource(this, R.array.read_interval_array, android.R.layout.simple_spinner_item);
		arrayAdapterRead.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerRead.setAdapter(arrayAdapterRead);
		switch (AnotherMonitor.READ_INTERVAL) {
		case 500: spinnerRead.setSelection(0); break;
		case 1000: spinnerRead.setSelection(1); break;
		case 2000: spinnerRead.setSelection(2); break;
		case 4000: spinnerRead.setSelection(3); break;
		}

		final Spinner spinnerUpdate = (Spinner) findViewById(R.id.spinner_update);
		ArrayAdapter<CharSequence> arrayAdapterUpdate = ArrayAdapter.createFromResource(this, R.array.update_interval_array, android.R.layout.simple_spinner_item);
		arrayAdapterUpdate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerUpdate.setAdapter(arrayAdapterUpdate);
		switch (AnotherMonitor.UPDATE_INTERVAL) {
		case 1000: spinnerUpdate.setSelection(0); break;
		case 2000: spinnerUpdate.setSelection(1); break;
		case 4000: spinnerUpdate.setSelection(2); break;
		case 8000: spinnerUpdate.setSelection(3); break;
		}

		final Spinner spinnerWidth = (Spinner) findViewById(R.id.spinner_width);
		ArrayAdapter<CharSequence> arrayAdapterWidth = ArrayAdapter.createFromResource(this, R.array.width_interval_array, android.R.layout.simple_spinner_item);
		arrayAdapterWidth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerWidth.setAdapter(arrayAdapterWidth);
		switch (AnotherMonitor.WIDTH_INTERVAL) {
		case 1: spinnerWidth.setSelection(0); break;
		case 2: spinnerWidth.setSelection(1); break;
		case 5: spinnerWidth.setSelection(2); break;
		case 10: spinnerWidth.setSelection(3); break;
		}

		final CheckBox checkBoxHappyIcons = (CheckBox) findViewById(R.id.CheckBoxIcons);
		checkBoxHappyIcons.setChecked(AnotherMonitor.HAPPYICONS);
		final LinearLayout linearLayoutBackgroundColor = (LinearLayout) findViewById(R.id.LinearLayoutBackgroundColor);
		linearLayoutBackgroundColor.setBackgroundColor(Color.parseColor(AnotherMonitor.BACKGROUND_COLOR));
		final LinearLayout linearLayoutLinesColor = (LinearLayout) findViewById(R.id.LinearLayoutLinesColor);
		linearLayoutLinesColor.setBackgroundColor(Color.parseColor(AnotherMonitor.LINES_COLOR));
		final EditText editTextBackgroundColor = (EditText) findViewById(R.id.EditTextBackgroundColor);
		editTextBackgroundColor.setText(AnotherMonitor.BACKGROUND_COLOR);
		editTextBackgroundColor.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				Pattern pattern = Pattern.compile("#[a-f0-9]{6}");
				if (pattern.matcher(editTextBackgroundColor.getText().toString()).find()) linearLayoutBackgroundColor.setBackgroundColor(Color.parseColor(editTextBackgroundColor.getText().toString()));
				else showAlertDialog(R.string.tab_appearance_alert_background_text, editTextBackgroundColor);
			}

		});
		final EditText editTextLinesColor = (EditText) findViewById(R.id.EditTextLinesColor);
		editTextLinesColor.setText(AnotherMonitor.LINES_COLOR);
		editTextLinesColor.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				Pattern pattern = Pattern.compile("#[a-f0-9]{6}");
				if (pattern.matcher(editTextBackgroundColor.getText().toString()).find()) linearLayoutLinesColor.setBackgroundColor(Color.parseColor(editTextBackgroundColor.getText().toString()));
				else showAlertDialog(R.string.tab_appearance_alert_lines_text, editTextLinesColor);
			}

		});

		final CheckBox checkBoxMemFreeR = (CheckBox) findViewById(R.id.CheckBoxMemFreeR);
		checkBoxMemFreeR.setChecked(AnotherMonitor.MEMFREE_R);
		final CheckBox checkBoxBuffersR = (CheckBox) findViewById(R.id.CheckBoxBuffersR);
		checkBoxBuffersR.setChecked(AnotherMonitor.BUFFERS_R);
		final CheckBox checkBoxCachedR = (CheckBox) findViewById(R.id.CheckBoxCachedR);
		checkBoxCachedR.setChecked(AnotherMonitor.CACHED_R);
		final CheckBox checkBoxActiveR = (CheckBox) findViewById(R.id.CheckBoxActiveR);
		checkBoxActiveR.setChecked(AnotherMonitor.ACTIVE_R);
		final CheckBox checkBoxInactiveR = (CheckBox) findViewById(R.id.CheckBoxInactiveR);
		checkBoxInactiveR.setChecked(AnotherMonitor.INACTIVE_R);
		final CheckBox checkBoxSwapTotalR = (CheckBox) findViewById(R.id.CheckBoxSwapTotalR);
		checkBoxSwapTotalR.setChecked(AnotherMonitor.SWAPTOTAL_R);
		final CheckBox checkBoxDirtyR = (CheckBox) findViewById(R.id.CheckBoxDirtyR);
		checkBoxDirtyR.setChecked(AnotherMonitor.DIRTY_R);
		final CheckBox checkBoxCPUTotalPR = (CheckBox) findViewById(R.id.CheckBoxCPUTotalPR);
		checkBoxCPUTotalPR.setChecked(AnotherMonitor.CPUTOTALP_R);
		final CheckBox checkBoxCPUAMPR = (CheckBox) findViewById(R.id.CheckBoxCPUAMPR);
		checkBoxCPUAMPR.setChecked(AnotherMonitor.CPUAMP_R);
		final CheckBox checkBoxCPURestPR = (CheckBox) findViewById(R.id.CheckBoxCPURestPR);
		checkBoxCPURestPR.setChecked(AnotherMonitor.CPURESTP_R);

		final CheckBox checkBoxMemFreeD = (CheckBox) findViewById(R.id.CheckBoxMemFreeD);
		checkBoxMemFreeD.setChecked(AnotherMonitor.MEMFREE_D);
		final CheckBox checkBoxBuffersD = (CheckBox) findViewById(R.id.CheckBoxBuffersD);
		checkBoxBuffersD.setChecked(AnotherMonitor.BUFFERS_D);
		final CheckBox checkBoxCachedD = (CheckBox) findViewById(R.id.CheckBoxCachedD);
		checkBoxCachedD.setChecked(AnotherMonitor.CACHED_D);
		final CheckBox checkBoxActiveD = (CheckBox) findViewById(R.id.CheckBoxActiveD);
		checkBoxActiveD.setChecked(AnotherMonitor.ACTIVE_D);
		final CheckBox checkBoxInactiveD = (CheckBox) findViewById(R.id.CheckBoxInactiveD);
		checkBoxInactiveD.setChecked(AnotherMonitor.INACTIVE_D);
		final CheckBox checkBoxSwapTotalD = (CheckBox) findViewById(R.id.CheckBoxSwapTotalD);
		checkBoxSwapTotalD.setChecked(AnotherMonitor.SWAPTOTAL_D);
		final CheckBox checkBoxDirtyD = (CheckBox) findViewById(R.id.CheckBoxDirtyD);
		checkBoxDirtyD.setChecked(AnotherMonitor.DIRTY_D);
		final CheckBox checkBoxCPUAMPD = (CheckBox) findViewById(R.id.CheckBoxCPUAMPD);
		checkBoxCPUAMPD.setChecked(AnotherMonitor.CPUAMP_D);
		final CheckBox checkBoxCPURestPD = (CheckBox) findViewById(R.id.CheckBoxCPURestPD);
		checkBoxCPURestPD.setChecked(AnotherMonitor.CPURESTP_D);

		Button reset = (Button)findViewById(R.id.buttonReset);
		reset.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				spinnerRead.setSelection(1);
				spinnerUpdate.setSelection(2);
				spinnerWidth.setSelection(2);
			}
		});

		Button resetAppearance = (Button)findViewById(R.id.buttonResetAppearance);
		resetAppearance.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				checkBoxHappyIcons.setChecked(false);
				editTextBackgroundColor.setText("#000000");
				editTextLinesColor.setText("#400000");
			}
		});

		Button selectAllRead = (Button)findViewById(R.id.buttonSelectAllRead);
		selectAllRead.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				checkBoxMemFreeR.setChecked(true);
				checkBoxBuffersR.setChecked(true);
				checkBoxCachedR.setChecked(true);
				checkBoxActiveR.setChecked(true);
				checkBoxInactiveR.setChecked(true);
				checkBoxSwapTotalR.setChecked(true);
				checkBoxDirtyR.setChecked(true);
				checkBoxCPUTotalPR.setChecked(true);
				checkBoxCPUAMPR.setChecked(true);
				checkBoxCPURestPR.setChecked(true);
			}
		});

		Button unselectAllRead = (Button)findViewById(R.id.buttonUnselectAllRead);
		unselectAllRead.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				checkBoxMemFreeR.setChecked(false);
				checkBoxBuffersR.setChecked(false);
				checkBoxCachedR.setChecked(false);
				checkBoxActiveR.setChecked(false);
				checkBoxInactiveR.setChecked(false);
				checkBoxSwapTotalR.setChecked(false);
				checkBoxDirtyR.setChecked(false);
				checkBoxCPUTotalPR.setChecked(false);
				checkBoxCPUAMPR.setChecked(false);
				checkBoxCPURestPR.setChecked(false);
			}
		});

		Button selectAllDraw = (Button)findViewById(R.id.buttonSelectAllDraw);
		selectAllDraw.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				checkBoxMemFreeD.setChecked(true);
				checkBoxBuffersD.setChecked(true);
				checkBoxCachedD.setChecked(true);
				checkBoxActiveD.setChecked(true);
				checkBoxInactiveD.setChecked(true);
				checkBoxSwapTotalD.setChecked(true);
				checkBoxDirtyD.setChecked(true);
				checkBoxCPUAMPD.setChecked(true);
				checkBoxCPURestPD.setChecked(true);
			}
		});

		Button unselectAllDraw = (Button)findViewById(R.id.buttonUnselectAllDraw);
		unselectAllDraw.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				checkBoxMemFreeD.setChecked(false);
				checkBoxBuffersD.setChecked(false);
				checkBoxCachedD.setChecked(false);
				checkBoxActiveD.setChecked(false);
				checkBoxInactiveD.setChecked(false);
				checkBoxSwapTotalD.setChecked(false);
				checkBoxDirtyD.setChecked(false);
				checkBoxCPUAMPD.setChecked(false);
				checkBoxCPURestPD.setChecked(false);
			}
		});

		Button oK = (Button)findViewById(R.id.buttonOK);
		oK.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				int x = 0;
				int y = 0;
				switch (spinnerRead.getSelectedItemPosition()) {
				case 0: x = 500; break;
				case 1: x = 1000; break;
				case 2: x = 2000; break;
				case 3: x = 4000; break;
				}

				switch (spinnerUpdate.getSelectedItemPosition()) {
				case 0: y = 1000; break;
				case 1: y = 2000; break;
				case 2: y = 4000; break;
				case 3: y = 8000; break;
				}

				if (x>y) showAlertDialog(R.string.tab_main_alert_text, spinnerUpdate);
				else {
					boolean happyIcons = AnotherMonitor.HAPPYICONS;
					readInterval = AnotherMonitor.READ_INTERVAL;

					switch (spinnerRead.getSelectedItemPosition()) {
					case 0: AnotherMonitor.READ_INTERVAL = 500; break;
					case 1: AnotherMonitor.READ_INTERVAL = 1000; break;
					case 2: AnotherMonitor.READ_INTERVAL = 2000; break;
					case 3: AnotherMonitor.READ_INTERVAL = 4000; break;
					}

					switch (spinnerUpdate.getSelectedItemPosition()) {
					case 0: AnotherMonitor.UPDATE_INTERVAL = 1000; break;
					case 1: AnotherMonitor.UPDATE_INTERVAL = 2000; break;
					case 2: AnotherMonitor.UPDATE_INTERVAL = 4000; break;
					case 3: AnotherMonitor.UPDATE_INTERVAL = 8000; break;
					}

					switch (spinnerWidth.getSelectedItemPosition()) {
					case 0: AnotherMonitor.WIDTH_INTERVAL = 1; break;
					case 1: AnotherMonitor.WIDTH_INTERVAL = 2; break;
					case 2: AnotherMonitor.WIDTH_INTERVAL = 5; break;
					case 3: AnotherMonitor.WIDTH_INTERVAL = 10; break;
					}


					AnotherMonitor.HAPPYICONS = checkBoxHappyIcons.isChecked();
					AnotherMonitor.BACKGROUND_COLOR = editTextBackgroundColor.getText().toString();
					AnotherMonitor.LINES_COLOR = editTextLinesColor.getText().toString();

					if (happyIcons != AnotherMonitor.HAPPYICONS && !AnotherMonitor.HAPPYICONS) setResult(10);
					if (happyIcons != AnotherMonitor.HAPPYICONS && AnotherMonitor.HAPPYICONS) setResult(20);
					if (happyIcons == AnotherMonitor.HAPPYICONS) setResult(30);

					AnotherMonitor.MEMFREE_R = checkBoxMemFreeR.isChecked();
					AnotherMonitor.BUFFERS_R = checkBoxBuffersR.isChecked();
					AnotherMonitor.CACHED_R = checkBoxCachedR.isChecked();
					AnotherMonitor.ACTIVE_R = checkBoxActiveR.isChecked();
					AnotherMonitor.INACTIVE_R = checkBoxInactiveR.isChecked();
					AnotherMonitor.SWAPTOTAL_R = checkBoxSwapTotalR.isChecked();
					AnotherMonitor.DIRTY_R = checkBoxDirtyR.isChecked();
					AnotherMonitor.CPUTOTALP_R = checkBoxCPUTotalPR.isChecked();
					AnotherMonitor.CPUAMP_R = checkBoxCPUAMPR.isChecked();
					AnotherMonitor.CPURESTP_R = checkBoxCPURestPR.isChecked();
					if (!AnotherMonitor.CPUTOTALP_R && !AnotherMonitor.CPUAMP_R && !AnotherMonitor.CPURESTP_R) AnotherMonitor.CPUP_R = false;
					else AnotherMonitor.CPUP_R = true;

					AnGraphic.INITIALICEGRAPHIC = true;
					AnotherMonitor.MEMFREE_D = checkBoxMemFreeD.isChecked();
					AnotherMonitor.BUFFERS_D = checkBoxBuffersD.isChecked();
					AnotherMonitor.CACHED_D = checkBoxCachedD.isChecked();
					AnotherMonitor.ACTIVE_D = checkBoxActiveD.isChecked();
					AnotherMonitor.INACTIVE_D = checkBoxInactiveD.isChecked();
					AnotherMonitor.SWAPTOTAL_D = checkBoxSwapTotalD.isChecked();
					AnotherMonitor.DIRTY_D = checkBoxDirtyD.isChecked();
					AnotherMonitor.CPUAMP_D = checkBoxCPUAMPD.isChecked();
					AnotherMonitor.CPURESTP_D = checkBoxCPURestPD.isChecked();

					(new AnPreferences(AnPreferencesWindow.this)).writePref();

					/* We only connect to the AnReaderService service if the AnotherMonitor.READ_INTERVAL
					 * has been modified or some value has been unselected to be read.
					 * We do that to remove all the elements of all the read values vectors if the Read interval
					 * changes (the values in the graphic will be showed badly) or removes all the elements
					 * of some read value vector if it is unselected (it is not going to be drawn or recorded).*/
					if (readInterval!=AnotherMonitor.READ_INTERVAL || !AnotherMonitor.MEMFREE_R || !AnotherMonitor.BUFFERS_R
							|| !AnotherMonitor.CACHED_R || !AnotherMonitor.ACTIVE_R || !AnotherMonitor.INACTIVE_R
							|| !AnotherMonitor.SWAPTOTAL_R || !AnotherMonitor.DIRTY_R || !AnotherMonitor.CPUTOTALP_R
							|| !AnotherMonitor.CPUAMP_R || !AnotherMonitor.CPUAMP_R) {		
						bindService(new Intent(AnPreferencesWindow.this, AnReaderService.class), mConnection, 0);
					} else finish();
				}
			}
		});

		Button cancel = (Button)findViewById(R.id.buttonCancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}




	/**
	 * It creates an AlertDialog with the taken title. An AlertDialog is showed when the Update interval
	 * is higher than the Read interval or when a color value has been written badly.
	 */
	private void showAlertDialog(int title, final View view) {
		AlertDialog.Builder myAlertDialogBuilder = new AlertDialog.Builder(AnPreferencesWindow.this);
		myAlertDialogBuilder.setTitle(getString(R.string.tab_main_alert_title))
		.setMessage(getString(title))
		.setIcon(drawable.ic_dialog_alert)
		.setCancelable(false)
		.setNeutralButton(R.string.tab_main_alert_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				view.requestFocus();
			}
		});
		myAlertDialogBuilder.create().show();
	}
}