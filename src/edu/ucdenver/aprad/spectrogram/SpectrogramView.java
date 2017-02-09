package edu.ucdenver.aprad.spectrogram;

import java.util.Vector;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

/**
 * The Spectrogram is used to map frequency and amplitude over time. This is
 * represented as frequency on the y axis, time on the x axis, amd amplitude
 * is the color of the point on the graph.
 * <br><br>
 * SpectrogramView is used to draw the information passed to the Spectrogram.
 * 
 * @author Kun Li
 * @author Robbie Perlstein
 * @author Dan Rolls
 * @author Michael Dewar
 *
 */
/*****************************
 ** 
 ** @modified_by Kun Li
 ** @modified_date 20 Feb 2015
 **
 *****************************/

public class SpectrogramView extends SurfaceView implements SurfaceHolder.Callback 
{
	public  float 				global_x_location;//for saving current X location
	private int                  scrWidth; 
	private int                  scrHeight;
	private int                  scaleWidth;
	private int                  scaleHeight;
	private boolean              isSurfaceCreated;
	private SurfaceHolder        surfaceHolder;
	private Resources            resources;

	private static final int     COLOR_RANGE 			= 256;
	private static final int     TIME_INTERVAL_COUNT 	= 6;
	public  static final int     MAX_SIGNALS_LENGTH 	= 720;
	public  static final int     SIGNALS_HEIGHT 		= 256;
	public  static final double  MAX_FREQUENCY 			= 48000.00;
	public  static final double  MIN_FREQUENCY 			= 8000.00;
	
	public  static final int     COLOR_GRAPH_HEIGHT 	= 360;
	private static final int     COLOR_GRAPH_WIDTH 		= 5;
  
  	private static final int     SIGNAL_PADDING_X_AXIS 	= 25;
  	private static final int     SIGNAL_PADDING_Y_AXIS 	= 20;
  
  	private static final int     GRID_TEXT_SIZE 		= 10;
	
	private static final String  START_RECORDING_STR 	= "Start Recording";
	private static final String  STOP_RECORDING_STR 	= "Stop Recording";
	private static final String  RENDERING_RECORDING_STR = "Rendering Data...";
	private static final String  RECORDING_INDICATOR_STR = "Recording in Background...";
	public  static final int     TOP_BAR_TEXT_SIZE 		= 19;
	public  static final int     TOP_PADDING 			= 5;
	
	private double               frequency 				= MIN_FREQUENCY;
	
	private static Paint spectroPaint;
	private static Paint legendPaint;
	private static Paint topBarPaint;
	private static Paint gridPaint;
	private static Paint purplePaint;
	
	private Canvas 		 canvas;
	private int 		 red 	 			= 0;
	private int 		 blue 	 			= 0;
	private int 		 green 	 			= 0;
	private boolean 	 logScaling 	 	= true;
  
	// SINGLETON relative scaling values
	private Float 		 relativeColorGraphWidth;
	private Float 		 relativeGridTextSize;
	private Float 		 relativePaddingAxisX;
	private Float 		 relativePaddingAxisY;
	private Float 		 relativeTopBarSize;
	
	private RectF myRectF = null;
	
	//////////////////////////////////////////////////////////////////////////////
	
	/**
	 * View that draws the spectrogram to the canvas
	 */
	public SpectrogramView( Context context, AttributeSet attrs ) 
	{
		super(context, attrs);
		init(context);
	}

	/**
   * View that draws the spectrogram to the canvas
   */
	public SpectrogramView( Context context ) 
	{
		super(context);
		init(context);
	}
	
	/**
	 * Initialize the SpectrogramView, for use with constructors.
	 * @param context
	 */
	private void init( Context context )
	{
	  canvas = null;
	  getHolder().addCallback(this);
	  surfaceHolder = getHolder();
	  setFocusable(true);
	  scrHeight = getHeight();
	  scrWidth = getWidth();
	  resources = getResources();
	  spectroPaint = new Paint();
	  legendPaint = new Paint();
	  getPaintTopBar();
	  getPaintGrid();
	}
	
