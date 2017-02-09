package edu.ucdenver.aprad.spectrum_analyzer;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import edu.ucdenver.aprad.R;
import edu.ucdenver.aprad.preferences.Preferences;
import edu.ucdenver.aprad.spectrogram.Spectrogram;
import edu.ucdenver.aprad.tools.AudioRecordListener;
import edu.ucdenver.aprad.tools.AudioRecorder;
import edu.ucdenver.aprad.tools.FFT;
import edu.ucdenver.aprad.tools.AudioRecorderListenerManipulate;

/**
 * SpectrumAnalyzer main class which extends Activity
 * @author Dan Rolls
 * @author Michael Dewar
 */
/*****************************
 ** 
 ** @modified_by Kun Li
 ** @modified_date 8 Dec 2014
 **
 *****************************/

public class SpectrumAnalyzer extends Activity  
{
	
	private SpectrumAnalyzerView spectrumVisualizer;	
	private SurfaceHolder surfaceHolder;
	
	private AudioRecorder signalCapture;


	/** 
	 * Called when the activity is first created. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spectrum_analyzer);
		
		// get the Surface view which is used to draw the spectrum.
		this.spectrumVisualizer = (SpectrumAnalyzerView) findViewById(R.id.surfaceView);
		this.surfaceHolder = this.spectrumVisualizer.getHolder();

	}
	
	/**
	 * Initialize AudioRecorder and register the AudioRecorder listener
	 */
	@Override
	protected void onResume()
	{
		super.onResume();
		updatePreferences();
		this.signalCapture = new AudioRecorder( AudioRecorderListenerManipulate.frequency, 
												AudioRecorderListenerManipulate.NUM_SAMPLES,
												spectrumVisualizer);
		//AudioRecorderListenerManipulate.registerFFTAvailListener(this);
	}
	
	private void updatePreferences(){
	  SharedPreferences sharedPreferences = getSharedPreferences( Preferences.PREFS_NAME, 0 );
	  AudioRecorderListenerManipulate.frequency = sharedPreferences.getFloat( Preferences.FREQUENCY, 8000.0f );
	}

  /**
	 * End the signal capture and unregister listener
	 */
	@Override
	protected void onPause()
	{
		super.onPause();
		this.signalCapture.end();
		//AudioRecorderListenerManipulate.unregisterFFTAvailListener(this);
	}
}
