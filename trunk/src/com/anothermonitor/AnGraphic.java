/* Copyright 2010 Antonio Redondo Lopez.
 * Source code published under the GNU GPL v3.
 * For further information visit http://code.google.com/p/anothermonitor
 */

package com.anothermonitor;

import java.util.Vector;

import android.util.AttributeSet;
import android.view.View;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * The AnGraphic class takes care of drawing and updating every time interval the graphic. Implements View and then, on the onDraw() method that every class what implements View has to have, it draws line to line the background, their grid and edge lines and each one of the memory and CPU usage values lines through the methods drawRect() and drawLine().
 * <p>
 * This class is a big resources consumer because the reiterative calling to the drawRect() and drawLine() methods every time interval. Thus, the onDraw() method implements the less needed number of operations to show the graphic, but unlucky, they are much. * @author Antonio Redondo
 * <p>
 * The initializeGraphic() is called exclusively by onDraw() every time we have to initialize the parameters of the graphic, it is to say, when it is the first time we draw the graphic or when the size or the colors of the graphic changes. In this case, the flag AnGrapic.INITIALICEGRAPHIC will be true and the initializeGraphic() will be called.
 *<p>
 * With setVectors() we set the vectors up to show. And the getPaint() method is exclusively used by initializeGraphic() to creates a new attributes Paint object.
 * 
 * @version 1.0.0
 */

public class AnGraphic extends View {

	static boolean INITIALICEGRAPHIC;
	private boolean LITTLELAND, LITTLEPORT;
	private int Y_BOTTOM_SPACE=16;
	private int X_LEFT_SPACE=3;
	private int memTotal, xLeft, xRight, yTop, yBottom, graphicHeight, graphicWidth, minutes, seconds;
	private String updateIntervalText = "Update interval"; // These strings will be afterwards updated with the right language.
	private String graphicPausedText = "Graphic paused";
	private String recordingText = "Recording";
	private Paint graphicBackgroudPaint, viewBackgroudPaint, circlePaint, textPaint, textPaint2, textPaint3, textPaint4, textRecordingPaint, linesPaint, cPUAMPPaint, cPURestPPaint, memFreePaint, buffersPaint, cachedPaint, activePaint, inactivePaint, swapTotalPaint, dirtyPaint;
	private Vector<String> memFree, buffers, cached, active, inactive, swapTotal, dirty;
	private Vector<Float> cPUAMP, cPURestP;




	// This constructor must be specified when the view is loaded from a xml file, like in this case.
	public AnGraphic(Context context, AttributeSet attrs) {
		super(context, attrs);
		INITIALICEGRAPHIC = true;
	}




