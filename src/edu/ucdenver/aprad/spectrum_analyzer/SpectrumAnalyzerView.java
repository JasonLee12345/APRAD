package edu.ucdenver.aprad.spectrum_analyzer;

import edu.ucdenver.aprad.tools.AudioRecorderListenerManipulate;
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

/**
 * Class SpecAnalyView
 * Handles drawing the results to the UI
 * @author Dan Rolls
 * @author Michael Dewar
 *
 */
/*****************************
 ** 
 ** @modified_by Kun Li
 ** @modified_date 8 Dec 2014
 **
 ** @modified_by Kun Li
 ** @modified_date 6 May 2015
 **
 *****************************/

public class SpectrumAnalyzerView extends SurfaceView implements SurfaceHolder.Callback 
{
	private Display 			display;
	private Context 			context;
	private int 				scrWidth; 
	private int 				scrHeight;
	private float         		scaleWidth;
	private float         		scaleHeight;
	private boolean 			isSurfaceCreated;
	private SurfaceHolder 		surfaceHolder;
	private Canvas 				canvas;
	Paint 						paint;
	Paint 						textPaint;
	private int 				densityDPI;
	public static double 		amplify_coeft = 6;
	
	private Resources resources;
	
	private static final int 	COLOR_RANGE = 256;
						 int 	red = 0;
						 int 	blue = 0;
						 int 	green = 0;
						 
	private static final int 		SHIFT_CONST 			= 50;
	private static final double 	SCALE_FACTOR 			= 1.0;	
	private static final int     	SIGNAL_OFFSET_Y_AXIS 	= 40;
	private static final int     	SIGNAL_PADDING_X_AXIS 	= 45;
	private static final int     	SIGNAL_PADDING_Y_AXIS 	= 30;	
	private double     				frequency 				= MAX_FREQUENCY;
	
	public  static final int     	SIGNAL_LENGTH 			= AudioRecorderListenerManipulate.NUM_SAMPLES/2;
	private static final int     	SIGNAL_OFFSET_X_AXIS 	= 60;
	public  static final double  	MAX_FREQUENCY 			= 48000.00;
	private static final float 		X_AXIS_LEFT_PADDING 	= 20.0f;
	private static final float 		Y_AXIS_BOTTOM_PADDING 	= 20.0f;
	private static final float 		SHORT_GRID_WIDTH 		= 4;
	private static final float 		Y_AXIS_SPACING 			= 0;
	private static final float 		GRID_TEXT_SIZE 			= 1;
	private static final float 		TEXT_SIZE 				= 8;
	private static final float		BIG_TEXT_SIZE			= 10;
	private static final int 		SPECTRUM_HEIGHT 		= 448;
	private final int 				Y_AXIS_ROW_COUNT 		= 10;
	
	
	DisplayMetrics dm = new DisplayMetrics();
	
