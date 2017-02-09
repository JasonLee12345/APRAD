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
import edu.ucdenver.aprad.education.rebuilder.ReadSavedFileData;
import edu.ucdenver.aprad.education.rebuilder.fd.RebuilderFDView;
import edu.ucdenver.aprad.preferences.Preferences;
import edu.ucdenver.aprad.spectrogram.Spectrogram;
import edu.ucdenver.aprad.spectrum_analyzer.SpectrumAnalyzer;
import edu.ucdenver.aprad.spectrum_analyzer.SpectrumAnalyzerView;
import edu.ucdenver.aprad.tools.AudioRecorder;
import edu.ucdenver.aprad.tools.AudioRecorderListenerManipulate;
import edu.ucdenver.aprad.tools.FFT;

/*****************************
 **
 ** @author Kun Li
 ** @created 7 Apr 2015
 ** 
 ** @modified_by Kun Li
 ** @modified_date 8 Apr 2015
 **
 *****************************/

public class SynWaveDataS extends Activity {
	
	public static final double 	PI	= (double) 3.1415926535898;
	
	private SynWaveDataSView synVisualizer;
	private SurfaceHolder surfaceHolder;
	
	public static double 		samplingRate; //frequency, default value is 8000.0
    public static double 		timeSpan;
	public short[] rawSigs;
    public static double[][] 	transformedSignal;
    public static final int 	sigInterval = 512;
    
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
	
	public static float MAX_F; //the frequency of minimal positive period
	public static float MIN_F; //the frequency of maximum positive period
	
	
	/**
	 * It is the number of samples which are taken into account.
	 * But the accuracy and error deviation depends on the width of the device screen.
	 * The program just takes screen width number of actual samples from
	 * "stepValue" number of samples.
	 */
	public final int stepValue = 49152; //it should be a multiple of 512
	
	
	/** 
	 * Called when the activity is first created. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.synthesize_s);
		
		this.synVisualizer = (SynWaveDataSView) findViewById(R.id.synSurfaceViewS);
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
	  	
	  	if(MAX_F != 0) {
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
	  		
	  		rawSigs = new short[stepValue];
	  		int j = 0;
	  		for(double t=0.0f; j<stepValue; t+=(double)1/(MIN_F*10)) {
	  			rawSigs[j] = (short)(
	  						synA1 * myCosSin(SynthesizeInput.firstSwitch, synF1, t) + 
	  						synA2 * myCosSin(SynthesizeInput.secondSwitch, synF2, t) + 
	  						synA3 * myCosSin(SynthesizeInput.thirdSwitch, synF3, t) + 
	  						synA4 * myCosSin(SynthesizeInput.fourthSwitch, synF4, t) + 
	  						synA5 * myCosSin(SynthesizeInput.fifthSwitch, synF5, t) + 
	  						synA6 * myCosSin(SynthesizeInput.sixthSwitch, synF6, t));
	  			j++;
	  		}
	  	}else {
	  		rawSigs = new short[stepValue];
	  		for(int j=0; j<stepValue; j++) {
	  			rawSigs[j] = 1;
	  		}
	  	}
	  	
	  	samplingRate = MAX_F;
		//timeSpan = ReadSavedFileData.theTimeSpan;
		transformedSignal = transToSpectrogram(rawSigs, sigInterval);
		
		seekBar=(SeekBar)findViewById(R.id.id_seekBar8);
		seekBar.setProgress( (int)((Logarithm(SynWaveDataSView.scalePoint, (double)10)+4)*((float)100/8)) );
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
	    	
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
	        
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
	        
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				SynWaveDataSView.scalePoint = (float)Math.pow((double)10, (double)progress*8/100 - 4);
			}
		});
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		synVisualizer.end();
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	
	public double Logarithm(double value, double base) {
		return Math.log(value) / Math.log(base);
	}
	
	
	/**
	 * @param cosSin either Cos() or Sin() function could be used by the method 
	 * @param f the frequency of the function,the original function is Cos(2 PI f t)
	 * @param t the parameter for any instant of time 
	 * @return the outcome value of the function
	 */
	public double myCosSin(boolean cosSin, double f, double t) {
		if(!cosSin) {
			double cosInnerValue = 2*PI*f*t;
			double sCosInnerValue = cosInnerValue;
			if(sCosInnerValue < 0) {
				while( sCosInnerValue < (-2*PI) )
				{
					sCosInnerValue += 2*PI;
				}	
			}
			else {
				while( sCosInnerValue > (2*PI) )
				{
					sCosInnerValue -= 2*PI;
				}	
			}
			double cosValue		 = Math.cos(sCosInnerValue);
			return cosValue;
		}
		else {
			double sinInnerValue = 2*PI*f*t;
			double sSinInnerValue = sinInnerValue;
			if(sSinInnerValue < 0) {
				while( sSinInnerValue < (-2*PI) )
				{
					sSinInnerValue += 2*PI;
				}	
			}
			else {
				while( sSinInnerValue > (2*PI) )
				{
					sSinInnerValue -= 2*PI;
				}	
			}
			double sinValue		 = Math.sin(sSinInnerValue);
			return sinValue;
		}
	}
	
	
	public double[][] transToSpectrogram(short[] sigs, int interval) {
		short cuttedRawSigs[] = new short[interval];
		
		int iteration = sigs.length / interval;
		double[][] transInnerSigs = new double[iteration][];
		
		FFT fft = new FFT(interval);
		for(int i=0; i<iteration; i++) {
			for(int j=0; j<interval; j++) {
				cuttedRawSigs[j] = sigs[j+interval*i];
			}
			transInnerSigs[i] = fft.calculateFFT(cuttedRawSigs);
		}
		return transInnerSigs;
	}
}
