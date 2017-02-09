package edu.ucdenver.aprad.info;

import android.app.Activity;
import android.os.Bundle;
import edu.ucdenver.aprad.R;

/**
 * Displays information about who made the program.
 * 
 * @author Robbie Perlstein
 * @author Dan Rolls
 * @author Michael Dewar
 */

public class About extends Activity {
  @Override
  public void onCreate(Bundle savedInstanceState) 
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.about);
  }
}