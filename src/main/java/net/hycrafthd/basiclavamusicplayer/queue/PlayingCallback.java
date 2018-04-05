package net.hycrafthd.basiclavamusicplayer.queue;

public class PlayingCallback {
	
	private boolean isQueued;
	private String error = "";
	
	public PlayingCallback() {
		this.isQueued = true;
	}
	
	public PlayingCallback(boolean isQueued, String error) {
		this.isQueued = isQueued;
		this.error = error;
	}
	
	public boolean isQueued() {
		return isQueued;
	}
	
	public String getError() {
		return error;
	}
	
	@Override
	public String toString() {
		return "IsQueued = " + isQueued + ", Error = " + error;
	}
	
}
