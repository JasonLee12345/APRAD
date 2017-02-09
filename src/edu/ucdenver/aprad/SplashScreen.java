package edu.ucdenver.aprad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

/*****************************
 **
 ** @author Unknown
 ** @created 
 ** 
 ** @modified_by Kun Li
 ** @modified_date 2 Feb 2015
 **
 *****************************/

public class SplashScreen extends Activity {
  private static int TIMER = 3000;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.splash);
      
      ImageView image1 = (ImageView) findViewById(R.id.splashLogo);
      image1.setAlpha(195 );
      
      ImageView image2 = (ImageView) findViewById(R.id.splashLogo2);
      image2.setAlpha(220);

      new Handler().postDelayed(new Runnable() {
          @Override
          public void run() {
              Intent intent = new Intent( SplashScreen.this, Aprad.class );
              startActivity( intent );

              // close this activity
              finish();
          }
      }, TIMER);
  }
}
