package edu.ucdenver.aprad.education.rebuilder.fd;

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
import edu.ucdenver.aprad.tools.AudioRecorderListenerManipulate;

/*****************************
 **
 ** @author Kun Li
 ** @created 5 Apr 2015
 ** 
 ** @modified_by 
 ** @modified_date 
 **
 *****************************/

public class RebuilderFDView extends SurfaceView implements SurfaceHolder.Callback {
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
	
	public static float			scalePoint = 1;
	
	
	/**
	 * Constructor for SpecAnalyView
	 * @param context Context of  the application
	 * @param attrs Attribute set of the application
	 */
	public RebuilderFDView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init( context );
	}
	
	
	/**
	 * Constructor for SpecAnalyView passing in only the context
	 * @param context Context of the application
	 */
	public RebuilderFDView(Context context) {
		super(context);
		init( context );
	}
	
	
	private void init(Context context) {
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
        		drawFD();
        	}
        }
    });
	
	
	public void end() {
		running = false;	
	}
	
	
	/**
	 * Main class to handle the drawing  of the UI
	 */
	public void drawFD() {
		if(isSurfaceCreated) {	
			canvas = surfaceHolder.lockCanvas(null);
			if( canvas != null ) {
				canvas.drawColor( Color.BLACK );
				paint.setColor(Color.WHITE);
				drawAxisX(RebuilderFD.samplingRate);
				drawAxisY();
				paint.setColor(Color.RED);
				drawSignalGraph(RebuilderFD.transformedSignal);
			}
			surfaceHolder.unlockCanvasAndPost( canvas );
		}
	}
	
	
	private void drawAxisX(double samplingRate) {
		paint.setStrokeWidth(2);
		int maxFrequencyInt = (int) samplingRate / 1000 /2;
		int xInterval = (scrWidth-100) / maxFrequencyInt;
		canvas.drawLine(50, scrHeight-50, scrWidth-50, scrHeight-50, paint );
		paint.setStrokeWidth(1);
		for(int i=0; i<=maxFrequencyInt; i++) {
			canvas.drawText(String.valueOf(i), 50+xInterval*i, scrHeight-25, textPaint);
			canvas.drawLine(50+xInterval*i, scrHeight-50, 50+xInterval*i, 50, paint);
		}
		textPaint.setTextSize(20);
		canvas.drawText("KHz", scrWidth-50, scrHeight-50, textPaint);
		textPaint.setTextSize(30);
	}
	
	
	/**
	 * Draws y-axis lines to provide clear reference to dB values for amplitude of signal
	 */
	private void drawAxisY() {
		paint.setStrokeWidth(2);
		int yInterval = (scrHeight-100) / 10;
		canvas.drawLine(50, scrHeight-50, 50, 50, paint );
		paint.setStrokeWidth(1);
		for(int i=0; i<=10; i++) {
			canvas.drawText(String.valueOf(i), 20, (scrHeight-50)-yInterval*i, textPaint);
			if(i%2 == 0)
				canvas.drawLine(50, (scrHeight-50)-yInterval*i, scrWidth-50, (scrHeight-50)-yInterval*i, paint);
		}
	}
	
	
	private void drawSignalGraph(double[] transformedSigs) {
		int scale = transformedSigs.length / (scrWidth-100);
		for(int i=0; i<scrWidth-100-1; i++) {
			canvas.drawLine(50+i, (scrHeight-50)-(int)transformedSigs[scale*i]*scalePoint, 
							50+i+1, (scrHeight-50)-(int)transformedSigs[scale*(i+1)]*scalePoint, paint);
		}
	}
	
    
	/**
	 * Get's the screen dimension in pixel and populates the class screen attributes
	 */
	private void getViewInfo() {
		this.scrWidth = getWidth();
		this.scrHeight = getHeight();
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
