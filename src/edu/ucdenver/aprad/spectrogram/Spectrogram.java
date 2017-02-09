package edu.ucdenver.aprad.spectrogram;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.ucdenver.aprad.Education;
import edu.ucdenver.aprad.R;
import edu.ucdenver.aprad.R.string;
import edu.ucdenver.aprad.preferences.Preferences;
import edu.ucdenver.aprad.tools.AudioRecordListener;
import edu.ucdenver.aprad.tools.AudioRecorder;
import edu.ucdenver.aprad.tools.AudioRecorderListenerManipulate;
import edu.ucdenver.aprad.tools.FFT;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.Toast;

/**
 * The Spectrogram is used to map frequency and amplitude over time. This is
 * represented as frequency on the y axis, time on the x axis, amd amplitude
 * is the color of the point on the graph.
 * 
 * @author Kun Li
 * @author Dan Rolls
 * @author Michael Dewar
 * @author Robbie Perlstein
 */
/*****************************
 ** 
 ** @modified_by Kun Li
 ** @modified_date 20 Feb 2015
 **
 *****************************/

public class Spectrogram extends Activity implements AudioRecordListener 
{
	public static boolean 	KEEP_RUNNING 				= true;
	public static int 		KEEP_RUNNING_COUNT 			= 0;
	public static boolean 	KEEP_RUNNING_MUTUAL_FLAG;
	public static boolean 	COUNTED						=false;
	
	public static short[][] 	rawSignalsForFiles;
	public static int 			signal_countForFiles;
	public static long			end_timeForFiles;
	
	public static String		absolute_start_time;
	public static String		absolute_end_time;
	public static float			absolute_time_span;
	public static double 		absolute_frequency;
	
	private SpectrogramView    	spectrogramView;
	private AudioRecorder      	signalCapture;
	private FFT 				fft;
	
	private boolean            	stopped       = false;
		
	private double             	frequency     = 8000.0;
	private int                	buffer_size   = AudioRecorderListenerManipulate.NUM_SAMPLES;
	private boolean            	liveRender    = false;
	private boolean            	scaling       = true;
	
	private final int          	START_STOP_BUTTON_X_SCALAR  = 8;
	private final int          	START_STOP_BUTTON_Y_SCALAR  = 3;
	
	private short[][] 			rawSignals;
	private int 				signal_count;
	
	private boolean 			rendering = false;
	
	private long 				start_time;
	private long 				end_time;
	private boolean 			buttonPress = false;
	private boolean 			paused = false;
	