	/**
	 * Constructor for SpecAnalyView
	 * @param context Context of  the application
	 * @param attrs Attribute set of the application
	 */
	public SpectrumAnalyzerView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		init( context );
	}

	/**
	 * Constructor for SpecAnalyView passing in only the context
	 * @param context Context of the application
	 */
	public SpectrumAnalyzerView(Context context) 
	{
		super(context);
		init( context );
	}
	
	private void init( Context context ){
	
	   paint = new Paint();
	   textPaint = new Paint();
	   
	   //paint.setColor(Color.WHITE);
	   //paint.setTextSize( getGridRelativeSize() );
	   //textPaint.setColor(Color.WHITE);
	   //textPaint.setTextSize( getTextRelativeSize() );
	   
	   surfaceHolder = getHolder();
	   getHolder().addCallback(this);    
	   setFocusable(true);
	   this.context = context;
	   this.display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();  
	   resources = getResources();
	   dm = resources.getDisplayMetrics();
	   densityDPI = dm.densityDpi;
	}
	
	/**
	 * Main class to handle the drawing  of the UI
	 * @param signal Array of processed FFT bytes
	 * @param samplingRate The rate at which the samples were harvested
	 * @param numberOfFFTPoints The number of FFT points analyzed
	 * @param maxFFTSample The max size of the FFT sample
	 */
	public void drawSpectrum( double[] transformedsignal, 
	                          double samplingRate, //frequency, default value is 8000.0
	                          int numberOfFFTPoints,//NUM_SAMPLES=512(points number in FFT, AudioRecorder) 
	                          int maxFFTSample )
	{
		if(isSurfaceCreated)
		{	
			loadScaleValues();
			
				//for( int i=0; i<4; ++ i )
				//{
					canvas = surfaceHolder.lockCanvas(null);
					if( canvas != null )
					{
						canvas.drawColor( Color.BLACK );
						drawAxisX( canvas, samplingRate );
						drawAxisY( canvas );
						drawSignalGraph( canvas, transformedsignal, maxFFTSample/2 );
					}
					surfaceHolder.unlockCanvasAndPost( canvas );
				//}
		}
	}
	
	/**
	 * Draws y-axis lines to provide clear reference to dB values for amplitude of signal
	 */
	private void drawAxisY( Canvas canvas ) {	  
	  paint.setColor(Color.WHITE);
	  
	  float yInterval = (SPECTRUM_HEIGHT*scaleHeight)/(Y_AXIS_ROW_COUNT);
	  
	  drawAxisYdBKey( canvas );
    
	  	for( int i = 0; i < Y_AXIS_ROW_COUNT; ++ i )
	  	{
	  		float y = i * yInterval + (int)getAxisYBottomPadding();
	  		drawAxisYdBValue( canvas, i, y );
	  		drawAxisYRowLine( canvas, i, y );
	  	}
	}

	
	 private void drawAxisYdBValue( Canvas canvas, int i, float y ) {
		 	paint.setTextSize( getTextRelativeSize() );
		 	String dBValue = "-" + (9-i)*10;
		 	canvas.drawText( dBValue, 10, scrHeight - y, paint);
	 }
	
	 
	
  private void drawAxisYRowLine(Canvas canvas, int i, float y) {
	  paint.setStrokeWidth(1);
    if( i % 3 == 0 ){
      drawRowLineWhole( canvas, y );
    } else {
      drawRowLineShort( canvas, y );
    }
  }

	/**
	 *Class to draw the spectrum ticks and numbers for the scale
	 * @param canvas The canvas to draw on
	 * @param samplingRate The rate at which the samples were harvested
	 * @param numberOfFFTPoints The number of FFT points analyzed
	 */
	private void drawAxisX( Canvas canvas, double samplingRate ) 
	{
		paint.setColor(Color.WHITE);
	
		int maxFrequency = (int) samplingRate / 1000 /2;
		float xAxisIntervalSize = (SIGNAL_LENGTH*scaleWidth) / maxFrequency;
		
		for(int i = 0; i <= maxFrequency; ++ i )
		{
			drawAxisXFrequencyValue( canvas, xAxisIntervalSize, i );
			drawAxisXColumnLine( xAxisIntervalSize, i );
		}
	}

	
	
  private void drawAxisXFrequencyValue( Canvas canvas, float xAxisIntervalSize, int i )
  {
	  paint.setTextSize( getBigTextRelativeSize() );
	  paint.setStrokeWidth(5);
   canvas.drawText( String.valueOf(i), xAxisIntervalSize*i + getAxisXLeftPadding() , scrHeight, paint );
  }
	
  
  
	private void drawAxisXColumnLine(float xAxisIntervalSize, int i) 
	{// TODO Auto-generated method stub
		paint.setStrokeWidth(1);
		canvas.drawLine(xAxisIntervalSize*i + getAxisXLeftPadding(), scrHeight - getAxisYBottomPadding(),
						xAxisIntervalSize*i + getAxisXLeftPadding(), 0 , paint);
    
	}

	
  /**
	 * Class to draw the actual signal coming from the FFT class
	 * @param canvas The canvas to draw on
	 * @param signal Array of processed FFT bytes
	 * @param maxFFTSample The max size of the FFT sample
	 */
	private void drawSignalGraph( Canvas canvas, double[] transformedsignal, int maxFFTSample )
	{
		double sampleValue, nextSampleValue;
		paint.setColor(Color.RED);
		paint.setStrokeWidth(3);
		
		int xAxisLeftPadding = (int) getAxisXLeftPadding();
		int yAxisBottomPadding = (int) getAxisYBottomPadding();

		for(int i = 0; i < SIGNAL_LENGTH - 1; i++)
		{
			sampleValue 	= transformedsignal[i];
			nextSampleValue = transformedsignal[i+1];
			
			drawSignalGraphInterval(canvas, sampleValue*amplify_coeft, nextSampleValue*amplify_coeft,
									xAxisLeftPadding,yAxisBottomPadding,i);
		}
	}

	
	
    private void drawSignalGraphInterval( Canvas canvas, double sampleValue, double nextSampleValue, 
    										int xAxisPadding, int yAxisPadding, int i )
    {
	  canvas.drawLine( i*scaleWidth+xAxisPadding, 
			  		   (scrHeight - (int)sampleValue*scaleHeight) - yAxisPadding, 
			  		   i*scaleWidth+scaleWidth+xAxisPadding, 
			  		   (scrHeight - (int)nextSampleValue*scaleHeight) - yAxisPadding, 
			  		   paint );
	  
    }
  
    
    
    private void drawAxisYdBKey(Canvas canvas) 
    {
		    canvas.drawText( "Unit", 0, getAxisYBottomPadding()/2, paint );
	}
		
   

    
    private void drawRowLineWhole(Canvas canvas, float y) 
    {
    	int xOffset = (int) getAxisXLeftPadding();//* ( densityDPI / 160 )
    	canvas.drawLine( xOffset, scrHeight - y, xOffset + SIGNAL_LENGTH*scaleWidth, scrHeight - y, paint);
    }

    private void drawRowLineShort(Canvas canvas, float y) 
    {
      int xOffset = (int) getAxisXLeftPadding();
      canvas.drawLine( xOffset, scrHeight - y, xOffset + getShortGridWidth(), scrHeight - y, paint);
    }

	/**
	 * Get's the screen dimension in pixel and populates the class screen attributes
	 */
	private void getViewInfo() 
	{
		this.scrWidth = getWidth();
		this.scrHeight = getHeight();
		Log.i("GetView: ","Width: "+getWidth()+" - Height: "+getHeight());
	}

	/**
	 * Override of the surfaceChanged class
	 * @param holder The surface holder
	 * @param format Screen format
	 * @param width Screen width
	 * @param height screen height
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) 
	{
		this.scrWidth = width;
		this.scrHeight = height;
	}
	
	/**
	 * Override of the surfaceCreated  class
	 * @param holder The surface holder
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) 
	{
		isSurfaceCreated = true;
		getViewInfo();
	}
	
	/**
	 * Override of the surfaceDestroyed class
	 * @param holder the surface holder
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) 
	{
		isSurfaceCreated = false;
	}
	
	/**
   * Loads the height and width scaling values for the current display if they are unset.
   * If less than 0, sets to 1
   */
  public void loadScaleValues() 
  {
	  if( scaleHeight == 0 || scaleWidth == 0 )
	  {	  
		  scaleHeight = (float)scrHeight / (float)SPECTRUM_HEIGHT;
		  scaleWidth  = ((float)scrWidth - (float)SIGNAL_OFFSET_X_AXIS)/(float)SIGNAL_LENGTH;
		  //scaleWidth  = getWidth() / SIGNAL_LENGTH;
		  
		  if( scaleHeight < 1 ){
			  scaleHeight = 1;
		  }
		  
		  if( scaleWidth < 1 ){
			  scaleWidth = 1;
		  }
		  
	  }
  }

  public float getAxisXLeftPadding()
  {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, X_AXIS_LEFT_PADDING, resources.getDisplayMetrics() );
  }
  
  
  public float getAxisYBottomPadding()
  {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Y_AXIS_BOTTOM_PADDING, resources.getDisplayMetrics() );
  }
  
  
  
  public float getShortGridWidth()
  {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, SHORT_GRID_WIDTH, resources.getDisplayMetrics() );
  }
  
  
  
  public float getAxisYSpacing()
  {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Y_AXIS_SPACING, resources.getDisplayMetrics() );
  }
  
  
  
  private float getGridRelativeSize()
  {
    return TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, GRID_TEXT_SIZE, resources.getDisplayMetrics() );
  }
  
  private float getBigTextRelativeSize()
  {
	  return TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, BIG_TEXT_SIZE, resources.getDisplayMetrics() );
  }
  
  private float getTextRelativeSize()
  {
	return TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE, resources.getDisplayMetrics() );
  }
  
}   
