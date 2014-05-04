package jk509.player.logging;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import jk509.player.Constants;

import logging.JsonWriter;

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
	
}
