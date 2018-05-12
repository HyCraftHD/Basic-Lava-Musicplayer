package net.hycrafthd.basiclavamusicplayer.search;

import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.*;

import net.hycrafthd.basiclavamusicplayer.event.MusicPlayerEventBus;
import net.hycrafthd.basiclavamusicplayer.event.events.EventSearch.*;
import net.hycrafthd.basiclavamusicplayer.event.events.EventSearch.EventSearchSuccess.State;
import net.hycrafthd.basiclavamusicplayer.queue.TrackScheduler;

public class TrackSearch {
	
	private AudioPlayerManager audioplayermanager;
	
	private TrackScheduler trackscheduler;
	
	public TrackSearch(AudioPlayerManager audioplayermanager, TrackScheduler trackscheduler) {
		this.audioplayermanager = audioplayermanager;
		this.trackscheduler = trackscheduler;
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
				MusicPlayerEventBus.post(new EventSearchPlayList(State.PLAY, playlist));
				List<AudioTrack> tracks = playlist.getTracks();
				trackscheduler.play(tracks.get(0));
				for (int i = tracks.size() - 1; i > 0; i--) {
					trackscheduler.queueFirst(tracks.get(i));
				}
			}
			
			private void queuePlayList(AudioPlaylist playlist) {
				MusicPlayerEventBus.post(new EventSearchPlayList(State.QUEUE, playlist));
				for (AudioTrack track : playlist.getTracks()) {
					trackscheduler.queueLast(track);
				}
			}
			
			private void playTrack(AudioTrack track) {
				MusicPlayerEventBus.post(new EventSearchTrack(State.PLAY, track));
				trackscheduler.play(track);
			}
			
			private void queueTrack(AudioTrack track) {
				MusicPlayerEventBus.post(new EventSearchTrack(State.QUEUE, track));
				trackscheduler.queueLast(track);
			}
			
			@Override
			public void noMatches() {
				if (!isSearch) {
					play("ytsearch: " + identifier, force);
					return;
				}
				MusicPlayerEventBus.post(new EventSearchFailed("no matches", new IllegalArgumentException("no matches")));
			}
			
			@Override
			public void loadFailed(FriendlyException exception) {
				MusicPlayerEventBus.post(new EventSearchFailed(exception.getMessage(), exception));
			}
		});
	}
	
}
