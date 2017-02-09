package edu.ucdenver.aprad.time_domain;

import java.io.FileOutputStream;
import java.io.IOException;

import edu.ucdenver.aprad.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.widget.EditText;
import edu.ucdenver.aprad.preferences.Preferences;
import edu.ucdenver.aprad.spectrogram.Spectrogram;
import edu.ucdenver.aprad.spectrum_analyzer.SpectrumAnalyzer;
import edu.ucdenver.aprad.spectrum_analyzer.SpectrumAnalyzerView;
import edu.ucdenver.aprad.tools.AudioRecorder;
import edu.ucdenver.aprad.tools.AudioRecorderListenerManipulate;

/*****************************
 **
 ** @author Kun Li
 ** @created 11 Dec 2014
 ** 
 ** @modified_by 
 ** @modified_date 
 **
 *****************************/

// TODO Replace with actual education system logic
public class RawSignal extends Activity {
	
	private RawSignalView rawVisualizer;
	private SurfaceHolder surfaceHolder;
	private AudioRecorder signalCapture;

	/** 
	 * Called when the activity is first created. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.time_domain);

		// get the Surface view which is used to draw the spectrum.
		this.rawVisualizer = (RawSignalView) findViewById(R.id.time_domainSurfaceView);
		this.surfaceHolder = this.rawVisualizer.getHolder();

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
												rawVisualizer);
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
