package jeremiahlowe.fightinggame.util;

import processing.core.PApplet;
import processing.core.PVector;

public class PGFX {
	public static final void polygon(PApplet a, PVector... ve) {
		if(ve.length < 3)
			throw new RuntimeException("Polygon must have >= 3 verticies!");
		a.noStroke();
		for(int i = 0; i < ve.length - 2; i++) {
			PVector v1 = ve[i], v2 = ve[i + 1], v3 = ve[i + 2];
			a.triangle(v1.x, v1.y, v2.x, v2.y, v3.x, v3.y);
		}
	}
	public static final void outlinePolygon(PApplet a, PVector... ve) {
		if(ve.length < 3)
			throw new RuntimeException("Polygon must have >= 3 verticies!");
		for(int i = 0; i < ve.length - 1; i++) {
			PVector v1 = ve[i], v2 = ve[i + 1];
			a.line(v1.x, v1.y, v2.x, v2.y);
		}
	}
}
