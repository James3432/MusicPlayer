package jk509.player.features;

import jAudioFeatureExtractor.Cancel;
import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;
import jAudioFeatureExtractor.ACE.XMLParsers.XMLDocumentParser;
import jAudioFeatureExtractor.AudioFeatures.Derivative;
import jAudioFeatureExtractor.AudioFeatures.FeatureExtractor;
import jAudioFeatureExtractor.AudioFeatures.MetaFeatureFactory;
import jAudioFeatureExtractor.AudioFeatures.StandardDeviation;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import jk509.player.Constants;
import jk509.player.core.Song;
import jk509.player.core.StaticMethods;
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
	
	//private List<double[]> results;
	//private List<double[]> results_w;
	
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
	
	/*public List<double[]> getResults(){
		return results;
	}
	public List<double[]> getWeightedResults(){
		ApplyWeights();
		return results_w; 
	}*/
	
	public void Normalise(){
		
	}
	
	//TODO?
	/*private void ApplyWeights(){
		double[] weights = (new AudioFeatures()).getWeights();
		for(double[] weighted : results){
			for(int i=0; i<weighted.length; ++i)
				weighted[i] = weighted[i] * weights[i];
			results_w.add(weighted);
		}
	}*/
	
	public void run(final List<Song> tracks, final GUIupdater updater){
		
		// TODO may need to change updater code? (remove mid-way announces)
		this.updater = updater;
		this.updater.setNumberOfFiles(tracks.size());
		
		defaults = (new AudioFeatures()).getFeatureArray();
		
		FeatureProcessor processorTemp = null;
		try{
			processorTemp = new FeatureProcessor(Constants.WINDOW_SIZE, Constants.WINDOW_OVERLAP, Constants.SAMPLING_RATE*1000, Constants.NORMALISE_AUDIO, features, defaults, cancel);
		}catch(Exception e){
			return;
		}
		final FeatureProcessor processor = processorTemp;
		
		if(Constants.MULTITHREADED){
			
			try {
				
				int threadCount = Constants.PARALELLISM;
				
				int procs = Runtime.getRuntime().availableProcessors();
				if(Constants.PARALLELISM_USE_PROC_COUNT)
					threadCount = procs;
				
				ExecutorService threadPool = Executors.newFixedThreadPool(threadCount);
				
				for(int i=0; i<tracks.size(); ++i){
					final int t = i;
					
					threadPool.execute(new Runnable() {
						@Override
						public void run() {
							try{
								String temp_name = GenerateString(Constants.TEMP_FILE_NAME_LENGTH);
								File temp = new File(StaticMethods.getSettingsDir()+temp_name+".mp3");
								processor.extractFeatures(tracks.get(t), updater, temp);
								try{
									temp.delete();
								}catch(Exception e){ }
								updater.announceUpdate(t+1, 0);
								System.out.println("Features extracted from file #"+t);
							}catch(Exception e){
								System.out.println("Feature extraction failed for file #"+t);
							}
							System.gc();
						}
					});
					
				}
				
				threadPool.shutdown();
				threadPool.awaitTermination(Integer.MAX_VALUE, TimeUnit.HOURS); // wait as long as it takes...
				
				SwingUtilities.invokeLater(updater.resumeGUI);
				//return res;
				//if(Constants.NORMALISE_FEATURES)
				//	results = Normalise(res);
				//else
				//	results = res;
				//TODO: invoke Normalise() which should process song list;
			} catch (Exception e) {
				e.printStackTrace();
				//results = new ArrayList<double[]>();
			}
			
		}else{
		
			try {
				
				String temp_name = GenerateString(Constants.TEMP_FILE_NAME_LENGTH);
				File temp = new File(StaticMethods.getSettingsDir()+temp_name+".mp3");
				
				for(int i=0; i<tracks.size(); ++i){
				//for(int i : new int[]{1, 3, 147, 148, 149}){
				//for(int i=1161; i<1191; ++i){
					try{
						processor.extractFeatures(tracks.get(i), updater, temp);
						updater.announceUpdate(i+1, 0);
						System.out.println("Features extracted from file #"+i);
					}catch(Exception e){
						//res.add(null);
						System.out.println("Feature extraction failed for file #"+i);
					}
					//tracks.get(i).setAudioFeatures(res.get(i));
	
					//EXPERIMENTAL
					//System.out.println("gc+");
					System.gc();
					//System.out.println("gc-");
				}
				
				try{
					temp.delete();
				}catch(Exception e){ }
				
				SwingUtilities.invokeLater(updater.resumeGUI);
				//return res;
				//if(Constants.NORMALISE_FEATURES)
				//	results = Normalise(res);
				//else
				//	results = res;
				//TODO
				// invoke Normalise();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//results = new ArrayList<double[]>();
			}
		}
	}
	
	private String GenerateString(int n){
		String s = "";
		for(int i=0; i<n; ++i){
			Random r = new Random();
			char c = (char)(r.nextInt(26) + 'a');
			s = s + c;
		}
		return s;
	}
	
	void PrintFeatures(){
		for(int i=0; i<features.length; ++i){
			System.out.println((i+1)+".  "+features[i].getFeatureDefinition().name);
		}
	}
	
	/*private List<double[]> Normalise(List<double[]> fs){
		List<double[]> res = new ArrayList<double[]>();
		return res;
	}
	
	double[] featuresToArray(List<double[]> arr){
		double[] res = new double[Constants.FEATURES];
		int x=0;
		for(int i=0; i<arr.size(); ++i){
			for(int j=0; j<arr.get(i).length; ++j){
				res[x++] = arr.get(i)[j];
			}
		}
		return res;
	}*/
	
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