	@Override
	protected void onDraw(Canvas canvas) {

		// This method is called the first time the graphic is drawn. Like this we does not uselessly recalculate variables.
		if (INITIALICEGRAPHIC) initializeGraphic();

		// Graphic background
		canvas.drawRect(new Rect(xLeft, yTop, xRight, yBottom), graphicBackgroudPaint);

		// Horizontal graphic grid lines
		canvas.drawLine(xLeft, yTop+graphicHeight/4, xRight, yTop+graphicHeight/4, linesPaint);
		canvas.drawLine(xLeft, yTop+graphicHeight/2, xRight, yTop+graphicHeight/2, linesPaint);
		canvas.drawLine(xLeft, yTop+graphicHeight/4*3, xRight, yTop+graphicHeight/4*3, linesPaint);

		// Vertical graphic grid lines
		for (int n=1;n<=minutes;n++) canvas.drawLine(xRight-n*AnotherMonitor.WIDTH_INTERVAL*(int)(60/((float)AnotherMonitor.READ_INTERVAL/1000)), yTop, xRight-n*AnotherMonitor.WIDTH_INTERVAL*(int)(60/((float)AnotherMonitor.READ_INTERVAL/1000)), yBottom, linesPaint);

		// Value lines
		if (cPUAMP != null && AnotherMonitor.CPUAMP_R && AnotherMonitor.CPUAMP_D) drawLineFloat(cPUAMP, canvas, cPUAMPPaint);
		if (cPURestP != null && AnotherMonitor.CPURESTP_R && AnotherMonitor.CPURESTP_D) drawLineFloat(cPURestP, canvas, cPURestPPaint);
		if (memFree != null && AnotherMonitor.MEMFREE_R && AnotherMonitor.MEMFREE_D) drawLine(memFree, canvas, memFreePaint);
		if (buffers != null && AnotherMonitor.BUFFERS_R && AnotherMonitor.BUFFERS_D) drawLine(buffers, canvas, buffersPaint);
		if (cached != null && AnotherMonitor.CACHED_R && AnotherMonitor.CACHED_D) drawLine(cached, canvas, cachedPaint);
		if (active != null && AnotherMonitor.ACTIVE_R && AnotherMonitor.ACTIVE_D) drawLine(active, canvas, activePaint);
		if (inactive != null && AnotherMonitor.INACTIVE_R && AnotherMonitor.INACTIVE_D) drawLine(inactive, canvas, inactivePaint);
		if (swapTotal != null && AnotherMonitor.SWAPTOTAL_R && AnotherMonitor.SWAPTOTAL_D) drawLine(swapTotal, canvas, swapTotalPaint);
		if (dirty != null && AnotherMonitor.DIRTY_R && AnotherMonitor.DIRTY_D) drawLine(dirty, canvas, dirtyPaint);

		// Update interval, GRAPHIC PAUSED and Recording indicators
		canvas.drawText(updateIntervalText+": "+AnotherMonitor.UPDATE_INTERVAL/1000+" s", xRight-5, yTop+15, textPaint);
		if (!AnotherMonitor.DRAW) canvas.drawText(graphicPausedText, xRight-5, yTop+30, textPaint);
		if (AnReaderService.RECORD) {
			if (LITTLELAND) {
				canvas.drawText(recordingText, xLeft+18, yBottom-5, textRecordingPaint);
				canvas.drawCircle(xLeft+10, yBottom-10, 5, circlePaint);
			}
			else {
				canvas.drawText(recordingText, xLeft+18, yTop+15, textRecordingPaint);
				canvas.drawCircle(xLeft+10, yTop+10, 5, circlePaint);
			}
		}

		// Graphic background
		canvas.drawRect(new Rect(0, yTop, xLeft, getHeight()), viewBackgroudPaint);
		canvas.drawRect(new Rect(0, yBottom, xRight, getHeight()), viewBackgroudPaint);

		// Vertical edges
		canvas.drawLine(xLeft, yTop, xLeft, yBottom, linesPaint);
		canvas.drawLine(xRight, yBottom, xRight, yTop, linesPaint);

		// Horizontal edges
		canvas.drawLine(xLeft, yTop, xRight, yTop, linesPaint);
		canvas.drawLine(xLeft, yBottom, xRight, yBottom, linesPaint);

		// Vertical legend
		if(LITTLELAND) {
			canvas.drawText("100", xLeft-X_LEFT_SPACE, yTop+5, textPaint3);
			canvas.drawText("75", xLeft-X_LEFT_SPACE, yTop+graphicHeight/4+5, textPaint3);
			canvas.drawText("50", xLeft-X_LEFT_SPACE, yTop+graphicHeight/2+5, textPaint3);
			canvas.drawText("25", xLeft-X_LEFT_SPACE, yTop+graphicHeight/4*3+5, textPaint3);
			canvas.drawText("0%", xLeft-X_LEFT_SPACE, yBottom, textPaint3);

		} else {
			canvas.drawText("100%", xLeft-X_LEFT_SPACE, yTop+10, textPaint);
			canvas.drawText("75%", xLeft-X_LEFT_SPACE, yTop+graphicHeight/4+5, textPaint);
			canvas.drawText("50%", xLeft-X_LEFT_SPACE, yTop+graphicHeight/2+5, textPaint);
			canvas.drawText("25%", xLeft-X_LEFT_SPACE, yTop+graphicHeight/4*3+5, textPaint);
			canvas.drawText("0%", xLeft-X_LEFT_SPACE, yBottom, textPaint);
		}

		// Horizontal legend
		if (LITTLEPORT) {
			for (int n=0;n<=minutes;n++) canvas.drawText(n+"'", xRight-n*AnotherMonitor.WIDTH_INTERVAL*(int)(60/((float)AnotherMonitor.READ_INTERVAL/1000)), yBottom+12, textPaint4);
			if (minutes==0) canvas.drawText(seconds+"\"", xLeft, yBottom+12, textPaint4);
		} else {
			for (int n=0;n<=minutes;n++) canvas.drawText(n+"'", xRight-n*AnotherMonitor.WIDTH_INTERVAL*(int)(60/((float)AnotherMonitor.READ_INTERVAL/1000)), yBottom+Y_BOTTOM_SPACE, textPaint2);
			if (minutes==0) canvas.drawText(seconds+"\"", xLeft, yBottom+Y_BOTTOM_SPACE, textPaint2);
		}
	}




