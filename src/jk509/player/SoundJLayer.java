package jk509.player;

import javazoom.jl.decoder.JavaLayerException;

public class SoundJLayer implements Runnable{
	private String filePath;
	private JLayerPlayerPausable player;
	private Thread playerThread;
	private String namePlayerThread = "AudioPlayerThread";
	private PlaybackListener playbackListener = new PlaybackListener();

	public SoundJLayer(String filePath){
		this.filePath = filePath;
	}
	
	public SoundJLayer(String filePath, String namePlayerThread){
		this.filePath = filePath;
		this.namePlayerThread = namePlayerThread;
	}

	public void play(){
		if (this.player == null){
			this.playerInitialize();
		}
		else if(!this.player.isPaused() || this.player.isComplete() || this.player.isStopped()){
			this.stop();
			this.playerInitialize();
		}
		this.playerThread = new Thread(this, namePlayerThread);
		this.playerThread.setDaemon(true);

		this.playerThread.start();
	}

	public void pause(){
		if (this.player != null){
			this.player.pause();

			if(this.playerThread != null){
				//this.playerThread.stop(); //unsafe method
				this.playerThread = null;
			}
		}
	}

	public void pauseToggle(){
		if (this.player != null){
			if (this.player.isPaused() && !this.player.isStopped()){
				this.play();
			}
			else{
				this.pause();
			}
		}
	}

	public boolean isPaused(){
		return this.player.isPaused();
	}
	
	public boolean isStopped(){
		return this.player.isStopped();
	}
	
	public void stop(){
		if (this.player != null){
			this.player.stop();

			if(this.playerThread != null){
				//this.playerThread.stop(); //unsafe method
				this.playerThread = null;
			}
		}
	}

	/*private void playerInitialize(){
		try{
			String urlAsString = 
					"file:///" 
							+ new java.io.File(".").getCanonicalPath() 
							+ "/" 
							+ this.filePath;

			this.player = new JLayerPlayerPausable(new java.net.URL(urlAsString));
			this.player.setPlaybackListener(this.playbackListener);
		}
		catch (JavaLayerException e){
			e.printStackTrace();
		}
	}*/
	private void playerInitialize(){
		try {
			this.player = new JLayerPlayerPausable(this.filePath);
			this.player.setPlaybackListener(this.playbackListener);
		}
		catch (JavaLayerException e) {
			e.printStackTrace();
		}
	}
	
	/*public FloatControl getFloatControl() throws JavaLayerException{
		return player.getFloatControl();
	}*/

	// IRunnable members
	public void run(){
		try{
			this.player.resume();
		}
		catch (javazoom.jl.decoder.JavaLayerException ex){
			ex.printStackTrace();
		}
	}
	
	private static class PlaybackListener extends JLayerPlayerPausable.PlaybackAdapter {
		// PlaybackListener members
		@Override
		public void playbackStarted(JLayerPlayerPausable.PlaybackEvent playbackEvent){
			System.err.println("PlaybackStarted()");
		}
		
		@Override
		public void playbackPaused(JLayerPlayerPausable.PlaybackEvent playbackEvent){
			System.err.println("PlaybackPaused()");
		}

		@Override
		public void playbackFinished(JLayerPlayerPausable.PlaybackEvent playbackEvent){
			System.err.println("PlaybackStopped()");
		}
	}
}