package edu.ucdenver.aprad;

import edu.ucdenver.aprad.R;
import edu.ucdenver.aprad.info.About;
import edu.ucdenver.aprad.info.Help;
import edu.ucdenver.aprad.preferences.Preferences;
import edu.ucdenver.aprad.spectrogram.Spectrogram;
import edu.ucdenver.aprad.spectrum_analyzer.SpectrumAnalyzer;
import edu.ucdenver.aprad.time_domain.RawSignal;
import edu.ucdenver.aprad.tools.AudioRecorder;
import edu.ucdenver.aprad.tools.AudioRecorderListenerManipulate;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/*****************************
 **
 ** @author Kun Li
 ** @created 19 Jan 2015
 ** 
 ** @modified_by 
 ** @modified_date 
 **
 *****************************/

public class RecordWave extends Activity{
	
	Button spectrumAnalyzerButton;
	Button spectrogramButton;
	Button timeDomainButton;
	Button configureButton;
	Button theHelpButton;
	
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);
	    getSharedPreferences(Preferences.PREFS_NAME, Context.MODE_PRIVATE);
	    
	    
	    timeDomainButton = (Button) findViewById(R.id.timeDomainButton);
	    timeDomainButton.setOnClickListener(
	          new OnClickListener()
	          {
	              @Override
	              public void onClick( View v )
	              {
	                  Intent intent = new Intent( v.getContext(), RawSignal.class);
	                  v.getContext().startActivity(intent);
	              }
	          }
	      );
	    
	    
	    spectrumAnalyzerButton = (Button) findViewById(R.id.spectrumAnalyzerButton);
	    spectrumAnalyzerButton.setOnClickListener( 
	    											new OnClickListener() 
	    											{
	    												@Override
	    												public void onClick( View v )
	    												{
	    													Intent intent = new Intent( v.getContext(), SpectrumAnalyzer.class);
	    													v.getContext().startActivity(intent);
	    												}
	    											}
	    										 );
	    
	    
	    spectrogramButton = (Button) findViewById(R.id.spectrogramButton);
	    spectrogramButton.setOnClickListener( 
	    											new OnClickListener() 
	    											{
	    												@Override
	    												public void onClick( View v )
	    												{
	    													Intent intent = new Intent( v.getContext(), Spectrogram.class);
	    													intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
	    													v.getContext().startActivity(intent);
	    												}
	    											}
	    									);
	    

	    configureButton = (Button) findViewById(R.id.configurationButton);
	    configureButton.setOnClickListener( 
	    									new OnClickListener() 
	    									{
	    										@Override
	    										public void onClick( View v )
	    										{
	    											Intent intent = new Intent( v.getContext(), Preferences.class);
	    											v.getContext().startActivity(intent);
	    										}
	    									}
	    							  );
	    
	    
	    theHelpButton = (Button) findViewById(R.id.helpButton);
	    theHelpButton.setOnClickListener( 
	    									new OnClickListener() 
	    									{
	    										@Override
	    										public void onClick( View v )
	    										{
	    											Intent intent = new Intent( v.getContext(), Help.class);
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
	
}
