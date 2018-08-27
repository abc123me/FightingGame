package jeremiahlowe.fightinggame.util;

public class Timing {
	private long start = 0;
	
	public Timing() {
		this.start = System.currentTimeMillis();
	}
	
	public void reset() {
		start();
	}
	public void start() {
		start = System.currentTimeMillis();
	}
	public long millis() {
		return System.currentTimeMillis() - start;
	}
}
