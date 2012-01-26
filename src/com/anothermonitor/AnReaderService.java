/* Copyright 2010 Antonio Redondo Lopez.
 * Source code published under the GNU GPL v3.
 * For further information visit http://code.google.com/p/anothermonitor
 */

package com.anothermonitor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Vector;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Process;
import android.widget.Toast;

/**
 * The main scope of this class is to be run like a service and reading and (if selected) recording the memory and CPU values of the mobile Linux system. The first called method, onCreate(), creates the vector for every value and shows the Android status bar notifications.
 * <p>
 * The created vectors will be passed through AnotherMonitor class to an instance of AnGraphic to be drawn in the graphic. The view AnGraphic does not connect directly to the service because, as a view, it extends View, and then, it can not extends Activity to can connect with the service (see further in the section TODO).
 * <p>
 * The data structure selected to keep the values is a vector (implemented in Java by the class Vector) in place of another like queues because the class Vector is the one that allows to see, modify or remove any element of the structure without the need of that element be in the first position.
 * <p>
 * To read the values, from the onCreate() method is created and started a new thread where from which is called every time interval to the read() method. This method reads and saves values, and if the AnReaderService.RECORD flag is true, call to the record() method.
 * <p>
 * The memory values are read directly from the meminfo file of the Linux proc file system. In another hand, the CPU usage values do not appear in any place and they must be calculate from the values of the stat file and the other stat file of the AnotherMonitor process. See with more detail how to calculate the CPU usage values on http://stackoverflow.com/questions/1420426 and http://kernel.org/doc/Documentation/filesystems/proc.txt. 
 * <p>
 * The stopRecord() is called when we want to stop a record and the getOutputStreamWriter() and getDate() methods are exclusively used by record().@author Antonio Redondo
 * 
 * @version 1.0.0
 */

public class AnReaderService extends Service {

    static boolean RECORD;
    int memTotal, pID;
    Vector<String> memFree, buffers, cached, active, inactive, swapTotal, dirty;
    Vector<Float> cPUTotalP, cPUAMP, cPURestP;
    private String x;
    private String[] a;
    private long workT, totalT, workAMT;
    private long total, totalBefore, work, workBefore, workAM, workAMBefore;
    private boolean FIRSTTIMEREAD_FLAG = true;
    private boolean FIRSTTIMERECORD_FLAG = true;
    private NotificationManager myNM;
    private Notification myNotificationRead, myNotificationRecord;
    private BufferedReader readStream;
    private OutputStreamWriter recordStream;
    
