/*
 * 
 * Adapted from jAudio package:
 * 
 * @(#)FeatureProcessor.java	1.01	April 9, 2005.
 *
 * McGill Univarsity
 */

package jk509.player.features;

import jAudioFeatureExtractor.Cancel;
import jAudioFeatureExtractor.ExplicitCancel;
import jAudioFeatureExtractor.Updater;
import jAudioFeatureExtractor.AudioFeatures.FeatureExtractor;
import jAudioFeatureExtractor.jAudioTools.DSPMethods;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import jk509.player.Constants;
import jk509.player.core.Song;

//import jAudioFeatureExtractor.jAudioTools.AudioMethods;

/**
 * This class is used to pre-process and extract features from audio recordings. An object of this class should be instantiated with parameters indicating the details of how features are to be extracted.
 * <p>
 * The extractFeatures method should be called whenever recordings are available to be analyzed. This mehtod should be called once for each recording. It will write the extracted feature values to an XML file after each call. This will also save feature definitions to another XML file.
 * <p>
 * The finalize method should be called when all features have been extracted. this will finish writing the feature values to the XML file.
 * <p>
 * Features are extracted for each window and, when appropriate, the average and standard deviation of each of these features is extracted for each recording.
 * 
 * @author Cory McKay
 */
public class FeatureProcessor {
	/* FIELDS ***************************************************************** */

	// The window size used for dividing up the recordings to classify.
	private int window_size;

	// The number of samples that windows are offset by. A value of zero
	// means that there is no window overlap.
	private int window_overlap_offset;

	// The sampling rate that all recordings are to be converted to before
	// feature extraction.
	private double sampling_rate;

	// Whether or not to normalise recordings before feature extraction.
	private boolean normalise;

	// The features that are to be extracted.
	private FeatureExtractor[] feature_extractors;

	// The dependencies of the features in the feature_extractors field.
	// The first indice corresponds to the feature_extractors indice
	// and the second identifies the number of the dependent feature.
	// The entry identifies the indice of the feature in feature_extractors
	// that corresponds to a dependant feature. The first dimension will be
	// null if there are no dependent features.
	private int[][] feature_extractor_dependencies;

	// The longest number of windows of previous features that each feature must
	// have before it can be extracted. The indice corresponds to that of
	// feature_extractors.
	private int[] max_feature_offsets;

	// Which features are to be saved after processing. Entries correspond to
	// the
	// feature_extractors field.
	private boolean[] features_to_save;

	// Whetehr or not to save the average and standard deviation of each
	// feature accross all windows.
	private boolean save_overall_recording_features = true;

	// Used to write to the feature_vector_file file to save feature values to.
	// private DataOutputStream values_writer;

	// Indicates what the type of the output format is
	// private int outputType;

	// hook for allowing visual updates of how far along the extraction is.
	private Updater updater;

	// allows external entity to halt execution
	private Cancel cancel;

	private jk509.player.features.AggregatorContainer aggregator;

	/* CONSTRUCTOR ************************************************************ */

