package net.hycrafthd.basiclavamusicplayer.testimpl;

import java.util.Scanner;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import net.hycrafthd.basiclavamusicplayer.MusicPlayer;
import net.hycrafthd.basiclavamusicplayer.event.MusicEventSubscriber;
import net.hycrafthd.basiclavamusicplayer.event.MusicPlayerEventBus;
import net.hycrafthd.basiclavamusicplayer.event.events.EventPlay;
import net.hycrafthd.basiclavamusicplayer.event.events.EventSearch.EventSearchFailed;
import net.hycrafthd.basiclavamusicplayer.event.events.EventSearch.EventSearchPlayList;
import net.hycrafthd.basiclavamusicplayer.event.events.EventSearch.EventSearchTrack;
import net.hycrafthd.basiclavamusicplayer.event.events.EventStop;

public class Main {
	
	public static void main(String[] args) {
		new Main();
	}
	
	public Main() {
		MusicPlayer musicplayer = new MusicPlayer();
		musicplayer.startAudioOutput();
		
		new Thread(() -> {
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(System.in);
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				if (line.startsWith("pause")) {
					musicplayer.getTrackScheduler().setPaused(true);
				} else if (line.startsWith("unpause")) {
					musicplayer.getTrackScheduler().setPaused(false);
				} else if (line.startsWith("play ")) {
					musicplayer.getTrackSearch().play(line.substring(5));
				} else if (line.startsWith("queue ")) {
					musicplayer.getTrackSearch().queue(line.substring(6));
				} else if (line.startsWith("repeat")) {
					musicplayer.getTrackScheduler().setRepeat(!musicplayer.getTrackScheduler().isRepeat());
				} else if (line.startsWith("shuffle")) {
					musicplayer.getTrackScheduler().setShuffle(!musicplayer.getTrackScheduler().isShuffle());
				} else if (line.startsWith("volume ")) {
					int volume = -1;
					try {
						volume = Integer.valueOf(line.substring(7));
					} catch (Exception ex) {
					}
					if (volume != -1) {
						musicplayer.setVolume(volume);
						System.out.println("Volume: " + volume);
					} else {
						System.out.println("Wrong number");
					}
				} else if (line.startsWith("queue")) {
					System.out.println("Now playing: " + getTrackInfo(musicplayer.getTrackScheduler().getCurrentTrack().getInfo()));
					System.out.println("Queue:");
					for (AudioTrack tracks : musicplayer.getTrackScheduler().getQueue()) {
						System.out.println(getTrackInfo(tracks.getInfo()));
					}
				} else if (line.startsWith("skip")) {
					musicplayer.getTrackScheduler().skip();
				} else if (line.startsWith("mix")) {
					musicplayer.getTrackScheduler().mix();
				}
			}
		}).start();
		
		MusicPlayerEventBus.register(this);
		
	}
	
	@MusicEventSubscriber
	public void on(EventStop event) {
		System.out.println("Player stopped");
	}
	
	@MusicEventSubscriber
	public void on(EventPlay event) {
		System.out.println("Playing now: " + getTrackInfo(event.getTrack().getInfo()));
	}
	
	@MusicEventSubscriber
	public void on(EventSearchFailed event) {
		System.out.println(event.getError());
		event.getException().printStackTrace();
	}
	
	@MusicEventSubscriber
	public void on(EventSearchTrack event) {
		System.out.println("Queued Track: " + getTrackInfo(event.getTrack().getInfo()) + " with state " + event.getState());
	}
	
	@MusicEventSubscriber
	public void on(EventSearchPlayList event) {
		System.out.println("Queued Playlist: " + event.getPlayList().getName() + " with state " + event.getState());
	}
	
	protected String getTrackInfo(AudioTrackInfo info) {
		if (info.author.equals("Unknown artist") && info.title.equals("Unknown title")) {
			return info.uri;
		}
		return info.author + " - " + info.title;
	}
	
}
