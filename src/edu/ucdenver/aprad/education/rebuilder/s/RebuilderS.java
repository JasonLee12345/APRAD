package edu.ucdenver.aprad.education.rebuilder.s;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.widget.SeekBar;
import edu.ucdenver.aprad.R;
import edu.ucdenver.aprad.education.rebuilder.ReadSavedFileData;
import edu.ucdenver.aprad.education.rebuilder.td.RebuilderTDView;
import edu.ucdenver.aprad.tools.AudioRecorderListenerManipulate;
import edu.ucdenver.aprad.tools.FFT;

/*****************************
 **
 ** @author Kun Li
 ** @created 6 Apr 2015
 ** 
 ** @modified_by 
 ** @modified_date 
 **
 *****************************/

public class RebuilderS extends Activity {
	
	private RebuilderSView 		rebuilderView;	
	private SurfaceHolder 		surfaceHolder;
	
    public static double 		samplingRate; //frequency, default value is 8000.0
    public static double 		timeSpan;
    public static double[][] 	transformedSignal;
    public static final int 	sigInterval = 512;
    
    
	/** 
	 * Called when the activity is first created. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rebuilder_s_view);
		
		// get the Surface view which is used to draw the spectrum.
		this.rebuilderView = (RebuilderSView) findViewById(R.id.reBSsurfaceView);
		this.surfaceHolder = this.rebuilderView.getHolder();

		samplingRate = ReadSavedFileData.theFrequency;
		timeSpan = ReadSavedFileData.theTimeSpan;
		transformedSignal = transToSpectrogram(ReadSavedFileData.actualData, sigInterval);
	}
	
	
	/**
	 * End the signal capture and unregister listener
	 */
	@Override
	protected void onPause() {
		super.onPause();
		rebuilderView.end();
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	
	public double Logarithm(double value, double base) {
		return Math.log(value) / Math.log(base);
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
