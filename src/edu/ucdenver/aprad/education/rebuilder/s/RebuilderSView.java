package edu.ucdenver.aprad.education.rebuilder.s;

import android.app.Dialog;
import android.app.ProgressDialog;
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
import edu.ucdenver.aprad.spectrogram.Spectrogram;
import edu.ucdenver.aprad.spectrogram.SpectrogramView;
import edu.ucdenver.aprad.tools.AudioRecorderListenerManipulate;

/*****************************
 **
 ** @author Kun Li
 ** @created 6 Apr 2015
 ** 
 ** @modified_by 
 ** @modified_date 
 **
 *****************************/

public class RebuilderSView extends SurfaceView implements SurfaceHolder.Callback {
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
	
	private static final int    SIGNALS_HEIGHT 		= RebuilderS.sigInterval / 2;
	private static final int    SIGNALS_WIDTH 		= SpectrogramView.MAX_SIGNALS_LENGTH;
	public  static final int    COLOR_RANGE			= 256;
	private int 		 		red 	 			= 0;
	private int 		 		blue 	 			= 0;
	private int 		 		green 	 			= 0;
	private double 				magic_n 			= 65.536;
	
	
	/**
	 * Constructor for SpecAnalyView
	 * @param context Context of  the application
	 * @param attrs Attribute set of the application
	 */
	public RebuilderSView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init( context );
	}
	
	
	/**
	 * Constructor for SpecAnalyView passing in only the context
	 * @param context Context of the application
	 */
	public RebuilderSView(Context context) {
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
        		drawS();
        	}
        }
    });
	
	
	public void end() {
		running = false;	
	}
	
	
	/**
	 * Main class to handle the drawing  of the UI
	 */
	public void drawS() {
		if(isSurfaceCreated) {	
			canvas = surfaceHolder.lockCanvas(null);
			if( canvas != null ) {
				canvas.drawColor( Color.BLACK );
				paint.setColor(Color.WHITE);
				drawAxisX();
				drawAxisY(RebuilderS.samplingRate);
				drawSignalGraph(RebuilderS.transformedSignal);
			}
			surfaceHolder.unlockCanvasAndPost( canvas );
		}
	}
	
	
	private void drawAxisX() {
		paint.setStrokeWidth(2);
		canvas.drawLine(50, scrHeight-50, scrWidth-50, scrHeight-50, paint );
	}
	
	
	/**
	 * Draws y-axis lines to provide clear reference to dB values for amplitude of signal
	 */
	private void drawAxisY(double samplingRate) {
		paint.setStrokeWidth(2);
		canvas.drawLine(50, scrHeight-50, 50, 50, paint );
		int maxFrequencyInt = (int) samplingRate / 1000 /2;
		int yInterval = (scrHeight-100) / maxFrequencyInt;
		paint.setStrokeWidth(1);
		for(int i=0; i<=maxFrequencyInt; i++) {
			canvas.drawText(String.valueOf(i), 20, scrHeight-50-i*yInterval, textPaint);
		}
		canvas.drawText("KHz", 15, 20, textPaint);
		canvas.drawText("Sample Rate: "+String.valueOf(samplingRate)+" Hz", scrWidth/2-200, 25, textPaint);
		canvas.drawText("Time Span: "+String.valueOf(RebuilderS.timeSpan)+" s", scrWidth-300, 25, textPaint);
	}
	
	
	private void drawSignalGraph(double[][] transformedSigs) {
		int sigLen = transformedSigs.length;
		float heightScalar = ((float)scrHeight-100) / SIGNALS_HEIGHT;
		float widthScalar = ((float)scrWidth-100) / SIGNALS_WIDTH;
		double signalValue;
		int colorAmplitude;
		for (int i = 0; i < sigLen; i++) {
			for (int j = 0; j < SIGNALS_HEIGHT; j++) {
				signalValue = 20.0 * ( Math.log( transformedSigs[i][j] / magic_n ) /  Math.log( 10 ) );
				colorAmplitude = (int)( 2048 + 24.975 * signalValue);
				colorSelecter(colorAmplitude);
				paint.setARGB(COLOR_RANGE - 1, red, green, blue);
				canvas.drawLine(51+i*widthScalar, scrHeight-51-j*heightScalar, 
								51+(i+1)*widthScalar, scrHeight-51-(j+1)*heightScalar, paint);
			}
		}
	}
	
	
	/**
	 * Sets the color to be drawn on the bitmap next<br><br>
	 * this.red =  ;<br>
	 * this.green =  ;<br>
	 * this.blue =  ;<br>
	 * @param colorIterator
	 */
	public void colorSelecter(final int colorIterator) {
		int colorFactor;
        if (colorIterator < 0) 
        {
            colorFactor = 0;
        }else {
            colorFactor = colorIterator;
        }
        
        if (colorFactor / COLOR_RANGE == 0)
        {
            this.red = 0;
            this.green = 0;
            this.blue = colorFactor % COLOR_RANGE;
        }
        
        if (colorFactor / COLOR_RANGE == 1)
        {
            this.red = 0;
            this.green = colorFactor % COLOR_RANGE;
            this.blue = COLOR_RANGE - 1;
        }
        
        if (colorFactor / COLOR_RANGE == 2)
        {
            this.red = 0;
            this.green = COLOR_RANGE - 1;
            //this.blue = (COLOR_RANGE & COLOR_RANGE - 1 - colorFactor);
            this.blue = 0;
        }
        
        if (colorFactor / COLOR_RANGE == 3)
        {
            this.red   = colorFactor % COLOR_RANGE;
            this.green = COLOR_RANGE - 1;
            this.blue  = 0;
        }
        
        if (colorFactor / COLOR_RANGE == 4) 
        {
            this.red   = COLOR_RANGE - 1;
            this.green = COLOR_RANGE - 1 - colorFactor % COLOR_RANGE;
            this.blue  = 0;
        }
        
        if (colorFactor / COLOR_RANGE == 5) 
        {
            this.red = COLOR_RANGE - 1;
            this.green = 0;
            this.blue = colorFactor % COLOR_RANGE;
        }
        
        if (colorFactor / COLOR_RANGE == 6) 
        {
            this.red = COLOR_RANGE - 1;
            this.green = colorFactor % COLOR_RANGE;
            this.blue = COLOR_RANGE - 1;
        }
        
        if (colorFactor / COLOR_RANGE == 7) 
        {
            this.red = COLOR_RANGE - 1;
            this.green = COLOR_RANGE - 1;
            this.blue = COLOR_RANGE - 1;
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
