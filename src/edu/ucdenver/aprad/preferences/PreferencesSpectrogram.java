package edu.ucdenver.aprad.preferences;

import edu.ucdenver.aprad.R;
import edu.ucdenver.aprad.spectrogram.Spectrogram;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

/*****************************
 **
 ** @author Kun Li
 ** @created 9 Mar 2015
 ** 
 ** @modified_by Kun Li
 ** @modified_date 6 May 2015
 **
 *****************************/

public class PreferencesSpectrogram extends Activity implements OnTouchListener, OnGestureListener{
	
	  private Spinner liveRenderSpinner;
	  private Spinner displayScalingSpinner;
	  private Button savePreferencesButton;
	  private SeekBar seekBar;
	  private TextView description;
	  
	  SharedPreferences sharedPreferences;
	  GestureDetector mGestureDetector;
	  private int verticalMinDistance = 20;  
	  private int minVelocity         = 0;
	  
	  @Override
	  public void onCreate( Bundle savedInstanceState )
	  {
	    super.onCreate( savedInstanceState );
	    setContentView( R.layout.pref_spectrogram );
	    sharedPreferences = getSharedPreferences(Preferences.PREFS_NAME, 0);
	    mGestureDetector = new GestureDetector((OnGestureListener) this);
	    LinearLayout viewSnsLayout = (LinearLayout)findViewById(R.id.id_mySlideView1);    
	    viewSnsLayout.setOnTouchListener(this);    
	    viewSnsLayout.setLongClickable(true); 
	    
	    
	    liveRenderSpinner = (Spinner) findViewById( R.id.liveRenderSpinner );
	    setLiveRenderSpinner();
	    
	    displayScalingSpinner = (Spinner) findViewById( R.id.displayScalingSpinner );
	    setDisplayScalingSpinner();
	    
	    savePreferencesButton = (Button) findViewById(R.id.savePreferencesSpectrogramButton);
	    savePreferencesButton.setOnClickListener( 
	    											new OnClickListener() 
	    											{
	    												@Override
	    												public void onClick( View v )
	    												{
	    													saveSharedPreferences();
	    													
	    													new  AlertDialog.Builder(v.getContext())
	    													.setTitle("Message" ) 
	    													.setMessage("Settings Saved!" )  
	    													.setPositiveButton("Got it" ,  null )  
	    													.show();
	    												}
	    											}
	    										);
	    
	    seekBar=(SeekBar)findViewById(R.id.seekBar1);
	    description=(TextView)findViewById(R.id.textView5);
	    seekBar.setProgress(Spectrogram.COLOR_SCALAR);
	    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
	    	
	        @Override
	        public void onStopTrackingTouch(SeekBar seekBar) {
	            description.setText("Displaying Scalar");
	        }
	        
	        @Override
	        public void onStartTrackingTouch(SeekBar seekBar) {
	            description.setText("Start dragging...");
	        }
	        
	        @Override
	        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	            description.setText("The Progress: " + progress + "%");
	            Spectrogram.COLOR_SCALAR = progress;
	        }
	    });
	    
	  }
	  
	  
	  private void setDisplayScalingSpinner() 
	  {
	    displayScalingSpinner.setSelection( (sharedPreferences.getBoolean( Preferences.LOG_SCALING, true )) ? 0 : 1 );
	  }

	  
	  private void setLiveRenderSpinner()
	  {
	    liveRenderSpinner.setSelection( (sharedPreferences.getBoolean( Preferences.LIVE_RENDER, true )) ? 0 : 1 );
	  }

	  
	  private void saveSharedPreferences() 
	  {
	    SharedPreferences.Editor editor = sharedPreferences.edit();
	    editor.putBoolean( Preferences.LIVE_RENDER, (liveRenderSpinner.getSelectedItemPosition() == 0) );
	    editor.putBoolean( Preferences.LOG_SCALING, (displayScalingSpinner.getSelectedItemPosition() == 0) );
	    editor.commit();
	  }
	  
	  
	  @Override
	  public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {  
		  
		    if (e1.getX() - e2.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {  
		    	
				
		    } else if (e2.getX() - e1.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {  
		    	
		    	Intent intent = new Intent( this, PreferencesFrequencyDomain.class);
		    	intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
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
