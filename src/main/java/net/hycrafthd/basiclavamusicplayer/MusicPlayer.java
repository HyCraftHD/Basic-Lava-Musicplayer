package net.hycrafthd.basiclavamusicplayer;

import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat.Codec;
import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration.ResamplingQuality;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import net.hycrafthd.basiclavamusicplayer.output.AudioOutput;
import net.hycrafthd.basiclavamusicplayer.queue.TrackScheduler;

public class MusicPlayer {
	
	private AudioPlayerManager audioplayermanager;
	private AudioDataFormat audiodataformat;
	private AudioPlayer audioplayer;
	private AudioOutput audiooutput;
	private TrackScheduler trackscheduler;
	
	public MusicPlayer() {
		audioplayermanager = new DefaultAudioPlayerManager();
		audiodataformat = new AudioDataFormat(2, 48000, 960, Codec.PCM_S16_LE);
		audioplayer = audioplayermanager.createPlayer();
		audiooutput = new AudioOutput(this);
		trackscheduler = new TrackScheduler(audioplayermanager, audioplayer);
		
		setup();
	}
	
	private void setup() {
		audioplayermanager.setFrameBufferDuration(1000);
		audioplayermanager.setPlayerCleanupThreshold(Long.MAX_VALUE);
		
		audioplayermanager.getConfiguration().setResamplingQuality(ResamplingQuality.HIGH);
		audioplayermanager.getConfiguration().setOpusEncodingQuality(10);
		audioplayermanager.getConfiguration().setOutputFormat(audiodataformat);
		
		AudioSourceManagers.registerRemoteSources(audioplayermanager);
		AudioSourceManagers.registerLocalSource(audioplayermanager);
		
		audioplayer.addListener(trackscheduler);
	}
	
	public AudioPlayerManager getAudioPlayerManager() {
		return audioplayermanager;
	}
	
	public AudioDataFormat getAudioDataFormat() {
		return audiodataformat;
	}
	
	public AudioPlayer getAudioPlayer() {
		return audioplayer;
	}
	
	public TrackScheduler getTrackScheduler() {
		return trackscheduler;
	}
	
	public void startAudioOutput() {
		audiooutput.start();
	}
	
	public void setVolume(int volume) {
		audioplayer.setVolume(volume);
	}
}
