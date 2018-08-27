package jeremiahlowe.fightinggame.util;

import java.util.Random;

import processing.core.PVector;

public class Math {
	public static float PI = (float) java.lang.Math.PI;
	public static float TWO_PI = 2 * (float) java.lang.Math.PI;
	
	private static final Random rng = new Random();
	
	//Clamps the value between a minium and maximum value
	public static final float clamp(float v, float min, float max) {
		if (v < min)
			return min;
		if (v > max)
			return max;
		return v;
	}
	//Returns the 1, 0, -1 identity of a vector
	public static final PVector identity(PVector of) {
		PVector out = new PVector(0, 0, 0);
		if (of.x != 0)
			out.x = of.x > 0 ? 1 : -1;
		if (of.y != 0)
			out.y = of.y > 0 ? 1 : -1;
		if (of.z != 0)
			out.z = of.z > 0 ? 1 : -1;
		return out;
	}
	//Negates a vector
	public static final PVector negate(PVector of) {
		PVector out = new PVector();
		out.x = of.x * -1;
		out.y = of.y * -1;
		out.z = of.z * -1;
		return out;
	}
	//Gets the square root of a float
	public static final float sqrt(float of) {
		return (float) java.lang.Math.sqrt(of);
	}
	//Gets the maximum of two numbers
	public static final float max(float a, float b) {
		return a > b ? a : b;
	}
	//Gets the maximum of two numbers
	public static final float min(float a, float b) {
		return a < b ? a : b;
	}
	//Gets the maximum positions of 2 vectors
	public static final PVector max(PVector a, PVector b) {
		PVector o = new PVector();
		o.x = max(a.x, b.x);
		o.y = max(a.y, b.y);
		o.z = max(a.z, b.z);
		return o;
	}
	//Gets the minimum positions of 2 vectors
	public static final PVector min(PVector a, PVector b) {
		PVector o = new PVector();
		o.x = min(a.x, b.x);
		o.y = min(a.y, b.y);
		o.z = min(a.z, b.z);
		return o;
	}
	//Limits the positions of a pvector to a minimum and maximum vector
	public static final PVector clamp(PVector p, PVector min, PVector max) {
		PVector o = new PVector();
		o.x = clamp(p.x, min.x, max.x);
		o.y = clamp(p.y, min.y, max.y);
		o.z = clamp(p.z, min.z, max.z);
		return o;
	}
	//Checks if a point is inside of a rectange
	public static final boolean inside(PVector pos, PVector start, PVector end) {
		PVector max = new PVector(max(start.x, end.x), max(start.y, end.y));
		PVector min = new PVector(min(start.x, end.x), min(start.y, end.y));
		if (pos.x >= min.x && pos.x <= max.x)
			if (pos.y >= min.y && pos.y <= max.y)
				return true;
		return false;
	}
	//Wraps an integer around a maximum value
	public static final int wrap(int val, int max) {
		while (val >= max)
			val -= max;
		return val;
	}
	//Gets the slope between two vectors
	public static final float slope(PVector v1, PVector v2) {
		float x = v2.x - v1.x, y = v2.y - v1.y;
		if (x == 0) {
			if (y > 0)
				return Float.POSITIVE_INFINITY;
			else
				return Float.NEGATIVE_INFINITY;
		}
		return y / x;
	}
	//Checks if a point is inside a polygon (defined by its verticies
	//This will work on any convex polygon, however is untested on concave polygons
	public static final boolean insidePolygon(PVector test, PVector... ve) {
		if (ve.length < 3)
			throw new RuntimeException("Polygon must have 3 or more verticies!");
		float a = 0, ta = polygonArea(ve);
		for (int i = 0; i < ve.length; i++)
			a += polygonArea(ve[i], test, ve[wrap(i + 1, ve.length)]);
		return a == ta;
	}
	//Checks to see how far a point is from a polygon
	public static final float distFromPolygon(PVector test, PVector... ve) {
		return sqrt(dist2FromPolygon(test, ve));
	}
	//Checks to see how far a point is from a polygon, returns the dist^2
	public static final float dist2FromPolygon(PVector test, PVector... ve) {
		if (ve.length < 3)
			throw new RuntimeException("Polygon must have 3 or more verticies!");
		int cv1 = -1, cv2 = -1;
		float md = Float.MAX_VALUE, md2 = Float.MAX_VALUE;
		for (int i = 0; i < ve.length; i++) {
			float d2 = dist2(test, ve[i]);
			if (d2 < md) {
				md = d2;
				cv1 = i;
			}
		}
		for (int i = 0; i < ve.length; i++) {
			float d2 = dist2(test, ve[i]);
			if (cv1 == i)
				continue;
			if (d2 < md2) {
				md2 = d2;
				cv2 = i;
			}
		}
		if (cv1 < 0 || cv2 < 0)
			throw new RuntimeException(
					String.format("What the literal fuck happened? (cv1 < 0 || cv2 < 0) cv1=%d cv2=%d", cv1, cv2));
		PVector a = ve[cv1], b = ve[cv2];
		float mx = b.x - a.x, my = b.y - a.y;
		PVector e;
		if (mx == 0) {
			float m = mx / my;
			float xi = a.x - m * a.y;
			e = new PVector(test.y * m + xi, test.y);
		} else {
			float m = my / mx;
			float yi = a.y - m * a.x;
			e = new PVector(test.x, test.x * m + yi);
		}
		e = clamp(e, min(a, b), max(a, b));
		return dist2(test, e);
	}
	//Gets a polygons area using the shoelace formula (https://en.wikipedia.org/wiki/Shoelace_formula)
	//This will work on any convex polygon, however is untested on concave polygons
	public static final float polygonArea(PVector... ve) {
		if (ve.length < 3)
			throw new RuntimeException("Polygon must have 3 or more verticies!");
		float a = 0;
		for (int j = 1; j >= -1; j -= 2) {
			for (int i = 0; i < ve.length; i++) {
				int x = i, y = wrap(i + 1, ve.length);
				if (j < 0) {
					int t = x;
					x = y;
					y = t;
				}
				a += j * ve[x].x * ve[y].y;
			}
		}
		if (a < 0)
			a *= -0.5;
		else
			a *= 0.5;
		return a;
	}
	//Midpoint of two pvectors
	public static final PVector mid(PVector a, PVector b) {
		return PVector.add(a, b).div(2);
	}
	//Squared distance bettwen 2 vectors
	public static final float dist2(PVector a, PVector b) {
		float x = b.x - a.x, y = b.y - a.y;
		x *= x;
		y *= y;
		return x + y;
	}
	public static final float random() {
		return random(1);
	}
	public static final float random(float max) {
		return random(0, max);
	}
	public static final float random(float min, float max) {
		return rng.nextFloat() * (max - min) + min;
	}
	public static final void setRandomSeed(long seed) {
		rng.setSeed(seed);
	}
	public static final Random getRandom() {
		return rng;
	}
	public static float normalizeAngle(float angle) {
		while(angle < 0)
			angle += TWO_PI;
		while(angle >= TWO_PI)
			angle -= TWO_PI;
		return angle;
	}
}
