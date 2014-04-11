package jk509.player.features;

import jAudioFeatureExtractor.Cancel;
import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;
import jAudioFeatureExtractor.ACE.XMLParsers.XMLDocumentParser;
import jAudioFeatureExtractor.AudioFeatures.Derivative;
import jAudioFeatureExtractor.AudioFeatures.FeatureExtractor;
import jAudioFeatureExtractor.AudioFeatures.MetaFeatureFactory;
import jAudioFeatureExtractor.AudioFeatures.StandardDeviation;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingUtilities;

import jk509.player.Constants;
import jk509.player.gui.GUIupdater;

public class FeatureGrabber implements Constants {

	/**
	 * list of which features are enabled by default
	 */
	public boolean[] defaults;

	/**
	 * list of all features available
	 */
	public FeatureExtractor[] features;
	
	/**
	 * Mapping between aggregator names and aggregator prototypes
	 */
	public java.util.HashMap<String, jAudioFeatureExtractor.Aggregators.Aggregator> aggregatorMap;
	
	/**
	 * whether or a feature is a derived feature or not
	 */
	public boolean[] is_primary;

	/**
	 * cached FeatureDefinitions for all available features
	 */
	public FeatureDefinition[] featureDefinitions;
	
	private GUIupdater updater;

	public Cancel cancel;
	
	private List<double[]> results;
	private List<double[]> results_norm;
	
