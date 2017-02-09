package edu.ucdenver.aprad.preferences;

import edu.ucdenver.aprad.R;
import edu.ucdenver.aprad.spectrogram.Spectrogram;
import edu.ucdenver.aprad.spectrum_analyzer.SpectrumAnalyzerView;
import edu.ucdenver.aprad.time_domain.RawSignalView;
import edu.ucdenver.aprad.tools.AudioRecorder;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/*****************************
 **
 ** @author Kun Li
 ** @created 6 May 2015
 ** 
 ** @modified_by 
 ** @modified_date 
 **
 *****************************/

public class PreferencesFrequencyDomain extends Activity implements OnTouchListener, OnGestureListener{

	SharedPreferences sharedPreferences;
	SeekBar  seekBar;
	TextView description;
	
	GestureDetector mGestureDetector;
	private int verticalMinDistance = 20;  
	private int minVelocity         = 0;
	
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.pref_frequencydomain);
	    sharedPreferences = getSharedPreferences(Preferences.PREFS_NAME, 0);
	    mGestureDetector = new GestureDetector((OnGestureListener) this);
	    LinearLayout viewSnsLayout = (LinearLayout)findViewById(R.id.id_mySlideView3);    
	    viewSnsLayout.setOnTouchListener(this);    
	    viewSnsLayout.setLongClickable(true); 
	    
	    seekBar=(SeekBar)findViewById(R.id.id_frequency_seekbar);
	    description=(TextView)findViewById(R.id.id_textview_amplify);
	    int ampliProgress;
	    if(SpectrumAnalyzerView.amplify_coeft >= 0.05 && SpectrumAnalyzerView.amplify_coeft <= 1) {
	    	ampliProgress = (int)((SpectrumAnalyzerView.amplify_coeft - 0.05) * 50 / 0.95);
	    }else {
	    	ampliProgress = (int)((SpectrumAnalyzerView.amplify_coeft - 1) * 50 / 19 + 50);
	    }
	    seekBar.setProgress(ampliProgress);
	    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
	    	
	        @Override
	        public void onStopTrackingTouch(SeekBar seekBar) {
	            description.setText("Amplification Coefficient");
	        }
	        
	        @Override
	        public void onStartTrackingTouch(SeekBar seekBar) {
	            description.setText("Start dragging...");
	        }
	        
	        @Override
	        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	            description.setText("Amplification Coefficient: " +  String.format("%.2f", SpectrumAnalyzerView.amplify_coeft));
	            if(progress >=0 && progress <= 50) {
	            	SpectrumAnalyzerView.amplify_coeft = 0.05 + ((double)progress / 50) * 0.95;
	            }else {
	            	SpectrumAnalyzerView.amplify_coeft = 1 + (((double)progress-50) / 50) * 19;
	            }
	            
	        }
	    });
	    
	    Button savePreferencesButton = (Button) findViewById(R.id.savePreferencesFrequencyDomainButton);
	    savePreferencesButton.setOnClickListener( 
	    											new OnClickListener() 
	    											{
	    												@Override
	    												public void onClick( View v )
	    												{
	    													savePreferences();
	    												}
	    											}
	    										);
	}
	
	
	private void savePreferences() 
	{
		
		new  AlertDialog.Builder(this)
						.setTitle("Message" ) 
						.setMessage("Settings Saved!" )  
						.setPositiveButton("Got it" ,  null )  
						.show();
	}
	
	
	  @Override
	  public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {  
		  
		    if (e1.getX() - e2.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {  

		    	Intent intent = new Intent( this, PreferencesSpectrogram.class);
		    	intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				
		    } else if (e2.getX() - e1.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {  
		    	
		    	Intent intent = new Intent( this, PreferencesTimeDomain.class);
		    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
		    }  
		  
		    return false;  
		} 
	  

	  	@Override
	  	public boolean onDown(MotionEvent e) {
	  		// TODO Auto-generated method stub
	  		return false;
	  	}


		@Override
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub
		
		}


		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}


		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			// TODO Auto-generated method stub
			return false;
		}


		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub
		
		}
		
		
	   public boolean onTouch(View v, MotionEvent event) {  
		   return mGestureDetector.onTouchEvent(event);  
	   }
	
}
