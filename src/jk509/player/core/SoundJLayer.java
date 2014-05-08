package jk509.player.core;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import javazoom.jl.decoder.JavaLayerException;
import jk509.player.MusicPlayer;
import jk509.player.core.JLayerPlayerPausable.DecodeEvent;
import jk509.player.core.JLayerPlayerPausable.PlaybackEvent;
import jk509.player.core.JLayerPlayerPausable.PlaybackEvent.EventType;
import jk509.player.logging.Logger;
import jk509.player.logging.Logger.LogType;

public class SoundJLayer implements Runnable {
	private String filePath;
	private JLayerPlayerPausable player;
	private Thread playerThread;
	private String namePlayerThread = "AudioPlayerThread";
	private MusicPlayer.PlaybackListener pl;
	private int startIndex;
	public boolean isWav = false;
	private Thread decodeTimer;
	private Clip line;
	private boolean wavStopped, wavPaused;

	// private PlaybackListener playbackListener = new PlaybackListener();

	public SoundJLayer(String filePath, MusicPlayer.PlaybackListener pl) {
		this.filePath = filePath;
		this.pl = pl;
		startIndex = -1;
		if (StaticMethods.getExtension(filePath).equals("wav")) {
			isWav = true;
			InitWav();
		}
	}

	// This isn't currently used
	public SoundJLayer(String filePath, String namePlayerThread, MusicPlayer.PlaybackListener pl) {
		this.filePath = filePath;
		this.namePlayerThread = namePlayerThread;
		this.pl = pl;
		startIndex = -1;
		if (StaticMethods.getExtension(filePath).equals("wav")) {
			isWav = true;
			InitWav();
		}
	}

	public void play() {
		if (isWav) {
			if(wavStopped)
				InitWav();
			
			line.start();
			
			if(!wavPaused)
				decodeTimer.start();
			
			pl.playbackStarted(new PlaybackEvent(filePath, EventType.Started, 0));
			wavStopped = false;
			wavPaused = false;
		} else {
			if (this.player == null) {
				this.playerInitialize(pl);
			} else if (!this.player.isPaused() || this.player.isComplete() || this.player.isStopped()) {
				this.stop();
				this.playerInitialize(pl);
			}
			this.startIndex = -1;
			this.playerThread = new Thread(this, namePlayerThread);
			this.playerThread.setDaemon(true);

			this.playerThread.start();
		}
	}

	public void play(int ms) {
		if (isWav) {
			wavStopped = false;
			wavPaused = false;
			line.setMicrosecondPosition(ms * 1000);
			line.start();
		} else {
			if (this.player == null) {
				this.playerInitialize(pl);
			} else if (!this.player.isPaused() || this.player.isComplete() || this.player.isStopped()) {
				this.stop();
				this.playerInitialize(pl);
			}
			this.startIndex = msToIndex(ms);
			this.playerThread = new Thread(this, namePlayerThread);
			this.playerThread.setDaemon(true);

			this.playerThread.start();
		}
	}

	private int msToIndex(int ms) {
		// convert time in milliseconds to which frame is needed in the buffer
		return (int) Math.floor((double) ms / (double) this.player.getMsPerFrame());
	}

	public void pause() throws NullPointerException {
		if (isWav) {
			wavPaused = true;
			line.stop();
			pl.playbackPaused(new PlaybackEvent(filePath, EventType.Paused, 0));
		} else {
			if (this.player != null) {
				this.player.pause();

				if (this.playerThread != null) {
					// this.playerThread.stop(); //unsafe method
					this.playerThread = null;
				}
			}
		}
	}

	public void pauseToggle() {
		if (isWav) {
			if (wavPaused && !wavStopped)
				pause();
			else
				play();
		} else {
			if (this.player != null) {
				if (this.player.isPaused() && !this.player.isStopped()) {
					this.play();
				} else {
					this.pause();
				}
			}
		}
	}

	public void reset(String filePath) {
		this.filePath = filePath;
	}

	public boolean isPaused() {
		if (isWav) {
			return wavPaused;
		} else {
			return this.player.isPaused();
		}
	}