	/**
	 * Updates the canvas to display the current signal, and if no amplitude
	 * legend has been drawn, it will draw that on the canvas as well. <br><br>
	 * Call drawSpectrogramColumn() "4 times";
	 * @return number of signals stored
	 * @param signal double array of 256 length
	 * @param samplingRate
	 * @param numberOfFFTPoints
	 * @param maxFFTSample
	 * @param offset = signal_count - 1
	 */
	public int drawSpectrogram( double[] signal, 
	                            double samplingRate, 
	                            int numberOfFFTPoints, 
	                            double maxFFTSample, 
	                            int offset )
	{
	  if( offset == MAX_SIGNALS_LENGTH )
	  {
		  return offset;
	  }
	  
  	  if( isSurfaceCreated )
  	  {
  		  // TODO have draw return a bitmap, and set the canvas bitmap
  		  for( int i = 0; i < 4; i++ )
  		  {
  			  canvas = surfaceHolder.lockCanvas();
  				drawSpectrogramColumn( canvas, signal, maxFFTSample, offset );
  			  surfaceHolder.unlockCanvasAndPost( canvas );
  		  }
  	  }
  	  
  	  //if( offset == MAX_SIGNALS_LENGTH )
  	  //{
  		  //drawButtonStart();
  	  //}
  	  
  	  return offset;
	}

	/**
	 * Loads the height and width scaling values for the current display if they are unset.
	 * If less than 0, sets to 1
	 */
  public void loadScaleValues() 
  {
    if( scaleHeight == 0 || scaleWidth == 0 )
    {
      scaleHeight = (int) ( ( (float)getHeight() - getRelativePaddingAxisY() ) / SIGNALS_HEIGHT );
      scaleWidth  = (int) ( ( (float)getWidth()  - getRelativePaddingAxisX() ) / MAX_SIGNALS_LENGTH );
      	if( scaleHeight < 1 )
      	{
      		scaleHeight = 1;
      	}
      	if( scaleWidth < 1 )
      	{
      		scaleWidth = 1;
      	}
    }
  }
	
	// TODO remove literals
	/**
	 * Draws left-side Y-axis grid lines correlating to frequency in KHz
	 * @param canvas Canvas to be drawn on
	 */
	private void drawGridKHz( Canvas canvas )
	{	  
	  // Calculate spacing and interval
	  int effectiveFrequency = (int) frequency / 2;
	  int gridIntervals =  effectiveFrequency / 2 / 1000;
	  gridIntervals = (gridIntervals < 3 ) ? 4 : gridIntervals;
	  int yAxisSpacing = ( SIGNALS_HEIGHT * scaleHeight ) / gridIntervals;
	  int frequencyValue = 0;
	  int textOffsetX = 0;
	  int yAxisOffset = 0;
	  
	  // Draw grid interval marks
    for( int i = 0; i < gridIntervals + 1; ++ i )
    {
      frequencyValue = ( effectiveFrequency / 1000 / gridIntervals ) * i;
      textOffsetX = ( frequencyValue >= 10 ) ? 0 : 5;
      yAxisOffset = scrHeight - (yAxisSpacing * i);
      drawKHzValues( canvas, frequencyValue, textOffsetX, yAxisOffset );
      drawKHzMarkings( canvas, yAxisOffset );
    }
    
    // Draw Legend indicator
    drawKHzKey( canvas );
  }
	

	
 /**
  * Draws bottom X-axis grid lines indicating time elapsed
  * @param canvas Canvas to be drawn on
  */
/*
 private void drawGridTime( Canvas canvas )
 {

   int xAxisSpacing = (MAX_SIGNALS_LENGTH * scaleWidth)/TIME_INTERVAL_COUNT;
   float paddingX = getRelativePaddingAxisX();
   float paddingY = getRelativePaddingAxisY();
   canvas.drawLine( paddingX,
		   			scrHeight - paddingY,
		   			MAX_SIGNALS_LENGTH * scaleWidth + paddingX + 5,
   					scrHeight - paddingY,
   					gridPaint);
   // Draw grid interval marks
   for( int i = 0; i < TIME_INTERVAL_COUNT + 1; ++ i )
   {
     int xAxisOffset = (int) (xAxisSpacing*i + getRelativePaddingAxisX());
     String intervalStr = String.valueOf( i*10 );
     
     canvas.drawLine( xAxisOffset, 
                      scrHeight - getRelativeGridTextSize(), 
                      xAxisOffset, 
                      scrHeight - getRelativeGridTextSize()*2, 
                      gridPaint );
     canvas.drawText( intervalStr, 
                      xAxisOffset - (gridPaint.measureText( intervalStr ) / 2), 
                      scrHeight, 
                      gridPaint );
   }
  
 }
*/
 
