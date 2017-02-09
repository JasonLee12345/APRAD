package edu.ucdenver.aprad.info;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import edu.ucdenver.aprad.R;

/******************************
 **
 ** @modified_by Kun Li
 ** @modified_date 25 Apr 2015
 **
 *****************************/

public class Help1 extends Activity {
	
  @Override
  public void onCreate(Bundle savedInstanceState) 
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.help1);
    
    ViewFlipper flipper = (ViewFlipper) findViewById(R.id.viewFlipper2);  
    flipper.startFlipping();
    
    ImageView image1 = (ImageView) findViewById(R.id.flipper_image1);
    image1.setAlpha(140);
    
    ImageView image2 = (ImageView) findViewById(R.id.flipper_image2); 
    image2.setAlpha(140);
    
    ImageView image3 = (ImageView) findViewById(R.id.flipper_image3);
    image3.setAlpha(140);
    
    ImageView image4 = (ImageView) findViewById(R.id.flipper_image4);
    image4.setAlpha(140);
    
    ImageView image5 = (ImageView) findViewById(R.id.flipper_image5); 
    image5.setAlpha(140);
    
    ImageView image6 = (ImageView) findViewById(R.id.flipper_image6);
    image6.setAlpha(140);
    
    ImageView image7 = (ImageView) findViewById(R.id.flipper_image7);
    image7.setAlpha(170);
  }
}