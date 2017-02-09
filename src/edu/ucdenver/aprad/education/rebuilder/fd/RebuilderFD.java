package edu.ucdenver.aprad.education.rebuilder.fd;

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
 ** @created 5 Apr 2015
 ** 
 ** @modified_by 
 ** @modified_date 
 **
 *****************************/

public class RebuilderFD extends Activity {
	
	private RebuilderFDView 	rebuilderView;	
	private SurfaceHolder 		surfaceHolder;
	
	public static int 			numberOfFFTPoints;
    public static double 		samplingRate; //frequency, default value is 8000.0
    public static double[] 		transformedSignal;
    
    private SeekBar seekBar;
    
	/** 
	 * Called when the activity is first created. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rebuilder_fd_view);
		
		// get the Surface view which is used to draw the spectrum.
		this.rebuilderView = (RebuilderFDView) findViewById(R.id.reBFDsurfaceView);
		this.surfaceHolder = this.rebuilderView.getHolder();

		numberOfFFTPoints = ReadSavedFileData.theLen;
		samplingRate = ReadSavedFileData.theFrequency;
		
		FFT fft = new FFT( numberOfFFTPoints );
		transformedSignal = fft.calculateFFT( ReadSavedFileData.actualData );
		
		seekBar=(SeekBar)findViewById(R.id.id_seekBar6);
		seekBar.setProgress( (int)((Logarithm(RebuilderFDView.scalePoint, (double)10)+4)*((float)100/8)) );
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
	    	
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
	        
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
	        
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				RebuilderFDView.scalePoint = (float)Math.pow((double)10, (double)progress*8/100 - 4);
			}
		});
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
}
