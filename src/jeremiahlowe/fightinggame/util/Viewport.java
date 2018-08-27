package jeremiahlowe.fightinggame.util;

import processing.core.PVector;

public class Viewport {
	public float x, y, w, h;

	public Viewport() {
		this(0, 0);
	}
	public Viewport(float w, float h) {
		this(w, h, 0, 0);
	}
	public Viewport(float w, float h, float x, float y) {
		this.w = w;
		this.h = h;
		setOffset(x, y);
	}

	public void translate(float x, float y) {
		this.x += x;
		this.y += y;
	}
	public void setOffset(float x, float y) {
		this.x = x;
		this.y = y;
	}
	public void setOffset(PVector off) {
		setOffset(off.x, off.y);
	}
	public float transformX(float x, Viewport to) {
		return ((x - this.x) / w) * to.w + to.x;
	}
	public float transformY(float y, Viewport to) {
		return ((y - this.y) / h) * to.h + to.y;
	}
	public PVector transform(PVector v, Viewport to) {
		float outX = transformX(v.x, to);
		float outY = transformY(v.y, to);
		return new PVector(outX, outY);
	}
	public PVector transformIgnoreOffset(PVector v, Viewport to) {
		float outX = (v.x / w) * to.w;
		float outY = (v.y / h) * to.h;
		return new PVector(outX, outY);
	}
	public PVector getSize() {
		return new PVector(w, h);
	}
	public PVector getOffset() {
		return new PVector(x, y);
	}
	public PVector center() {
		return new PVector(w / 2, h / 2);
	}
	public float aspRatio() {
		float a = w / h;
		if(a < 0) a *= -1;
		return a;
	}
	public void zoom(float a) {
		w += a;
		h += a;
	}
	public void zoom(float a, float aspRatio) {
		w += a * aspRatio;
		h += a;
	}
}