	public boolean isStopped() {
		if (isWav) {
			return wavStopped;
		} else {
			return this.player.isStopped();
		}
	}

	public void stop() {
		if (isWav) {
			wavStopped = true;
			line.stop();
			line.setMicrosecondPosition(0);
			decodeTimer.interrupt();
			line.close();
		} else {
			if (this.player != null) {
				this.player.stop();

				if (this.playerThread != null) {
					// this.playerThread.stop(); //unsafe method
					this.playerThread = null;
				}
			}
		}
	}

	/*
	 * private void playerInitialize(){ try{ String urlAsString = "file:///" + new java.io.File(".").getCanonicalPath() + "/" + this.filePath;
	 * 
	 * this.player = new JLayerPlayerPausable(new java.net.URL(urlAsString)); this.player.setPlaybackListener(this.playbackListener); } catch (JavaLayerException e){ Logger.log(e, LogType.ERROR_LOG); } }
	 */
	private void playerInitialize(MusicPlayer.PlaybackListener pl) {
		try {
			this.player = new JLayerPlayerPausable(this.filePath);
			this.player.setPlaybackListener(pl);
		} catch (JavaLayerException e) {
			Logger.log(e, LogType.ERROR_LOG);
		}
	}

	private void InitWav(){
		try {
			AudioInputStream is = AudioSystem.getAudioInputStream(new File(this.filePath));
			
			DataLine.Info info = new DataLine.Info(Clip.class, is.getFormat()); // format is an AudioFormat object
			if (!AudioSystem.isLineSupported(info)) {
				Logger.log("WAV audio system line not supported", LogType.ERROR_LOG);
			}
			
			// Obtain and open the line.
			line = (Clip) AudioSystem.getLine(info);
			line.open(is);
			
			this.decodeTimer = new Thread() {
				long pos = 0;

				@Override
				public void run() {

					while (true) {// line.isActive()){
						try {
							long p = line.getMicrosecondPosition();
							if (p - pos > 100000) {
								pos = p;
								pl.frameDecoded(new DecodeEvent(null, pos/1000, 0, 0, filePath));
							}
							Thread.sleep(100);
						} catch (InterruptedException e) {
							break;
						}
					}
				}
			};
			

			line.addLineListener(new LineListener() {
				@Override
				public void update(LineEvent event) {
					LineEvent.Type type = event.getType();

					if (type == LineEvent.Type.START) {
						//System.out.println("Playback started.");

					} else if (type == LineEvent.Type.STOP) {
						if(event.getFramePosition() >= line.getFrameLength())
							pl.playbackFinished(new PlaybackEvent(filePath, EventType.Stopped, 0));
						else
							pl.playbackPaused(new PlaybackEvent(filePath, EventType.Paused, 0));
					}
				}
			});

		} catch (LineUnavailableException ex) {
			Logger.log(ex, LogType.ERROR_LOG);
		} catch (UnsupportedAudioFileException e1) {
			Logger.log(e1, LogType.ERROR_LOG);
		} catch (IOException e1) {
			Logger.log(e1, LogType.ERROR_LOG);
		}

	}

	/*
	 * public FloatControl getFloatControl() throws JavaLayerException{ return player.getFloatControl(); }
	 */

	// IRunnable members
	public void run() {
		try {
			if (startIndex != -1)
				this.player.play(startIndex);
			else
				this.player.resume();
		} catch (javazoom.jl.decoder.JavaLayerException ex) {
			Logger.log(ex, LogType.ERROR_LOG);
		}
	}

	/*
	 * private static class PlaybackListener extends JLayerPlayerPausable.PlaybackAdapter { // PlaybackListener members
	 * 
	 * @Override public void playbackStarted(JLayerPlayerPausable.PlaybackEvent playbackEvent) { // System.err.println("PlaybackStarted()"); }
	 * 
	 * @Override public void playbackPaused(JLayerPlayerPausable.PlaybackEvent playbackEvent) { // System.err.println("PlaybackPaused()"); }
	 * 
	 * @Override public void playbackFinished(JLayerPlayerPausable.PlaybackEvent playbackEvent) { // System.err.println("PlaybackStopped()"); } }
	 */
}