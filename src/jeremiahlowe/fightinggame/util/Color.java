package jeremiahlowe.fightinggame.util;

public class Color {
	public static final Color BLACK = new Color(0, 0, 0);
	public static final Color RED = new Color(255, 0, 0);
	public static final Color GREEN = new Color(0, 255, 0);
	public static final Color BLUE = new Color(0, 0, 255);
	public static final Color YELLOW = new Color(255, 255, 0);
	public static final Color AQUA = new Color(0, 255, 255);
	public static final Color MAGENTA = new Color(255, 0, 255);
	public static final Color WHITE = new Color(255, 255, 255);
	
	public static final int ALPHA_OPAQUE = 0xFF;
	public static final int ALPHA_TRANSPARENT = 0x00;
	
	private byte r, g, b, a;
	private int argb;
	
	public Color() {
		this(0);
	}
	public Color(int grey) {
		this(grey, grey, grey);
	}
	public Color(Color c) {
		this.r = c.r; 
		this.g= c.g;
		this.b = c.b; 
		this.a = c.a;
	}
	public Color(Color c, int a) {
		this.r = c.r; 
		this.g= c.g;
		this.b = c.b;
		this.a = verifyColor(a);
	}
	public Color(int r, int g, int b) {
		this(r, g, b, ALPHA_OPAQUE);
	}
	public Color(int r, int g, int b, int a) {
		this.r = verifyColor(r);
		this.g = verifyColor(g);
		this.b = verifyColor(b);
		this.a = verifyColor(a);
		generateARGB();
	}
	
	private byte verifyColor(int c) {
		if(c < 0)
			throw new RuntimeException("Color value (" + c + ") must be between 0 and 255!");
		if(c > 255)
			throw new RuntimeException("Color value (" + c + ") must be between 0 and 255!");
		return (byte)c;
	}
	private void generateARGB() {
		argb = a << 24;
		argb += r << 16;
		argb += g << 8;
		argb += b;
	}
	
	public int argb() {
		return argb;
	}
	
	public void r(int v) {
		r = verifyColor(v);
		generateARGB();
	}
	public void g(int v) {
		g = verifyColor(v);
		generateARGB();
	}
	public void b(int v) {
		b = verifyColor(v);
		generateARGB();
	}
	public void a(int v) {
		a = verifyColor(v);
		generateARGB();
	}
	public int r() {
		return Byte.toUnsignedInt(r);
	}
	public int g() {
		return Byte.toUnsignedInt(g);
	}
	public int b() {
		return Byte.toUnsignedInt(b);
	}
	public int a() {
		return Byte.toUnsignedInt(a);
	}
	public byte rb() {
		return r;
	}
	public byte gb() {
		return g;
	}
	public byte bb() {
		return b;
	}
	public byte ab() {
		return a;
	}
}
