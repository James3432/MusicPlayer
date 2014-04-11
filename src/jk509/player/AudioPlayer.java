package jk509.player;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;


public class AudioPlayer
{
    private int bufferSize = 4096; // Tamanho de buffer padrão 4k
    private volatile boolean paused = false;
    private final Object lock = new Object();
    private SourceDataLine line;
    private int secondsFade = 0;
    //private ArrayList<AudioPlayerListener> _listeners = new ArrayList<AudioPlayerListener>();

    public void stop()
    {
        if(line != null)
        {
            line.stop();
            line.close();
        }
    }

    public boolean isPaused()
    {
        return this.paused;
    }


    public void pause()
    {
        if(!this.isPaused())
            paused = true;
    }

    public void resume()
    {
        if(this.isPaused())
        {
            synchronized(lock){
                lock.notifyAll();
                paused = false;
            }
        }
    }

    public static void main(String[] args){
    	AudioPlayer p = new AudioPlayer();
    	try {
			p.play(new File("E:\\Users\\James\\Music\\iTunes\\iTunes Media\\Music\\Muse\\Absolution\\08 Hysteria.mp3"));
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void play(File file) throws UnsupportedAudioFileException, IOException, LineUnavailableException, InterruptedException
    {
        AudioInputStream encoded = AudioSystem.getAudioInputStream(file);
        AudioFormat encodedFormat = encoded.getFormat();
        AudioFormat decodedFormat = this.getDecodedFormat(encodedFormat);
        AudioInputStream currentDecoded = AudioSystem.getAudioInputStream(decodedFormat, encoded);
        
        //SampleRateConversionProvider srcp = new SampleRateConversionProvider();
        //srcp.getAudioInputStream(targetFormat, sourceStream)
        
        line = AudioSystem.getSourceDataLine(decodedFormat);
        line.open(decodedFormat);
        line.start();
        
        byte[] b = new byte[this.bufferSize];
        int i = 0;
        
            
            while(true)
            {
              
                i = currentDecoded.read(b, 0, b.length);
                if(i == -1)
                    break;

                line.write(b, 0, i);
            }
        

        line.drain();
        line.stop();
        line.close();
        currentDecoded.close();
        encoded.close();
    }

    

    protected AudioFormat getDecodedFormat(AudioFormat format)
    {
        AudioFormat decodedFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,  // Encoding to use
                22050,//format.getSampleRate(),           // sample rate (same as base format)
                16,               // sample size in bits (thx to Javazoom)
                format.getChannels(),             // # of Channels
                format.getChannels()*2,           // Frame Size
                format.getSampleRate(),           // Frame Rate
                true                 // Big Endian
        );
        return decodedFormat;    
    }


    public int getBufferSize()
    {
        return bufferSize;
    }


    public void setBufferSize(int bufferSize)
    {
        if(bufferSize <= 0)
            return;
        this.bufferSize = bufferSize;
    }

    /**
     * @return the secondsFade
     */
    public int getSecondsFade() {
        return secondsFade;
    }

    /**
     * @param secondsFade the secondsFade to set
     */
    public void setSecondsFade(int secondsFade) {
        if(secondsFade < 0 || secondsFade > 10)
            throw new IllegalArgumentException("Erro ao configurar cross-fade com valor em segundos: "+secondsFade);
        this.secondsFade = secondsFade;
    }


}