	/**
	 * Validates and stores the configuration to use for extracting features from audio recordings. Prepares the feature_vector_file and feature_key_file XML files for saving.
	 * 
	 * @param window_size
	 *            The size of the windows that the audio recordings are to be broken into.
	 * @param window_overlap
	 *            The fraction of overlap between adjacent windows. Must be between 0.0 and less than 1.0, with a value of 0.0 meaning no overlap.
	 * @param sampling_rate
	 *            The sampling rate that all recordings are to be converted to before feature extraction
	 * @param normalise
	 *            Whether or not to normalise recordings before feature extraction.
	 * @param all_feature_extractors
	 *            All features that can be extracted.
	 * @param features_to_save_among_all
	 *            Which features are to be saved. Entries correspond to the all_feature_extractors parameter.
	 * @param save_features_for_each_window
	 *            Whether or not to save features individually for each window.
	 * @param save_overall_recording_features
	 *            Whetehr or not to save the average and standard deviation of each feature accross all windows.
	 * @param feature_values_save_path
	 *            The path of the feature_vector_file XML file to save feature values to.
	 * @param feature_definitions_save_path
	 *            The path of the feature_key_file file to save feature definitions to.
	 * @throws Exception
	 *             Throws an informative exception if the input parameters are invalid.
	 */
	public FeatureProcessor(int window_size, double window_overlap, double sampling_rate, boolean normalise, FeatureExtractor[] all_feature_extractors, boolean[] features_to_save_among_all, Cancel cancel) throws Exception {
		this.cancel = cancel;
		aggregator = new jk509.player.features.AggregatorContainer();
		aggregator.add(new Aggregator[] { new Mean() });

		// if (feature_values_save_path.equals(""))
		// throw new Exception("No save path specified for feature values.");
		// if (feature_definitions_save_path.equals(""))
		// throw new Exception(
		// "No save path specified for feature definitions.");
		if (window_overlap < 0.0 || window_overlap >= 1.0)
			throw new Exception("Window overlap fraction is " + window_overlap + ".\n" + "This value must be 0.0 or above and less than 1.0.");
		if (window_size < 3)
			throw new Exception("Window size is " + window_size + ".\n" + "This value must be above 2.");
		boolean one_selected = false;
		for (int i = 0; i < features_to_save_among_all.length; i++)
			if (features_to_save_among_all[i])
				one_selected = true;
		if (!one_selected)
			throw new Exception("No features have been set to be saved.");

		// Prepare the files for writing
		// File feature_values_save_file = new File(feature_values_save_path);
		// File feature_definitions_save_file = new File(
		// feature_definitions_save_path);
		//
		// // Throw an exception if the given file paths are not writable.
		// Involves
		// // creating a blank file if one does not already exist.
		// if (feature_values_save_file.exists())
		// if (!feature_values_save_file.canWrite())
		// throw new Exception("Cannot write to "
		// + feature_values_save_path + ".");
		// if (feature_definitions_save_file.exists())
		// if (!feature_definitions_save_file.canWrite())
		// throw new Exception("Cannot write to "
		// + feature_definitions_save_path + ".");
		// if (!feature_values_save_file.exists())
		// feature_values_save_file.createNewFile();
		// if (!feature_definitions_save_file.exists() && (outputType == 0)) {
		// feature_definitions_save_file.createNewFile();
		// }
		//
		// // Prepare stream writers
		// FileOutputStream values_to = new FileOutputStream(
		// feature_values_save_file);
		// FileOutputStream definitions_to = new FileOutputStream(
		// feature_definitions_save_file);

		// Save parameters as fields
		this.window_size = window_size;
		this.sampling_rate = sampling_rate;
		this.normalise = normalise;

		// Calculate the window offset
		window_overlap_offset = (int) (window_overlap * (double) window_size);

		// Find which features need to be extracted and in what order. Also find
		// the indices of dependencies and the maximum offsets for each feature.
		findAndOrderFeaturesToExtract(all_feature_extractors, features_to_save_among_all);

		aggregator.add(feature_extractors, features_to_save);
	}

	private double[] featuresToArray(List<double[]> arr){
		double[] res = new double[Constants.FEATURES];
		int x=0;
		for(int i=0; i<arr.size(); ++i){
			for(int j=0; j<arr.get(i).length; ++j){
				res[x++] = arr.get(i)[j];
			}
		}
		return res;
	}
	
	/* PUBLIC METHODS ********************************************************* */

	/**
	 * Extract the features from the provided audio file. This includes pre-processing involving sample rate conversion, windowing and, possibly, normalisation. The feature values are automatically saved to the feature_vector_file XML file referred to by the values_writer field. The definitions of the features that are saved are also saved to the feature_key_file XML file referred to by the definitions_writer field.
	 * 
	 * @param recording_file
	 *            The audio file to extract features from.
	 */
	
