package net.hycrafthd.basiclavamusicplayer;

import java.util.List;
import java.util.function.Consumer;

import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat.Codec;
import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration.ResamplingQuality;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.hycrafthd.basiclavamusicplayer.output.AudioOutput;
import net.hycrafthd.basiclavamusicplayer.queue.PlayingCallback;
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
		trackscheduler = new TrackScheduler(audioplayer);
		
		setup();
	}
	
	private void setup() {
		audioplayermanager.setFrameBufferDuration(1000);
		audioplayermanager.setPlayerCleanupThreshold(Long.MAX_VALUE);
		
		// Max quality
		audioplayermanager.getConfiguration().setResamplingQuality(ResamplingQuality.HIGH);
		audioplayermanager.getConfiguration().setOpusEncodingQuality(10);
		audioplayermanager.getConfiguration().setOutputFormat(audiodataformat);
		
		// Register remote sources like http and youtube
		AudioSourceManagers.registerRemoteSources(audioplayermanager);
		
		// Add trackscheduler as listener for audio events
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
	
	public void setPause() {
		audioplayer.setPaused(true);
	}
	
	public void setUnpause() {
		audioplayer.setPaused(false);
	}
	
	public boolean toggleRepeat() {
		return trackscheduler.repeat();
	}
	
	public void skip() {
		trackscheduler.nextTrack();
	}
	
	public void queue(String identifier, Consumer<PlayingCallback> callback) {
		play(identifier, false, callback);
	}
	
	public void play(String identifier, Consumer<PlayingCallback> callback) {
		play(identifier, true, callback);
	}
	
	public void play(String identifier, boolean force, Consumer<PlayingCallback> callback) {
		boolean isSearch = identifier.startsWith("ytsearch:") || identifier.startsWith("scsearch:");
		audioplayermanager.loadItemOrdered(this, identifier, new AudioLoadResultHandler() {
			
			@Override
			public void trackLoaded(AudioTrack track) {
				if (force) {
					playTrack(track);
				} else {
					queueTrack(track);
				}
				callback.accept(new PlayingCallback());
			}
			
			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				List<AudioTrack> tracks = playlist.getTracks();
				if (tracks.size() == 0) {
					noMatches();
					return;
				}
				if (playlist.isSearchResult()) {
					AudioTrack track = tracks.get(0);
					if (force) {
						playTrack(track);
					} else {
						queueTrack(track);
					}
				} else {
					if (force) {
						playPlayList(playlist);
					} else {
						queuePlayList(playlist);
					}
				}
				callback.accept(new PlayingCallback());
			}
			
			private void playPlayList(AudioPlaylist playlist) {
				List<AudioTrack> tracks = playlist.getTracks();
				trackscheduler.play(tracks.get(0));
				for (int i = tracks.size() - 1; i > 0; i--) {
					trackscheduler.queueFirst(tracks.get(i));
				}
			}
			
			private void queuePlayList(AudioPlaylist playlist) {
				for (AudioTrack track : playlist.getTracks()) {
					trackscheduler.queue(track);
				}
			}
			
			private void playTrack(AudioTrack track) {
				trackscheduler.play(track);
			}
			
			private void queueTrack(AudioTrack track) {
				trackscheduler.queue(track);
			}
			
			@Override
			public void noMatches() {
				if (!isSearch) {
					play("ytsearch: " + identifier, force, callback);
					return;
				}
				callback.accept(new PlayingCallback(false, "no matches"));
			}
			
			@Override
			public void loadFailed(FriendlyException exception) {
				callback.accept(new PlayingCallback(false, exception.getMessage()));
			}
		});
	}
	
}
