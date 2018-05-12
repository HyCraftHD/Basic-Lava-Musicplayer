package net.hycrafthd.basiclavamusicplayer.queue;

import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.*;

import net.hycrafthd.basiclavamusicplayer.event.MusicPlayerEventBus;
import net.hycrafthd.basiclavamusicplayer.event.events.*;

public class TrackScheduler extends AudioEventAdapter {
	
	private AudioPlayer audioplayer;
	
	private PlayList playlist;
	
	private boolean repeat, shuffle;
	
	public TrackScheduler(AudioPlayer audioplayer) {
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
}
