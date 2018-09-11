package jeremiahlowe.fightinggame.client.launcher;

public enum ResolutionPreset {
	Custom(-1, -1), 
	$1920x1080(1920, 1080), 
	$1024x768(1024, 768), 
	$1366x768(1366, 768), 
	$640x800(640, 800), 
	$500x300(500, 300), 
	$300x300(300, 300);
	
	public final int w, h;
	
	private ResolutionPreset(int w, int h) {
		this.w = w;
		this.h = h;
	}
	
	@Override
	public String toString() {
		String out = super.toString();
		if(out.startsWith("$"))
			out = out.substring(1);
		return out;
	}
}
