package edu.ucdenver.aprad.preferences;

import edu.ucdenver.aprad.R;
import edu.ucdenver.aprad.spectrogram.Spectrogram;
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
 ** @created 9 Mar 2015
 ** 
 ** @modified_by Kun Li
 ** @modified_date 6 May 2015
 **
 *****************************/

public class PreferencesTimeDomain extends Activity implements OnTouchListener, OnGestureListener{

	SharedPreferences sharedPreferences;
	EditText text1;
	EditText text2;
	SeekBar  seekBar;
	TextView description;
	
	GestureDetector mGestureDetector;
	private int verticalMinDistance = 20;  
	private int minVelocity         = 0;
	
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
	    super.onCreate( savedInstanceState );
	    setContentView( R.layout.pref_timedomain );
	    sharedPreferences = getSharedPreferences(Preferences.PREFS_NAME, 0);
	    mGestureDetector = new GestureDetector((OnGestureListener) this);
	    LinearLayout viewSnsLayout = (LinearLayout)findViewById(R.id.id_mySlideView2);    
	    viewSnsLayout.setOnTouchListener(this);    
	    viewSnsLayout.setLongClickable(true); 
	    
	    
	    text1 = (EditText) findViewById( R.id.input_yaxis );
	    text2 = (EditText) findViewById( R.id.input_framesrefresh );
	    
	    text1.setText(String.valueOf(RawSignalView.SIGNAL_RANGE));
		text2.setText(String.valueOf(AudioRecorder.SAMPLING_TIME_INTERVAL));
	    
	    seekBar=(SeekBar)findViewById(R.id.id_seekBar2);
	    description=(TextView)findViewById(R.id.id_textView5);
	    int intensityProgress = 101 - RawSignalView.EXPANDE_SCALAR;
	    seekBar.setProgress(intensityProgress);
	    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
	    	
	        @Override
	        public void onStopTrackingTouch(SeekBar seekBar) {
	            description.setText("Samples in number (0 to 512)");
	        }
	        
	        @Override
	        public void onStartTrackingTouch(SeekBar seekBar) {
	            description.setText("Start dragging...");
	        }
	        
	        @Override
	        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	            description.setText("The Progress: " + progress + "%");
	            if(progress == 0)
	            	progress = 1;
	            RawSignalView.EXPANDE_SCALAR = 101 - progress;  //y= -x + 101
	        }
	    });
	    
	    Button savePreferencesButton = (Button) findViewById(R.id.savePreferencesTimeDomainButton);
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
		String inputText1 = text1.getText().toString();
		String inputText2 = text2.getText().toString();
		if(inputText1.equals(""))
		{
			Toast toast = Toast.makeText(getApplicationContext(), "Missing Y-Axis Span!", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
		else 
			if(inputText2.equals(""))
			{
				Toast toast = Toast.makeText(getApplicationContext(), "Missing Frames Refresh Time!", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
			else
			{
				RawSignalView.SIGNAL_RANGE = Integer.parseInt(inputText1);
				AudioRecorder.SAMPLING_TIME_INTERVAL = Integer.parseInt(inputText2);
				
				new  AlertDialog.Builder(this)
				.setTitle("Message" ) 
				.setMessage("Settings Saved!" )  
				.setPositiveButton("Got it" ,  null )  
				.show();
			}
		
	}
	
	
	  @Override
	  public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {  
		  
		    if (e1.getX() - e2.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {  

		    	Intent intent = new Intent( this, PreferencesFrequencyDomain.class);
		    	intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				
		    } else if (e2.getX() - e1.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {  
		    	
		    	Intent intent = new Intent( this, Preferences.class);
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
