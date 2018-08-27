package jeremiahlowe.fightinggame.ui;

import jeremiahlowe.fightinggame.Bullet;
import jeremiahlowe.fightinggame.ins.Instance;
import jeremiahlowe.fightinggame.util.SafeArrayList;
import processing.core.PApplet;

public class Statistics implements IDrawable {
	public int level = 0;
	public Bullet lastBullet;

	public void incrStatLevel() {
		if (level < 3)
			level++;
	}
	public void decrStatLevel() {
		if (level > 0)
			level--;
	}
	private void drawNoVP(PApplet p, Instance i) {
		if (level < 0)
			return;
		p.stroke(0);
		p.fill(0);
		float y = 0, h = p.textAscent();
		p.text(String.format("FPS: %.3f (%d)", p.frameRate, level), 0, y += h);
		if (level < 1)
			return;
		y = textQueue(p, 0, y, h, "Drawables: %d %s %s", i.getPhysicsObjects());
		y = textQueue(p, 0, y, h, "Physx Objs: %d %s %s", i.getDrawables());
		if (level < 2)
			return;
		if (lastBullet != null && lastBullet.enabled()) {
			p.text("Last shot: " + lastBullet.getParent(), 0, y += h);
			p.text("Time left: " + lastBullet.timeLeft(), 0, y += h);
		}
	}
	private float textQueue(PApplet p, float x, float y, float h, String t, SafeArrayList<?> q) {
		int ta = q.removeQueueSize(), tr = q.addQueueSize();
		String pa = ta > 0 ? "-" + ta : "", pr = tr > 0 ? "+" + tr : "";
		p.text(String.format(t, q.size(), pa, pr), x, y += h);
		return y;
	}
	
	@Override
	public void draw(PApplet p, Instance i) {
		drawNoVP(p, i);
	}
	@Override
	public int getDrawPriority() {
		return Instance.STATISTICS_DRAW_PRIORITY;
	}
	@Override
	public boolean enabled() {
		return level > 0;
	}
}