	/**
	 * It is a percentage of the magic n 
	 * that determine how you want to filter out noise colors.
	 */
	public static int 			COLOR_SCALAR = 10; 
	

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.spectrogram );
		
		this.spectrogramView = (SpectrogramView) findViewById( R.id.surfaceView );
		fft = new FFT( buffer_size );
	}
	
	/*
	Handler handler = new Handler() {

  	  @Override
	  		public void handleMessage(Message msg) {
	  			
	  			//saveProcessingDialog.dismiss();
	  			Toast.makeText(getApplicationContext(), "Your data have been saved!", 0).show();
	  		}
		
	  };
	 */
	
	
	/**
	 * Initialize AudioRecorder and register the AudioRecorder listener
	 */
	@Override
	protected void onResume()
	{
		super.onResume();
		updatePreferences();
		this.spectrogramView.setFrequency( frequency );
		this.spectrogramView.setLogScaling( scaling );
			if( !paused )
			{
				try {  
		            Thread.sleep(1900);
		        } catch (InterruptedException e) {  
		            //Log.e(TAG, "error : ", e);  
		        }
				
				beginDataCollection();
			} 
			else 
			{
				//onDrawableSampleAvailable( null );
			}
		//paused = false;
	}
	
	
	/**
	 * liveRender = sharedPreferences.getBoolean( Preferences.LIVE_RENDER, true );<br>
	 * frequency = sharedPreferences.getFloat( Preferences.FREQUENCY, 8000.0f );<br>
	 * scaling = sharedPreferences.getBoolean( Preferences.LOG_SCALING, true );
	 */
	 private void updatePreferences() 
	 {
	    SharedPreferences sharedPreferences = getSharedPreferences(Preferences.PREFS_NAME, 0);
	    liveRender = sharedPreferences.getBoolean( Preferences.LIVE_RENDER, true );
	    frequency = sharedPreferences.getFloat( Preferences.FREQUENCY, 8000.0f );
	    scaling = sharedPreferences.getBoolean( Preferences.LOG_SCALING, true );
	 }
	
	
   /**
	 * End the signal capture and unregister listener
	 */
	@Override
	protected void onPause()
	{
		super.onPause();
		
		end_time = System.currentTimeMillis();
		if(!COUNTED)
			spectrogramView.drawTopDuration( getElapsedTime() );
		
		
		spectrogramView.drawButtonWaiting();
		try {
            Thread.sleep(100); 
        } catch (InterruptedException e) {
            //Log.e(TAG, "error : ", e);  
        }
		spectrogramView.drawButtonWaiting1();
		try {
            Thread.sleep(50);  
        } catch (InterruptedException e) {
            //Log.e(TAG, "error : ", e);  
        }
		spectrogramView.drawButtonWaiting2();
		try {
            Thread.sleep(25);  
        } catch (InterruptedException e) {
            //Log.e(TAG, "error : ", e);  
        }
		spectrogramView.drawButtonWaiting3();
		try {
            Thread.sleep(25);  
        } catch (InterruptedException e) {
            //Log.e(TAG, "error : ", e);  
        }
		spectrogramView.drawButtonWaiting4();
		try {
            Thread.sleep(1400); 
        } catch (InterruptedException e) {
            //Log.e(TAG, "error : ", e);  
        }
		
		KEEP_RUNNING = false;
		this.signalCapture.end();	
		stopped = true;
		paused  = true;
	}
	
	/*
	@Override
	protected void onStop()
	{
		super.onStop();
		this.signalCapture.end();
	}
	
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		this.signalCapture.end();
	}
	*/
	
	
	@Override
  public boolean onTouchEvent(MotionEvent event)
  {
	  if( !buttonPress )
	  {
	    buttonPress = true;
  	  	if( !rendering )
  	  	{
  	  		if( event.getAction() == MotionEvent.ACTION_DOWN )
  	  		{
  	  			float relativeTextSize = spectrogramView.getRelativeTopBarSize();
  	  			
      	  		if( event.getX() <= relativeTextSize * START_STOP_BUTTON_X_SCALAR &&
      	  			event.getY() <= relativeTextSize * START_STOP_BUTTON_Y_SCALAR )
      	  		{
      	  			toggleRecord();
      	  			
      	  		}
  	  		} 
  	  	}
  	  	buttonPress = false;
	  }
	  return true;
  }
  
 
	/**
	 * call this method after pressing the left top button
	 */
	private void toggleRecord()
	{
		if( stopped )
		{				      
			spectrogramView.drawButtonWaiting();
			try {  
					Thread.sleep(500);
				} catch (InterruptedException e) {  
					//Log.e(TAG, "error : ", e);  
				}
			spectrogramView.drawButtonWaiting1();
			try {  
					Thread.sleep(500);
				} catch (InterruptedException e) {  
					//Log.e(TAG, "error : ", e);  
				}
			spectrogramView.drawButtonWaiting2();
			try {  
					Thread.sleep(400);
				} catch (InterruptedException e) {  
					//Log.e(TAG, "error : ", e); 
				}
			spectrogramView.drawButtonWaiting3();
			try {  
					Thread.sleep(300);
				} catch (InterruptedException e) {  
					//Log.e(TAG, "error : ", e);  
				}
			spectrogramView.drawButtonWaiting4();
			try {  
					Thread.sleep(250);
				} catch (InterruptedException e) {  
					//Log.e(TAG, "error : ", e);  
				}
			
			beginDataCollection();
		} 
		else 
		{			
			stopDataCollection();
			
			if( stopped ){
				toDo();
			}		 
			
			renderCollectedData();
		}
	}
	
	/**
	 * convert collected rawSignals to static variables
	 * and for the use of saving them in the files
	 */
	public void changeRawSignalsToStatic()
	{
		rawSignalsForFiles = rawSignals;
		signal_countForFiles = signal_count;
		end_timeForFiles = end_time;
	}

	
	/**
	 * Return system time
	 * Default format: yyyy-mm-dd hh:mm:ss
	 */
	public static String getCurrentTime()
	{ 
		String returnStr = null;
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		returnStr = f.format(date);
		return returnStr;
	}
	
	
	/**
	 * Set stopped =false<br>
	 * Initialize a new AudioRecorder instance--signalCapture<br>
	 * A new short[720]--rawSignals<br>
	 * Signal_count=0<br>
	 * Capture start_time in millis
	 */
	public void beginDataCollection()
	{
		KEEP_RUNNING = true;
		KEEP_RUNNING_COUNT = 0;
		
	  stopped = false;
	  Spectrogram.absolute_start_time = getCurrentTime();
	  start_time = System.currentTimeMillis();
	  this.signalCapture = new AudioRecorder(frequency,buffer_size, this );
	  //AudioRecorderListenerManipulate.registerFFTAvailListenerOnSpectrogram(this);
	  rawSignals = new short[SpectrogramView.MAX_SIGNALS_LENGTH][];
	  signal_count = 0;
	  
	  //////////////////////////////////////////////////////////////////
	  new Thread(new Runnable() {
		  
	      @Override  
	      public void run() 
	      { 
	    	  
	  		while(KEEP_RUNNING)
	  		{
	  			//spectrogramView.drawBottomTimeIndicator();
	  			KEEP_RUNNING_MUTUAL_FLAG = true;
	  			
	  			try {  
	  	            Thread.sleep(100);
	  	        } catch (InterruptedException e) {  
	  	        	return;
	  	            //Log.e(TAG, "error : ", e);  
	  	        } 
	  	        
	  			KEEP_RUNNING_COUNT += 5;
	  			KEEP_RUNNING_MUTUAL_FLAG = false;
	  			
	  			try {  
	  	            Thread.sleep(4900);
	  	        } catch (InterruptedException e) {  
	  	        	return;
	  	            //Log.e(TAG, "error : ", e);  
	  	        } 
	  				  			
	  		}
	      		
	      }
	      	
	  }).start();
	  //////////////////////////////////////////////////////////////////
	  
	}
  
	
	/**
	 * Capture end_time in millis<br>
	 * KEEP_RUNNING = false<br>
	 * SignalCapture.end()<br>
	 * Set stopped = true
	 */
	public void stopDataCollection()
	{
	  end_time = System.currentTimeMillis();
	  
	  KEEP_RUNNING = false;
	  
	  Spectrogram.absolute_end_time = getCurrentTime();
	  this.signalCapture.end();
	  stopped = true;
	  
	}
	
	
	/**
	 * spectrogramView.undrawRecordingIndicator();<br>
	 * spectrogramView.drawButtonRendering();<br>
	 * drawSpectrogramRange();
	 */
	public void renderCollectedData()
	{
	  rendering = true;
	  if( !liveRender )
	  {
		  spectrogramView.undrawRecordingIndicator();
		  spectrogramView.drawButtonRendering();
		  drawSpectrogramRange();
		  spectrogramView.drawButtonStart();
	  }
	  rendering = false;
	}
	
	
  private void redrawData() {
    // TODO Auto-generated method stub
    
  }
  
	
	public void saveDataDialog()
	{
	  DialogFragment newFragment = new NewSaveDialog();
	  newFragment.show(getFragmentManager(), "Whatever?");
	}
	
	
	/**
	 * Implements AudioRecorder listener. <br>
	 * Draws a new sample on the graph.
	 */
	@Override
	public void onDrawableSampleAvailable( short[] signal ) 
	{
		  loadSpectrogramGrid();
		  storeNextSignal( signal );
		  testLimitation();
		
		  // Render signal live
		  if( liveRender )
		  {
		    drawSpectrogram( signal_count - 1 );
		  // Record then render signal
		  } else if( recordingComplete() ){
			  renderCollectedData();
		  }
	}
	
	
	/**
	 * 
	 * @return end_time - start_time
	 */
	private long getElapsedTime() 
	{
		COUNTED = true;
		return end_time - start_time;
	}

  
	/**
	 * Tests to see :<br><br>
	 * if signal_count >= 720 ? <br>
	 * stopDataCollection(); -->signal_capture.end();
	 */
	public void testLimitation(){
		
		if( signal_count == SpectrogramView.MAX_SIGNALS_LENGTH ){
			
			stopDataCollection();
			toDo();
		}
	}
	
	
	public void toDo(){
		
		spectrogramView.drawTopDuration( getElapsedTime() );
		absolute_time_span = (float) getElapsedTime() / 1000;
		absolute_frequency = frequency;
		spectrogramView.drawButtonStart();
		changeRawSignalsToStatic();
		
		saveDataDialog();
		/*
		AlertDialog.Builder builder = new AlertDialog.Builder(Spectrogram.this);
  		builder.setTitle("Reminder").setMessage("Preparing your data...")
  				.setPositiveButton("Go for saving them!", new DialogInterface.OnClickListener() 	    		  
		{
           public void onClick(DialogInterface dialog, int id) 
           {  
        	   try {  
   	 					Thread.sleep(600);
   	 				} catch (InterruptedException e) {  
   	 					//Log.e(TAG, "error : ", e);  
   	 				}
        	   saveDataDialog();
           }
        }).show();
        */
		
	}

	
  /**
   * Store raw signals in short[][] rawSignals<br><br>
   * rawSignals[signal_count] = signal;<br>
    	++ signal_count;
   * @param signal short[]
   */
  public void storeNextSignal( short[] signal )
  {
    if( signal == null )
    {
      return;
    }
    rawSignals[signal_count] = signal;
    signal_count++;
  }

  
  /**
   * Clear the graph, and redraw Axis<br><br>
   * if( signal_count == 0 )<br>
      spectrogramView.loadScaleValues();<br>
      spectrogramView.clearSprectrogramView();<br>
      spectrogramView.drawGrid();<br>
      spectrogramView.drawButtonStop();<br>
      spectrogramView.drawTopSampleRate();
   */
  public void loadSpectrogramGrid()
  {
    if( signal_count == 0 )
    {
    	spectrogramView.loadScaleValues();
	    spectrogramView.clearSprectrogramView();
	    spectrogramView.drawGrid();
	    spectrogramView.drawButtonStop();
	    spectrogramView.drawTopSampleRate();
	    if( !liveRender )
	    {
	      spectrogramView.drawRecordingIndicator();
	    }
    }
  }

  /**
   * Draw the current(the last) signals size of 512 in one column.<br><br>
   * Call "spectrogramView.drawSpectrogram( double[] transformedSignals, ... ,offset )"<br>
   * That will call "spectrogramView.drawSpectrogramColumn()"
   * @param offset = signal_count-1, is the end location of collected signals
   */
  private void drawSpectrogram( int offset )
  {
    double[] signal = fft.calculateFFT( rawSignals[offset] );
    spectrogramView.drawSpectrogram(  signal, 
    								  frequency, 
    								  buffer_size, 
    								  signalCapture.getMaxFFTSample(),
    								  offset );
  }
  
  
  /**
   * Call spectrogramView.drawSpectrogramRange
   * ( double[][] transformedSignals, ... , signal_count, signal_count )
   */
  private void drawSpectrogramRange()
  {
    double[][] signals = calculateRawSignalsFFT();
    spectrogramView.drawSpectrogramRange( signals,
    									  frequency,
    									  buffer_size,
    									  signalCapture.getMaxFFTSample(),
    									  signal_count,
    									  signal_count );
  }

  /**
   * for i=0 to signal_count<br>
   * signals[i] = fft.calculateFFT( rawSignals[i] )
   * @return double[][] signals
   */
  private double[][] calculateRawSignalsFFT()
  {
    double[][] signals = new double[SpectrogramView.MAX_SIGNALS_LENGTH][];
    for( int i = 0; i < signal_count; ++ i )
    {
      signals[i] = fft.calculateFFT( rawSignals[i] );
    }
    return signals;
  }

  
  /**
   * 
   * @return whether signal_count = 720 ?
   */
  public boolean recordingComplete()
  {
    return (signal_count == SpectrogramView.MAX_SIGNALS_LENGTH);
  }
}
