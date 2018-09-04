package jeremiahlowe.fightinggame.util;

import net.net16.jeremiahlowe.shared.math.Vector;
import processing.core.PApplet;

public class PGFX {
	public static final void polygon(PApplet a, Vector... ve) {
		if(ve.length < 3)
			throw new RuntimeException("Polygon must have >= 3 verticies!");
		a.noStroke();
		for(int i = 0; i < ve.length - 2; i++) {
			Vector v1 = ve[i], v2 = ve[i + 1], v3 = ve[i + 2];
			a.triangle(v1.x, v1.y, v2.x, v2.y, v3.x, v3.y);
		}
	}
	public static final void outlinePolygon(PApplet a, Vector... ve) {
		if(ve.length < 3)
			throw new RuntimeException("Polygon must have >= 3 verticies!");
		for(int i = 0; i < ve.length - 1; i++) {
			Vector v1 = ve[i], v2 = ve[i + 1];
			a.line(v1.x, v1.y, v2.x, v2.y);
		}
	}
}