	public void extractFeatures(Song track, Updater updater, File temp) throws Exception {
		File recording_file = new File(track.getLocation());
		
		// Pre-process the recording and extract the samples from the audio
		this.updater = updater;
		// System.out.println("about to pre");
		double[] samples = preProcessRecording(recording_file, temp); // TODO as slow as the feature extraction...
		// System.out.println("done pre");
		if (cancel.isCancel()) {
			throw new ExplicitCancel("Killed after loading data");
		}
		// Calculate the window start indices
		LinkedList<Integer> window_start_indices_list = new LinkedList<Integer>();
		int this_start = 0;
		while (this_start < samples.length) {
			window_start_indices_list.add(new Integer(this_start));
			this_start += window_size - window_overlap_offset;
		}
		Integer[] window_start_indices_I = window_start_indices_list.toArray(new Integer[1]);
		int[] window_start_indices = new int[window_start_indices_I.length];

		// if were using a progress bar, set its max update
		if (updater != null) {
			updater.setFileLength(window_start_indices.length);
		}

		for (int i = 0; i < window_start_indices.length; i++)
			window_start_indices[i] = window_start_indices_I[i].intValue();

		// Extract the feature values from the samples
		double[][][] window_feature_values = getFeatures(samples, window_start_indices);
		
		if (save_overall_recording_features) {
			aggregator.aggregate(window_feature_values);
		}
		track.setAudioFeatures(featuresToArray(aggregator.getResult()));
	}
	public List<double[]> extractFeatures(File recording_file, Updater updater) throws Exception {
		// Pre-process the recording and extract the samples from the audio
		this.updater = updater;
		// System.out.println("about to pre");
		double[] samples = preProcessRecording(recording_file, new File("temp.mp3")); // TODO as slow as the feature extraction...
		// System.out.println("done pre");
		if (cancel.isCancel()) {
			throw new ExplicitCancel("Killed after loading data");
		}
		// Calculate the window start indices
		LinkedList<Integer> window_start_indices_list = new LinkedList<Integer>();
		int this_start = 0;
		while (this_start < samples.length) {
			window_start_indices_list.add(new Integer(this_start));
			this_start += window_size - window_overlap_offset;
		}
		Integer[] window_start_indices_I = window_start_indices_list.toArray(new Integer[1]);
		int[] window_start_indices = new int[window_start_indices_I.length];

		// if were using a progress bar, set its max update
		if (updater != null) {
			updater.setFileLength(window_start_indices.length);
		}

		for (int i = 0; i < window_start_indices.length; i++)
			window_start_indices[i] = window_start_indices_I[i].intValue();

		// Extract the feature values from the samples
		double[][][] window_feature_values = getFeatures(samples, window_start_indices);

		// Find the feature averages and standard deviations if appropriate
		// AggregatorContainer aggContainer = new AggregatorContainer();
		// FeatureDefinition[][] overall_feature_definitions = new
		// FeatureDefinition[1][];
		// overall_feature_definitions[0] = null;
		// double[][] overall_feature_values = null;
		if (save_overall_recording_features) {
			// Aggregator[] aggList = new Aggregator[10];
			// aggList[0] = new Mean();
			// aggList[1] = new StandardDeviation();
			// aggList[2] = new AreaMoments();
			// aggList[2].setParameters(new String[]{"MFCC"},new String[]{});
			// aggList[3] = new AreaMoments();
			// aggList[3].setParameters(new String[]{"LPC"},new String[]{});
			// aggList[4] = new AreaMoments();
			// aggList[4].setParameters(new String[]{"Derivative of MFCC"},new String[]{});
			// aggList[5] = new AreaMoments();
			// aggList[5].setParameters(new String[]{"Derivative of LPC"},new String[]{});
			// aggList[6] = new AreaMoments();
			// aggList[6].setParameters(new String[]{"Derivative of Method of Moments"},new String[]{});
			// aggList[7] = new AreaMoments();
			// aggList[7].setParameters(new String[]{"Method of Moments"},new String[]{});
			// aggList[8] = new AreaMoments();
			// aggList[8].setParameters(new String[]{"Area Method of Moments"},new String[]{});
			// aggList[9] = new AreaMoments();
			// aggList[9].setParameters(new String[]{"Derivative of Area Method of Moments"},new String[]{});
			// aggList[2] = new MFCC();
			// aggList[2] = new MultipleFeatureHistogram(new FeatureExtractor[]{new RMS(),new ZeroCrossings()},8);
			// aggList[3] = new MultipleFeatureHistogram(new FeatureExtractor[]{new MFCC()},4);

			// aggContainer.add(aggList);

			aggregator.aggregate(window_feature_values);
		}
		// overall_feature_values = getOverallRecordingFeatures(
		// window_feature_values, overall_feature_definitions);

		// Save the feature values for this recording
		// return getFeatureVectorsForARecording(window_feature_values,
		// window_start_indices, recording_file.getPath(),
		// aggregator);
		return aggregator.getResult();
	}

