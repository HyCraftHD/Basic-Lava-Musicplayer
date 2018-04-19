package net.hycrafthd.basiclavamusicplayer.event.events;

import com.sedmelluq.discord.lavaplayer.track.*;

import net.hycrafthd.basiclavamusicplayer.event.MusicPlayerEvent;

public abstract class EventQueue implements MusicPlayerEvent {
	
	public static class EventQueueSuccess extends EventQueue {
		
		private final State state;
		
		public EventQueueSuccess(State state) {
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
	
	public static class EventQueueTrack extends EventQueueSuccess {
		
		private final AudioTrack track;
		
		public EventQueueTrack(State state, AudioTrack track) {
			super(state);
			this.track = track;
		}
		
		public AudioTrack getTrack() {
			return track;
		}
		
	}
	
	public static class EventQueuePlayList extends EventQueueSuccess {
		
		private final AudioPlaylist playlist;
		
		public EventQueuePlayList(State state, AudioPlaylist playlist) {
			super(state);
			this.playlist = playlist;
		}
		
		public AudioPlaylist getPlayList() {
			return playlist;
		}
	}
	
	public static class EventQueueFailed extends EventQueue {
		
		private final String error;
		private final Exception exception;
		
		public EventQueueFailed(String error, Exception exception) {
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
