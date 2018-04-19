package net.hycrafthd.basiclavamusicplayer.event.events;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.hycrafthd.basiclavamusicplayer.event.MusicPlayerEvent;

public class EventPlay implements MusicPlayerEvent {
	
	private final AudioTrack track;
	
	public EventPlay(AudioTrack track) {
		this.track = track;
	}
	
	public AudioTrack getTrack() {
		return track;
	}
}