	/**
	 * It draws the line of the taken memory value vector. This action consumes quite system resources.
	 * 
	 * This method is only called from the onDraw() overrided method.
	 */
	private void drawLine(Vector<String> y, Canvas canvas, Paint paint) {
		if(y.size()>1) for(int m=0; m<(y.size()-1) && m<AnotherMonitor.TOTAL_INTERVALS; m++) {
			canvas.drawLine(xRight-AnotherMonitor.WIDTH_INTERVAL*m,
					yBottom-Integer.parseInt(y.elementAt(m))*graphicHeight/memTotal,
					xRight-AnotherMonitor.WIDTH_INTERVAL*m-AnotherMonitor.WIDTH_INTERVAL,
					yBottom-Integer.parseInt(y.elementAt(m+1))*graphicHeight/memTotal, paint);
		}
	}




	/**
	 * It draws the line of the taken CPU usage value vector. This action consumes quite system resources.
	 * 
	 * This method is only called from the onDraw() overrided method.
	 */
	private void drawLineFloat(Vector<Float> y, Canvas canvas, Paint paint) {
		if(y.size()>1) for(int m=0; m<(y.size()-1) && m<AnotherMonitor.TOTAL_INTERVALS; m++) {
			canvas.drawLine(xRight-AnotherMonitor.WIDTH_INTERVAL*m,
					yBottom-y.elementAt(m)*graphicHeight/100,
					xRight-AnotherMonitor.WIDTH_INTERVAL*m-AnotherMonitor.WIDTH_INTERVAL,
					yBottom-y.elementAt(m+1)*graphicHeight/100, paint);
		}
	}




