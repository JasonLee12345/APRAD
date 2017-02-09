package edu.ucdenver.aprad.education.rebuilder.td;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.widget.SeekBar;
import edu.ucdenver.aprad.R;
import edu.ucdenver.aprad.education.rebuilder.ReadSavedFileData;
import edu.ucdenver.aprad.tools.AudioRecorderListenerManipulate;
import edu.ucdenver.aprad.tools.FFT;

/*****************************
 **
 ** @author Kun Li
 ** @created 7 Apr 2015
 ** 
 ** @modified_by 
 ** @modified_date 
 **
 *****************************/

public class RebuilderTD extends Activity {
	
	private RebuilderTDView 	rebuilderView;	
	private SurfaceHolder 		surfaceHolder;
	
	private SeekBar seekBar;
	private SeekBar seekBar1;
	
	public static int 			numberOfFFTPoints;
    public static double 		samplingRate; //frequency, default value is 8000.0
    
    
	/** 
	 * Called when the activity is first created. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rebuilder_td_view);
		
		// get the Surface view which is used to draw the spectrum.
		this.rebuilderView = (RebuilderTDView) findViewById(R.id.reBTDsurfaceView);
		this.surfaceHolder = this.rebuilderView.getHolder();

		numberOfFFTPoints = ReadSavedFileData.theLen;
		samplingRate = ReadSavedFileData.theFrequency;
		
		seekBar=(SeekBar)findViewById(R.id.id_seekBar4);
		seekBar.setProgress( (int)(((float)RebuilderTDView.startPosition/RebuilderTDView.endPosition)*100) );
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
	    	
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
	        
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
	        
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				RebuilderTDView.startPosition = (int)(((float)progress/100) * RebuilderTDView.endPosition);
			}
		});
		
		seekBar1=(SeekBar)findViewById(R.id.id_seekBar5);
		seekBar1.setProgress( (int)((Logarithm(RebuilderTDView.scalePoint, (double)10)+4)*((float)100/8)) );
		seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
	    	
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
	        
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
	        
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				RebuilderTDView.scalePoint = (float)Math.pow((double)10, (double)progress*8/100 - 4);
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