  /**
   * Draws a "KHz" marking at the top of the left side y-axis
   * of the graph.
   */
  private void drawKHzKey( Canvas canvas ) 
  {
    canvas.drawText( "KHz", 
                      0, 
                      scrHeight - (SIGNALS_HEIGHT * scaleHeight) - getRelativePaddingAxisX() - getRelativeGridTextSize(), 
                      gridPaint );
  }

  /**
   * Draws the small line markings on the left side y-axis
   * of the graph to correlate with the KHz values.
   */
  private void drawKHzMarkings( Canvas canvas, int yAxisOffset )
  {
    float markingPadding = gridPaint.measureText( "0" )*2;
    canvas.drawLine(  getRelativePaddingAxisX(), 
                      yAxisOffset - getRelativePaddingAxisY(), 
                      getRelativePaddingAxisX() - markingPadding, 
                      yAxisOffset - getRelativePaddingAxisY(), 
                      gridPaint );
  }

  /**
   * Draws the KHz value markings on the left side y-axis
   * of the graph.
   */
  private void drawKHzValues( Canvas canvas, 
                              int frequencyValue,
                              int textOffsetX, 
                              int yAxisOffset )
  {
    canvas.drawText( String.valueOf( frequencyValue ), 
                     textOffsetX, 
                     yAxisOffset - getRelativePaddingAxisY() + 5, 
                     gridPaint );
  }

  /**
   * Draws right-side Y-axis color graph and legend.
   * <br>
   * Used to indicate the amplitude value of the signal displayed.
   */
  private void drawGridAmplitude( Canvas canvas )
  {
    drawAmplitudeSpectrum( canvas );
    if( logScaling )
    {
      drawAmplitudeValues( canvas, new String[]{ "-93", "-75", "-50", "-25", "0" } );     
      drawAmplitudeKey( canvas, "Log" );
      
    } else {
      drawAmplitudeValues( canvas, new String[]{ "0%", "25%", "50%", "75%", "100%" } );      
      drawAmplitudeKey( canvas, "Lin" );
      
    }
    
  }
  
    // TODO move literal to variables
	// TODO change 360 to 256
	// TODO change color range to remove black and white
	/**
	 * Prints the legend for amplitude versus color
	 * @param canvas Required for drawing, get from surfaceHolder
	 */
	private void drawAmplitudeSpectrum( Canvas canvas ) 
	{ 
	  int iteration_max = getScalarHeight();
	  float graphWidth = getRelativeColorGraphWidth();
		for( int i = 0; i < COLOR_GRAPH_HEIGHT; ++ i )
		{
			final int colorIter = (int)(5.688888888888889 * (double) i);
			colorSelecter(colorIter);
			//anotherColorHSLSelecter( i, ( (double)i / 360 ) ); 
			legendPaint.setARGB( COLOR_RANGE - 1, red, green, blue );
			float yAxisHeight = 0;
			
			for( int j = 0; j < iteration_max; ++ j )
			{
			  yAxisHeight = (float) scrHeight - iteration_max*i + j;
			  canvas.drawLine( scrWidth-graphWidth, 
					  		   yAxisHeight, 
					  		   scrWidth, 
					  		   yAxisHeight, 
					  		   legendPaint );
			}
		}		
	}
	
	
   /**
   * Draws dB labels for Color Graph 
   */
  private void drawAmplitudeValues( Canvas canvas, String[] intervalValues )
  {
    float graphWidth = getRelativeColorGraphWidth();
    int intervalHeight = ( COLOR_GRAPH_HEIGHT * getScalarHeight() ) / 4;
    
    for( int i = 0; i < intervalValues.length; ++ i )
    {
      canvas.drawText( intervalValues[i], 
    		  		   scrWidth - graphWidth - gridPaint.measureText( intervalValues[i] + " "), 
    		  		   scrHeight - intervalHeight*i, 
    		  		   gridPaint );
    }
  }

  
  /**
   * @return return (scaleHeight > 1) ? scaleHeight-1 : 1;
   */
  private int getScalarHeight() 
  {
    return (scaleHeight > 1) ? scaleHeight-1 : 1;
  }
  
  
  /**
   * Draws a key type marking on top of the amplitude color graph<br>
   * Usually "Log" and "Lin"...
   */
  private void drawAmplitudeKey( Canvas canvas, String markingStr )
  {
    int yAxisOffset = COLOR_GRAPH_HEIGHT * getScalarHeight();
    canvas.drawText( markingStr, 
                     scrWidth - gridPaint.measureText( markingStr + " "),
                     scrHeight - yAxisOffset - getRelativeTopBarSize(),
                     gridPaint );
  }
	
