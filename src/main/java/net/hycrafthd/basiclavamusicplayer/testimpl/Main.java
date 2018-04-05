package net.hycrafthd.basiclavamusicplayer.testimpl;

import java.util.Scanner;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import net.hycrafthd.basiclavamusicplayer.MusicPlayer;

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
					musicplayer.setPause();
					System.out.println("Pause");
				} else if (line.startsWith("unpause")) {
					musicplayer.setUnpause();
					System.out.println("Unpause");
				} else if (line.startsWith("play ")) {
					musicplayer.play(line.substring(5), (callback) -> System.out.println(callback));
				} else if (line.startsWith("queue ")) {
					musicplayer.queue(line.substring(6), (callback) -> System.out.println(callback));
				} else if (line.startsWith("repeat")) {
					System.out.println("Repeat: " + musicplayer.toggleRepeat());
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
					musicplayer.skip();
				}
			}
		}).start();
		
	}
	
	protected String getTrackInfo(AudioTrackInfo info) {
		if (info.author.equals("Unknown artist") && info.title.equals("Unknown title")) {
			return info.uri;
		}
		return info.author + " - " + info.title;
	}
	
}
