package jk509.player.logging;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import jk509.player.Constants;
import jk509.player.clustering.AbstractCluster;
import jk509.player.clustering.LeafCluster;
import jk509.player.clustering.SongCluster;
import jk509.player.core.Song;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Logger {
	
	public static void main(String[] args){
		//Tests
		Statistics s = new Statistics();
		s.values[0] = 1.6234;
		s.values[1] = -0.203;
		writeJson(s);
		System.out.println(readJson());
	}
	
	public enum LogType {
		ERROR_LOG,
		STATS_LOG,
		USAGE_LOG,
		LEARNING_LOG
	}
	
	public static PrintWriter SetWriter(LogType t) throws IOException{
		PrintWriter out = null;
		switch(t){
			case ERROR_LOG: out = new PrintWriter(new BufferedWriter(new FileWriter(Constants.ERROR_LOG, true))); break;
			case STATS_LOG: out = new PrintWriter(new BufferedWriter(new FileWriter(Constants.STATS_LOG, true))); break;
			case USAGE_LOG: out = new PrintWriter(new BufferedWriter(new FileWriter(Constants.USAGE_LOG, true))); break;
			case LEARNING_LOG: out = new PrintWriter(new BufferedWriter(new FileWriter(Constants.LEARNING_LOG, true))); break;
			default: break;
		}
		return out;
	}
	
	public static void log(String s, LogType t) {
		if(s == null)
			return;
		PrintWriter out = null;
		try {
			out = SetWriter(t);
			out.print(new Date() + ": ");
			out.println(s);
			out.println();
		} catch (IOException e) {
			
		}
		finally{
			out.close();
		}
	}
	
	public static void log(String[] ss, LogType t){
		if(ss == null || ss.length < 1)
			return;
		PrintWriter out = null;
		try {
			out = SetWriter(t);
			out.println(new Date() + ": ");
			for(int i=0; i<ss.length; ++i)
				out.println(ss[i]);
			out.println();
		} catch (IOException e) {
			
		}
		finally{
			out.close();
		}
	}
	
	public static void log(Exception e, LogType t){
		if(e == null)
			return;
		PrintWriter out = null;
		try {
			out = SetWriter(t);
			out.println(new Date() + ": ");
			e.printStackTrace(out);
			out.println();
		} catch (IOException e1) {
			
		}
		finally{
			out.close();
		}
	}
	
	public static void log(Object o, LogType t){
		if(o == null)
			return;
		PrintWriter out = null;
		try {
			out = SetWriter(t);
			out.println(new Date() + ": ");
			out.println(o.toString());
			out.println();
		} catch (IOException e1) {
			
		}
		finally{
			out.close();
		}
	}
	
	public static Statistics readJson(){
		String fpath = Constants.STATS_LOG;
		
		JSONParser parser = new JSONParser();
		
		Statistics s = new Statistics();
		 
		try {
	 
			Object obj = parser.parse(new FileReader(fpath));
	 
			JSONObject jsonObject = (JSONObject) obj;
			
			for(int i=0; i<s.descriptors.length; ++i){
				s.values[i] = (Double) jsonObject.get(s.descriptors[i]);
			}
	 
			/*// loop array
			JSONArray msg = (JSONArray) jsonObject.get("messages");
			Iterator<String> iterator = msg.iterator();
			while (iterator.hasNext()) {
				System.out.println(iterator.next());
			}*/
	 
		} catch (FileNotFoundException e) {
			Logger.log(e, LogType.ERROR_LOG);
		} catch (IOException e) {
			Logger.log(e, LogType.ERROR_LOG);
		} catch (ParseException e) {
			Logger.log(e, LogType.ERROR_LOG);
		}
		
		return s;
	}
	
	@SuppressWarnings("unchecked")
	public static void writeJson(Statistics s){
		JSONObject obj = new JSONObject();
		int items = Math.max(s.descriptors.length, s.values.length);
		for(int i=0; i<items; ++i){
			obj.put(s.descriptors[i], s.values[i]);
		}
	 
		/*JSONArray list = new JSONArray();
		list.add("msg 1");
		list.add("msg 2");
		list.add("msg 3");
	 
		obj.put("messages", list);*/
		
		FileWriter file = null;
		try {
			file = new FileWriter(Constants.STATS_LOG);
			file.write(JsonWriter.formatJson(obj.toJSONString()));
			file.flush();
			file.close();
		} catch (IOException e) {
			Logger.log(e, LogType.ERROR_LOG);
		}
	 
	}
	
	public static synchronized void backupFeatures(int file, double[] features){
		String fpath = Constants.FEATURES_LOG;
		
		JSONParser parser = new JSONParser();
		List<Double[]> currentObjs = null;
		List<Integer> currentInds = null;
		List<JSONObject> objarr = null;
		
		boolean nofile = false;
		
		try {
	 
			Object obj = parser.parse(new FileReader(fpath));
	 
			JSONObject jsonObject = (JSONObject) obj;
			
			currentInds = new ArrayList<Integer>();
			currentObjs = new ArrayList<Double[]>();
			objarr = new ArrayList<JSONObject>();
			
			/*for(int i=0; i<jsonObject.size(); ++i){
				current.add( (Double[]) jsonObject.get(i));
			}*/
	 
			JSONArray arr = (JSONArray) jsonObject.get("Feature list");
			Iterator<Object> iterator = arr.iterator();
			while (iterator.hasNext()) {
				objarr.add((JSONObject) iterator.next());
			}
			
			for(int i=0; i<objarr.size(); ++i){
				currentInds.add((int) (long) (Long) objarr.get(i).get("File"));
				JSONArray array = (JSONArray) objarr.get(i).get("Features");
				Iterator<Object> it = array.iterator();
				List<Double> temparray = new ArrayList<Double>();
				while(it.hasNext())
					temparray.add((Double) it.next());
				
				Double[] tmp = new Double[temparray.size()];
				for(int j=0; j<temparray.size(); ++j)
					tmp[j] = temparray.get(j);
				currentObjs.add(tmp);
			}
	 
		} catch (FileNotFoundException e) {
			nofile = true;
		} catch (IOException e) {
			Logger.log(e, LogType.ERROR_LOG);
		} catch (ParseException e) {
			Logger.log(e, LogType.ERROR_LOG);
		}

		JSONObject master_obj = new JSONObject();
		JSONArray master_list = new JSONArray();
		
		if(!nofile){
			for(int i=0; i<currentInds.size(); ++i){
				JSONObject obj = new JSONObject();
				JSONArray list = new JSONArray();
				obj.put("File", currentInds.get(i));
				for(int j=0; j<currentObjs.get(i).length; ++j){
					list.add(currentObjs.get(i)[j]);
				}
				obj.put("Features", list);
				master_list.add(obj);
			}
		}
		
		JSONObject obj = new JSONObject();
		JSONArray list = new JSONArray();
		obj.put("File", file);
		for(int i=0; i<features.length; ++i){
			list.add(features[i]);
		}
		obj.put("Features", list);
		master_list.add(obj);
		
		master_obj.put("Feature list", master_list);
		
		FileWriter fileout = null;
		try {
			fileout = new FileWriter(fpath);
			fileout.write(JsonWriter.formatJson(master_obj.toJSONString()));
			fileout.flush();
			fileout.close();
		} catch (IOException e) {
			Logger.log(e, LogType.ERROR_LOG);
		}
	}
	
	
	public static void backupAllFeatures(List<Song> tracks){
		String fpath = Constants.FEATURES_LOG;
		
		JSONObject master_obj = new JSONObject();
		JSONArray master_list = new JSONArray();
		
		for(int i=0; i<tracks.size(); ++i){
			double[] features = tracks.get(i).getAudioFeatures();
		
			JSONObject obj = new JSONObject();
			JSONArray list = new JSONArray();
			obj.put("File", i);
			for(int j=0; j<features.length; ++j){
				list.add(features[j]);
			}
			obj.put("Features", list);
			master_list.add(obj);
		}
		
		master_obj.put("Feature list", master_list);
		
		FileWriter fileout = null;
		try {
			fileout = new FileWriter(fpath);
			fileout.write(JsonWriter.formatJson(master_obj.toJSONString()));
			fileout.flush();
			fileout.close();
		} catch (IOException e) {
			Logger.log(e, LogType.ERROR_LOG);
		}
	}
	
	
	public static void backupClusters(SongCluster root){
		
		String fpath = Constants.CLUSTERS_LOG;
		
		JSONObject master_obj = new JSONObject();
		JSONArray master_list = new JSONArray();
		
		master_list = SubClustersToJson(root);
		master_obj.put("Clusters", master_list);
		
		FileWriter fileout = null;
		try {
			fileout = new FileWriter(fpath);
			fileout.write(JsonWriter.formatJson(master_obj.toJSONString()));
			fileout.flush();
			fileout.close();
		} catch (IOException e) {
			Logger.log(e, LogType.ERROR_LOG);
		}
		
	}
	
	private static JSONArray SubClustersToJson(SongCluster cs){
		List<AbstractCluster> nested = cs.getChildren();
		JSONArray arr = new JSONArray();
		for(AbstractCluster c : nested){
			if(c instanceof LeafCluster){
				arr.add(((LeafCluster) c).getTrack().toString());
			}else{
				arr.add(SubClustersToJson((SongCluster) c));
			}
		}
		return arr;
	}
	
}
