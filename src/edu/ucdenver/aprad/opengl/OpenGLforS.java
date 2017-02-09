package edu.ucdenver.aprad.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnTouchListener;
import android.widget.Toast;
import edu.ucdenver.aprad.education.rebuilder.ReadSavedFileData;
import edu.ucdenver.aprad.education.rebuilder.s.RebuilderS;
import edu.ucdenver.aprad.opengl.OpenGLforFD.MyGLSurfaceView;
import edu.ucdenver.aprad.opengl.OpenGLforFD.MyRenderer;
import edu.ucdenver.aprad.tools.FFT;

/*****************************
 **
 ** @author Kun Li
 ** @created 9 Apr 2015
 ** 
 ** @modified_by 
 ** @modified_date 
 **
 *****************************/

public class OpenGLforS extends Activity implements OnTouchListener, OnGestureListener {
	
    private GLSurfaceView mGLView;
    GestureDetector mGestureDetector; 
    float eyeX = 40, eyeY = 140.0f, eyeZ = -25;
    float centX = 55.0f, centY = -5.5f, centZ = -70.0f;
    float deltaX = 0, deltaY = 0, deltaZ = 0;
    float deltaCentX = 0, deltaCentY = 0, deltaCentZ = 0;
    boolean isChangingEyeYMode = false;
    
    public double 		samplingRate; //frequency, default value is 8000.0
    public double 		timeSpan;
    public double[][] 	transformedSignal;
    public static final int 	sigInterval 	= 512;
    private static final int    SIGNALS_HEIGHT 	= sigInterval / 2;
    
    public  static final int    COLOR_RANGE			= 256;
	private int 		 		red 	 			= 0;
	private int 		 		blue 	 			= 0;
	private int 		 		green 	 			= 0;
	private double 				magic_n 			= 65.536;
    
    /**
     * The variable for shrinking X axis.
     */
    static float ShrinkRatio;
    
    /**
     * The variable for shrinking 
     * X axis (different with the same variable in OpenGLforTD). 
     * E.g., if sampling frequency is 8000 Hz, and the ShrinkNum is 2,
     * then we can get an X axis length of 4000 in 3D space.
     */
    static int ShrinkNum = 1;
    
    /**
     * For shrinking the actual values of saved data in files.
     */
    static int ShrinkYValues = 20;
    
    /**
     * For how quickly you want to drag the graph on the screen.
     */
    static int ShrinkDelta;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        samplingRate = ReadSavedFileData.theFrequency;
		timeSpan = ReadSavedFileData.theTimeSpan;
		transformedSignal = transToSpectrogram(ReadSavedFileData.actualData, sigInterval);

        mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);
        
        mGestureDetector = new GestureDetector((OnGestureListener) this);
        mGLView.setOnTouchListener(this);    
        mGLView.setLongClickable(true); 
    }
    
    
    class MyGLSurfaceView extends GLSurfaceView {
        public MyGLSurfaceView(Context context)
        {
            super(context);

            try
            {
                setRenderer(new MyRenderer());
                setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                // Notice the quoting sequence above, which could drive the program nuts. - .-
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    
    public class MyRenderer implements GLSurfaceView.Renderer {
    	static final int one = 0x10000;
    	
        private IntBuffer CoordinateXBuffer;
        private IntBuffer CoordinateYBuffer;
        private IntBuffer CoordinateZBuffer;
        private IntBuffer CoordinateColorBuffer;
        
        private int[] xAxis = new int[] {
        		0, 0, 0,
        		(int) (ReadSavedFileData.theFrequency/ShrinkNum * one), 0, 0
        };
        
        private int[] yAxis = new int[] {
        		0, -100 * one, 0,
        		0, 100 * one, 0
        };
        
        private int[] zAxis = new int[] {
        		0, 0, -100 * one,
        		0, 0,  100 * one
        };
        
        private int[] axisColor = new int[] {
        		0, 0, 0, one,  
                0, 0, 0, one
        };
       
        
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        	gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
            gl.glClearColor(0.9f, 0.9f, 0.9f, 1.0f);
            gl.glShadeModel(GL10.GL_SMOOTH);
            
            gl.glClearDepthf(1.0f);
            gl.glEnable(GL10.GL_DEPTH_TEST);
            gl.glDepthFunc(GL10.GL_LEQUAL);
        }
        
        
        public void onDrawFrame(GL10 gl) {
        	gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT); 
            gl.glLoadIdentity(); 
        	GLU.gluLookAt(gl, eyeX+deltaX, eyeY+deltaY, eyeZ+deltaZ, 
        			centX+deltaCentX, centY+deltaCentY, centZ+deltaCentZ, 0, 1, 0);
        	eyeX += deltaX; eyeY += deltaY; eyeZ += deltaZ; 
        	centX += deltaCentX; centY += deltaCentY; centZ += deltaCentZ;
        	////////////////////////////////////////////////////////////////////////
        	CoordinateXBuffer = OpenGLTools.intToBuffer(xAxis);
        	CoordinateYBuffer = OpenGLTools.intToBuffer(yAxis);
        	CoordinateZBuffer = OpenGLTools.intToBuffer(zAxis);
        	CoordinateColorBuffer = OpenGLTools.intToBuffer(axisColor);
            ////////////////////////////////////////////////////////////////////////
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);  
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);  
            ////////////////////////////////////////////////////////////////////////
            gl.glLineWidth(5);
            OpenGLTools.drawPointsAndColorsInt(gl, CoordinateXBuffer, CoordinateColorBuffer, GL10.GL_LINES, 2);
            OpenGLTools.drawPointsAndColorsInt(gl, CoordinateYBuffer, CoordinateColorBuffer, GL10.GL_LINES, 2);
            OpenGLTools.drawPointsAndColorsInt(gl, CoordinateZBuffer, CoordinateColorBuffer, GL10.GL_LINES, 2);
            
            int sigLen = transformedSignal.length;
            double signalValue;
    		int colorAmplitude;
            for (int i = 0; i < sigLen; i++) {
    			for (int j = 0; j < SIGNALS_HEIGHT; j++) {
    				signalValue = 20.0 * ( Math.log( transformedSignal[i][j] / magic_n ) /  Math.log( 10 ) );
    				colorAmplitude = (int)( 2048 + 24.975 * signalValue);
    				colorSelecter(colorAmplitude);
    				drawSuperBox(gl, (float)i, (float)j, (float)transformedSignal[i][j]);
    			}
    		}
            ////////////////////////////////////////////////////////////////////////
            gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        }
        
        
        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            float ratio = (float) width / height;
            gl.glViewport(0, 0, width, height);
            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadIdentity();
            GLU.gluPerspective(gl, 80, ratio, 1, 400000);
            gl.glMatrixMode(GL10.GL_MODELVIEW); 
            gl.glLoadIdentity();
        }
        
        
        public void drawSuperBox(GL10 gl, float  i, float j, float height) {
        	float localR = (float)red/COLOR_RANGE,
              	  localG = (float)green/COLOR_RANGE,
              	  localB = (float)blue/COLOR_RANGE;
        	
            //FloatBuffer Face1Buffer;
            FloatBuffer Face2Buffer;
            FloatBuffer Face3Buffer;
            //FloatBuffer Face4Buffer;
            FloatBuffer Face5Buffer;
            FloatBuffer BoxTopColorBuffer;
            FloatBuffer BoxSideColorBuffer;
            
            /*
            float[] face1 = new float[] {
            		1.0f, height*1.0f, 0,
            		1.0f, height*1.0f, -1.0f,
            		1.0f, 0, 0,
            		1.0f, 0, -1.0f
            };
            */
            float[] face2 = new float[] {
            		0, 0, 0,
            		0, height*1.0f, 0,
            		0, 0, -1.0f,
            		0, height*1.0f, -1.0f
            };
            
            float[] face3 = new float[] {
            		0, 		0, 0,
            		1.0f, 	0, 0,
            		0, 		height*1.0f, 0,
            		1.0f, 	height*1.0f, 0
            };
            /*
            float[] face4 = new float[] {
            		0, 		0, -1.0f,
            		1.0f, 	0, -1.0f,
            		0, 		height*1.0f, -1.0f,
            		1.0f, 	height*1.0f, -1.0f
            };
            */
            float[] face5 = new float[] {
            		0, 		height*1.0f, 0,
            		1.0f, 	height*1.0f, 0,
            		0, 		height*1.0f, -1.0f,
            		1.0f, 	height*1.0f, -1.0f
            };
            
            float[] boxTopColor = new float[] {
            		localR, localG, localB, 1.0f,  
            		localR, localG, localB, 1.0f,
            		localR, localG, localB, 1.0f,
            		localR, localG, localB, 1.0f
            };
            
            float[] boxSideColor = new float[] {
            		0, 		0, 		0, 		1.0f,  
            		localR, localG, localB, 1.0f,
            		0, 		0, 		1.0f, 	1.0f,
            		1.0f, 	1.0f, 	1.0f, 	1.0f
            };
            
        	//Face1Buffer = floatToBuffer(face1);
        	Face2Buffer = OpenGLTools.floatToBuffer(face2);
        	Face3Buffer = OpenGLTools.floatToBuffer(face3);
        	//Face4Buffer = floatToBuffer(face4);
        	Face5Buffer = OpenGLTools.floatToBuffer(face5);
        	BoxTopColorBuffer = OpenGLTools.floatToBuffer(boxTopColor);
        	BoxSideColorBuffer = OpenGLTools.floatToBuffer(boxSideColor);
        	
            gl.glTranslatef(i, 0.0f, -j);
            //drawPointsAndColorsFloat(gl, Face1Buffer, BoxColorBuffer, GL10.GL_TRIANGLE_STRIP, 4);
            OpenGLTools.drawPointsAndColorsFloat(gl, Face2Buffer, BoxSideColorBuffer, GL10.GL_TRIANGLE_STRIP, 4);
            OpenGLTools.drawPointsAndColorsFloat(gl, Face3Buffer, BoxTopColorBuffer, GL10.GL_TRIANGLE_STRIP, 4);
            //drawPointsAndColorsFloat(gl, Face4Buffer, BoxColorBuffer, GL10.GL_TRIANGLE_STRIP, 4);
            OpenGLTools.drawPointsAndColorsFloat(gl, Face5Buffer, BoxTopColorBuffer, GL10.GL_TRIANGLE_STRIP, 4);
            gl.glTranslatef(-i, 0.0f, j);
        }
    }
    
    
    @Override
  	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
	    return false;  
	} 
    
    
  	@Override
  	public boolean onDown(MotionEvent e) {
  		return false;
  	}
  	
  	
	@Override
	public void onShowPress(MotionEvent e) {
	}
	
	
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		if(isChangingEyeYMode) {
			Toast.makeText(this, "Viewing Mode", 1000).show();
			isChangingEyeYMode = false;
		}else{
			Toast.makeText(this, "Changing Height Mode", 1000).show();
			isChangingEyeYMode = true;
		}
		return false;
	}
	
	
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		
		if(isChangingEyeYMode) {
			deltaY = ((e2.getY() - e1.getY()) / 10);
			deltaY /= 4;
			deltaCentY = deltaY;
			
			deltaX = 0;
			deltaCentX = 0;
			deltaZ = 0;
		}else {
			deltaX = ((e1.getX() - e2.getX()) / 10);
			deltaX /= 2;
	    	//deltaX /= (37 - ShrinkDelta); //Actually 368640 is the maximum size of the file in Data Center.
	    	deltaCentX = deltaX;
	    	
	    	deltaY = 0;
	    	deltaCentY = 0;
	    	
	    	deltaZ = ((e1.getY() - e2.getY()) / 50);
	    	deltaCentZ = ((e1.getY() - e2.getY()) / 50);
		}
    	
    	mGLView.requestRender();
		return false;
	}
	
	
	@Override
	public void onLongPress(MotionEvent e) {
	}
	
	
	public boolean onTouch(View v, MotionEvent event) {  
		return mGestureDetector.onTouchEvent(event);
	}
	
	
	public double[][] transToSpectrogram(short[] sigs, int interval) {
		short cuttedRawSigs[] = new short[interval];
		
		int iteration = sigs.length / interval;
		double[][] transInnerSigs = new double[iteration][];
		
		FFT fft = new FFT(interval);
		for(int i=0; i<iteration; i++) {
			for(int j=0; j<interval; j++) {
				cuttedRawSigs[j] = sigs[j+interval*i];
			}
			transInnerSigs[i] = fft.calculateFFT(cuttedRawSigs);
		}
		return transInnerSigs;
	}
	
	
	/**
	 * Sets the color to be drawn on the bitmap next<br><br>
	 * this.red =  ;<br>
	 * this.green =  ;<br>
	 * this.blue =  ;<br>
	 * @param colorIterator
	 */
	public void colorSelecter(final int colorIterator) {
		int colorFactor;
        if (colorIterator < 0) 
        {
            colorFactor = 0;
        }else {
            colorFactor = colorIterator;
        }
        
        if (colorFactor / COLOR_RANGE == 0)
        {
            this.red = 0;
            this.green = 0;
            this.blue = colorFactor % COLOR_RANGE;
        }
        
        if (colorFactor / COLOR_RANGE == 1)
        {
            this.red = 0;
            this.green = colorFactor % COLOR_RANGE;
            this.blue = COLOR_RANGE - 1;
        }
        
        if (colorFactor / COLOR_RANGE == 2)
        {
            this.red = 0;
            this.green = COLOR_RANGE - 1;
            //this.blue = (COLOR_RANGE & COLOR_RANGE - 1 - colorFactor);
            this.blue = 0;
        }
        
        if (colorFactor / COLOR_RANGE == 3)
        {
            this.red   = colorFactor % COLOR_RANGE;
            this.green = COLOR_RANGE - 1;
            this.blue  = 0;
        }
        
        if (colorFactor / COLOR_RANGE == 4) 
        {
            this.red   = COLOR_RANGE - 1;
            this.green = COLOR_RANGE - 1 - colorFactor % COLOR_RANGE;
            this.blue  = 0;
        }
        
        if (colorFactor / COLOR_RANGE == 5) 
        {
            this.red = COLOR_RANGE - 1;
            this.green = 0;
            this.blue = colorFactor % COLOR_RANGE;
        }
        
        if (colorFactor / COLOR_RANGE == 6) 
        {
            this.red = COLOR_RANGE - 1;
            this.green = colorFactor % COLOR_RANGE;
            this.blue = COLOR_RANGE - 1;
        }
        
        if (colorFactor / COLOR_RANGE == 7) 
        {
            this.red = COLOR_RANGE - 1;
            this.green = COLOR_RANGE - 1;
            this.blue = COLOR_RANGE - 1;
        }
	}
}

