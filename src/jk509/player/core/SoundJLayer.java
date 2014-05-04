package jk509.player.core;

import javazoom.jl.decoder.JavaLayerException;
import jk509.player.MusicPlayer;
import jk509.player.logging.Logger;
import jk509.player.logging.Logger.LogType;

public class SoundJLayer implements Runnable {
	private String filePath;
	private JLayerPlayerPausable player;
	private Thread playerThread;
	private String namePlayerThread = "AudioPlayerThread";
	private MusicPlayer.PlaybackListener pl;
	private int startIndex;

	// private PlaybackListener playbackListener = new PlaybackListener();

	public SoundJLayer(String filePath, MusicPlayer.PlaybackListener pl) {
		this.filePath = filePath;
		this.pl = pl;
		startIndex = -1;
	}

	public SoundJLayer(String filePath, String namePlayerThread, MusicPlayer.PlaybackListener pl) {
		this.filePath = filePath;
		this.namePlayerThread = namePlayerThread;
		this.pl = pl;
		startIndex = -1;
	}

	public void play() {
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

	public void play(int ms) {
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

	private int msToIndex(int ms) {
		// convert time in milliseconds to which frame is needed in the buffer
		return (int) Math.floor((double) ms / (double) this.player.getMsPerFrame());
	}

	public void pause() throws NullPointerException {
		if (this.player != null) {
			this.player.pause();

			if (this.playerThread != null) {
				// this.playerThread.stop(); //unsafe method
				this.playerThread = null;
			}
		}
	}

	public void pauseToggle() {
		if (this.player != null) {
			if (this.player.isPaused() && !this.player.isStopped()) {
				this.play();
			} else {
				this.pause();
			}
		}
	}

	public void reset(String filePath) {
		this.filePath = filePath;
	}

	public boolean isPaused() {
		return this.player.isPaused();
	}

	public boolean isStopped() {
		return this.player.isStopped();
	}

	public void stop() {
		if (this.player != null) {
			this.player.stop();

			if (this.playerThread != null) {
				// this.playerThread.stop(); //unsafe method
				this.playerThread = null;
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
			ex.printStackTrace();
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