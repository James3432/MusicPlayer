package jk509.player.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import jk509.player.Constants;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
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
	public static void upload() {
		String uniqueString = GenerateString(10);
		String anon_id = Security.getSerialNumber(Constants.USERNAME_AS_ID);
		int date = Days.daysBetween(Constants.STUDY_START_DATE, new DateTime()).getDays();
		/*File[] files = new File[] { 
				new File(StaticMethods.getSettingsDir() + "features.txt"),
				new File(StaticMethods.getSettingsDir() + "clusters.txt")
		};*/
		
		File[] files = new File[Constants.UPLOAD_FILE_LIST.length];
		for(int i=0; i<files.length; ++i)
			files[i] = new File(Constants.UPLOAD_FILE_LIST[i]);
	
		// String[] descriptors = new String[] { "Features file", "Clusters file" };
	
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpPost httppost = new HttpPost(Constants.UPLOAD_URL);
	
			MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create();
	
			for (int i = 0; i < files.length; ++i) {
				FileBody f = new FileBody(files[i]);
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
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static String GenerateString(int n) {
		String s = "";
		for (int i = 0; i < n; ++i) {
			Random r = new Random();
			char c = (char) (r.nextInt(26) + 'a');
			s = s + c;
		}
		return s;
	}

}