	//TODO move literal to variables
	/**
	 * *The most Important method in Spectrogram!<br><br>
	 * Draws the colored bitmap column of the signal at the current signal_offset
	 * @param canvas Required for drawing, get from surfaceHolder
	 * @param signal double array of 256 length
	 * @param maxFFTSample
	 */
	private void drawSpectrogramColumn( Canvas canvas,
	                                    double[] signal,
	                                    double maxFFTSample,
	                                    int offset )
	{
		
		int scaleHeightMult = 0;
		
		for(int k=0; k < scaleWidth; k++)
		{
			int scaleWidthMult  = scaleWidth  * offset + k;

			float startX  	= getRelativePaddingAxisX() + scaleWidthMult;
			float startY  	= scrHeight - getRelativePaddingAxisY();
			float stopX   	= startX;
			global_x_location = startX;
			
			float stopY   	= scrHeight - getRelativePaddingAxisY() - scaleHeight; 
			// original code was like this: scrHeight - getRelativePaddingAxisY() + 1 + scaleHeight
	  
			// This is THE magic n
			double magic_n 			= 65.536; //maybe it's the maximum number after being processed from FFT
			double signalValue 		= 0.0;
			int    colorAmplitude 	= 0;
	  
	  
			for (int i = 0; i < SIGNALS_HEIGHT; i++)
			{
				if( logScaling )
				{
					signalValue = 20.0 * ( Math.log( signal[i] / magic_n ) /  Math.log( 10 ) );
					colorAmplitude = (int)( 2048 + 24.975 * signalValue);
				} else {
					signalValue = (0.0 + signal[i]);
					colorAmplitude = (int)( 31.25 * signalValue);
				}
     
				float persentage = ( (float)colorAmplitude / 2048) * 100;
				if(persentage >= Spectrogram.COLOR_SCALAR)
				{
					colorSelecter(colorAmplitude);
					spectroPaint.setARGB(COLOR_RANGE - 1, red, green, blue);
					//scaleWidthMult  = scaleWidth  * offset;
					scaleHeightMult = scaleHeight * i;
					canvas.drawLine(startX , 
									(startY - scaleHeightMult), 
									stopX  , 
									(stopY  - scaleHeightMult), 
									spectroPaint);
				}
			}		
		}
				
		if(Spectrogram.KEEP_RUNNING_MUTUAL_FLAG)
		{
			drawBottomTimeIndicator();
		}
		
	}
	
	
	/**
	 * Automatically draw Time Indicators at the bottom
	 * Every 5 seconds
	 */
	public void drawBottomTimeIndicator()
	{
		String bottom_time = Spectrogram.KEEP_RUNNING_COUNT + "s";
		
		
			canvas.drawText( 	bottom_time,
      							global_x_location,
      							scrHeight - getRelativePaddingAxisY() + getRelativeGridTextSize(),
    		  		   	 		gridPaint );
		
	}

	
	/**
	 * this.scrWidth = width;<br>
		this.scrHeight = height;<br>
		Log.i("Surface Changed: ","Surface Changed: new width: "+width+" - new height: "+height);
	 */
	@Override
	public void surfaceChanged( SurfaceHolder holder, 
	                            int format, 
	                            int width,
	                            int height ) 
	{
		this.scrWidth = width;
		this.scrHeight = height;
		Log.i("Surface Changed: ","Surface Changed: new width: "+width+" - new height: "+height);
	}
	
	@Override
	public void surfaceCreated( SurfaceHolder holder ) 
	{
		Log.d("Surface Created: ","surfaceCreated");
		isSurfaceCreated = true;
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) 
	{
		isSurfaceCreated = false;
	}

