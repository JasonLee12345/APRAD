package edu.ucdenver.aprad;

import edu.ucdenver.aprad.R;
import edu.ucdenver.aprad.education.rebuilder.FileChooser;
import edu.ucdenver.aprad.education.synthesize.SynthesizeInput;
import edu.ucdenver.aprad.education.synthesize.SynthesizeWaveData;
import edu.ucdenver.aprad.info.About;
import edu.ucdenver.aprad.info.Help;
import edu.ucdenver.aprad.info.Help1;
import edu.ucdenver.aprad.preferences.Preferences;
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

/**
 * The Education feature is used to allow Physicist and Electrical Engineers to either real saved
 * wave data, or sythesize wave data adjustable in the areas of frequency, amplitude and phase
 * shift. The ability to study and display multiple waves at once, and the interaction between them
 * is desired. This will allow the Physicists and Electrical Engineers to study the resultant waves.
 * <br><br>
 * 
 *****************************
 **
 * @author Nathan Vanderau
 * @created 21 Nov 2014
 * 
 * @modified_by Kun Li
 * @modified_date 19 Jan 2015
 * ****************************/

public class Education extends Activity {

    public static final String SHARED_PREFERENCES = "SharedPreferences";
    //SharedPreferences sharedPreferences;

    @Override
    public void onCreate( Bundle savedInstanceState ){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.education);
        getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

        
        /*********************************
         **
         **
         */
        Button synthesizeWaveDataButton = (Button) findViewById(R.id.synthesizeWaveDataButton);
        synthesizeWaveDataButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ){
                Intent intent = new Intent( v.getContext(), SynthesizeInput.class);
                v.getContext().startActivity(intent);
            }
        });

        
        /*********************************
         **
         ** When we implement the actual usage of the
         ** data in the selected CSV file, we'll need
         ** to change the intent's class to UseSavedData
         **
         *********************************/
        Button useSavedDataButton = (Button) findViewById(R.id.useSavedDataButton);
        useSavedDataButton.setOnClickListener( new OnClickListener() {
        	
            @Override
            public void onClick( View v ){
                Intent intent = new Intent( v.getContext(), FileChooser.class);
                v.getContext().startActivity(intent);
            }
        });

        /**************************************************
         **
         **  Moved to main.xml/Aprad.java
         **
         **************************************************/
/*      Button viewRawSignalButton = (Button) findViewById(R.id.timeDomainButton);
        viewRawSignalButton.setOnClickListener( new OnClickListener()
        {
            @Override
            public void onClick( View v ){
                Intent intent = new Intent( v.getContext(), TimeDomain.class);
                v.getContext().startActivity(intent);
            }
        });
*/
        Button helpButton = (Button) findViewById(R.id.helpButton);
        helpButton.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick( View v ){
                Intent intent = new Intent( v.getContext(), Help1.class);
                v.getContext().startActivity(intent);
            }
        });

    }
    
    
    /**
	 * 
	 */
	@Override
	protected void onResume()
	{
		super.onResume();
		//updatePreferences();
		//this.signalCapture = new AudioRecorder(synVisualizer);
		//AudioRecorderListenerManipulate.registerFFTAvailListener(this);
	}
	
}
