package edu.ucdenver.aprad;

import edu.ucdenver.aprad.R;
import edu.ucdenver.aprad.info.About;
import edu.ucdenver.aprad.info.Help;
import edu.ucdenver.aprad.preferences.Preferences;
import edu.ucdenver.aprad.preferences.PreferencesSpectrogram;
import edu.ucdenver.aprad.preferences.PreferencesTimeDomain;
import edu.ucdenver.aprad.spectrogram.Spectrogram;
import edu.ucdenver.aprad.spectrum_analyzer.SpectrumAnalyzer;
import edu.ucdenver.aprad.time_domain.RawSignal;
import edu.ucdenver.aprad.tools.AudioRecorder;
import edu.ucdenver.aprad.tools.AudioRecorderListenerManipulate;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

/*****************************
 **
 ** @author Unknown
 ** @created 
 ** 
 ** @modified_by Kun Li
 ** @modified_date 19 Jan 2015
 **
 *****************************/

public class Aprad extends Activity {

  Button recordButton;
  Button simulateButton;
  Button helpButton;
  Button aboutButton;
  
  ActionBar actionBar;

  //public static final String PREFS_NAME = "SharedPreferences";
  //SharedPreferences sharedPreferences;
  
  
  @Override
  public void onCreate( Bundle savedInstanceState )
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.front_page);
    getSharedPreferences(Preferences.PREFS_NAME, Context.MODE_PRIVATE);
    
    actionBar=getActionBar();
    actionBar.show();
    
    ImageView image1 = (ImageView) findViewById(R.id.wave_image);
    image1.setAlpha(90);
    
    recordButton = (Button) findViewById(R.id.id_record);
    //recordButton.getBackground().setAlpha(125);
    recordButton.setOnClickListener( 
    									new OnClickListener() 
    									{
    										@Override
    										public void onClick( View v )
    										{
    											Intent intent = new Intent( v.getContext(), RecordWave.class);
    											v.getContext().startActivity(intent);
    										}
    									}
    								  );
    
    
    simulateButton = (Button) findViewById(R.id.id_simulate);
    simulateButton.setOnClickListener( 
    									new OnClickListener()
    									{
    										@Override
    										public void onClick( View v )
    										{
    											Intent intent = new Intent( v.getContext(), Education.class);
    											v.getContext().startActivity(intent);
    										}
    									}
    								  );
    
    
    helpButton = (Button) findViewById(R.id.helpButton);
    helpButton.setOnClickListener( 
    									new OnClickListener() 
    									{
    										@Override
    										public void onClick( View v )
    										{
    											Dialog dialog1 = new Dialog(Aprad.this, R.style.dialog_style);  
    						            		dialog1.setContentView(R.layout.previewer);
    						            		dialog1.show();
    						            		TextView text = (TextView) dialog1.findViewById(R.id.textView1);
    						            		text.setText("Help Page:");
    						            		TextView text3 = (TextView) dialog1.findViewById(R.id.textView3);
    						            		text3.setText("APRAD (Autonomous Portable low frequency Receiver And Display) "
    						            					+ "provides a means of measuring and analyzing electromagnetic waves or sound waves in the environment.\n\n"
    						            					+ "1. <Record/Save Data> After hitting this button, the program will navigate into "
    						            					+ "RRM (Real-time Recording Module) with three functionalities. "
    						            					+ "Users can save data in \"Spectrogram/Save Data\" functionality.\n\n"
    						            					+ "2. <Synthesize/Load Data> After hitting this button, the program will navigate into "
    						            					+ "EM (Educational Module). Users can synthesize customized waves and load the previously saved data.");
    						            		text3.setTextSize(14);
    										}
    									}
    							 );
    
    
    aboutButton = (Button) findViewById(R.id.aboutButton);
    aboutButton.setOnClickListener( 
    									new OnClickListener()
    									{
    										@Override
    										public void onClick( View v )
    										{
    											Intent intent = new Intent( v.getContext(), About.class);
    											v.getContext().startActivity(intent);
    										}
    									}
    								  );
    
  }
  
  	
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
  
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.aprad, menu);
      return true;
  }

  
  @Override
  public boolean onOptionsItemSelected( MenuItem item ){
    // Handle item selection
    switch ( item.getItemId() )
    {
    	case R.id.virtualSettings:
    		Intent settingsIntent = new Intent( Aprad.this, About.class );
    		settingsIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );//| Intent.FLAG_ACTIVITY_CLEAR_TASK 
    		this.startActivity(settingsIntent);
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
    }
  }

  
}