	/**
	 * HSL color model. <br>
	 * Another formula works well. It'd call colorTC() for further calculating.
	 * @param degree
	 * @param light
	 */
	public void anotherColorHSLSelecter(int degree, double light)
	{
		int    hue 		  = degree;
		double saturation = 1;
		double lightness  = 0.6;
		double s		  = (double)0.333333333333333;
		double q;
			//if(lightness<0.5)
				q=lightness*(lightness+saturation);
			//else
				//q=lightness+saturation-(lightness*saturation);
		
		double p =2*lightness-q;
		double h = ( (double)hue ) /360;
		
		double tR=h+s;
			if(tR>1)
				tR=tR-1;
			this.red=colorTC(tR,p,q);
			
		double tG=h;
			this.green=colorTC(tG,p,q);
		
		double tB=h-s;
			if(tB<0)
				tB=tB+1;
			this.blue=colorTC(tB,p,q);
		
	}
		
	public int colorTC(double tC, double p, double q)
	{
		double s1=((double)1)/6;
		double s2=((double)1)/2;
		double s3=((double)2)/3;
		
		int value=0;
		
		if(tC < s1)
			value= (int) ( ( p+ (q-p) * 6 * tC ) *256 );
		if(tC>=s1 && tC<s2)
			value= (int) ( q * 256 );
		if(tC>=s2 && tC<s3)
			value= (int) ( ( p+ (q-p) * 6 * (s3-tC)  ) *256 );
		if(tC>=s3)
			value= (int) ( p * 256);
		
		return value;
	}
	
	
	/**
	 * HSL color model <br>
	 * Used to convert color to RGB in 360 degrees <br><br>
	 * Formula according to: http://en.wikipedia.org/wiki/HSL_and_HSV <br>
	 * Given an HSL color with hue H ¡Ê [0¡ã, 360¡ã), saturation SHSL ¡Ê [0, 1], and lightness L ¡Ê [0, 1]
	 */
	public void colorHSLSelecter( int degree, double l )
	{
		int    hue 		  = degree;
		double saturation = 1;
		double lightness  = l;
		double c		  = (1 - Math.abs(2 * lightness - 1) ) * saturation;
		double h 		  = hue / 60;
		double x      	  = c * ( 1 - Math.abs( (h % 2) - 1 ) );
		double m 		  = lightness - 0.5 * c;
		double r		  = 0,
			   g		  = 0,
			   b		  = 0;
		
		if( h >= 0 && h < 1 )
		{
			r = c;
			g = x;
			b = 0;			
		}
		
		if( h >= 1 && h < 2 )
		{
			r = x;
			g = c;
			b = 0;
		}
		
		if( h >= 2 && h < 3 )
		{
			r = 0;
			g = c;
			b = x;			
		}
		
		if( h >= 3 && h < 4 )
		{
			r = 0;
			g = x;
			b = c;			
		}
		
		if( h >= 4 && h < 5 )
		{
			r = x;
			g = 0;
			b = c;			
		}
		
		if( h >= 5 && h < 6 )
		{
			r = c;
			g = 0;
			b = x;		
		}
		
		this.red   = (int)( (r + m) * 256 );
		this.green = (int)( (g + m) * 256 );
		this.blue  = (int)( (b + m) * 256 );
		
	}
	
	
	