    // The Runnable used to read every time interval the memory and CPU usage.
    private Runnable readRunnable = new Runnable() {
	public void run() {
	    read(); // We call here read() because to draw the graphic we need at less 2 read values.
	    while (readThread == Thread.currentThread()) {
		try {
		    read();
		    Thread.sleep(AnotherMonitor.READ_INTERVAL);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	}
    };
    
    // The Runnable that reads the values will be run from a separated thread. The performance is better.
    private Thread readThread = new Thread(readRunnable, "readThread");




    // A service must have a inner class which extends Binder and returns the service class.
    class AnReadDataBinder extends Binder {
	AnReaderService getService() {
	    return AnReaderService.this;
	}
    }




    @Override
    public void onCreate() {
	
	/* We create the vector of every value with the default number of elements.
	Afterwards this number will be automatically changed. */
	memFree = new Vector<String>(AnotherMonitor.TOTAL_INTERVALS);
	buffers = new Vector<String>(AnotherMonitor.TOTAL_INTERVALS);
	cached = new Vector<String>(AnotherMonitor.TOTAL_INTERVALS);
	active = new Vector<String>(AnotherMonitor.TOTAL_INTERVALS);
	inactive = new Vector<String>(AnotherMonitor.TOTAL_INTERVALS);
	swapTotal = new Vector<String>(AnotherMonitor.TOTAL_INTERVALS);
	dirty = new Vector<String>(AnotherMonitor.TOTAL_INTERVALS);
	cPUTotalP = new Vector<Float>(AnotherMonitor.TOTAL_INTERVALS);
	cPUAMP = new Vector<Float>(AnotherMonitor.TOTAL_INTERVALS);
	cPURestP = new Vector<Float>(AnotherMonitor.TOTAL_INTERVALS);

	// We create the notifications for the status bar.
	myNM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	myNotificationRead = new Notification(R.drawable.iconstatusbar1, getString(R.string.notify_read), System.currentTimeMillis());
	myNotificationRead.setLatestEventInfo(getApplicationContext(), "anotherMonitor", getString(R.string.notify_read2), PendingIntent.getActivity(this, 0, new Intent(this, AnotherMonitor.class), 0));
	myNotificationRecord = new Notification(R.drawable.iconstatusbar2, getString(R.string.notify_record), System.currentTimeMillis());
	myNotificationRecord.setLatestEventInfo(getApplicationContext(), "anotherMonitor", getString(R.string.notify_record2), PendingIntent.getActivity(this, 0, new Intent(this, AnotherMonitor.class), 0));
	myNM.notify(3, myNotificationRead);
	
	pID = Process.myPid();
	readThread.start(); // We start the threat which read the values.
    }




    @Override
    public IBinder onBind(Intent intent) {
	return new AnReadDataBinder();
    }




    @Override
    public void onDestroy() {
	if (RECORD) stopRecord();
	myNM.cancelAll();
	try {
	    readThread.interrupt();
	} catch (Exception e) {
	    e.printStackTrace();
	}
	readThread = null;
    }




    /**
     * It reads every time interval the values of the memory and CPU usage.
     */
    private void read() {
	try {
	    readStream = new BufferedReader(new FileReader("/proc/meminfo"));
	    x = readStream.readLine();
	    while (x!=null) {
		
		/* When the limit TOTAL_INTERVALS is surpassed by some vector we have to remove all
		 * the surpassed elements because, if not, the capacity of the vector will be increase x2. */
		while (memFree.size()>=AnotherMonitor.TOTAL_INTERVALS) memFree.remove(memFree.size()-1);
		while (buffers.size()>=AnotherMonitor.TOTAL_INTERVALS) buffers.remove(buffers.size()-1);
		while (cached.size()>=AnotherMonitor.TOTAL_INTERVALS) cached.remove(cached.size()-1);
		while (active.size()>=AnotherMonitor.TOTAL_INTERVALS) active.remove(active.size()-1);
		while (inactive.size()>=AnotherMonitor.TOTAL_INTERVALS) inactive.remove(inactive.size()-1);
		while (swapTotal.size()>=AnotherMonitor.TOTAL_INTERVALS) swapTotal.remove(swapTotal.size()-1);
		while (dirty.size()>=AnotherMonitor.TOTAL_INTERVALS) dirty.remove(dirty.size()-1);
		while (cPUTotalP.size()>=AnotherMonitor.TOTAL_INTERVALS) cPUTotalP.remove(dirty.size()-1);
		while (cPUAMP.size()>=AnotherMonitor.TOTAL_INTERVALS) cPUAMP.remove(dirty.size()-1);
		while (cPURestP.size()>=AnotherMonitor.TOTAL_INTERVALS) cPURestP.remove(dirty.size()-1);

		// We read the memory values. The percents are calculated in the AnotherMonitor class.
		if (FIRSTTIMEREAD_FLAG && x.startsWith("MemTotal:")) memTotal = Integer.parseInt(x.split("[ ]+", 3)[1]); FIRSTTIMEREAD_FLAG = false;
		if (AnotherMonitor.MEMFREE_R && x.startsWith("MemFree:")) memFree.add(0, x.split("[ ]+", 3)[1]);
		if (AnotherMonitor.BUFFERS_R && x.startsWith("Buffers:")) buffers.add(0, x.split("[ ]+", 3)[1]);
		if (AnotherMonitor.CACHED_R && x.startsWith("Cached:")) cached.add(0, x.split("[ ]+", 3)[1]);
		if (AnotherMonitor.ACTIVE_R && x.startsWith("Active:")) active.add(0, x.split("[ ]+", 3)[1]);
		if (AnotherMonitor.INACTIVE_R && x.startsWith("Inactive:")) inactive.add(0, x.split("[ ]+", 3)[1]);
		if (AnotherMonitor.SWAPTOTAL_R && x.startsWith("SwapTotal:")) swapTotal.add(0, x.split("[ ]+", 3)[1]);
		if (AnotherMonitor.DIRTY_R && x.startsWith("Dirty:")) dirty.add(0, x.split("[ ]+", 3)[1]);
		x = readStream.readLine();
	    }
	    
	    /* We read and calculate the CPU usage percents. It is possible that negative values or values higher than 100% appear.
	    Get more information about how it is done on http://stackoverflow.com/questions/1420426 
	    To see what is each number, see http://kernel.org/doc/Documentation/filesystems/proc.txt */
	    if (AnotherMonitor.CPUP_R) {
		readStream = new BufferedReader(new FileReader("/proc/stat"));
		a = readStream.readLine().split("[ ]+", 9);
		work = Long.parseLong(a[1]) + Long.parseLong(a[2]) + Long.parseLong(a[3]);
		total = work + Long.parseLong(a[4]) + Long.parseLong(a[5]) + Long.parseLong(a[6]) + Long.parseLong(a[7]);

		readStream = new BufferedReader(new FileReader("/proc/"+pID+"/stat"));
		a = readStream.readLine().split("[ ]+", 18);
		workAM = Long.parseLong(a[13]) + Long.parseLong(a[14]) + Long.parseLong(a[15]) + Long.parseLong(a[16]);
		
		if (totalBefore != 0) {
		    workT = work - workBefore;
		    totalT = total - totalBefore;
		    workAMT = workAM - workAMBefore;
		    if (AnotherMonitor.CPUTOTALP_R) cPUTotalP.add(0, workT*100/(float)totalT);
		    if (AnotherMonitor.CPUAMP_R) cPUAMP.add(0, workAMT*100/(float)totalT);
		    if (AnotherMonitor.CPURESTP_R) cPURestP.add(0, (workT - workAMT)*100/(float)totalT);
		}
		workBefore = work;
		totalBefore = total;
		workAMBefore = workAM;
	    }
	    readStream.close();
	    if (RECORD) record();

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }




    /**
     * It writes a CSV file saving the read values. If the application is suddenly finished
     * the data that is in the buffer and is not still has not been written will be lost
     * (to the max 10-20 lines).
     * 
     * The method is only called from the read() method if the RECORD flag is true.
     */
    private void record() {
	if (recordStream==null) recordStream = getOutputStreamWriter();
	try {
	    if (FIRSTTIMERECORD_FLAG) {
		recordStream.write(getString(R.string.app_name)+" Record,Date:,"+getDate()+",Read interval (ms):,"+AnotherMonitor.READ_INTERVAL
			+"\nMemTotal (kB),MemFree (kB),Buffers (kB),Cached (kB),Active (kB),Inactive (kB),SwapTotal (kB),Dirty (kB),CPUTotal (%),CPUanotherMonitor (%),CPURest (%)\n");
		myNM.cancelAll();
		myNM.notify(4, myNotificationRecord);
		FIRSTTIMERECORD_FLAG=false;
	    }
	    recordStream.write(memTotal+","+memFree.firstElement()+","+buffers.firstElement()
		    +","+cached.firstElement()+","+active.firstElement()
		    +","+inactive.firstElement()+","+swapTotal.firstElement()
		    +","+dirty.firstElement()+","+cPUTotalP.firstElement()
		    +","+cPUAMP.firstElement()+","+cPURestP.firstElement()+"\n");
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }




    /**
     * It flush and closes any open OutputStreamWriter, informs with a Toast to the user and
     * change the state of the status bar.
     */
    void stopRecord() {
	RECORD=false;
	try {
	    recordStream.flush();
	    recordStream.close();
	    recordStream = null;
	} catch (Exception e) {
	    e.printStackTrace();
	    Toast.makeText(this, getString(R.string.notify_toast_error)+e.getMessage(), Toast.LENGTH_SHORT).show();
	}
	FIRSTTIMERECORD_FLAG=true;
	Toast.makeText(this, getString(R.string.notify_toast_saved), Toast.LENGTH_SHORT).show();
	myNM.cancelAll();
	myNM.notify(4, myNotificationRead);
    }




    /**
     * It returns an OutputStreamWriter object with the adequate file name to be used by the record() method.
     * 
     * The method is only called from the record() method if the FIRSTTIMERECORD_FLAG flag is true.
     */
    private OutputStreamWriter getOutputStreamWriter() {
	StringBuilder fileName = new StringBuilder();
	fileName.append(getString(R.string.app_name)).append("Record").append(getDate()).append(".csv");
	try {
	    recordStream = new OutputStreamWriter(openFileOutput(fileName.toString(), MODE_WORLD_READABLE));
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return recordStream;
    }




    /**
     * It returns a String with the current date, for instance, 20100725163142. In a more legible mode
     * it means 2010-07-25 16:31:42.
     * 
     * The method is only called from the record() and getOutputStreamWriter() methods.
     */
    private String getDate() {
	Calendar myCalendar = Calendar.getInstance();
	StringBuilder date = new StringBuilder();
	DecimalFormat myFormat = new DecimalFormat("00");
	date.append(myFormat.format(myCalendar.get(Calendar.YEAR)))
	.append(myFormat.format(myCalendar.get(Calendar.MONTH)+1))
	.append(myFormat.format(myCalendar.get(Calendar.DATE)))
	.append(myFormat.format(myCalendar.get(Calendar.HOUR_OF_DAY)))
	.append(myFormat.format(myCalendar.get(Calendar.MINUTE)))
	.append(myFormat.format(myCalendar.get(Calendar.SECOND)));
	return date.toString();
    }
}