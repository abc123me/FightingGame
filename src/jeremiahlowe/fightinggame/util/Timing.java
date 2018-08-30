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
	public double secs() {
		return millis() / 1000.0;
	}

	public static final boolean sleep(long i) {
		try { 
			Thread.sleep(i); 
			return true;
		}
		catch(InterruptedException e) {
			return false;
		}
	}
}
