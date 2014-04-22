package jk509.player;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;

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

public class Test {

	private JFrame frame;
	private JButton btnTestGuiThreading;
	private JButton btnTestFileupload;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Test window = new Test();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Test() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		btnTestGuiThreading = new JButton("Test Gui threading");
		btnTestGuiThreading.setBounds(31, 32, 181, 23);
		frame.getContentPane().add(btnTestGuiThreading);
		
		btnTestFileupload = new JButton("Test fileupload");
		btnTestFileupload.addActionListener(new BtnTestFileuploadActionListener());
		btnTestFileupload.setBounds(31, 102, 214, 23);
		frame.getContentPane().add(btnTestFileupload);
	}
	
	/*
	 * TODO s: directory structure/namings for uploads
	 */
	private class BtnTestFileuploadActionListener implements ActionListener {
		@SuppressWarnings("unused")
		public void actionPerformed(ActionEvent arg0) {
			String uniqueString = GenerateString(10);
			String date = (new Date()).toString();
			File[] files = new File[]{ new File("C:\\users\\james\\music factory\\library.ser") };
			
			
			CloseableHttpClient httpclient = HttpClients.createDefault();
	        try {
	            HttpPost httppost = new HttpPost(Constants.UPLOAD_URL);

	            FileBody bin = new FileBody(files[0]);
	            StringBody comment = new StringBody("A binary file of some kind: "+date, ContentType.TEXT_PLAIN);

	            HttpEntity reqEntity = MultipartEntityBuilder.create()
	                    .addPart("files", bin)
	                    .addPart("text", comment)
	                    .build();


	            httppost.setEntity(reqEntity);

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
	                    if(result.startsWith("success")){
	                    	// it worked
	                    	System.out.println("Success");
	                    }else{
	                    	// error
	                    	System.out.println("Error");
	                    }
	                }
	                EntityUtils.consume(resEntity);
	            } finally {
	                response.close();
	            }
	        } catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
	            try {
					httpclient.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	        System.exit(0);
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
}
