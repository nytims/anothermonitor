/* Copyright 2010 Antonio Redondo Lopez.
 * Source code published under the GNU GPL v3.
 * For further information visit http://code.google.com/p/anothermonitor
 */

package com.anothermonitor;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Show a window with information about the program and the author. There is no much to explain.
 * 
 * @version 1.0.0
 */

public class AnAbout extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	
	setContentView(R.layout.about);

	Button oK = (Button)findViewById(R.id.buttonOK);
	oK.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
			finish();
		}
	});
    }
}