	/**
	 * Sets the color to be drawn on the bitmap next<br><br>
	 * this.red =  ;<br>
	 * this.green =  ;<br>
	 * this.blue =  ;<br>
	 * @param colorIterator
	 */
  final void colorSelecter(final int colorIterator) 
  {
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
   * set this.frequency = frequency(formal parameter)
   * @param frequency
   */
  public void setFrequency( double frequency )
  {
    this.frequency = frequency;
  }

  
  /**
   * Draw "Start Recording" button in an specific order
   */
  public void drawButtonStart()
  {
    drawTopRecordingButton( RENDERING_RECORDING_STR, 0xFF6A5ACD );//android.graphics.Color.BLACK
    drawTopRecordingButton( STOP_RECORDING_STR, 0xFF6A5ACD );
    drawTopRecordingButton( START_RECORDING_STR, android.graphics.Color.WHITE );
  }
  
  
  /**
   * Draw "Stop Recording" button in an specific order
   */
  public void drawButtonStop() 
  {
    drawTopRecordingButton( RENDERING_RECORDING_STR, 0xFF6A5ACD );
    drawTopRecordingButton( START_RECORDING_STR, 0xFF6A5ACD );
    drawTopRecordingButton( STOP_RECORDING_STR, android.graphics.Color.WHITE );
  }
  
  /**
   * Indicates the application is currently recording in the background
   * for future rendering. Placed in the middle of the screen.<br><br>
   * Draw "Recording in Background..." at exactly middle of the screen 
   * @param
   */
  private void drawRecordingIndicator( int color )
  {
    topBarPaint.setColor( color );
    
    for( int i = 0; i < 4; ++ i )
    {       
      canvas = surfaceHolder.lockCanvas();
      	canvas.drawText( RECORDING_INDICATOR_STR, 
    		  		   	 scrWidth/2 - topBarPaint.measureText( RECORDING_INDICATOR_STR )/2, 
    		  		   	 scrHeight/2, 
    		  		   	 topBarPaint );
      surfaceHolder.unlockCanvasAndPost( canvas );
    }
  }
  
  /**
   * call the practical drawRecordingIndicator( WHITE )<br>
   * Indicates the application is currently recording in the background for future rendering.
   */
  public void drawRecordingIndicator()
  {
    drawRecordingIndicator( android.graphics.Color.WHITE );
  }
  
  
  /**
   * call the practical drawRecordingIndicator( BLACK )<br>
   * Indicates the application is currently recording in the background for future rendering.
   */
  public void undrawRecordingIndicator()
  {
    drawRecordingIndicator( android.graphics.Color.BLACK );
  }
  
  /**
   * draw "Rendering Data..." as button at (5,19+5) by using topBarPaint<br>
   */
  public void drawButtonRendering()
  {
    drawTopRecordingButton( START_RECORDING_STR, 0xFF6A5ACD );
    drawTopRecordingButton( STOP_RECORDING_STR, 0xFF6A5ACD );
    drawTopRecordingButton( RENDERING_RECORDING_STR, android.graphics.Color.WHITE );
  }
  
  
  /**
   * draw "Waiting." as button at (5,19+5) by using topBarPaint<br>
   */
  public void drawButtonWaiting()
  {
    drawTopRecordingButton( START_RECORDING_STR, 0xFF6A5ACD );
    drawTopRecordingButton( STOP_RECORDING_STR, 0xFF6A5ACD );
    drawTopRecordingButton( "Waiting.", android.graphics.Color.WHITE );
  }
  
  
  /**
   * draw "Waiting.." as button at (5,19+5) by using topBarPaint<br>
   */
  public void drawButtonWaiting1()
  {
    drawTopRecordingButton( "Waiting..", android.graphics.Color.WHITE );
  }
  
  
  /**
   * draw "Waiting..." as button at (5,19+5) by using topBarPaint<br>
   */
  public void drawButtonWaiting2()
  {
    drawTopRecordingButton( "Waiting...", android.graphics.Color.WHITE );
  }
  
  
  /**
   * draw "Waiting...." as button at (5,19+5) by using topBarPaint<br>
   */
  public void drawButtonWaiting3()
  {
    drawTopRecordingButton( "Waiting....", android.graphics.Color.WHITE );
  }
  
  
  /**
   * draw "Waiting....." as button at (5,19+5) by using topBarPaint<br>
   */
  public void drawButtonWaiting4()
  {
    drawTopRecordingButton( "Waiting.....", android.graphics.Color.WHITE );
  }
  
  
  /**
   * draw a Text as button at (5,19+5) by using topBarPaint 
   * @param message
   * @param color
   */
  private void drawTopRecordingButton( String message, int color )
  {
    float textPadding  = getRelativeButtonPadding();
    float relativeSize = getRelativeTopBarSize();
    topBarPaint.setColor( color );
    for( int i = 0; i < 4; ++ i )
    {       
      canvas = surfaceHolder.lockCanvas();
      	canvas.drawText( message, textPadding, relativeSize + textPadding, topBarPaint );
      surfaceHolder.unlockCanvasAndPost( canvas );
    }
  }

  /**
   * set relativeTopBarSize = TOP_BAR_TEXT_SIZE = 19 DIP
   * @return relativeTopBarSize
   */
  public float getRelativeTopBarSize()
  {
    if( relativeTopBarSize == null )
    {
      relativeTopBarSize = TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, TOP_BAR_TEXT_SIZE, resources.getDisplayMetrics() );
    }
    return relativeTopBarSize;
  }

  
  /**
   * 
   * @return TOP_PADDING = 5 DIP in float
   */
  public float getRelativeButtonPadding()
  {
    return TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 
    								  TOP_PADDING, 
    								  resources.getDisplayMetrics() );
  }
  
  
  /**
   * set relativeGridTextSize = GRID_TEXT_SIZE =10 in DIP
   * @return relativeGridTextSize
   */
  private float getRelativeGridTextSize()
  {
    if( relativeGridTextSize == null )
    {
      relativeGridTextSize = TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 
    		  											GRID_TEXT_SIZE, 
    		  											resources.getDisplayMetrics() );
    }
    return relativeGridTextSize;
  }
  
  /**
   * set relativeColorGraphWidth = COLOR_GRAPH_WIDTH =5 DIP
   * @return relativeColorGraphWidth
   */
  private float getRelativeColorGraphWidth()
  {
    if( relativeColorGraphWidth == null )
    {
      relativeColorGraphWidth = TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 
    		  												COLOR_GRAPH_WIDTH, 
    		  												resources.getDisplayMetrics() );
    }
    return relativeColorGraphWidth;
  }
  
  
  /**
   * set relativePaddingAxisX = SIGNAL_PADDING_X_AXIS =25 DIP
   * @return relativePaddingAxisX
   */
  private float getRelativePaddingAxisX(){
    if( relativePaddingAxisX == null ){
      relativePaddingAxisX = TypedValue.applyDimension( 
        TypedValue.COMPLEX_UNIT_DIP, 
        SIGNAL_PADDING_X_AXIS, 
        resources.getDisplayMetrics() );
    }
    return relativePaddingAxisX;
  }
  
  
  /**
   * set relativePaddingAxisY = SIGNAL_PADDING_Y_AXIS = 20 DIP
   * @return relativePaddingAxisY
   */
  private float getRelativePaddingAxisY()
  {
    if( relativePaddingAxisY == null )
    {
      relativePaddingAxisY = TypedValue.applyDimension( 
        TypedValue.COMPLEX_UNIT_DIP, 
        SIGNAL_PADDING_Y_AXIS, 
        resources.getDisplayMetrics() );
    }
    return relativePaddingAxisY;
  }

  /**
   * Erases the canvas and sets it entirely to black in "4 times"
   */
  public void clearSprectrogramView() 
  {
    for( int i = 0; i < 4; ++ i )
    {       
      canvas = surfaceHolder.lockCanvas();
      	canvas.drawColor( Color.BLACK );
      surfaceHolder.unlockCanvasAndPost( canvas );
    }
  }

  /**
   * loop SpectrogramColumn signal_count times to draw a complete colored bitmap graph
   * @param signals
   * @param frequency
   * @param buffer_size
   * @param maxFFTSample
   * @param intervalLenth here this param = signal_count, and we can change it for other usage
   * @param signal_count
   */
  public void drawSpectrogramRange( double[][] signals, 
                                    double frequency,
                                    int buffer_size, 
                                    double maxFFTSample,
                                    int intervalLenth, 
                                    int signal_count )
  {
    //for( int j = 0; j < 4; ++ j )
    //{
      canvas = surfaceHolder.lockCanvas();
      
      		for( int i = signal_count - intervalLenth; i < signal_count; ++ i )
      		{
      			drawSpectrogramColumn( canvas, signals[i], maxFFTSample, i );
      		}
      	
      surfaceHolder.unlockCanvasAndPost(canvas);
    //}
  }

  
  /**
   * Draws labeled X & Y axes, and right-side Y-axis Color Graph by using:<br><br>
   * drawGridAmplitude( canvas );<br>
   * drawGridKHz( canvas ); // Y-axis<br>
   * drawGridTime( canvas ); // X-axis
   */
  public void drawGrid()
  {
	  
	  
    for( int i = 0; i < 4; i++ )
    {       
      canvas = surfaceHolder.lockCanvas();
      	drawGridAmplitude( canvas );
      	drawGridKHz( canvas );
      	
      	//drawGridTime( canvas );
      	float paddingX = getRelativePaddingAxisX();
        float paddingY = getRelativePaddingAxisY();
      	canvas.drawLine( 	paddingX,
	   						scrHeight - paddingY,
	   						MAX_SIGNALS_LENGTH * scaleWidth + paddingX + 5,
	   						scrHeight - paddingY,
	   						gridPaint);
      	
      	if( purplePaint == null )
        {
      		purplePaint = new Paint();
      		purplePaint.setARGB(255, 106, 90, 205);
      		//purplePaint.setColor( android.graphics.Color. );
      		purplePaint.setTextSize( getRelativeGridTextSize() );
        }
      	myRectF = new RectF(5, 5, getRelativeGridTextSize()*16, getRelativeGridTextSize()*3);
      	canvas.drawRect(myRectF, purplePaint);
      	       	
      surfaceHolder.unlockCanvasAndPost( canvas );
    }
  }

  /**
   * Displays the duration of a signal sample as a decimal value in the 
   * top right corner.
   * @param duration time in milliseconds
   */
  public void drawTopDuration( long duration )
  {
    float time = (float) duration / 1000;
    float textPadding = getRelativeButtonPadding();
    float relativeSize = getRelativeTopBarSize();
    Paint paint = getPaintTopBar();
    //topBarPaint.setColor( android.graphics.Color.WHITE );
    String durationMessage = "Time: " + String.format("%.2f", time) + "s ";
    	
    	for(int i=0;i<4;i++)
    	{       
    		canvas = surfaceHolder.lockCanvas();
    		canvas.drawText( durationMessage, 
    						 scrWidth - paint.measureText( durationMessage ) - textPadding, 
    						 relativeSize + textPadding, 
    						 paint );
    		surfaceHolder.unlockCanvasAndPost( canvas );
    	}
  }
  
  
  /**
   * initialize topBarPaint as a Paint, in WHITE,<br>
   * using getRelativeTopBarSize() to set paint TextSize as 19 DIP
   * @return topBarPaint
   */
  private Paint getPaintTopBar() 
  {
    //if( topBarPaint == null )
    //{
      topBarPaint = new Paint();
      topBarPaint.setColor( android.graphics.Color.WHITE );
      topBarPaint.setTextSize( getRelativeTopBarSize() );
    //}
    return topBarPaint;
  }
  
  
  /**
   * Initialize gridPaint as a Paint, in WHITE,<br>
   * Using getRelativeGridTextSize() to set paint TextSize as 10 DIP
   * @return gridPaint
   */
  private Paint getPaintGrid() 
  {
    if( gridPaint == null )
    {
      gridPaint = new Paint();
      gridPaint.setColor( android.graphics.Color.WHITE );
      gridPaint.setTextSize( getRelativeGridTextSize() );
    }
    return gridPaint;
  }

  /**
   * set this.surfaceHolder = surfaceHolder;
   * @param surfaceHolder
   */
  public void setSurfaceHolder( SurfaceHolder surfaceHolder )
  {
    this.surfaceHolder = surfaceHolder;
  }

  
  /**
   * Draws top X-axis centered "sample rate" text string in dark grey
   */
  public void drawTopSampleRate()
  {
    float textPadding = getRelativeButtonPadding();
    float relativeSize = getRelativeTopBarSize();
    topBarPaint.setColor( android.graphics.Color.GRAY );
    String sampleRateMessage = "Sample Rate: " + String.format("%.0f", frequency) + " Hz";
    for( int i = 0; i < 4 ; ++ i )
    {       
      canvas = surfaceHolder.lockCanvas();
      canvas.drawText( sampleRateMessage, 
    		  			scrWidth/2 - topBarPaint.measureText( sampleRateMessage )/2, 
    		  			relativeSize + textPadding, 
    		  			topBarPaint );
      surfaceHolder.unlockCanvasAndPost( canvas );
    }
  }
  
  
/**
 * set logScaling = scaling(formal parameter)
 * @param scaling
 */
  public void setLogScaling( boolean scaling )
  {
    logScaling = scaling;
  }

}   
