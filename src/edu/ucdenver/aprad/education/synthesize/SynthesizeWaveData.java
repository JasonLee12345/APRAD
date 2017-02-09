package edu.ucdenver.aprad.education.synthesize;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import android.widget.SeekBar;
import android.widget.TextView;
import edu.ucdenver.aprad.education.rebuilder.Option;
import edu.ucdenver.aprad.preferences.Preferences;
import edu.ucdenver.aprad.spectrogram.Spectrogram;
import edu.ucdenver.aprad.spectrum_analyzer.SpectrumAnalyzer;
import edu.ucdenver.aprad.spectrum_analyzer.SpectrumAnalyzerView;
import edu.ucdenver.aprad.tools.AudioRecorder;
import edu.ucdenver.aprad.tools.AudioRecorderListenerManipulate;

/**********************************************
 **
 **	@author Kun Li
 ** @created 21 Nov 2014
 **
 ** @modified_by Kun Li
 ** @modified_date 11 Dec 2014
 ** @modified_date 15 Mar 2015
 **
 **********************************************/

// TODO Replace with actual education system logic
public class SynthesizeWaveData extends Activity {
	
	private SynthesizeWaveDataView synVisualizer;
	private SurfaceHolder surfaceHolder;
	private SeekBar seekBar;
	
	public static double synF1 = (double)SynthesizeInput.f1;
	public static double synA1 = (double)SynthesizeInput.a1;
	public static double synF2 = (double)SynthesizeInput.f2;
	public static double synA2 = (double)SynthesizeInput.a2;
	public static double synF3 = (double)SynthesizeInput.f3;
	public static double synA3 = (double)SynthesizeInput.a3;
	
	public static double synF4 = (double)SynthesizeInput.f4;
	public static double synA4 = (double)SynthesizeInput.a4;
	public static double synF5 = (double)SynthesizeInput.f5;
	public static double synA5 = (double)SynthesizeInput.a5;
	public static double synF6 = (double)SynthesizeInput.f6;
	public static double synA6 = (double)SynthesizeInput.a6;
	
	/**
	 * It means how many ¦Ð you want to display on the whole screen. The initial value is 1.
	 * The period of the function is 1/f, thus if we want to display just one period,
	 * t on the screen should be from -1/2f to 1/2f (-1/2f < t < 1/2f).
	 * If we want a graph with 1 or up to 20 periods,
	 * t should be either (-1/2f < t < 1/2f) or (-10/f < t < 10/f).
	 * Therefore, according to our method,
	 * the HALF_RANGE should be from 1/2¦Ðf to 20/2¦Ðf (1/2¦Ðf < HALF_RANGE < 20/2¦Ðf).
	 */
	public static float HALF_RANGE = (float) 1;
	
	public static float MAX_F; //the frequency of minimal positive period
	public static float MIN_F; //the frequency of maximum positive period
	public static float BASE_MAX_F; //1/(2 ¦Ð MAX_F)
	/**
	 * @param MAGNITUDE = 50 * log(MAX_F / MIN_F) + 20 
	 * @param It is used for finding a reasonable good scaling function 
	 * for the magnitude of the quotient of 
	 * the max frequency and the min frequency.
	 * And it is used to decide the range of the signals
	 * you want to display on the screen.
	 * @param This simple demo model works reasonably well 
	 * when log(MAX_F / MIN_F) belongs to 1 to 3.
	 */
	public static double MAGNITUDE;

	/** 
	 * Called when the activity is first created. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.synthesize);
		
		// get the Surface view which is used to draw the spectrum.
		this.synVisualizer = (SynthesizeWaveDataView) findViewById(R.id.synSurfaceView);
		this.surfaceHolder = this.synVisualizer.getHolder();
		
		synF1 = (double)SynthesizeInput.f1;
		synA1 = (double)SynthesizeInput.a1;
		synF2 = (double)SynthesizeInput.f2;
		synA2 = (double)SynthesizeInput.a2;
		synF3 = (double)SynthesizeInput.f3;
		synA3 = (double)SynthesizeInput.a3;
		
		synF4 = (double)SynthesizeInput.f4;
		synA4 = (double)SynthesizeInput.a4;
		synF5 = (double)SynthesizeInput.f5;
		synA5 = (double)SynthesizeInput.a5;
		synF6 = (double)SynthesizeInput.f6;
		synA6 = (double)SynthesizeInput.a6;
		
		MAX_F = (float) ((synF1 > synF2)? synF1 :synF2);
	  	MAX_F = (float) ((MAX_F > synF3)? MAX_F :synF3);
	  	MAX_F = (float) ((MAX_F > synF4)? MAX_F :synF4);
	  	MAX_F = (float) ((MAX_F > synF5)? MAX_F :synF5);
	  	MAX_F = (float) ((MAX_F > synF6)? MAX_F :synF6);
	  	
	  	if(MAX_F != 0)
	  	{
	  		BASE_MAX_F = (float) ((float)1 / (2*SynthesizeWaveDataView.PI*MAX_F));
	  		HALF_RANGE = 5 * BASE_MAX_F; //the default setting, 5 "periods"
	  		
	  		List<Float>find_min = new ArrayList<Float>();
	  		find_min.add((float) synF1);
	  		find_min.add((float) synF2);
	  		find_min.add((float) synF3);
	  		find_min.add((float) synF4);
	  		find_min.add((float) synF5);
	  		find_min.add((float) synF6);
	  		Collections.sort(find_min);
	  		int i=0;
	  		while(find_min.get(i) == 0)
	  		{
	  			i++;
	  		}
	  		MIN_F = find_min.get(i);
	  		MAGNITUDE = 50 * ( Math.log( (MAX_F / MIN_F) ) ) + 20;
	  	}
	  	else
	  	{
	  		HALF_RANGE = (float) 1;
	  		MAGNITUDE = 1;
	  	}
	  	
	  	
		seekBar=(SeekBar)findViewById(R.id.id_seekBar3);
		/**
		 * You could change "39" to whatever value you want.
		 * But you'd better use 29, 39, 49, like these...
		 * It is the maximum period you want to display, e.g., 
		 * if you use 39, that means you want to display at most 40 periods 
		 * of the signals on the whole screen.
		 * It should be an integer.
		 */
		seekBar.setProgress((int) ((HALF_RANGE-BASE_MAX_F) / ( (float)MAGNITUDE*BASE_MAX_F) * 100));
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
	    	
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
	        
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
	        
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				HALF_RANGE = ((float)progress/100) * ( (float)MAGNITUDE*BASE_MAX_F ) + BASE_MAX_F;
			}
		});
	  
	}
	
	
	/*
	@Override
	protected void onResume()
	{
		super.onResume();
		updatePreferences();
	}
	
	private void updatePreferences(){
	  SharedPreferences sharedPreferences = getSharedPreferences( Preferences.PREFS_NAME, 0 );
	  AudioRecorderListenerManipulate.frequency = sharedPreferences.getFloat( Preferences.FREQUENCY, 8000.0f );
	}
	*/
	
	@Override
	protected void onPause()
	{
		super.onPause();
		synVisualizer.end();
	}
	
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}
}
