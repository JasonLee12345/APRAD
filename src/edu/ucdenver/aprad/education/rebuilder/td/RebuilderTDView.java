package edu.ucdenver.aprad.education.rebuilder.td;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import edu.ucdenver.aprad.education.rebuilder.ReadSavedFileData;
import edu.ucdenver.aprad.tools.AudioRecorderListenerManipulate;

/*****************************
 **
 ** @author Kun Li
 ** @created 7 Apr 2015
 ** 
 ** @modified_by 
 ** @modified_date 
 **
 *****************************/

public class RebuilderTDView extends SurfaceView implements SurfaceHolder.Callback {
	private Context 			context;
	private Display 			display;
	
	private boolean 			running;

	private boolean 			isSurfaceCreated;
	private SurfaceHolder 		surfaceHolder;
	private Canvas 				canvas;
	Paint 						paint;
	Paint 						textPaint;
	
	private int 				scrWidth; 
	private int 				scrHeight;
	
	public static int 			startPosition = 0;
	public static int 			endPosition;
	
	public static float			scalePoint = 1;
	
	/**
	 * Constructor for SpecAnalyView
	 * @param context Context of  the application
	 * @param attrs Attribute set of the application
	 */
	public RebuilderTDView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init( context );
	}
	
	
	/**
	 * Constructor for SpecAnalyView passing in only the context
	 * @param context Context of the application
	 */
	public RebuilderTDView(Context context) {
		super(context);
		init( context );
	}
	
	
	private void init( Context context ) {
	   paint = new Paint();
	   textPaint = new Paint();
	   textPaint.setStrokeWidth(2);
	   textPaint.setColor(Color.WHITE);
	   textPaint.setTextSize(30);
	   
	   running = true;
	   surfaceHolder = getHolder();
	   getHolder().addCallback(this);    
	   setFocusable(true);
	   this.context = context;
	   this.display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	}
	
	
	public Thread mThread = new Thread(new Runnable() {
        @Override  
        public void run() {
        	while (running) {
        		drawTD();
        	}
        }
    });
	
	
	public void end() {
		running = false;	
	}
	
	
	/**
	 * Main class to handle the drawing  of the UI
	 */
	public void drawTD() {
		if(isSurfaceCreated) {	
			canvas = surfaceHolder.lockCanvas(null);
			if( canvas != null ) {
				canvas.drawColor( Color.BLACK );
				paint.setColor(Color.WHITE);
				drawAxisX();
				drawAxisY();
				paint.setColor(Color.GREEN);
				drawSignalGraph();
			}
			surfaceHolder.unlockCanvasAndPost( canvas );
		}
	}
	
	
	private void drawAxisX() {
		paint.setStrokeWidth(2);
		canvas.drawLine(50, scrHeight/2, scrWidth-50, scrHeight/2, paint );
		paint.setStrokeWidth(1);
		for(int i=0; i<scrWidth; i+=100) {
			canvas.drawText(String.valueOf(startPosition+i), 50+i, scrHeight-40, textPaint);
			canvas.drawLine(50+i, scrHeight-50, 50+i, 50, paint);
		}
	}
	
	
	/**
	 * Draws y-axis lines to provide clear reference to dB values for amplitude of signal
	 */
	private void drawAxisY() {
		paint.setStrokeWidth(2);
		canvas.drawLine(50, scrHeight-50, 50, 50, paint );
	}
	
	
	private void drawSignalGraph() {
		for(int i=0; i<scrWidth-100-1; i++) {
			canvas.drawLine(50+i, scrHeight/2-(int)ReadSavedFileData.actualData[startPosition+i]*scalePoint, 
							50+i+1, scrHeight/2-(int)ReadSavedFileData.actualData[startPosition+i+1]*scalePoint, paint);
		}
	}
	
    
	/**
	 * Get's the screen dimension in pixel and populates the class screen attributes
	 */
	private void getViewInfo() {
		this.scrWidth = getWidth();
		this.scrHeight = getHeight();
		endPosition = RebuilderTD.numberOfFFTPoints - scrWidth;
	}
	
	
	/**
	 * Override of the surfaceChanged class
	 * @param holder The surface holder
	 * @param format Screen format
	 * @param width Screen width
	 * @param height screen height
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		this.scrWidth = width;
		this.scrHeight = height;
		getViewInfo();
	}
	
	
	/**
	 * Override of the surfaceCreated  class
	 * @param holder The surface holder
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		isSurfaceCreated = true;
		mThread.start(); 
	}
	
	
	/**
	 * Override of the surfaceDestroyed class
	 * @param holder the surface holder
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		isSurfaceCreated = false;
	}
}   
