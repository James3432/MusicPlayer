package jk509.player.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jk509.player.Constants;
import jk509.player.logging.Logger;
import jk509.player.logging.Logger.LogType;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;

public class FileUploader {

	/*
	 * TODO; handle errors, set file list from Constants
	 */
	public static void upload(String anon_id) throws Exception {
		CloseableHttpClient httpclient = null;
		try{
			String uniqueString = StaticMethods.generateString(10);
			
			int date = Days.daysBetween(Constants.STUDY_START_DATE, new DateTime()).getDays();
			/*File[] files = new File[] { 
					new File(StaticMethods.getSettingsDir() + "features.txt"),
					new File(StaticMethods.getSettingsDir() + "clusters.txt")
			};*/
			
			List<File> files = new ArrayList<File>();
			for(int i=0; i<Constants.UPLOAD_FILE_LIST.length; ++i){
				File afile = new File(Constants.UPLOAD_FILE_LIST[i]);
				if(afile.exists())
					files.add(afile);
			}
		
			// String[] descriptors = new String[] { "Features file", "Clusters file" };
		
			httpclient = HttpClients.createDefault();
			
			HttpPost httppost = new HttpPost(Constants.UPLOAD_URL);
	
			MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create();
	
			for (int i = 0; i < files.size(); ++i) {
				FileBody f = new FileBody(files.get(i));
				StringBody comment = new StringBody(/*"User data: " + */anon_id + " " + date + " " + uniqueString/* + " $$ " + descriptors[i]*/, ContentType.TEXT_PLAIN);
				reqEntity.addPart("file_"+i, f);
				reqEntity.addPart("text_"+i, comment);
			}
	
			httppost.setEntity(reqEntity.build());
	
			System.out.println("executing request " + httppost.getRequestLine());
			CloseableHttpResponse response = httpclient.execute(httppost);
			try {
				System.out.println("----------------------------------------");
				System.out.println(response.getStatusLine());
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					System.out.println("Response content length: " + resEntity.getContentLength());
					ByteArrayOutputStream output = new ByteArrayOutputStream();
					resEntity.writeTo(output);
					String result = output.toString();
					if (result.startsWith("success")) {
						// it worked
						System.out.println("Success");
					} else {
						// error
						System.out.println("Error");
					}
					//System.out.println(result);
				}
				EntityUtils.consume(resEntity);
			} finally {
				response.close();
			}
		} finally{
			try {
				httpclient.close();
			} catch (IOException e) {
				Logger.log(e, LogType.ERROR_LOG);
			}
		}
		
	}

	

}