	/**
	 * It initializes all the size variables, Paint objects and another stuff the first time
	 * the graphic is drawn or when the screen size change, as for example, the screen orientation changes.
	 * Like this we does not uselessly recalculate variables every time the graphic is drawn.
	 * 
	 * This method is only called once from the onDraw() overrided method if the INITIALICEGRAPHIC flag is true.
	 */
	private void initializeGraphic() {
		xLeft = (int)(getWidth()*0.14);
		xRight = (int)(getWidth()*0.95);
		yTop = (int)(getHeight()*0.06);
		yBottom = (int)(getHeight()*0.88);
		graphicWidth = xRight - xLeft;
		graphicHeight = yBottom - yTop;
		AnotherMonitor.TOTAL_INTERVALS = (int)Math.ceil(graphicWidth/AnotherMonitor.WIDTH_INTERVAL)+3;
		minutes = (int)(Math.floor(AnotherMonitor.TOTAL_INTERVALS/(60/((float)AnotherMonitor.READ_INTERVAL/1000))));
		seconds = AnotherMonitor.TOTAL_INTERVALS % (int)(60/((float)AnotherMonitor.READ_INTERVAL/1000));

		/* This check is performed to add compatibility with 320x240 screens.
	If we would not do this implementation, the graphics would fit badly in 320x240 screens. */
		if (getWidth()<220) LITTLELAND = true;
		if (getHeight()<=160 && getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) LITTLEPORT = true;

		graphicBackgroudPaint = getPaint(Color.parseColor(AnotherMonitor.BACKGROUND_COLOR), Paint.Align.CENTER, 12, false);
		viewBackgroudPaint = getPaint(Color.parseColor("#181818"), Paint.Align.CENTER, 12, false);
		circlePaint = getPaint(Color.RED, Paint.Align.CENTER, 12, false);
		linesPaint = getPaint(Color.parseColor(AnotherMonitor.LINES_COLOR), Paint.Align.CENTER, 12, false);
		textPaint = getPaint(Color.LTGRAY, Paint.Align.RIGHT, 12, true);
		textPaint2 = getPaint(Color.LTGRAY, Paint.Align.CENTER, 12, true);
		textPaint3 = getPaint(Color.LTGRAY, Paint.Align.RIGHT, 10, true);
		textPaint4 = getPaint(Color.LTGRAY, Paint.Align.CENTER, 10, true);
		textRecordingPaint = getPaint(Color.LTGRAY, Paint.Align.LEFT, 12, true);
		cPUAMPPaint = getPaint(Color.parseColor("#804000"), Paint.Align.CENTER, 12, false);
		cPURestPPaint = getPaint(Color.parseColor("#7D0000"), Paint.Align.CENTER, 12, false);
		memFreePaint = getPaint(Color.GREEN, Paint.Align.CENTER, 12, false);
		buffersPaint = getPaint(Color.BLUE, Paint.Align.CENTER, 12, false);
		cachedPaint = getPaint(Color.CYAN, Paint.Align.CENTER, 12, false);
		activePaint = getPaint(Color.YELLOW, Paint.Align.CENTER, 12, false);
		inactivePaint = getPaint(Color.MAGENTA, Paint.Align.CENTER, 12, false);
		swapTotalPaint = getPaint(Color.parseColor("#006000"), Paint.Align.CENTER, 12, false);
		dirtyPaint = getPaint(Color.parseColor("#800080"), Paint.Align.CENTER, 12, false);

		INITIALICEGRAPHIC=false;		
	}




	/**
	 * It initializes all the size variables, Paint objects and another stuff the first time
	 * the graphic is drawn or when the screen size change, as for example, the screen orientation changes.
	 * Like this we does not uselessly recalculate variables every time the graphic is drawn.
	 * 
	 * This method is only called from the initializeGraphic() method.
	 */
	private Paint getPaint(int color, Paint.Align align, int textSize, boolean b) {
		Paint p = new Paint();
		p.setColor(color);
		p.setTextSize(textSize);
		p.setTextAlign(align);
		p.setAntiAlias(b);
		return p;
	}




	/**
	 * Set the vectors to be drawn. Also, get the Context object of AnotherMonitor to write the text labels
	 * of the graphic in the right language.
	 * 
	 * This method is only called from the onServiceConnected() method of the AnotherMonitor class.
	 */
	void setVectors(int memTotal, Vector<String> memFree, Vector<String> buffers, Vector<String> cached,
			Vector<String> active, Vector<String> inactive, Vector<String> swapTotal, Vector<String> dirty,
			Vector<Float> cPUAMP, Vector<Float> cPURestP, Context myContext) {
		this.memTotal = memTotal;
		this.memFree = memFree;
		this.buffers = buffers;
		this.cached = cached;
		this.active = active;
		this.inactive = inactive;
		this.swapTotal = swapTotal;
		this.dirty = dirty;
		this.cPUAMP = cPUAMP;
		this.cPURestP = cPURestP;
		updateIntervalText = myContext.getString(R.string.graphic_update_interval);
		graphicPausedText = myContext.getString(R.string.graphic_paused);
		recordingText = myContext.getString(R.string.graphic_recording);
	}
}