package jline.console.history;

public class FixedTimeSource implements TimeSource {
	
	private final long timestamp;

	public FixedTimeSource(long timestamp) {
		this.timestamp = timestamp;
	}
	
	@Override
	public long currentTimeMillis() {
		return timestamp;
	}
}
