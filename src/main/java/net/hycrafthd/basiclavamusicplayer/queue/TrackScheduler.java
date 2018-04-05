package net.hycrafthd.basiclavamusicplayer.queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

public class TrackScheduler extends AudioEventAdapter {
	
	private AudioPlayer audioplayer;
	
	private LinkedBlockingDeque<AudioTrack> queue;
	
	private boolean repeat;
	
	public TrackScheduler(AudioPlayer audioplayer) {
		this.audioplayer = audioplayer;
		this.queue = new LinkedBlockingDeque<>();
		this.repeat = false;
	}
	
	public void play(AudioTrack track) {
		audioplayer.playTrack(track);
	}
	
	public void queueFirst(AudioTrack track) {
		if (audioplayer.getPlayingTrack() == null) {
			play(track);
		} else {
			queue.offerFirst(track);
		}
	}
	
	public void queue(AudioTrack track) {
		if (audioplayer.getPlayingTrack() == null) {
			play(track);
		} else {
			queue.offer(track);
		}
	}
	
	public void stop() {
		queue.clear();
		audioplayer.stopTrack();
	}
	
	public void nextTrack() {
		AudioTrack track = queue.poll();
		if (track == null) {
			stop();
		}
		audioplayer.playTrack(track);
	}
	
	@Override
	public void onTrackEnd(AudioPlayer audioplayer, AudioTrack track, AudioTrackEndReason endReason) {
		if (endReason == AudioTrackEndReason.FINISHED || endReason == AudioTrackEndReason.LOAD_FAILED) {
			if (!repeat) {
				nextTrack();
			} else {
				audioplayer.playTrack(track.makeClone());
			}
		} else if (endReason == AudioTrackEndReason.STOPPED) {
			stop();
		}
	}
	
	public boolean repeat() {
		return repeat = !repeat;
	}
	
	public boolean isRepeat() {
		return repeat;
	}
	
	public void shuffle() {
		List<AudioTrack> tracks = new ArrayList<>();
		queue.drainTo(tracks);
		Collections.shuffle(tracks);
		tracks.forEach(queue::offer);
	}
	
	public List<AudioTrack> getQueue() {
		List<AudioTrack> tracks = new ArrayList<>();
		queue.forEach(tracks::add);
		return tracks;
	}
	
	public AudioTrack getCurrentTrack() {
		return audioplayer.getPlayingTrack();
	}
	
}
