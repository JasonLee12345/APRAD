package edu.ucdenver.aprad.tools;

/**
 * Class FFT
 * Used to do a Cooley-Tukey FFT type
 * @link http://en.wikipedia.org/wiki/Cooley%E2%80%93Tukey_FFT_algorithm
 * @author Dan Rolls
 * @author Michael Dewar
 */
/*****************************
 **
 ** @author Kun Li
 ** @created 
 ** 
 ** @modified_by Kun Li
 ** @modified_date 3 Apr 2015
 ** @category Adding a pre-calculation 
 ** procedure to select a qualified argument
 ** which is the power of 2 not bigger than incoming argument.
 *****************************/

public class FFT
{
	private int numFFTSamples;
	private double maxFFTSample;

	/**
	 * Constructor
	 * @param points The number of FFT Samples
	 */
	public FFT(int points)
	{
		// To find a maximum number which is the power of 2 not bigger than "points".
		int rotation = 2;
		while(rotation <= points)
		{
			rotation = rotation*2;
		}
		rotation = rotation/2;
		this.numFFTSamples = rotation;
	}

	/**
	 * Start doing calculations for FFT
	 * @param signal Raw byte signal from AudioRecorder
	 * @return double[] of FFT results
	 */
	public double[] calculateFFT(short[] signal)
	{
		double temp;
		Complex[] y;
		Complex[] complexSignal = new Complex[numFFTSamples];
		double[] signalProcess = new double[numFFTSamples/2];

		for(int i = 0; i < numFFTSamples; i++){
			temp = (double)signal[i] / 32768.0F;
			complexSignal[i] = new Complex(temp,0.0);
		}

		y = fft(complexSignal);

		maxFFTSample = 0.0;
		for(int i = 0; i < (numFFTSamples/2); i++)
		{
			 signalProcess[i] =  Math.sqrt(Math.pow(y[i].real(), 2) + Math.pow(y[i].imaginary(), 2)) ;

			 if(signalProcess[i] > maxFFTSample)
			 {
				 maxFFTSample = signalProcess[i];
			 }
		}

		return signalProcess;
	}

	/**
	 *  Retrieves the maxFFT Sample
	 * @return
	 */
	public double getMaxFFTSample(){
		return maxFFTSample;
	}

	/**
	 * Provides the finalized FFT result
	 * @param complex Complex number
	 * @return an array of complex numbers
	 */
	public static Complex[] fft(Complex[] complex)
    {
        int N = complex.length;
        
        // base case
        if (N == 1) return new Complex[]
        {
        	complex[0]
		};

        // In order for FFT to work, it needs to be a power of 2
        if (N % 2 != 0)
        {
        	throw new RuntimeException("N is not a power of 2");
        }

        // fft of even terms
        Complex[] even = new Complex[N/2];

        for (int i = 0; i < N/2; i++)
        {
            even[i] = complex[2*i];
        }
        Complex[] q = fft(even);

        // fft of odd terms
        Complex[] odd  = even;
        for (int i = 0; i < N/2; i++) {
            odd[i] = complex[2*i + 1];
        }
        Complex[] r = fft(odd);

        // combine even and odd results
        Complex[] y = new Complex[N];
        for (int i = 0; i < N/2; i++)
        {
            double k = -2 * i * Math.PI / N;
            Complex wk = new Complex(Math.cos(k), Math.sin(k));
            y[i]       = q[i].plus(wk.times(r[i]));
            y[i + N/2] = q[i].minus(wk.times(r[i]));
        }
        return y;
    }
}
