package edu.ucdenver.aprad.education.synthesize;

import edu.ucdenver.aprad.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;

/*****************************
 **
 ** @author Kun Li
 ** @created 16 Feb 2015
 ** 
 ** @modified_by Kun Li
 ** @modified_date 7 Apr 2015
 **
 *****************************/

public class SynthesizeInput extends Activity{
	
	public static final String SHARED_PREFERENCES = "SharedPreferences";
	
	public static boolean firstSwitch;
	public static float f1;
	public static float a1;
	
	public static boolean secondSwitch;
	public static float f2;
	public static float a2;
	
	public static boolean thirdSwitch;
	public static float f3;
	public static float a3;
	
	public static boolean fourthSwitch;
	public static float f4;
	public static float a4;
	
	public static boolean fifthSwitch;
	public static float f5;
	public static float a5;
	
	public static boolean sixthSwitch;
	public static float f6;
	public static float a6;
	
	private Switch switch1;
	private Switch switch2;
	private Switch switch3;
	private Switch switch4;
	private Switch switch5;
	private Switch switch6;
	
    public boolean COS = false;
    public boolean SIN = true;
    
   
	@Override
    public void onCreate( Bundle savedInstanceState )
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input);
        getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        
        switch1 = (Switch) findViewById(R.id.switch1);
        switch2 = (Switch) findViewById(R.id.switch2);
        switch3 = (Switch) findViewById(R.id.switch3);
        switch4 = (Switch) findViewById(R.id.switch4);
        switch5 = (Switch) findViewById(R.id.switch5);
        switch6 = (Switch) findViewById(R.id.switch6);
        
        if(firstSwitch)
        	switch1.setChecked(SIN);
        switch1.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
            @Override  
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {  
            	
                if (isChecked) {  
                	firstSwitch = SIN;
                } else {  
                	firstSwitch = COS;
                }  
            }  
        });
        
        if(secondSwitch)
        	switch2.setChecked(SIN);
        switch2.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
            @Override  
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {  
            	
                if (isChecked) {  
                	secondSwitch = SIN;
                } else {  
                	secondSwitch = COS;
                }  
            }  
        });
        
        if(thirdSwitch)
        	switch3.setChecked(SIN);
        switch3.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
            @Override  
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {  
            	
                if (isChecked) {  
                	thirdSwitch = SIN;
                } else {  
                	thirdSwitch = COS;
                }  
            }  
        });
        
        if(fourthSwitch)
        	switch4.setChecked(SIN);
        switch4.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
            @Override  
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {  
            	
                if (isChecked) {  
                	fourthSwitch = SIN;
                } else {  
                	fourthSwitch = COS;
                }  
            }  
        });
        
        if(fifthSwitch)
        	switch5.setChecked(SIN);
        switch5.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
            @Override  
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {  
            	
                if (isChecked) {  
                	fifthSwitch = SIN;
                } else {  
                	fifthSwitch = COS;
                }  
            }  
        });
        
        if(sixthSwitch)
        	switch6.setChecked(SIN);
        switch6.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
            @Override  
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {  
            	
                if (isChecked) {  
                	sixthSwitch = SIN;
                } else {  
                	sixthSwitch = COS;
                }  
            }  
        });
        
        
        Button goButton = (Button) findViewById(R.id.goButton);
        goButton.setOnClickListener( new View.OnClickListener()
        {
        	
        	EditText frequencyCos1 = (EditText) findViewById(R.id.freq_cos_1);
        	EditText amplitudeCos1 = (EditText) findViewById(R.id.ampl_cos_1);
        	EditText frequencySin2 = (EditText) findViewById(R.id.freq_sin_2);
        	EditText amplitudeSin2 = (EditText) findViewById(R.id.ampl_sin_2);
        	
        	EditText a_third = (EditText) findViewById(R.id.a_third);
        	EditText f_third = (EditText) findViewById(R.id.f_third);
        	EditText a_fourth = (EditText) findViewById(R.id.a_fourth);
        	EditText f_fourth = (EditText) findViewById(R.id.f_fourth);
        	EditText a_fifth = (EditText) findViewById(R.id.a_fifth);
        	EditText f_fifth = (EditText) findViewById(R.id.f_fifth);
        	EditText a_sixth = (EditText) findViewById(R.id.a_sixth);
        	EditText f_sixth = (EditText) findViewById(R.id.f_sixth);
        	    
            @Override
            public void onClick( View v )
            {	////////////////////////////////////////////////////////////
            	String str1 = frequencyCos1.getText().toString();
            	if(!str1.equals(""))
            		f1 = Float.parseFloat(str1);
            	else
            		f1 = (float) 0.0;
                
                String str2 = amplitudeCos1.getText().toString();
                if(!str2.equals(""))
                	a1 = Float.parseFloat(str2);
                else
                	a1 = (float) 0.0;
                ////////////////////////////////////////////////////////////
                String str3 = frequencySin2.getText().toString();
                if(!str3.equals(""))
                	f2 = Float.parseFloat(str3);
                else
                	f2 = (float) 0.0;
                
                String str4 = amplitudeSin2.getText().toString();
                if(!str4.equals(""))
                	a2 = Float.parseFloat(str4);
                else
                	a2 = (float) 0.0;
                ////////////////////////////////////////////////////////////
                String str5 = a_third.getText().toString();
                if(!str5.equals(""))
                	a3 = Float.parseFloat(str5);
                else
                	a3 = (float) 0.0;
                
                String str6 = f_third.getText().toString();
                if(!str6.equals(""))
                	f3 = Float.parseFloat(str6);
                else
                	f3 = (float) 0.0;
                ////////////////////////////////////////////////////////////
                String str7 = a_fourth.getText().toString();
                if(!str7.equals(""))
                	a4 = Float.parseFloat(str7);
                else
                	a4 = (float) 0.0;
                
                String str8 = f_fourth.getText().toString();
                if(!str8.equals(""))
                	f4 = Float.parseFloat(str8);
                else
                	f4 = (float) 0.0;
                ////////////////////////////////////////////////////////////
                String str9 = a_fifth.getText().toString();
                if(!str9.equals(""))
                	a5 = Float.parseFloat(str9);
                else
                	a5 = (float) 0.0;
                
                String str10 = f_fifth.getText().toString();
                if(!str10.equals(""))
                	f5 = Float.parseFloat(str10);
                else
                	f5 = (float) 0.0;
                ////////////////////////////////////////////////////////////
                String str11 = a_sixth.getText().toString();
                if(!str11.equals(""))
                	a6 = Float.parseFloat(str11);
                else
                	a6 = (float) 0.0;
                
                String str12 = f_sixth.getText().toString();
                if(!str12.equals(""))
                	f6 = Float.parseFloat(str12);
                else
                	f6 = (float) 0.0;
                ////////////////////////////////////////////////////////////
                Intent intent = new Intent( v.getContext(), SynthesizeWaveData.class);
                intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                v.getContext().startActivity(intent);
            }
        });
        
        
        Button goButton1 = (Button) findViewById(R.id.goButton1);
        goButton1.setOnClickListener( new View.OnClickListener()
        {
        	
        	EditText frequencyCos1 = (EditText) findViewById(R.id.freq_cos_1);
        	EditText amplitudeCos1 = (EditText) findViewById(R.id.ampl_cos_1);
        	EditText frequencySin2 = (EditText) findViewById(R.id.freq_sin_2);
        	EditText amplitudeSin2 = (EditText) findViewById(R.id.ampl_sin_2);
        	
        	EditText a_third = (EditText) findViewById(R.id.a_third);
        	EditText f_third = (EditText) findViewById(R.id.f_third);
        	EditText a_fourth = (EditText) findViewById(R.id.a_fourth);
        	EditText f_fourth = (EditText) findViewById(R.id.f_fourth);
        	EditText a_fifth = (EditText) findViewById(R.id.a_fifth);
        	EditText f_fifth = (EditText) findViewById(R.id.f_fifth);
        	EditText a_sixth = (EditText) findViewById(R.id.a_sixth);
        	EditText f_sixth = (EditText) findViewById(R.id.f_sixth);
        	    
            @Override
            public void onClick( View v )
            {	////////////////////////////////////////////////////////////
            	String str1 = frequencyCos1.getText().toString();
            	if(!str1.equals(""))
            		f1 = Float.parseFloat(str1);
            	else
            		f1 = (float) 0.0;
                
                String str2 = amplitudeCos1.getText().toString();
                if(!str2.equals(""))
                	a1 = Float.parseFloat(str2);
                else
                	a1 = (float) 0.0;
                ////////////////////////////////////////////////////////////
                String str3 = frequencySin2.getText().toString();
                if(!str3.equals(""))
                	f2 = Float.parseFloat(str3);
                else
                	f2 = (float) 0.0;
                
                String str4 = amplitudeSin2.getText().toString();
                if(!str4.equals(""))
                	a2 = Float.parseFloat(str4);
                else
                	a2 = (float) 0.0;
                ////////////////////////////////////////////////////////////
                String str5 = a_third.getText().toString();
                if(!str5.equals(""))
                	a3 = Float.parseFloat(str5);
                else
                	a3 = (float) 0.0;
                
                String str6 = f_third.getText().toString();
                if(!str6.equals(""))
                	f3 = Float.parseFloat(str6);
                else
                	f3 = (float) 0.0;
                ////////////////////////////////////////////////////////////
                String str7 = a_fourth.getText().toString();
                if(!str7.equals(""))
                	a4 = Float.parseFloat(str7);
                else
                	a4 = (float) 0.0;
                
                String str8 = f_fourth.getText().toString();
                if(!str8.equals(""))
                	f4 = Float.parseFloat(str8);
                else
                	f4 = (float) 0.0;
                ////////////////////////////////////////////////////////////
                String str9 = a_fifth.getText().toString();
                if(!str9.equals(""))
                	a5 = Float.parseFloat(str9);
                else
                	a5 = (float) 0.0;
                
                String str10 = f_fifth.getText().toString();
                if(!str10.equals(""))
                	f5 = Float.parseFloat(str10);
                else
                	f5 = (float) 0.0;
                ////////////////////////////////////////////////////////////
                String str11 = a_sixth.getText().toString();
                if(!str11.equals(""))
                	a6 = Float.parseFloat(str11);
                else
                	a6 = (float) 0.0;
                
                String str12 = f_sixth.getText().toString();
                if(!str12.equals(""))
                	f6 = Float.parseFloat(str12);
                else
                	f6 = (float) 0.0;
                ////////////////////////////////////////////////////////////
                Intent intent = new Intent( v.getContext(), SynWaveDataFD.class);
                intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                v.getContext().startActivity(intent);
            }
        });
        
        
        Button goButton2 = (Button) findViewById(R.id.goButton2);
        goButton2.setOnClickListener( new View.OnClickListener()
        {
        	
        	EditText frequencyCos1 = (EditText) findViewById(R.id.freq_cos_1);
        	EditText amplitudeCos1 = (EditText) findViewById(R.id.ampl_cos_1);
        	EditText frequencySin2 = (EditText) findViewById(R.id.freq_sin_2);
        	EditText amplitudeSin2 = (EditText) findViewById(R.id.ampl_sin_2);
        	
        	EditText a_third = (EditText) findViewById(R.id.a_third);
        	EditText f_third = (EditText) findViewById(R.id.f_third);
        	EditText a_fourth = (EditText) findViewById(R.id.a_fourth);
        	EditText f_fourth = (EditText) findViewById(R.id.f_fourth);
        	EditText a_fifth = (EditText) findViewById(R.id.a_fifth);
        	EditText f_fifth = (EditText) findViewById(R.id.f_fifth);
        	EditText a_sixth = (EditText) findViewById(R.id.a_sixth);
        	EditText f_sixth = (EditText) findViewById(R.id.f_sixth);
        	    
            @Override
            public void onClick( View v )
            {	////////////////////////////////////////////////////////////
            	String str1 = frequencyCos1.getText().toString();
            	if(!str1.equals(""))
            		f1 = Float.parseFloat(str1);
            	else
            		f1 = (float) 0.0;
                
                String str2 = amplitudeCos1.getText().toString();
                if(!str2.equals(""))
                	a1 = Float.parseFloat(str2);
                else
                	a1 = (float) 0.0;
                ////////////////////////////////////////////////////////////
                String str3 = frequencySin2.getText().toString();
                if(!str3.equals(""))
                	f2 = Float.parseFloat(str3);
                else
                	f2 = (float) 0.0;
                
                String str4 = amplitudeSin2.getText().toString();
                if(!str4.equals(""))
                	a2 = Float.parseFloat(str4);
                else
                	a2 = (float) 0.0;
                ////////////////////////////////////////////////////////////
                String str5 = a_third.getText().toString();
                if(!str5.equals(""))
                	a3 = Float.parseFloat(str5);
                else
                	a3 = (float) 0.0;
                
                String str6 = f_third.getText().toString();
                if(!str6.equals(""))
                	f3 = Float.parseFloat(str6);
                else
                	f3 = (float) 0.0;
                ////////////////////////////////////////////////////////////
                String str7 = a_fourth.getText().toString();
                if(!str7.equals(""))
                	a4 = Float.parseFloat(str7);
                else
                	a4 = (float) 0.0;
                
                String str8 = f_fourth.getText().toString();
                if(!str8.equals(""))
                	f4 = Float.parseFloat(str8);
                else
                	f4 = (float) 0.0;
                ////////////////////////////////////////////////////////////
                String str9 = a_fifth.getText().toString();
                if(!str9.equals(""))
                	a5 = Float.parseFloat(str9);
                else
                	a5 = (float) 0.0;
                
                String str10 = f_fifth.getText().toString();
                if(!str10.equals(""))
                	f5 = Float.parseFloat(str10);
                else
                	f5 = (float) 0.0;
                ////////////////////////////////////////////////////////////
                String str11 = a_sixth.getText().toString();
                if(!str11.equals(""))
                	a6 = Float.parseFloat(str11);
                else
                	a6 = (float) 0.0;
                
                String str12 = f_sixth.getText().toString();
                if(!str12.equals(""))
                	f6 = Float.parseFloat(str12);
                else
                	f6 = (float) 0.0;
                ////////////////////////////////////////////////////////////
                Intent intent = new Intent( v.getContext(), SynWaveDataS.class);
                intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                v.getContext().startActivity(intent);
            }
        });
        
	}

}
