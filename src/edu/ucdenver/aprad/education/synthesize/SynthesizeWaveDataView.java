package edu.ucdenver.aprad.education.synthesize;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.math.*;

/**********************************************
 **
 **	@author Kun Li
 ** @created 21 Nov 2014
 **
 ** @modified_by Kun Li
 ** @modified_date 11 Dec 2014
 ** @modified_date 15 Mar 2015
 **
 **********************************************/

public class SynthesizeWaveDataView extends SurfaceView implements SurfaceHolder.Callback {
	
	public static final double 	PI	= (double) 3.1415926535898;
	
	private Display 			display;
	private Context 			context;
	
	private int 				scrWidth; 
	private int 				scrHeight;
	
	private int         		widthScalar;
	private int         		heightScalar;
	
	private boolean 			running;
	private boolean 			isSurfaceCreated;
	private SurfaceHolder 		surfaceHolder;
	private Canvas 				canvas;
	Paint 						paint;
	
	
	/**
	 * Constructor for SpecAnalyView
	 * @param context Context of  the application
	 * @param attrs Attribute set of the application
	 */
	public SynthesizeWaveDataView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init( context );
	}
	
	
	/**
	 * Constructor for SpecAnalyView passing in only the context
	 * @param context Context of the application
	 */
	public SynthesizeWaveDataView(Context context)
	{
		super(context);
		init( context );
	}
	
	
	private void init( Context context )
	{
	   paint = new Paint();
	   running = true;
	   
	   surfaceHolder = getHolder();
	   getHolder().addCallback(this);    
	   setFocusable(true);
	   this.context = context;
	   this.display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	}
	
	
	public Thread mThread = new Thread(new Runnable() {  
		  
        @Override  
        public void run() 
        {  
        	while (running)
        	{
        		drawSynthesize();  
        		//unDrawSynthesize();
        	}
        }
    });
	
	
	public void end() {
		running = false;	
	}
	
	
	/**
	 * Main class to handle the drawing  of the UI
	 */
	public void drawSynthesize() {
		if(isSurfaceCreated)
		{		
			canvas = surfaceHolder.lockCanvas(null);
			if( canvas != null )
			{
				canvas.drawColor( Color.BLACK );
				paint.setColor(Color.WHITE);
				drawXaxis();
				drawYaxis();
				paint.setColor(Color.GREEN);
				drawGraph();
			}
			surfaceHolder.unlockCanvasAndPost( canvas );
		}
	}
	
	
	/**
	 * Main class to handle the drawing  of the UI
	 */
	public void unDrawSynthesize()
	{
		if(isSurfaceCreated)
		{		
			canvas = surfaceHolder.lockCanvas(null);
			if( canvas != null )
			{
				canvas.drawColor( Color.BLACK );
				paint.setColor(Color.BLACK);
				drawXaxis();
				drawYaxis();
				paint.setColor(Color.BLACK);
				drawGraph();
			}
			surfaceHolder.unlockCanvasAndPost( canvas );
		}
	}
	
	
	/**
	 * Get's the screen dimension in pixel and populates the class screen attributes
	 */
	private void getViewInfo() 
	{
		this.scrWidth = getWidth();
		this.scrHeight = getHeight();
		loadScreenScalar();
		Log.i("GetView: ","Width: "+getWidth()+" - Height: "+getHeight());
	}
	
	
	public void loadScreenScalar()
	{
		widthScalar  = scrWidth/240;
		heightScalar = scrHeight/320;
	}
	
	
	public void drawXaxis()
	{
		paint.setStrokeWidth(2);
		
		canvas.drawLine(0, 
						scrHeight/2, 
						scrWidth, 
		  		   		scrHeight/2, 
		  		   		paint );
		
		drawXscalars();
	}
	
	
	public void drawXscalars()
	{
		if(SynthesizeWaveData.MAX_F != 0)
		{
			float period = (float) (SynthesizeWaveData.HALF_RANGE*2*PI*SynthesizeWaveData.MAX_F);
			int dotNumber = (int) (2*period + 1);
			float intervalLength = scrWidth / (dotNumber-1);
			for(int i=0;i<dotNumber;i++)
			{
				canvas.drawLine(intervalLength*i, 
								scrHeight/2, 
								intervalLength*i, 
								scrHeight/2-6*heightScalar, 
								paint );
			
				float timeDuration = (float) (SynthesizeWaveData.HALF_RANGE*2*PI);
				float timeInterval = timeDuration / (dotNumber-1);
				float showingNumber = (float) (-SynthesizeWaveData.HALF_RANGE*PI + timeInterval*i);
				if(dotNumber<26)
					if(i%2==0)
						canvas.drawText( String.format("%.5f", showingNumber), 
										intervalLength*i, scrHeight/2+15*heightScalar, paint);
					else
						canvas.drawText( String.format("%.5f", showingNumber), 
										intervalLength*i, scrHeight/2-10*heightScalar, paint);
			}
		}
	}
	
	
	public void drawYaxis()
	{
		paint.setStrokeWidth(2);
		canvas.drawLine(scrWidth/2, 
						0, 
						scrWidth/2, 
						scrHeight, 
						paint );
		
		drawYScalars();
	}
	
	
	public void drawYScalars()
	{
		int SCALAR_NUMBER = 7; //it must be odd
		
		//!!!!!!!!!!!!!!!!change the code here, if you want to add more input fields
		int myScalarHeight = scrHeight/(SCALAR_NUMBER-1);
		double myAmplitude = ((SynthesizeWaveData.synA1 + SynthesizeWaveData.synA2 + SynthesizeWaveData.synA3
				+ SynthesizeWaveData.synA4 + SynthesizeWaveData.synA5 + SynthesizeWaveData.synA6) / 2);
		
		paint.setTextSize( 20*heightScalar );
		for(int i=0; i<SCALAR_NUMBER; i++)
		{
			canvas.drawLine(scrWidth/2, 
							myScalarHeight*i, 
							scrWidth/2 + 3*widthScalar, 
							myScalarHeight*i, 
							paint );
			
			float showingNumber = (float) (2*myAmplitude - 4*myAmplitude/(SCALAR_NUMBER-1) *i);
			if(showingNumber != 0)
			{
				String myXvalue = String.format("%.2f", showingNumber) + "";
				canvas.drawText( myXvalue, scrWidth/2 - 13*widthScalar, myScalarHeight*i, paint);
			}
		}
	}
	
	
	public void drawGraph()
	{
		paint.setStrokeWidth(2);
		float previous=0;
		for(int i=0; i<scrWidth; i++)
		{
			double inputValue = -SynthesizeWaveData.HALF_RANGE*PI + (2*SynthesizeWaveData.HALF_RANGE*PI)/(double)scrWidth*i; //( (double)i-HALF_RANGE*PI ) + ...
			
			//!!!!!!!!!!!!!!!!change the code here, if you want to add more input fields
			double outValue = SynthesizeWaveData.synA1 * myCosSin(SynthesizeInput.firstSwitch, SynthesizeWaveData.synF1, inputValue) + 
							  SynthesizeWaveData.synA2 * myCosSin(SynthesizeInput.secondSwitch, SynthesizeWaveData.synF2, inputValue) + 
					 		  SynthesizeWaveData.synA3 * myCosSin(SynthesizeInput.thirdSwitch, SynthesizeWaveData.synF3, inputValue) + 
					 		  SynthesizeWaveData.synA4 * myCosSin(SynthesizeInput.fourthSwitch, SynthesizeWaveData.synF4, inputValue) + 
					 		  SynthesizeWaveData.synA5 * myCosSin(SynthesizeInput.fifthSwitch, SynthesizeWaveData.synF5, inputValue) + 
					 		  SynthesizeWaveData.synA6 * myCosSin(SynthesizeInput.sixthSwitch, SynthesizeWaveData.synF6, inputValue);
			
			//!!!!!!!!!!!!!!!!change the code here, if you want to add more input fields
			double scaledOutValue = 0;
			//in case every A is 0, thus the denominator below won't be 0
			if(SynthesizeWaveData.synA1 != 0 || SynthesizeWaveData.synA2 != 0 || SynthesizeWaveData.synA3 != 0 ||
			   SynthesizeWaveData.synA4 != 0 || SynthesizeWaveData.synA5 != 0 || SynthesizeWaveData.synA6 != 0) 
				scaledOutValue = outValue * (double)(scrHeight/2) /
					(SynthesizeWaveData.synA1 + SynthesizeWaveData.synA2 + SynthesizeWaveData.synA3 + 
							SynthesizeWaveData.synA4 + SynthesizeWaveData.synA5 + SynthesizeWaveData.synA6);
			
			float drawYValue = (float) ( (double)scrHeight/2 - scaledOutValue );
			
			canvas.drawPoint(i, 
							 drawYValue, 
					 		 paint);
			
			if(i>0)
			{
				canvas.drawLine(i-1, 
								previous, 
								i, 
								drawYValue, 
								paint );
			}
			
			previous = drawYValue;
		}
	}
	
	
	/**
	 * @param cosSin either Cos() or Sin() function could be used by the method 
	 * @param f the frequency of the function,the original function is Cos(2 PI f t)
	 * @param t the parameter for any instant of time 
	 * @return the outcome value of the function
	 */
	public double myCosSin(boolean cosSin, double f, double t)
	{
		if(!cosSin)
		{
			double cosInnerValue = 2*PI*f*t;
			double sCosInnerValue = cosInnerValue;
			if(sCosInnerValue < 0)
			{
				while( sCosInnerValue < (-2*PI) )
				{
					sCosInnerValue += 2*PI;
				}	
			}
			else
			{
				while( sCosInnerValue > (2*PI) )
				{
					sCosInnerValue -= 2*PI;
				}	
			}
			double cosValue		 = Math.cos(sCosInnerValue);
			return cosValue;
		}
		else
		{
			double sinInnerValue = 2*PI*f*t;
			double sSinInnerValue = sinInnerValue;
			if(sSinInnerValue < 0)
			{
				while( sSinInnerValue < (-2*PI) )
				{
					sSinInnerValue += 2*PI;
				}	
			}
			else
			{
				while( sSinInnerValue > (2*PI) )
				{
					sSinInnerValue -= 2*PI;
				}	
			}
			double sinValue		 = Math.sin(sSinInnerValue);
			return sinValue;
		}
	}
	
	
	/**
	 * Override of the surfaceChanged class
	 * @param holder The surface holder
	 * @param format Screen format
	 * @param width Screen width
	 * @param height screen height
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) 
	{
		this.scrWidth = width;
		this.scrHeight = height;
		getViewInfo();
	}
	
	
	/**
	 * Override of the surfaceCreated  class
	 * @param holder The surface holder
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) 
	{
		isSurfaceCreated = true;
		mThread.start();
	}
	
	
	/**
	 * Override of the surfaceDestroyed class
	 * @param holder the surface holder
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		isSurfaceCreated = false;
	}
}
