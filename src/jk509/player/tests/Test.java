package jk509.player.tests;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import jk509.player.Constants;
import jk509.player.core.Security;
import jk509.player.core.StaticMethods;
import jk509.player.logging.Logger;
import jk509.player.logging.Logger.LogType;

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

public class Test {

	private JFrame frame;
	private JButton btnTestGuiThreading;
	private JButton btnTestFileupload;
	private JButton btnLizzyImport;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		String path = "C:\\users\\james\\music\\13 Venus Morena.wav";
		try {
			AudioInputStream is = AudioSystem.getAudioInputStream(new File(path));
			final Clip line;
			DataLine.Info info = new DataLine.Info(Clip.class, is.getFormat()); // format is an AudioFormat object
			if (!AudioSystem.isLineSupported(info)) {
				// Handle the error.
			}
			// Obtain and open the line.
			try {

				line = (Clip) AudioSystem.getLine(info);
				line.open(is);
				Thread time = new Thread() {
					int pos = 0;

					@Override
					public void run() {

						while (true) {// line.isActive()){
							try {
								long p = line.getMicrosecondPosition();
								if (p - pos * 1000000 > 1000000) {
									pos = (int) (line.getMicrosecondPosition() / 1000000);
									System.out.println(pos + "s");
								}
								Thread.sleep(100);
							} catch (InterruptedException e) {
								break;
							}
						}
					}
				};
				time.start();

				line.start();
				// Thread.sleep(2000);
				// line.stop();
				// Thread.sleep(2000);
				// line.start();
				// line.setMicrosecondPosition(line.getMicrosecondLength() - 10000000);
				Thread.sleep(5000);
				time.interrupt();
				line.addLineListener(new LineListener() {
					@Override
					public void update(LineEvent event) {
						LineEvent.Type type = event.getType();

						if (type == LineEvent.Type.START) {
							System.out.println("Playback started.");

						} else if (type == LineEvent.Type.STOP) {
							System.out.println("Playback completed.");
						}
					}
				});

			} catch (LineUnavailableException ex) {
				// Handle the error.
				// ...
			}

			/*
			 * String path = "C:\\users\\james\\music\\13 Venus Morena.wav"; FoundationAudioStream f = new FoundationAudioStream(); f.setDataSource(new File(path)); f.initLine(); f.start(); Thread.sleep(2000); f.pause(); Thread.sleep(2000); f.resume();
			 */

			/*
			 * AudioInputStream stream; stream = AudioSystem.getAudioInputStream(new File(path)); AudioFormat format = stream.getFormat(); if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) { format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, format .getSampleRate(), format.getSampleSizeInBits() * 2, format .getChannels(), format.getFrameSize() * 2, format .getFrameRate(), true); // big endian stream = AudioSystem.getAudioInputStream(format, stream); } DataLine.Info info = new DataLine.Info(Clip.class, stream.getFormat(), ((int) stream.getFrameLength() * format.getFrameSize())); Clip clip = (Clip) AudioSystem.getLine(info); clip.close(); double output = clip.getBufferSize() / (clip.getFormat().getFrameSize() * clip.getFormat() .getFrameRate()); System.out.println(output);
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(System.getProperty("user.dir"));
		System.out.println(System.getProperty("java.library.path"));
		int days = Days.daysBetween(Constants.STUDY_START_DATE, new DateTime()).getDays();
		System.out.println(days);
		StaticMethods.deleteTempFiles();
		Logger.backupFeatures(5, new double[] { 5.2, 0.5, 6.3, 1.2, 2.35234, 1023. });
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Test window = new Test();
					window.frame.setVisible(true);
				} catch (Exception e) {
					Logger.log(e, LogType.ERROR_LOG);
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

		btnLizzyImport = new JButton("lizzy import");
		// btnLizzyImport.addActionListener(new BtnLizzyImportActionListener());
		btnLizzyImport.setBounds(31, 165, 239, 23);
		frame.getContentPane().add(btnLizzyImport);
	}

	private class BtnTestFileuploadActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			String uniqueString = GenerateString(10);
			String anon_id = Security.getSerialNumber(Constants.USERNAME_AS_ID);
			int date = Days.daysBetween(Constants.STUDY_START_DATE, new DateTime()).getDays();
			File[] files = new File[] { new File(StaticMethods.getSettingsDir() + "features.txt"), new File(StaticMethods.getSettingsDir() + "clusters.txt")
			// TODO: more
			};

			// String[] descriptors = new String[] { "Features file", "Clusters file" };

			CloseableHttpClient httpclient = HttpClients.createDefault();
			try {
				HttpPost httppost = new HttpPost(Constants.UPLOAD_URL);

				MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create();

				for (int i = 0; i < files.length; ++i) {
					FileBody f = new FileBody(files[i]);
					StringBody comment = new StringBody(/* "User data: " + */anon_id + " " + date + " " + uniqueString/* + " $$ " + descriptors[i] */, ContentType.TEXT_PLAIN);
					reqEntity.addPart("file_" + i, f);
					reqEntity.addPart("text_" + i, comment);
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
						// System.out.println(result);
					}
					EntityUtils.consume(resEntity);
				} finally {
					response.close();
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				Logger.log(e, LogType.ERROR_LOG);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Logger.log(e, LogType.ERROR_LOG);
			} finally {
				try {
					httpclient.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Logger.log(e, LogType.ERROR_LOG);
				}
			}
			System.exit(0);
		}
	}

	/*
	 * private Playlist importPlaylist(String fpath){ try { File file = new File(fpath); SpecificPlaylist specificPlaylist;
	 * 
	 * specificPlaylist = SpecificPlaylistFactory.getInstance().readFrom(file);
	 * 
	 * if (specificPlaylist == null) { // error } else { Playlist genericPlaylist = specificPlaylist.toPlaylist(); List<String> locs = new ArrayList<String>(); StaticMethods.playlistConverter(genericPlaylist.getRootSequence(), locs); List<Song> tracks = null; List<Song> playlist = StaticMethods.getSongsByLocation(locs, tracks); jk509.player.core.Playlist p = new jk509.player.core.Playlist("test", jk509.player.core.Playlist.USER, playlist); } } catch (Exception e) { Logger.log(e, LogType.ERROR_LOG); } }
	 */

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
