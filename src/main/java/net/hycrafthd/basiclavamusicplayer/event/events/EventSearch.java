package net.hycrafthd.basiclavamusicplayer.event.events;

import com.sedmelluq.discord.lavaplayer.track.*;

import net.hycrafthd.basiclavamusicplayer.event.MusicPlayerEvent;

public abstract class EventSearch implements MusicPlayerEvent {
	
	public static class EventSearchSuccess extends EventSearch {
		
		private final State state;
		
		public EventSearchSuccess(State state) {
			this.state = state;
		}
		
		public State getState() {
			return state;
		}
		
		public enum State {
			PLAY,
			QUEUE;
		}
	}
	
	public static class EventSearchTrack extends EventSearchSuccess {
		
		private final AudioTrack track;
		
		public EventSearchTrack(State state, AudioTrack track) {
			super(state);
			this.track = track;
		}
		
		public AudioTrack getTrack() {
			return track;
		}
		
	}
	
	public static class EventSearchPlayList extends EventSearchSuccess {
		
		private final AudioPlaylist playlist;
		
		public EventSearchPlayList(State state, AudioPlaylist playlist) {
			super(state);
			this.playlist = playlist;
		}
		
		public AudioPlaylist getPlayList() {
			return playlist;
		}
	}
	
	public static class EventSearchFailed extends EventSearch {
		
		private final String error;
		private final Exception exception;
		
		public EventSearchFailed(String error, Exception exception) {
			this.error = error;
			this.exception = exception;
		}
		
		public String getError() {
			return error;
		}
		
		public Exception getException() {
			return exception;
		}
	}
}