	/**
	 * Write the ending tags to the feature_vector_file XML file. Close the DataOutputStreams that were used to write it.
	 * <p>
	 * This method should be called when all features have been extracted.
	 * 
	 * @throws Exception
	 *             Throws an exception if cannot write or close the output streams.
	 */
	/*
	 * public void finalize() throws Exception { if (outputType == 0) { values_writer.writeBytes("</feature_vector_file>"); } values_writer.close(); }
	 */
	/* PRIVATE METHODS ******************************************************** */

	/**
	 * Fills the feature_extractors, feature_extractor_dependencies, max_feature_offsets and features_to_save fields. This involves finding which features need to be extracted and in what order and finding the indices of dependencies and the maximum offsets for each feature.
	 * <p>
	 * Daniel McEnnis 05-07-05 added feature offset of dependancies to max_offset
	 * 
	 * @param all_feature_extractors
	 *            All features that can be extracted.
	 * @param features_to_save_among_all
	 *            Which features are to be saved. Entries correspond to the all_feature_extractors parameter.
	 */
	private void findAndOrderFeaturesToExtract(FeatureExtractor[] all_feature_extractors, boolean[] features_to_save_among_all) {
		// Find the names of all features
		String[] all_feature_names = new String[all_feature_extractors.length];
		for (int feat = 0; feat < all_feature_extractors.length; feat++)
			all_feature_names[feat] = all_feature_extractors[feat].getFeatureDefinition().name;

		// Find dependencies of all features marked to be extracted.
		// Mark as null if features are not to be extracted. Note that will also
		// be null if there are no dependencies.
		String[][] dependencies = new String[all_feature_extractors.length][];
		for (int feat = 0; feat < all_feature_extractors.length; feat++) {
			if (features_to_save_among_all[feat])
				dependencies[feat] = all_feature_extractors[feat].getDepenedencies();
			else
				dependencies[feat] = null;
		}

		// Add dependencies to dependencies and if any features are not marked
		// for
		// saving but are marked as a dependency of a feature that is marked to
		// be
		// saved. Also fill features_to_extract in order to know what features
		// to
		// extract(but not necessarily save).
		boolean done = false;
		boolean[] features_to_extract = new boolean[dependencies.length];
		for (int feat = 0; feat < features_to_extract.length; feat++) {
			if (features_to_save_among_all[feat])
				features_to_extract[feat] = true;
			else
				features_to_extract[feat] = false;
		}
		while (!done) {
			done = true;
			for (int feat = 0; feat < dependencies.length; feat++)
				if (dependencies[feat] != null)
					for (int i = 0; i < dependencies[feat].length; i++) {
						String name = dependencies[feat][i];
						for (int j = 0; j < all_feature_names.length; j++) {
							if (name.equals(all_feature_names[j])) {
								if (!features_to_extract[j]) {
									features_to_extract[j] = true;
									dependencies[j] = all_feature_extractors[j].getDepenedencies();
									if (dependencies[j] != null)
										done = false;
								}
								j = all_feature_names.length;
							}
						}
					}
		}

		// Find the correct order to extract features in by filling the
		// feature_extractors field
		int number_features_to_extract = 0;
		for (int i = 0; i < features_to_extract.length; i++)
			if (features_to_extract[i])
				number_features_to_extract++;
		feature_extractors = new FeatureExtractor[number_features_to_extract];
		features_to_save = new boolean[number_features_to_extract];
		for (int i = 0; i < features_to_save.length; i++)
			features_to_save[i] = false;
		boolean[] feature_added = new boolean[dependencies.length];
		for (int i = 0; i < feature_added.length; i++)
			feature_added[i] = false;
		int current_position = 0;
		done = false;
		while (!done) {
			done = true;

			// Add all features that have no remaining dependencies and remove
			// their dependencies from all unadded features
			for (int feat = 0; feat < dependencies.length; feat++) {
				if (features_to_extract[feat] && !feature_added[feat])
					if (dependencies[feat] == null) // add feature if it has no
					// dependencies
					{
						feature_added[feat] = true;
						feature_extractors[current_position] = all_feature_extractors[feat];
						features_to_save[current_position] = features_to_save_among_all[feat];
						current_position++;
						done = false;

						// Remove this dependency from all features that have
						// it as a dependency and are marked to be extracted
						for (int i = 0; i < dependencies.length; i++)
							if (features_to_extract[i] && dependencies[i] != null) {
								int num_defs = dependencies[i].length;
								for (int j = 0; j < num_defs; j++) {
									if (dependencies[i][j].equals(all_feature_names[feat])) {
										if (dependencies[i].length == 1) {
											dependencies[i] = null;
											j = num_defs;
										} else {
											String[] temp = new String[dependencies[i].length - 1];
											int m = 0;
											for (int k = 0; k < dependencies[i].length; k++) {
												if (k != j) {
													temp[m] = dependencies[i][k];
													m++;
												}
											}
											dependencies[i] = temp;
											j--;
											num_defs--;
										}
									}
								}
							}
					}
			}
		}

		// Find the indices of the feature extractor dependencies for each
		// feature
		// extractor
		feature_extractor_dependencies = new int[feature_extractors.length][];
		String[] feature_names = new String[feature_extractors.length];
		for (int feat = 0; feat < feature_names.length; feat++) {
			feature_names[feat] = feature_extractors[feat].getFeatureDefinition().name;
		}
		String[][] feature_dependencies_str = new String[feature_extractors.length][];
		for (int feat = 0; feat < feature_dependencies_str.length; feat++)
			feature_dependencies_str[feat] = feature_extractors[feat].getDepenedencies();
		for (int i = 0; i < feature_dependencies_str.length; i++)
			if (feature_dependencies_str[i] != null) {
				feature_extractor_dependencies[i] = new int[feature_dependencies_str[i].length];
				for (int j = 0; j < feature_dependencies_str[i].length; j++)
					for (int k = 0; k < feature_names.length; k++)
						if (feature_dependencies_str[i][j].equals(feature_names[k]))
							feature_extractor_dependencies[i][j] = k;
			}

		// Find the maximum offset for each feature
		// Daniel McEnnis 5-07-05 added feature offset of dependancies to
		// max_offset
		max_feature_offsets = new int[feature_extractors.length];
		for (int i = 0; i < max_feature_offsets.length; i++) {
			if (feature_extractors[i].getDepenedencyOffsets() == null)
				max_feature_offsets[i] = 0;
			else {
				int[] these_offsets = feature_extractors[i].getDepenedencyOffsets();
				max_feature_offsets[i] = Math.abs(these_offsets[0] + max_feature_offsets[feature_extractor_dependencies[i][0]]);
				for (int k = 0; k < these_offsets.length; k++) {
					int val = Math.abs(these_offsets[k]) + max_feature_offsets[feature_extractor_dependencies[i][k]];
					if (val > max_feature_offsets[i]) {
						max_feature_offsets[i] = val;
					}
				}
			}
		}
	}

