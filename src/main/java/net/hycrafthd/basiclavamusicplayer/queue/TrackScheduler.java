package net.hycrafthd.basiclavamusicplayer.queue;

import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.*;

import net.hycrafthd.basiclavamusicplayer.event.MusicPlayerEventBus;
import net.hycrafthd.basiclavamusicplayer.event.events.*;
import net.hycrafthd.basiclavamusicplayer.event.events.EventQueue.EventQueueSuccess.State;

public class TrackScheduler extends AudioEventAdapter {
	
	private AudioPlayerManager audioplayermanager;
	private AudioPlayer audioplayer;
	
	private PlayList playlist;
	
	private boolean repeat, shuffle;
	
	public TrackScheduler(AudioPlayerManager audioplayermanager, AudioPlayer audioplayer) {
		this.audioplayermanager = audioplayermanager;
		this.audioplayer = audioplayer;
		this.playlist = new PlayList();
		this.repeat = false;
		this.shuffle = false;
	}
	
	public void play(AudioTrack track) {
		if (track == null) {
			stop();
			return;
		}
		MusicPlayerEventBus.post(new EventPlay(track));
		audioplayer.playTrack(track);
	}
	
	public boolean isPlaying() {
		return audioplayer.getPlayingTrack() != null;
	}
	
	private void queue(AudioTrack track, boolean first) {
		if (!isPlaying()) {
			play(track);
		} else {
			if (first) {
				playlist.offerFirst(track);
			} else {
				playlist.offerLast(track);
			}
		}
	}
	
	public void queueFirst(AudioTrack track) {
		queue(track, true);
	}
	
	public void queueLast(AudioTrack track) {
		queue(track, false);
	}
	
	public void stop() {
		MusicPlayerEventBus.post(new EventStop());
		playlist.clear();
		audioplayer.stopTrack();
	}
	
	public void skip() {
		AudioTrack track = playlist.pollFirst();
		if (track == null) {
			stop();
		} else {
			play(track);
		}
	}
	
	public void shuffle() {
		AudioTrack track = playlist.pollRandom();
		if (track == null) {
			stop();
		} else {
			play(track);
		}
	}
	
	public void mix() {
		playlist.mix();
	}
	
	public List<AudioTrack> getQueue() {
		return playlist.getTracks();
	}
	
	public AudioTrack getCurrentTrack() {
		return audioplayer.getPlayingTrack();
	}
	
	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}
	
	public void setShuffle(boolean shuffle) {
		this.shuffle = shuffle;
	}
	
	public boolean isRepeat() {
		return repeat;
	}
	
	public boolean isShuffle() {
		return shuffle;
	}
	
	public void setPaused(boolean pause) {
		audioplayer.setPaused(pause);
	}
	
	public boolean isPaused() {
		return audioplayer.isPaused();
	}
	
	@Override
	public void onTrackEnd(AudioPlayer audioplayer, AudioTrack track, AudioTrackEndReason reason) {
		switch (reason) {
		case FINISHED:
		case LOAD_FAILED:
			if (repeat) {
				play(track.makeClone());
			} else if (shuffle) {
				shuffle();
			} else {
				skip();
			}
			break;
		case STOPPED:
			stop();
			break;
		default:
			break;
		}
	}
	
	public void queue(String identifier) {
		play(identifier, false);
	}
	
	public void play(String identifier) {
		play(identifier, true);
	}
	
	public void play(String identifier, boolean force) {
		boolean isSearch = identifier.startsWith("ytsearch:") || identifier.startsWith("scsearch:");
		audioplayermanager.loadItemOrdered(this, identifier, new AudioLoadResultHandler() {
			
			@Override
			public void trackLoaded(AudioTrack track) {
				if (force) {
					playTrack(track);
				} else {
					queueTrack(track);
				}
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
			}
			
			private void playPlayList(AudioPlaylist playlist) {
				MusicPlayerEventBus.post(new EventQueue.EventQueuePlayList(State.PLAY, playlist));
				List<AudioTrack> tracks = playlist.getTracks();
				play(tracks.get(0));
				for (int i = tracks.size() - 1; i > 0; i--) {
					queueFirst(tracks.get(i));
				}
			}
			
			private void queuePlayList(AudioPlaylist playlist) {
				MusicPlayerEventBus.post(new EventQueue.EventQueuePlayList(State.QUEUE, playlist));
				for (AudioTrack track : playlist.getTracks()) {
					queueLast(track);
				}
			}
			
			private void playTrack(AudioTrack track) {
				MusicPlayerEventBus.post(new EventQueue.EventQueueTrack(State.PLAY, track));
				play(track);
			}
			
			private void queueTrack(AudioTrack track) {
				MusicPlayerEventBus.post(new EventQueue.EventQueueTrack(State.QUEUE, track));
				queueLast(track);
			}
			
			@Override
			public void noMatches() {
				if (!isSearch) {
					play("ytsearch: " + identifier, force);
					return;
				}
				MusicPlayerEventBus.post(new EventQueue.EventQueueFailed("no matches", new IllegalArgumentException("no matches")));
			}
			
			@Override
			public void loadFailed(FriendlyException exception) {
				MusicPlayerEventBus.post(new EventQueue.EventQueueFailed(exception.getMessage(), exception));
			}
		});
	}
}
