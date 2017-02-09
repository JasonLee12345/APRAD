package edu.ucdenver.aprad.info;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import edu.ucdenver.aprad.R;

/**
 * Displays information about how to use the program.
 * 
 * @author Robbie Perlstein
 * @author Dan Rolls
 * @author Michael Dewar
 * @author Kun Li
 * @author Nathan Vaderau
 ******************************
 **
 ** @modified_by Kun Li
 ** @modified_date 16 Feb 2015
 **
 *****************************/

public class Help extends Activity {
	
  @Override
  public void onCreate(Bundle savedInstanceState) 
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.help);
    
    ViewFlipper flipper = (ViewFlipper) findViewById(R.id.viewFlipper1);  
    flipper.startFlipping();
    
    ImageView image1 = (ImageView) findViewById(R.id.flipper_image1);
    image1.setAlpha(160);
    
    ImageView image2 = (ImageView) findViewById(R.id.flipper_image2); 
    image2.setAlpha(120);
    
    ImageView image3 = (ImageView) findViewById(R.id.flipper_image3);
    image3.setAlpha(170);
    
  }
}