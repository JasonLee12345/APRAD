package edu.ucdenver.aprad.tools;

import edu.ucdenver.aprad.time_domain.RawSignal;
import edu.ucdenver.aprad.time_domain.RawSignalView;
import edu.ucdenver.aprad.education.synthesize.SynthesizeWaveDataView;
import edu.ucdenver.aprad.spectrogram.SpectrogramView;
import edu.ucdenver.aprad.spectrum_analyzer.SpectrumAnalyzer;
import edu.ucdenver.aprad.spectrum_analyzer.SpectrumAnalyzerView;
import edu.ucdenver.aprad.spectrogram.Spectrogram;

/*****************************
 **
 ** @author Kun Li
 ** @created 15 Oct 2014
 ** 
 ** @modified_by 
 ** @modified_date 
 **
 *****************************/

public class AudioRecorderListenerManipulate implements AudioRecordListener
{
	public static final int NUM_SAMPLES = 512;
	public static double frequency = 8000.0;
	
	private static SpectrumAnalyzerView spectrumVisualizer;
	private static Spectrogram spectrogram;
	private static RawSignalView rawSignalVisualizer;
	private static SynthesizeWaveDataView synthesizeVisualizer;
	private static AudioRecordListener audioListener;
	
	private FFT fft = new FFT( NUM_SAMPLES );
	
	/**
	 * register the audiolistener to the class listner
	 * @param listener AudioRecord listener
	 */
	public static void registerFFTAvailListenerOnSpectrumAnalyzer( AudioRecorderListenerManipulate manipulater, SpectrumAnalyzerView spectrumVisual )
	{
		audioListener = manipulater;
		spectrumVisualizer = spectrumVisual;
	}
	
	
	public static void registerFFTAvailListenerOnSpectrogram( AudioRecorderListenerManipulate manipulater, Spectrogram spectro )
	{
		audioListener = manipulater;
		spectrogram = spectro;
	}
	
	public static void registerFFTAvailListenerOnRaw( AudioRecorderListenerManipulate manipulater, RawSignalView rawVisual )
	{
		audioListener = manipulater;
		rawSignalVisualizer = rawVisual;
	}
	
	
	public static void registerFFTAvailListenerOnSynthesize( SynthesizeWaveDataView synVisual )
	{
		synthesizeVisualizer = synVisual;
	}
	
	
	/**
	 * Unregisters listener. Sets listener to null 
	 */
	public static void unregisterFFTAvailListener( )
	{
		audioListener 			= null;
		spectrumVisualizer 		= null;
		spectrogram 			= null;
		rawSignalVisualizer 	= null;
		synthesizeVisualizer 	= null;
	}
	
	
	
	/**
	 * Lets SpectrumAnalyzer  know to go ahead and draw
	 * @param signal The raw data converted into an FFT
	 */
	public static void notifyListenerSignalRead( short[] signal )
	{
		if( audioListener != null )
		{		
			if(spectrumVisualizer!=null)
				audioListener.onDrawableSampleAvailable( signal );
			else			
				if(spectrogram!=null)
				{
					AudioRecorderListenerManipulate manipulate = new AudioRecorderListenerManipulate();
					manipulate.onDrawableSampleAvailableOnSpectrogram( signal );
				}
				else
				{
						AudioRecorderListenerManipulate manipulate = new AudioRecorderListenerManipulate();
						manipulate.onDrawableSampleAvailableOnRawSignal( signal );		
			
				}
			
		}
	}
	
	
	/**
	 * Implements listener. 
	 * Creates a  new  Runnable but runs it on the UI thread
	 * @param signal Array of FFT Signals
	 */
	@Override
	public void onDrawableSampleAvailable( short[] signal ) 
	{
		
	  double[] transformedSignal = fft.calculateFFT( signal );
	  AudioRecorderListenerManipulate.spectrumVisualizer.drawSpectrum( transformedSignal, frequency, 
			  															NUM_SAMPLES, NUM_SAMPLES );
	}
	
	
	public double[] convertShortToDouble(short[] signal)
	{
		double[] convertedSignal = new double[NUM_SAMPLES];
		for(int i=0;i<signal.length;i++)
		{
			convertedSignal[i] = (double)signal[i];	
		}
	
		return convertedSignal;
	}
	
	
	
	public void onDrawableSampleAvailableOnSpectrogram( short[] signal )
	{
	  AudioRecorderListenerManipulate.spectrogram.onDrawableSampleAvailable(signal);
 
	}
	
	
	public void onDrawableSampleAvailableOnRawSignal( short[] signal ) 
	{
		double[] convertedSignal = convertShortToDouble(signal);
		AudioRecorderListenerManipulate.rawSignalVisualizer.drawSpectrum( convertedSignal, frequency, 
			  															  NUM_SAMPLES, NUM_SAMPLES );
	  
	}
	
	public static void onDrawableSampleAvailableOnSynthesize() 
	{
	    AudioRecorderListenerManipulate.synthesizeVisualizer.drawSynthesize();
	  
	}
	
}