	/**
	 * Returns the samples stored in the given audio file.
	 * <p>
	 * The samples are re-encoded using the sampling rate in the sampling_rate field. All channels are projected into one channel. Samples are normalised if the normalise field is true.
	 * 
	 * @param recording_file
	 *            The audio file to extract samples from.
	 * @return The processed audio samples. Values will fall between a minimum of -1 and +1. The indice identifies the sample number.
	 * @throws Exception
	 *             An exception is thrown if a problem occurs during file reading or pre- processing.
	 */
	private double[] preProcessRecording(File recording, File temp) throws Exception {
		// Get the original audio and its format
		try {
			try {
				Process p = Runtime.getRuntime().exec(new String[] { "lame.exe", "-b", Integer.toString(Constants.ENCODE_BITRATE), "--resample", Integer.toString(Constants.SAMPLING_RATE), recording.getPath(), temp.getPath() });
				BufferedReader input = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				//String line;
				while (input.readLine() != null) {
					// System.out.println(line);
				}
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			File recording_file = temp;

			AudioInputStream original_stream = AudioSystem.getAudioInputStream(recording_file);
			AudioFormat original_format = original_stream.getFormat();

			// Set the bit depth
			int bit_depth = original_format.getSampleSizeInBits();
			if (bit_depth != 8 && bit_depth != 16)
				bit_depth = 16;

			// AudioInputStream new_stream = original_stream;

			AudioFormat new_format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, original_format.getSampleRate(), bit_depth, original_format.getChannels(), original_format.getChannels() * (bit_depth / 8), original_format.getSampleRate(), true);
			AudioInputStream new_stream = AudioSystem.getAudioInputStream(new_format, original_stream);

			// If the audio is not PCM signed big endian, then convert it to PCM
			// signed
			// This is particularly necessary when dealing with MP3s
			/*
			 * AudioInputStream second_stream = original_stream; if (original_format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED || original_format.isBigEndian() == false) { AudioFormat new_format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, original_format.getSampleRate(), bit_depth, original_format.getChannels(), original_format.getChannels() * (bit_depth / 8), original_format.getSampleRate(), true); second_stream = AudioSystem.getAudioInputStream(new_format, original_stream); }
			 */

			// Convert to the set sampling rate, if it is not already at this
			// sampling rate.
			// Also, convert to an appropriate bit depth if necessary.
			/*
			 * AudioInputStream new_stream = second_stream; if (original_format.getSampleRate() != sampling_rate || bit_depth != original_format.getSampleSizeInBits()) { AudioFormat new_format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, (float) sampling_rate, bit_depth, original_format.getChannels(), original_format.getChannels() * (bit_depth / 8), original_format.getSampleRate(), true); new_stream = AudioSystem.getAudioInputStream(new_format, second_stream); }
			 */

			/*
			 * if (original_format.getFrameRate() != 16000f) { AudioFormat new_format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, (float) sampling_rate, bit_depth, original_format.getChannels(), original_format.getChannels() * (bit_depth / 8), (float) sampling_rate, true); new_stream = AudioSystem.getAudioInputStream(new_format, second_stream); }
			 */

			double[][] channel_samples;
			double[] samples = null;
			
			channel_samples = AudioMethods.extractSampleValues(new_stream);
			if (channel_samples.length > 1)
				samples = DSPMethods.getSamplesMixedDownIntoOneChannel(channel_samples);
			else if(channel_samples.length == 1)
				samples = channel_samples[0];
			else if(channel_samples.length < 1)
				samples = null;
			
			if (normalise) 
				samples = DSPMethods.normalizeSamples(samples);

			if (samples == null || samples.length < 1) {
				return null;
			}

			// Close streams
			original_stream.close();
			// second_stream.close();
			new_stream.close();

			// Return all channels compressed into one
			return samples;
		} catch (java.lang.OutOfMemoryError e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	// This is the original method
	/*
	 * private double[] preProcessRecording(File recording_file) throws Exception { // Get the original audio and its format AudioInputStream original_stream = AudioSystem.getAudioInputStream(recording_file); AudioFormat original_format = original_stream.getFormat();
	 * 
	 * // Set the bit depth int bit_depth = original_format.getSampleSizeInBits(); if (bit_depth != 8 && bit_depth != 16) bit_depth = 16;
	 * 
	 * AudioInputStream new_stream = original_stream; if (original_format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED || original_format.isBigEndian() == false || original_format.getSampleRate() != (float) sampling_rate || bit_depth != original_format.getSampleSizeInBits()) { AudioFormat new_format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, (float) sampling_rate, bit_depth, original_format.getChannels(), original_format.getChannels() * (bit_depth / 8), original_format.getSampleRate(), true); new_stream = AudioSystem.getAudioInputStream(new_format, original_stream); }
	 * 
	 * // Extract data from the AudioInputStream AudioSamples audio_data = new AudioSamples(new_stream, recording_file.getPath(), false);
	 * 
	 * 
	 * // If the audio is not PCM signed big endian, then convert it to PCM // signed // This is particularly necessary when dealing with MP3s AudioInputStream second_stream = original_stream; if (original_format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED || original_format.isBigEndian() == false) { AudioFormat new_format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, original_format.getSampleRate(), bit_depth, original_format.getChannels(), original_format.getChannels() * (bit_depth / 8), original_format.getSampleRate(), true); second_stream = AudioSystem.getAudioInputStream(new_format, original_stream); }
	 * 
	 * // Convert to the set sampling rate, if it is not already at this // sampling rate. // Also, convert to an appropriate bit depth if necessary. AudioInputStream new_stream = second_stream; if (original_format.getSampleRate() != (float) sampling_rate || bit_depth != original_format.getSampleSizeInBits()) { AudioFormat new_format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, (float) sampling_rate, bit_depth, original_format.getChannels(), original_format.getChannels() * (bit_depth / 8), original_format.getSampleRate(), true); new_stream = AudioSystem.getAudioInputStream(new_format, second_stream); }
	 * 
	 * if(Constants.CUSTOM_FEATUREPROC_CODE){ //if(recording_file.getPath().equals("E:\\Users\\James\\Music\\iTunes\\iTunes Media\\Music\\Soundtrack\\Saturday Night Fever\\06 5th Symphony Disco Remix.mp3")){ // MY TEST BIT AudioInputStream encoded = original_stream;//AudioSystem.getAudioInputStream(new File("E:\\Users\\James\\Music\\iTunes\\iTunes Media\\Music\\Soundtrack\\Saturday Night Fever\\06 5th Symphony Disco Remix.mp3")); AudioFormat encodedFormat = encoded.getFormat(); AudioFormat decodedFormat = new AudioFormat( AudioFormat.Encoding.PCM_SIGNED, // Encoding to use (float) sampling_rate,//format.getSampleRate(), // sample rate (same as base format) bit_depth, // sample size in bits (thx to Javazoom) encodedFormat.getChannels(), // # of Channels encodedFormat.getChannels()*(bit_depth/8), // Frame Size encodedFormat.getSampleRate(), // Frame Rate true // Big Endian ); AudioInputStream currentDecoded = AudioSystem.getAudioInputStream(decodedFormat, encoded); new_stream = currentDecoded; //} // END TEST }
	 * 
	 * // Extract data from the AudioInputStream TestPlay(new_stream); AudioSamples audio_data = new AudioSamples(new_stream, recording_file.getPath(), false);
	 * 
	 * if(audio_data.getSamplesMixedDown() == null || audio_data.getSamplesMixedDown().length < 1){ System.out.println("   alternative feature extraction code triggered"); AudioInputStream encoded = AudioSystem.getAudioInputStream(recording_file); AudioFormat encodedFormat = encoded.getFormat(); AudioFormat decodedFormat = new AudioFormat( AudioFormat.Encoding.PCM_SIGNED, // Encoding to use (float) sampling_rate,//format.getSampleRate(), // sample rate (same as base format) bit_depth, // sample size in bits (thx to Javazoom) encodedFormat.getChannels(), // # of Channels encodedFormat.getChannels()*(bit_depth/8), // Frame Size encodedFormat.getSampleRate(), // Frame Rate true // Big Endian ); AudioInputStream currentDecoded = AudioSystem.getAudioInputStream(decodedFormat, encoded); new_stream = currentDecoded; audio_data = new AudioSamples(new_stream, recording_file.getPath(), false); }
	 * 
	 * 
	 * 
	 * if(audio_data.getSamplesMixedDown() == null || audio_data.getSamplesMixedDown().length < 1){ return null; }
	 * 
	 * // Normalise samples if this option has been requested if (normalise) audio_data.normalizeMixedDownSamples();
	 * 
	 * // Close streams original_stream.close(); //second_stream.close(); new_stream.close();
	 * 
	 * // Return all channels compressed into one return audio_data.getSamplesMixedDown(); }
	 */

	/**
	 * Breaks the given samples into the appropriate windows and extracts features from each window.
	 * 
	 * @param samples
	 *            The samples to extract features from. Sample values should generally be between -1 and +1.
	 * @param window_start_indices
	 *            The indices of samples that correspond to where each window should start.
	 * @return The extracted feature values for this recording. The first indice identifies the window, the second identifies the feature and the third identifies the feature value. The third dimension will be null if the given feature could not be extracted for the given window.
	 * @throws Exception
	 *             Throws an exception if a problem occurs.
	 */
	private double[][][] getFeatures(double[] samples, int[] window_start_indices) throws Exception {
		// The extracted feature values for this recording. The first indice
		// identifies the window, the second identifies the feature and the
		// third identifies the feature value.
		double[][][] results = new double[window_start_indices.length][feature_extractors.length][];

		// Calculate how frequently to make updates to the updater;
		int updateThreshold = 1;
		if (window_start_indices.length > 100) {
			updateThreshold = window_start_indices.length / 100;
		}

		// Extract features from each window one by one and add save the
		// results.
		// The last window is zero-padded at the end if it falls off the edge of
		// the
		// provided samples.
		for (int win = 0; win < window_start_indices.length; win++) {
			// Do we need to update the progress bar or not
			if ((updater != null) && (win % updateThreshold == 0)) {
				updater.announceUpdate(win);
				if (cancel.isCancel()) {
					throw new ExplicitCancel("Killed while processing features");
				}
			}

			// Find the samples in this window and zero-pad if necessary
			double[] window = new double[window_size];
			int start_sample = window_start_indices[win];
			int end_sample = start_sample + window_size - 1;
			if (end_sample < samples.length)
				for (int samp = start_sample; samp <= end_sample; samp++)
					window[samp - start_sample] = samples[samp];
			else
				for (int samp = start_sample; samp <= end_sample; samp++) {
					if (samp < samples.length)
						window[samp - start_sample] = samples[samp];
					else
						window[samp - start_sample] = 0.0;
				}

			// Extract the features one by one
			for (int feat = 0; feat < feature_extractors.length; feat++) {
				// Only extract this feature if enough previous information
				// is available to extract this feature
				if (win >= max_feature_offsets[feat]) {
					// Find the correct feature
					FeatureExtractor feature = feature_extractors[feat];

					// Find previously extracted feature values that this
					// feature
					// needs
					double[][] other_feature_values = null;
					if (feature_extractor_dependencies[feat] != null) {
						other_feature_values = new double[feature_extractor_dependencies[feat].length][];
						for (int i = 0; i < feature_extractor_dependencies[feat].length; i++) {
							int feature_indice = feature_extractor_dependencies[feat][i];
							int offset = feature.getDepenedencyOffsets()[i];
							other_feature_values[i] = results[win + offset][feature_indice];
						}
					}

					// Store the extracted feature values
					results[win][feat] = feature.extractFeature(window, sampling_rate, other_feature_values);
				} else
					results[win][feat] = null;
			}
		}

		// Return the results
		return results;
	}

}