	@SuppressWarnings("unchecked")
	public FeatureGrabber(){
		cancel = new Cancel();
		
		LinkedList<MetaFeatureFactory> metaExtractors = new LinkedList<MetaFeatureFactory>();

		metaExtractors.add(new Derivative());
		metaExtractors.add(new jAudioFeatureExtractor.AudioFeatures.Mean());
		metaExtractors.add(new StandardDeviation());
		metaExtractors.add(new Derivative(new jAudioFeatureExtractor.AudioFeatures.Mean()));
		metaExtractors.add(new Derivative(new StandardDeviation()));

		LinkedList<FeatureExtractor> extractors = new LinkedList<FeatureExtractor>();
		LinkedList<Boolean> def = new LinkedList<Boolean>();
		aggregatorMap = new java.util.HashMap<String, jAudioFeatureExtractor.Aggregators.Aggregator>();
		try {

			Object[] lists = (Object[]) XMLDocumentParser.parseXMLDocument(
					featureXMLLocation, "feature_list");
			extractors = (LinkedList<FeatureExtractor>) lists[0];
			def = (LinkedList<Boolean>) lists[1];
			jAudioFeatureExtractor.Aggregators.Aggregator[] aggArray = ((LinkedList<jAudioFeatureExtractor.Aggregators.Aggregator>) lists[2])
					.toArray(new jAudioFeatureExtractor.Aggregators.Aggregator[] {});

			for (int i = 0; i < aggArray.length; ++i) {
				aggregatorMap.put(aggArray[i].getAggregatorDefinition().name,
						aggArray[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		populateMetaFeatures(metaExtractors, extractors, def);
	}
	
	/*public static void main(String[] args){
		FeatureGrabber f = new FeatureGrabber();
		File[] files = new File[]{ new File("C:\\Users\\James\\Desktop\\Hysteria.mp3"), new File("C:\\Users\\James\\Desktop\\Plug in Baby.mp3")} ;
		f.run(files);
		List<double[]> res = f.getNormalisedResults();
		for(int i=0; i<res.size(); ++i){
			for(int j=0; j<res.get(i).length; ++j){
				System.out.print(res.get(i)[j] + "  ");
			}
			System.out.println();
			System.out.println("----------------");
		}
		if(res.size() < 1)
			System.out.println("No data");
		System.exit(0);
	}*/
	
	public List<double[]> getNormalisedResults(){
		return results;
		// TODO!
		//return results_norm; 
	}
	
	public void Normalise(){
		// TODO!
		double[] weights = (new AudioFeatures()).getWeights();
		// take results and produce results_norm
	}
	
	public void run(File[] files, GUIupdater updater){
		
		try {
			
			//PrintFeatures();
			defaults = (new AudioFeatures()).getFeatureArray();
			FeatureProcessor processor = new FeatureProcessor(512,0.0, 16000.0, false, features, defaults, cancel);
			
			List<double[]> res = new ArrayList<double[]>();
			
			this.updater = updater;
			this.updater.setNumberOfFiles(files.length);
			
			//int i=0 ; while(true){
			for(int i=0; i<files.length; ++i){
			//for(int i=145; i<149; ++i){
				try{
					res.add(featuresToArray(processor.extractFeatures(files[i], updater)));
					//System.out.println((featuresToArray(processor.extractFeatures(files[68], updater))));
					updater.announceUpdate(i+1, 0);
					System.out.println("Features extracted from file #"+i);
				}catch(Exception e){
					res.add(null);
					System.out.println("Feature extraction failed for file #"+i);
				}
			}
			
			SwingUtilities.invokeLater(updater.resumeGUI);
			//return res;
			results = res;
			//TODO
			// invoke Normalise();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			results = new ArrayList<double[]>();
		}
		//return new ArrayList<double[]>();
	}
	
	void PrintFeatures(){
		for(int i=0; i<features.length; ++i){
			System.out.println((i+1)+".  "+features[i].getFeatureDefinition().name);
		}
	}
	
	double[] featuresToArray(List<double[]> arr){
		double[] res = new double[FEATURES];
		int x=0;
		for(int i=0; i<arr.size(); ++i){
			for(int j=0; j<arr.get(i).length; ++j){
				res[x++] = arr.get(i)[j];
			}
		}
		return res;
	}
	
	void populateMetaFeatures(LinkedList<MetaFeatureFactory> listMFF,
			LinkedList<FeatureExtractor> listFE, LinkedList<Boolean> def) {
		LinkedList<Boolean> tmpDefaults = new LinkedList<Boolean>();
		LinkedList<FeatureExtractor> tmpFeatures = new LinkedList<FeatureExtractor>();
		LinkedList<Boolean> isPrimaryList = new LinkedList<Boolean>();
		Iterator<FeatureExtractor> lFE = listFE.iterator();
		Iterator<Boolean> lD = def.iterator();
		while (lFE.hasNext()) {
			FeatureExtractor tmpF = lFE.next();
			Boolean tmpB = lD.next();
			tmpFeatures.add(tmpF);
			tmpDefaults.add(tmpB);
			isPrimaryList.add(new Boolean(true));
			//tmpF.setParent(this);
			if (tmpF.getFeatureDefinition().dimensions != 0) {
				Iterator<MetaFeatureFactory> lM = listMFF.iterator();
				while (lM.hasNext()) {
					MetaFeatureFactory tmpMFF = lM.next();
					FeatureExtractor tmp = tmpMFF
							.defineFeature((FeatureExtractor) tmpF.clone());
					//tmp.setParent(this);
					tmpFeatures.add(tmp);
					tmpDefaults.add(new Boolean(false));
					isPrimaryList.add(new Boolean(false));
				}
			}
		}
		this.features = tmpFeatures.toArray(new FeatureExtractor[1]);
		Boolean[] defaults_temp = tmpDefaults.toArray(new Boolean[1]);
		Boolean[] is_primary_temp = isPrimaryList.toArray(new Boolean[] {});
		this.defaults = new boolean[defaults_temp.length];
		is_primary = new boolean[defaults_temp.length];
		for (int i = 0; i < this.defaults.length; i++) {
			this.defaults[i] = defaults_temp[i].booleanValue();
			is_primary[i] = is_primary_temp[i].booleanValue();
		}
		this.featureDefinitions = new FeatureDefinition[this.defaults.length];
		for (int i = 0; i < this.featureDefinitions.length; ++i) {
			this.featureDefinitions[i] = features[i].getFeatureDefinition();
		}

	}
}
