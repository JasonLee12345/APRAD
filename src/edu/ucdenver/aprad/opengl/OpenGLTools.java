package edu.ucdenver.aprad.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

import edu.ucdenver.aprad.education.rebuilder.ReadSavedFileData;

/*****************************
 **
 ** @author Kun Li
 ** @created 27 Apr 2015
 ** 
 *****************************/

public class OpenGLTools {
	
	/**
     * Tools for allocating buffer.
     * @param a
     * @return
     */
    public static IntBuffer intToBuffer(int[] a) {
    	IntBuffer mBuffer;
    	ByteBuffer mbb = ByteBuffer.allocateDirect(a.length*4);
        mbb.order(ByteOrder.nativeOrder());  
        mBuffer = mbb.asIntBuffer();  
        mBuffer.put(a);  
        mBuffer.position(0);  
        return mBuffer;  
    }
    
    
    public static FloatBuffer floatToBuffer(float[] a) {
    	FloatBuffer mBuffer;
    	ByteBuffer mbb = ByteBuffer.allocateDirect(a.length*4);
        mbb.order(ByteOrder.nativeOrder());  
        mBuffer = mbb.asFloatBuffer();  
        mBuffer.put(a);  
        mBuffer.position(0);  
        return mBuffer;  
    }
    
    
    public static void drawPointsAndColorsInt(GL10 gl, IntBuffer p, IntBuffer c, int type, int size) {
        gl.glVertexPointer(3, GL10.GL_FIXED, 0, p);
        gl.glColorPointer(4, GL10.GL_FIXED, 0, c);
        gl.glDrawArrays(type, 0, size); 
    }
    
    
    public static void drawPointsAndColorsFloat(GL10 gl, FloatBuffer p, FloatBuffer c, int type, int size) {
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, p);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, c);
        gl.glDrawArrays(type, 0, size);
    }
    
    
    public static void drawPointsAndColorsFloat(GL10 gl, FloatBuffer p, int type, int size) {
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, p);
        gl.glDrawArrays(type, 0, size);
    }
    
    
    /**
     * 
     * @param gl
     * @param value
     * @param x
     * @param y
     * @param z
     * @param orientation 1 means the number towards x direction, 2 means y direction, 3 means z.
     */
    public static void drawNumbers(GL10 gl, float value, float x, float y, float z, int orientation) {
    	/**
    	 * Calculate the length of the value
    	 */
    	int length;
    	int intValue = (int)value;
    	if(intValue == 0) 
        	length = 1;
    	else {
    		length = 0;
    		for(; intValue!=0; length++) {
    			intValue /= 10;
    		}
    	}
    	
    	/**
    	 * Put each individual digit into an array, numbers[length]
    	 */
    	intValue = (int)value;
    	int[] numbers = new int[length];
    	for(int i=0; i<length; i++) {
    		numbers[length-1-i] = intValue % 10;
    		intValue /= 10;
    	}
    	
    	for(int i=0; i<length; i++) {
    		switch(numbers[i]) {
    			case 0: drawZero(gl,	(float) (x+(float)i*1.4), y, z, orientation);
    					break;
    			case 1: drawOne(gl, 	(float) (x+(float)i*1.4), y, z, orientation);
						break;
    			case 2: drawTwo(gl, 	(float) (x+(float)i*1.4), y, z, orientation);
						break;
    			case 3: drawThree(gl, 	(float) (x+(float)i*1.4), y, z, orientation);
						break;
    			case 4: drawFour(gl, 	(float) (x+(float)i*1.4), y, z, orientation);
						break;
    			case 5: drawFive(gl, 	(float) (x+(float)i*1.4), y, z, orientation);
						break;
    			case 6: drawSix(gl, 	(float) (x+(float)i*1.4), y, z, orientation);
						break;
    			case 7: drawSeven(gl, 	(float) (x+(float)i*1.4), y, z, orientation);
						break;
    			case 8: drawEight(gl, 	(float) (x+(float)i*1.4), y, z, orientation);
						break;
    			case 9: drawNine(gl, 	(float) (x+(float)i*1.4), y, z, orientation);
						break;
    		}
    	}
    }//drawNumbers ends
    
    public static void drawZero(GL10 gl, float x, float y, float z, int o) {
    	float[] p = new float[5*3];
        p[0] = x; 		p[1] = y; 		p[2] = z;
        p[3] = x+1; 	p[4] = y; 		p[5] = z;
        p[6] = x+1; 	p[7] = y-2; 	p[8] = z;
        p[9] = x; 		p[10] = y-2; 	p[11] = z;
        p[12] = x;		p[13] = y;		p[14] = z;
        
        float[] c = new float[5*4];
        for(int i=0; i<5; i++) {
        	c[4*i] = 0.0f;
        	c[4*i+1] = 0.0f;
        	c[4*i+2] = 1.0f;
        	c[4*i+3] = 1.0f;
        }
        
        FloatBuffer pBuffer;
        FloatBuffer cBuffer;
        pBuffer = floatToBuffer(p);
        cBuffer = floatToBuffer(c);
        
        drawPointsAndColorsFloat(gl, pBuffer, cBuffer, GL10.GL_LINE_STRIP, 5);
    }
    
    public static void drawOne(GL10 gl, float x, float y, float z, int o) {
    	float[] p = new float[2*3];
        p[0] = x+1; 	p[1] = y; 		p[2] = z;
        p[3] = x+1; 	p[4] = y-2; 	p[5] = z;
        
        float[] c = new float[2*4];
        for(int i=0; i<2; i++) {
        	c[4*i] = 0.0f;
        	c[4*i+1] = 0.0f;
        	c[4*i+2] = 1.0f;
        	c[4*i+3] = 1.0f;
        }
        
        FloatBuffer pBuffer;
        FloatBuffer cBuffer;
        pBuffer = floatToBuffer(p);
        cBuffer = floatToBuffer(c);
        
        drawPointsAndColorsFloat(gl, pBuffer, cBuffer, GL10.GL_LINE_STRIP, 2);
    }
    
    public static void drawTwo(GL10 gl, float x, float y, float z, int o) {
    	float[] p = new float[6*3];
        p[0] = x; 		p[1] = y; 		p[2] = z;
        p[3] = x+1; 	p[4] = y; 		p[5] = z;
        p[6] = x+1; 	p[7] = y-1; 	p[8] = z;
        p[9] = x; 		p[10] = y-1; 	p[11] = z;
        p[12] = x;		p[13] = y-2;	p[14] = z;
        p[15] = x+1;	p[16] = y-2;	p[17] = z;
        
        float[] c = new float[6*4];
        for(int i=0; i<6; i++) {
        	c[4*i] = 0.0f;
        	c[4*i+1] = 0.0f;
        	c[4*i+2] = 1.0f;
        	c[4*i+3] = 1.0f;
        }
        
        FloatBuffer pBuffer;
        FloatBuffer cBuffer;
        pBuffer = floatToBuffer(p);
        cBuffer = floatToBuffer(c);
        
        drawPointsAndColorsFloat(gl, pBuffer, cBuffer, GL10.GL_LINE_STRIP, 6);
    }
    
    public static void drawThree(GL10 gl, float x, float y, float z, int o) {
    	float[] p = new float[7*3];
        p[0] = x; 		p[1] = y; 		p[2] = z;
        p[3] = x+1; 	p[4] = y; 		p[5] = z;
        p[6] = x+1; 	p[7] = y-1; 	p[8] = z;
        p[9] = x; 		p[10] = y-1; 	p[11] = z;
        p[12] = x+1;	p[13] = y-1;	p[14] = z;
        p[15] = x+1;	p[16] = y-2;	p[17] = z;
        p[18] = x;		p[19] = y-2;	p[20] = z;
        
        float[] c = new float[7*4];
        for(int i=0; i<7; i++) {
        	c[4*i] = 0.0f;
        	c[4*i+1] = 0.0f;
        	c[4*i+2] = 1.0f;
        	c[4*i+3] = 1.0f;
        }
        
        FloatBuffer pBuffer;
        FloatBuffer cBuffer;
        pBuffer = floatToBuffer(p);
        cBuffer = floatToBuffer(c);
        
        drawPointsAndColorsFloat(gl, pBuffer, cBuffer, GL10.GL_LINE_STRIP, 7);
    }    
    
    public static void drawFour(GL10 gl, float x, float y, float z, int o) {
    	float[] p = new float[5*3];
        p[0] = x; 		p[1] = y; 		p[2] = z;
        p[3] = x; 		p[4] = y-1; 	p[5] = z;
        p[6] = x+1; 	p[7] = y-1; 	p[8] = z;
        p[9] = x+1; 	p[10] = y; 		p[11] = z;
        p[12] = x+1;	p[13] = y-2;	p[14] = z;
        
        float[] c = new float[5*4];
        for(int i=0; i<5; i++) {
        	c[4*i] = 0.0f;
        	c[4*i+1] = 0.0f;
        	c[4*i+2] = 1.0f;
        	c[4*i+3] = 1.0f;
        }
        
        FloatBuffer pBuffer;
        FloatBuffer cBuffer;
        pBuffer = floatToBuffer(p);
        cBuffer = floatToBuffer(c);
        
        drawPointsAndColorsFloat(gl, pBuffer, cBuffer, GL10.GL_LINE_STRIP, 5);
    }
    
    public static void drawFive(GL10 gl, float x, float y, float z, int o) {
    	float[] p = new float[6*3];
        p[0] = x+1; 	p[1] = y; 		p[2] = z;
        p[3] = x; 		p[4] = y; 		p[5] = z;
        p[6] = x; 		p[7] = y-1; 	p[8] = z;
        p[9] = x+1;		p[10] = y-1;	p[11] = z;
        p[12] = x+1;	p[13] = y-2;	p[14] = z;
        p[15] = x;		p[16] = y-2;	p[17] = z;
        
        float[] c = new float[6*4];
        for(int i=0; i<6; i++) {
        	c[4*i] = 0.0f;
        	c[4*i+1] = 0.0f;
        	c[4*i+2] = 1.0f;
        	c[4*i+3] = 1.0f;
        }
        
        FloatBuffer pBuffer;
        FloatBuffer cBuffer;
        pBuffer = floatToBuffer(p);
        cBuffer = floatToBuffer(c);
        
        drawPointsAndColorsFloat(gl, pBuffer, cBuffer, GL10.GL_LINE_STRIP, 6);
    }    
    
    public static void drawSix(GL10 gl, float x, float y, float z, int o) {
    	float[] p = new float[6*3];
        p[0] = x+1; 	p[1] = y; 		p[2] = z;
        p[3] = x; 		p[4] = y; 		p[5] = z;
        p[6] = x; 		p[7] = y-2; 	p[8] = z;
        p[9] = x+1; 	p[10] = y-2; 	p[11] = z;
        p[12] = x+1;	p[13] = y-1;	p[14] = z;
        p[15] = x;		p[16] = y-1;	p[17] = z;
        
        float[] c = new float[6*4];
        for(int i=0; i<6; i++) {
        	c[4*i] = 0.0f;
        	c[4*i+1] = 0.0f;
        	c[4*i+2] = 1.0f;
        	c[4*i+3] = 1.0f;
        }
        
        FloatBuffer pBuffer;
        FloatBuffer cBuffer;
        pBuffer = floatToBuffer(p);
        cBuffer = floatToBuffer(c);
        
        drawPointsAndColorsFloat(gl, pBuffer, cBuffer, GL10.GL_LINE_STRIP, 6);
    }
    
    public static void drawSeven(GL10 gl, float x, float y, float z, int o) {
    	float[] p = new float[3*3];
        p[0] = x; 		p[1] = y; 		p[2] = z;
        p[3] = x+1; 	p[4] = y; 		p[5] = z;
        p[6] = x+1; 	p[7] = y-2; 	p[8] = z;
        
        float[] c = new float[3*4];
        for(int i=0; i<3; i++) {
        	c[4*i] = 0.0f;
        	c[4*i+1] = 0.0f;
        	c[4*i+2] = 1.0f;
        	c[4*i+3] = 1.0f;
        }
        
        FloatBuffer pBuffer;
        FloatBuffer cBuffer;
        pBuffer = floatToBuffer(p);
        cBuffer = floatToBuffer(c);
        
        drawPointsAndColorsFloat(gl, pBuffer, cBuffer, GL10.GL_LINE_STRIP, 3);
    }
    
    public static void drawEight(GL10 gl, float x, float y, float z, int o) {
    	float[] p = new float[7*3];
        p[0] = x; 		p[1] = y; 		p[2] = z;
        p[3] = x+1; 	p[4] = y; 		p[5] = z;
        p[6] = x+1; 	p[7] = y-2; 	p[8] = z;
        p[9] = x; 		p[10] = y-2; 	p[11] = z;
        p[12] = x;		p[13] = y;		p[14] = z;
        p[15] = x;		p[16] = y-1;	p[17] = z;
        p[18] = x+1;	p[19] = y-1;	p[20] = z;
        
        float[] c = new float[7*4];
        for(int i=0; i<7; i++) {
        	c[4*i] = 0.0f;
        	c[4*i+1] = 0.0f;
        	c[4*i+2] = 1.0f;
        	c[4*i+3] = 1.0f;
        }
        
        FloatBuffer pBuffer;
        FloatBuffer cBuffer;
        pBuffer = floatToBuffer(p);
        cBuffer = floatToBuffer(c);
        
        drawPointsAndColorsFloat(gl, pBuffer, cBuffer, GL10.GL_LINE_STRIP, 7);
    }
    
    public static void drawNine(GL10 gl, float x, float y, float z, int o) {
    	float[] p = new float[6*3];
        p[0] = x+1; 	p[1] = y-1; 	p[2] = z;
        p[3] = x; 		p[4] = y-1; 	p[5] = z;
        p[6] = x; 		p[7] = y; 		p[8] = z;
        p[9] = x+1; 	p[10] = y; 		p[11] = z;
        p[12] = x+1;	p[13] = y-2;	p[14] = z;
        p[15] = x;		p[16] = y-2;	p[17] = z;
        
        float[] c = new float[6*4];
        for(int i=0; i<6; i++) {
        	c[4*i] = 0.0f;
        	c[4*i+1] = 0.0f;
        	c[4*i+2] = 1.0f;
        	c[4*i+3] = 1.0f;
        }
        
        FloatBuffer pBuffer;
        FloatBuffer cBuffer;
        pBuffer = floatToBuffer(p);
        cBuffer = floatToBuffer(c);
        
        drawPointsAndColorsFloat(gl, pBuffer, cBuffer, GL10.GL_LINE_STRIP, 6);
    }
    
}
