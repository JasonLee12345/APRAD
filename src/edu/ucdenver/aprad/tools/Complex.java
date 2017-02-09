package edu.ucdenver.aprad.tools; 

/**
 * Complex Object
 * <P>Various attributes and methods related to calculation of Complex numbers.
 * 
 * @author Dan Rolls
 * @author Michael Dewar
 * @version 1.0
 */
public class Complex 
{
    private final double real;   
    private final double imaginary;

    /**
     * Constructor for Complex class
     * @param real The real part of the complex number
     * @param imag The imaginary part of the complex number
     */
    public Complex(double real, double imag) 
    {
        this.real = real;
        this.imaginary = imag;
    }

    /**
     * Addition of two complex numbers
     * @param b The Complex number to be evaluated
     * @return this Complex plus b Complex
     */
    public Complex plus(Complex b) 
    {
        Complex a = this;             
        double real = a.real + b.real;
        double imag = a.imaginary + b.imaginary;
        return new Complex(real, imag);
    }

    /**
     * Subtraction of two complex numbers
     * @param b The complex number to be evaluated
     * @return this Complex minux b Complex
     */
    public Complex minus(Complex b) 
    {
        Complex a = this;
        double real = a.real - b.real;
        double imag = a.imaginary - b.imaginary;
        return new Complex(real, imag);
    }

    /**
     * Multiplication of two complex numbers
     * @param b The complex to be evaluated
     * @return this Complex times b Complex
     */
    public Complex times(Complex b) 
    {
        Complex a = this;
        double real = a.real * b.real - a.imaginary * b.imaginary;
        double imag = a.real * b.imaginary + a.imaginary * b.real;
        return new Complex(real, imag);
    }

    /**
     * Returns real part
     * @return real part of complex number
     */
    public double real() 
    { 
    	return this.real; 
    }
    
    /**
     * Returns imaginary part
     * @return  imaginary part of complex number
     */
    public double imaginary() 
    { 
    	return this.imaginary; 
    }
    
    /**
     * toString class returner
     * @return String representation of Complex class
     */
    public String toString() 
    {
        if (this.imaginary == 0) 
        	return this.real + "";
        if (this.real == 0) 
        	return this.imaginary + "i";
        if (this.imaginary <  0) 
        	return this.real + " - " + (-this.imaginary) + "i";
        
        return this.real + " + " + this.imaginary + "i";
    }
}
