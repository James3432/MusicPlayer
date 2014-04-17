package jk509.player.features;

import jk509.player.Constants;

/*
 *  Set of audio feature data items associated with a track
 */
public class AudioFeatures implements Constants {

	// TODO: is there any way of weighting this feature vector?
	
	public double[] values;    // All that's being used within Song class at present
	
	private double[] features;
	private double[] weights;  // 0==unused 1==important
	private int[]    indices;  // indices into jAudio features array
	private double[] maxs;     // maximum values for each field
	private double[] mins;     // minimum values for each field
	
	public AudioFeatures(){    //                                                                                    x13    x10    x5
		features = new double[]{ msoa, psoa, scoa, srpoa, sfoa, coa, svoa, rmsoa, flewoa, zcoa, sboa, bsoa, ssboa, mfccoa, lpcoa, mmoa, pdoa, pbscoa, pbsfoa, pbssoa, rdfoa, ammoa };
		weights  = new double[]{  0.0, 0.0,  1.0,  1.0,    1.0, 1.0, 1.0,  1.0,   1.0,    1.0,   1.0, 1.0,   1.0,  1.0,   1.0,    1.0,  0.0,  0.0,    0.0,    0.0,    0.0,   0.0   };
		indices  = new int[]   {  0,    1,   3,    9,      15,  21,   27,  33,    39,     45,    53,  59,   65,    89,     95,   101,   107,  108,    114,    120,    126,   132   };
		maxs     = new double[]{ /* TODO */ };
		mins     = new double[]{ /* TODO */ };
	}
	
	public AudioFeatures(double[] vals){
		this();
		values = vals;
	}
	
	public double[] getFeatures(){
		return features;
	}
	
	public double[] getWeights(){
		double[] ws = new double[Constants.FEATURES];
		int next = 0;
		for(int i=0; i<weights.length; ++i){
			if(indices[i] == 89)
				for(int j=0; j<13; ++j)
					ws[next++] = weights[i];
			else if(indices[i] == 95)
				for(int j=0; j<10; ++j)
					ws[next++] = weights[i];
			else if(indices[i] == 101)
				for(int j=0; j<5; ++j)
					ws[next++] = weights[i];
			else if(weights[i] > 0.0)
				ws[next++] = weights[i];
		}
		return ws;
	}
	
	public double[] getMaxs(){
		double[] ms = new double[Constants.FEATURES];
		int next = 0;
		for(int i=0; i<maxs.length; ++i){
			if(indices[i] == 89)
				for(int j=0; j<13; ++j)
					ms[next++] = maxs[i];
			if(indices[i] == 95)
				for(int j=0; j<10; ++j)
					ms[next++] = maxs[i];
			if(indices[i] == 101)
				for(int j=0; j<5; ++j)
					ms[next++] = maxs[i];
			if(weights[i] > 0.0)
				ms[next++] = maxs[i];
		}
		return ms;
	}
	public double[] getMins(){
		double[] ms = new double[Constants.FEATURES];
		int next = 0;
		for(int i=0; i<mins.length; ++i){
			if(indices[i] == 89)
				for(int j=0; j<13; ++j)
					ms[next++] = mins[i];
			if(indices[i] == 95)
				for(int j=0; j<10; ++j)
					ms[next++] = mins[i];
			if(indices[i] == 101)
				for(int j=0; j<5; ++j)
					ms[next++] = mins[i];
			if(weights[i] > 0.0)
				ms[next++] = mins[i];
		}
		return ms;
	}
	
	public double get(int i){
		try{
			return features[i];
		}catch(ArrayIndexOutOfBoundsException e){
			return 0.0;
		}
	}
	
	public boolean[] getFeatureArray(){
		boolean[] arr = new boolean[JAUDIO_FEATURE_COUNT];
		for(int i=0; i<JAUDIO_FEATURE_COUNT; ++i)
			arr[i] = false;
		for(int i=0; i<weights.length; ++i){
			if(weights[i] > 0)
				arr[indices[i]] = true;
		}
		return arr;
	}
	
	/*
	 * Average features over all windows in track
	 */

	private double msoa; 
	// Magnitude Spectrum Overall Average
	// A measure of the strength of different frequency components.
	
	private double psoa; 
	// Power Spectrum Overall Average
	// A measure of the power of different frequency components.
	
	private double scoa;
	// Spectral Centroid Overall Average
	// The centre of mass of the power spectrum.
	
	private double srpoa;
	// Spectral Rolloff Point Overall Average
	// The fraction of bins in the power spectrum at which 85% of the power is at lower frequencies. This is a measure of the right-skewedness of the power spectrum.
	
	private double sfoa;
	// Spectral Flux Overall Average
	// A measure of the amount of spectral change in a signal. Found by calculating the change in the magnitude spectrum from frame to frame.

	private double coa;
	// Compactness Overall Average
	// A measure of the noisiness of a signal. Found by comparing the components of a window's magnitude spectrum with the magnitude spectrum of its neighbouring windows.

	private double svoa;
	// Spectral Variability Overall Average
	// The standard deviation of the magnitude spectrum. This is a measure of the variance of a signal's magnitude spectrum.

	private double rmsoa;
	// Root Mean Square Overall Average
	// A measure of the power of a signal.

	private double flewoa;
	// Fraction Of Low Energy Windows Overall Average
	// The fraction of the last 100 windows that has an RMS less than the mean RMS in the last 100 windows. This can indicate how much of a signal is quiet relative to the rest of the signal.

	private double zcoa;
	// Zero Crossings Overall Average
	// The number of times the waveform changed sign. An indication of frequency as well as noisiness.

	private double sboa; // aka. BPM
	// Strongest Beat Overall Average
	// The strongest beat in a signal, in beats per minute, found by finding the strongest bin in the beat histogram.

	private double bsoa;
	// Beat Sum Overall Average
	// The sum of all entries in the beat histogram. This is a good measure of the importance of regular beats in a signal.

	private double ssboa;
	// Strength Of Strongest Beat Overall Average
	// How strong the strongest beat in the beat histogram is compared to other potential beats.

	private double mfccoa;
	// MFCC Overall Average
	// MFCC calculations based upon Orange Cow code

	private double lpcoa;
	// LPC Overall Average
	// Linear Prediction Coefficients calculated using autocorrelation and Levinson-Durbin recursion.

	private double mmoa;
	// Method of Moments Overall Average
	// Statistical Method of Moments of the Magnitude Spectrum.

	private double pdoa;
	// Peak Detection Overall Average
	// All peaks that are within an order of magnitude of the highest point

	private double pbscoa;
	// Partial Based Spectral Centroid Overall Average
	// Spectral Centroid calculated based on the center of mass of partials instead of center of mass of bins.

	private double pbsfoa;
	// Partial Based Spectral Flux Overall Average
	// Calculate the correlation bettween adjacent frames based peaks instead of spectral bins.  Peak tracking is primitive - when the number of bins changes, the bottom bins are matched sequentially and the extra unmatched bins are ignored.

	private double pbssoa;
	// Peak Based Spectral Smoothness Overall Average
	// Peak Based Spectral Smoothness is calculated from partials, not frequency bins. It is implemented according to McAdams 99 McAdams, S. 1999. 

	private double rdfoa;
	// Relative Difference Function Overall Average
	// log of the derivative of RMS.  Used for onset detection.

	private double ammoa;
	// Area Method of Moments Overall Average
	// 2D statistical method of moments
	
	
	// Feature standard deviations
	//TODO?
}
