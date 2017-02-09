package edu.ucdenver.aprad.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import edu.ucdenver.aprad.R;
import edu.ucdenver.aprad.education.rebuilder.ReadSavedFileData;
import edu.ucdenver.aprad.preferences.PreferencesSpectrogram;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.Toast;

/*****************************
 **
 ** @author Kun Li
 ** @created 6 Apr 2015
 ** 
 ** @modified_by Kun Li
 ** @modified_date 9 Apr 2015
 **
 ** @modified_by Kun Li
 ** @modified_date 26 Apr 2015
 *****************************/

public class OpenGLforTD extends Activity implements OnTouchListener, OnGestureListener {
	
    private GLSurfaceView mGLView;
    GestureDetector mGestureDetector; 
    float eyeX = 50, eyeY = 10.0f, eyeZ = 120;
    float centX = 40, centY = 0, centZ = -5.5f;
    float deltaX = 0, deltaY = 0, deltaZ = 0;
    float deltaCentX = 0, deltaCentY = 0, deltaCentZ = 0;
    boolean isChangingEyeYMode = false;
    
    public static int coordinateLinesInterval = 1500;
    
    /**
     * The variable for shrinking x and y axis.
     */
    static int ShrinkNum = 10;
    
    /**
     * For shrinking the actual values of saved data in files.
     */
    static int ShrinkYValues = 20;
    
    /**
     * For how quickly you want to drag the graph on the screen.
     */
    static int ShrinkDelta = ReadSavedFileData.theLen / 10000;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);
        
        mGestureDetector = new GestureDetector((OnGestureListener) this);  
        mGLView.setOnTouchListener(this);    
        mGLView.setLongClickable(true);
        
        Intent mIntent = this.getIntent();
        ReadSavedFileData gotFile = (ReadSavedFileData) mIntent.getSerializableExtra("aa");
        if (null != gotFile) {
        	
        }
    }
    
    
    class MyGLSurfaceView extends GLSurfaceView {
        public MyGLSurfaceView(Context context)
        {
            super(context);

            try
            {
                // Create an OpenGL ES 2.0 context
            	// If you want to use your own shader, turn on the line below.
                //setEGLContextClientVersion(2);

                // Set the Renderer for drawing on the GLSurfaceView
                setRenderer(new MyRenderer());

                // Render the view only when there is a change in the drawing
                // data
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
        
        private int[] xAxis = new int[] {
        		0, 0, 0,
        		(ReadSavedFileData.theLen/ShrinkNum) * one, 0, 0
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
        
        private int[] smallAxisColor = new int[] {
        		0, 0, 1 * one, one,  
                0, 0, 1 * one, one
        };
        
        private int[][] coordinateLines;
        private float[]	coordinateValues;
        private float[] dataTD;
        private float[] dataTDColor;
        
        private IntBuffer CoordinateXBuffer;
        private IntBuffer CoordinateYBuffer;
        private IntBuffer CoordinateZBuffer;
        private IntBuffer[] CoordinateLinesBuffer = new IntBuffer[ReadSavedFileData.theLen/coordinateLinesInterval];
        private IntBuffer CoordinateColorBuffer;
        private IntBuffer CoordinateSmallColorBuffer;
        
        private FloatBuffer dataTDBuffer;
        private FloatBuffer dataTDColorBuffer;
        
        
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        	gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
            gl.glClearColor(0.9f, 0.9f, 0.9f, 1.0f);
            gl.glShadeModel(GL10.GL_SMOOTH);

            gl.glClearDepthf(1.0f);
            gl.glEnable(GL10.GL_DEPTH_TEST);
            gl.glDepthFunc(GL10.GL_LEQUAL);
            
            coordinateLines = new int[ReadSavedFileData.theLen/coordinateLinesInterval][6];
            coordinateValues = new float[ReadSavedFileData.theLen/coordinateLinesInterval];
            for(int i=0; i<ReadSavedFileData.theLen/coordinateLinesInterval; i++) {
            	coordinateLines[i][0] = (i * coordinateLinesInterval / ShrinkNum) * one;
            	coordinateLines[i][1] = 0;
            	coordinateLines[i][2] = 15 * one;
            	
            	coordinateLines[i][3] = (i * coordinateLinesInterval / ShrinkNum) * one;
                coordinateLines[i][4] = 0;
                coordinateLines[i][5] = -10 * one;
                
                coordinateValues[i] = (float)i * coordinateLinesInterval;
            }
            
            dataTD = new float[ReadSavedFileData.theLen*3];
            for(int i=0; i<ReadSavedFileData.theLen; i++) {
            	dataTD[3*i] = (float)i/ShrinkNum;
            	dataTD[3*i+1] = (float)ReadSavedFileData.actualData[i]/ShrinkYValues;
            	dataTD[3*i+2] = 0.0f;
            }
            
            dataTDColor = new float[ReadSavedFileData.theLen*4];
            for(int i=0; i<ReadSavedFileData.theLen; i++) {
            	dataTDColor[4*i] = 0.133f;
            	dataTDColor[4*i+1] = 0.543f;
            	dataTDColor[4*i+2] = 0.133f;
            	dataTDColor[4*i+3] = 1.0f;
            }
            
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
        	for(int i=0; i<ReadSavedFileData.theLen/coordinateLinesInterval; i++) {
        		CoordinateLinesBuffer[i] = OpenGLTools.intToBuffer(coordinateLines[i]);
        	}
        	CoordinateColorBuffer = OpenGLTools.intToBuffer(axisColor);
        	CoordinateSmallColorBuffer = OpenGLTools.intToBuffer(smallAxisColor);
        	
        	dataTDBuffer = OpenGLTools.floatToBuffer(dataTD);
        	dataTDColorBuffer = OpenGLTools.floatToBuffer(dataTDColor);
            ////////////////////////////////////////////////////////////////////////
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);  
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);  
            ////////////////////////////////////////////////////////////////////////
            //gl.glTranslatef(1.5f, 0.0f, 6.0f);
            gl.glLineWidth(8);
            OpenGLTools.drawPointsAndColorsInt(gl, CoordinateXBuffer, CoordinateColorBuffer, GL10.GL_LINES, 2);
            OpenGLTools.drawPointsAndColorsInt(gl, CoordinateYBuffer, CoordinateColorBuffer, GL10.GL_LINES, 2);
            OpenGLTools.drawPointsAndColorsInt(gl, CoordinateZBuffer, CoordinateColorBuffer, GL10.GL_LINES, 2);
            gl.glLineWidth(4);
            for(int i=0; i<ReadSavedFileData.theLen/coordinateLinesInterval; i++) {
            	OpenGLTools.drawPointsAndColorsInt(gl, CoordinateLinesBuffer[i], CoordinateSmallColorBuffer, GL10.GL_LINES, 2);
            	OpenGLTools.drawNumbers(gl, coordinateValues[i], i * coordinateLinesInterval / ShrinkNum + 1, 4, 0, 3);
            }
            //OpenGLTools.drawNumbers(gl, 7654321, 200/ShrinkNum, 8, 0, 3);
            gl.glLineWidth(2);
            OpenGLTools.drawPointsAndColorsFloat(gl, dataTDBuffer, dataTDColorBuffer, GL10.GL_LINE_STRIP, ReadSavedFileData.theLen);
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
            //gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
            GLU.gluPerspective(gl, 80, ratio, 1, 400000);
            gl.glMatrixMode(GL10.GL_MODELVIEW); 
            gl.glLoadIdentity();
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
	    	deltaZ = ((e1.getY() - e2.getY()) / 50);
	    	
	    	deltaY = 0;
	    	deltaCentY = 0;
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
}
