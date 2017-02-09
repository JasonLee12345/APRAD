package edu.ucdenver.aprad.tools;

/**
 * Interface for AudioRecord to let SpecAnalyView to start drawing
 * @author Dan Rolls
 * @author Michael Dewar
 */
public interface AudioRecordListener {
	
	void onDrawableSampleAvailable(short[] signal);
	
	//void onDrawableSampleAvailableOnSpectrogram(short[] signal);
	
}
