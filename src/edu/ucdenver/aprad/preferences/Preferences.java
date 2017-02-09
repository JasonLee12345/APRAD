package edu.ucdenver.aprad.preferences;

import edu.ucdenver.aprad.Aprad;
import edu.ucdenver.aprad.R;
import edu.ucdenver.aprad.education.rebuilder.FileChooser;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

/*****************************
 **
 ** @author Kun Li
 ** @created 9 Mar 2015
 ** 
 ** @modified_by 
 ** @modified_date 
 **
 *****************************/

public class Preferences extends Activity implements OnTouchListener, OnGestureListener {
  
  public static final String PREFS_NAME  = "MySharedPreferences";
  public static final String FREQUENCY   = "FREQUENCY";
  public static final String LIVE_RENDER = "LIVE_RENDER";
  public static final String LOG_SCALING = "LOG_SCALING";
  
  private Spinner frequencySpinner;
  private Button  savePreferencesButton;
  private Button  instructionsPreferencesButton;
  
  SharedPreferences sharedPreferences;
  
  GestureDetector mGestureDetector; 
  private int verticalMinDistance = 20;  
  private int minVelocity         = 0;
  

  public static boolean firstTime = true;
  
  @Override
  public void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.preferences );
    
    sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
    
    	AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("Little Tip")
    		.setMessage("The pages are slidable here")
    		.setPositiveButton("Got it!", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id)
                {         	 
                	firstTime = false;
                }
    		}).create(); 
    
    	Window window = alertDialog.getWindow(); 
    	WindowManager.LayoutParams lp = window.getAttributes(); 
    	lp.alpha = 0.7f; 
    	window.setAttributes(lp); 
    	
    	if(firstTime)
    		alertDialog.show();
    
    mGestureDetector = new GestureDetector((OnGestureListener) this);
    
    LinearLayout viewSnsLayout = (LinearLayout)findViewById(R.id.id_mySlideView);    
    viewSnsLayout.setOnTouchListener(this);    
    viewSnsLayout.setLongClickable(true);    
    
    
    frequencySpinner = (Spinner) findViewById( R.id.frequencySpinner );
    setFrequencySpinnerSelection();
    
    savePreferencesButton = (Button) findViewById(R.id.savePreferencesButton);
    savePreferencesButton.setOnClickListener( 
    											new OnClickListener() 
    											{
    												@Override
    												public void onClick( View v )
    												{
    													saveSharedPreferences();
    													//Intent intent = new Intent( v.getContext(), Aprad.class);
    													//v.getContext().startActivity(intent);
    													new  AlertDialog.Builder(v.getContext())
    													.setTitle("Message" ) 
    													.setMessage("Settings Saved!" )  
    													.setPositiveButton("Got it" ,  null )  
    													.show();
    												}
    											}
    										);
    
    instructionsPreferencesButton = (Button) findViewById(R.id.instructionPreferencesButton);
    instructionsPreferencesButton.setOnClickListener( 
			new OnClickListener() 
			{
				@Override
				public void onClick( View v )
				{
					Dialog dialog1 = new Dialog(Preferences.this, R.style.dialog_style);  
            		dialog1.setContentView(R.layout.previewer);
            		dialog1.show();
            		TextView text = (TextView) dialog1.findViewById(R.id.textView1);
            		text.setText("Instructions:");
            		TextView text3 = (TextView) dialog1.findViewById(R.id.textView3);
            		text3.setText("For Global Setting: \n"
            					+ "1. The user may select sampling frequencies from the list offering sampling rates between 8000-48000 Hertz (Hz).\n"
            					+ "2. After you select a new sampling frequency, please hit the <Save> button for the changes to take affect. "
            					+ "Then RRM will work with the latest changes (other setting pages have the same working mechanism).\n\n"
            					+ "For Time Domain Setting:\n"
            					+ "1. <Y-Axis Span> This field is working for how much (the amplitude of real-time waves) you want the program to display the Y-Axis span for the waves shown in time domain.\n"
            					+ "2. <Frame Refresh Time> This is used for how quickly you want the program to refresh the graph. E.g., 0 means completely real-time displaying.\n"
            					+ "3. <Samples (in number)> This means how many sample points you want the program to display on the screen for each frame on X-Axis.\n\n"
            					+ "For Frequency Domain Setting:\n"
            					+ "<Amplification Coefficient> This works for either diminishing or amplifying the signals in real-time.\n"
            					+ "The coefficient is ranging from 0.05 to 20.\n\n"
            					+ "For Spectrogram Setting:\n"
            					+ "1. <Recording and Display> When the Live Data Rendering option is selected, the spectrogram is generated in real-time as the ambient frequency data is processed, "
            					+ "whereas the Record Then Render Data option temporarily saves the incoming ambient frequency data, then graphs the data after the user selects the <Stop Recording> button.\n"
            					+ "2. <Data Display Scaling Type> The option allows the user to select whether they would like to view their data in a Linear or Logarithmic scale. "
            					+ "The default is to view the data in a Logarithmic scale.\n"
            					+ "3. <Color Scale> The option is a slider to help the user reduce the visibility of weak signals and background noise from the spectrogram (basically, it is about changing the marker position on the colorbar). "
            					+ "This slider allows the user to truncate “noise” that is less than a certain percentage of the maximum relative amplitude.");
            		text3.setTextSize(14);
				}
			}
		);
   
    /*
    setBehindContentView(R.layout.my_side_menu);
    android.app.FragmentManager fragmentManager = getFragmentManager();
    android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    SettingsListFragment menuFragment = new SettingsListFragment();
    //fragmentTransaction.replace(R.id.sideListMenu, menuFragment);
    fragmentTransaction.commit();
    
    
    SlidingMenu sm = getSlidingMenu();
    sm.setShadowWidth(50);
    //sm.setShadowDrawable(R.drawable.shadow);
    sm.setBehindOffset(60);
    sm.setFadeDegree(0.35f);
    //设置slding menu的几种手势模式
    //TOUCHMODE_FULLSCREEN 全屏模式，在content页面中，滑动，可以打开sliding menu
    //TOUCHMODE_MARGIN 边缘模式，在content页面中，如果想打开slding ,你需要在屏幕边缘滑动才可以打开slding menu
    //TOUCHMODE_NONE 自然是不能通过手势打开啦
    sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
    
    */
  }
  
  
  private void setFrequencySpinnerSelection()
  {
    ArrayAdapter myAdap = (ArrayAdapter) frequencySpinner.getAdapter(); //cast to an ArrayAdapter

    int spinnerPosition = myAdap.getPosition( String.valueOf( (int) sharedPreferences.getFloat( FREQUENCY, 8000.0f ) ) + " Hz" );

    //set the default according to value
    frequencySpinner.setSelection(spinnerPosition);
  }
  
  
  private void saveSharedPreferences() 
  {
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putFloat( FREQUENCY, Float.parseFloat( frequencySpinner.getSelectedItem().toString().replaceAll("[^\\d.]", "") ));
    editor.commit();
  }
  
  	
  	@Override
  	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {  
	  
	    if (e1.getX() - e2.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {  
	  
	    	Intent intent = new Intent( this, PreferencesTimeDomain.class);
	    	intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			
	    } else if (e2.getX() - e1.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {  
	    	
	    	
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
