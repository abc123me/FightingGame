package jeremiahlowe.fightinggame.ins;

import jeremiahlowe.fightinggame.ui.IDrawable;
import jeremiahlowe.fightinggame.ui.IStatistic;
import jeremiahlowe.fightinggame.ui.Statistics;
import jeremiahlowe.fightinggame.ui.IStatistic.ITextStatistic;
import net.net16.jeremiahlowe.shared.QueuedArrayList;
import net.net16.jeremiahlowe.shared.math.Viewport;
import processing.core.PApplet;

public abstract class GraphicalInstance extends Instance{
	public static final int STATISTICS_DRAW_PRIORITY = 9000;
	public static final int BULLET_DRAW_PRIORITY = 3;
	public static final int FIGHTER_DRAW_PRIORITY = 2;
	
	protected QueuedArrayList<IDrawable> drawables;
	
	public Viewport world, screen;
	public PApplet applet;
	public Statistics statistics;
	
	public GraphicalInstance(PApplet applet) {
		super();
		drawables = new QueuedArrayList<IDrawable>(IDrawable.PRIORITY_SORT);
		this.applet = applet;
		statistics = new Statistics();
		drawables.add(statistics);
		statistics.addStatistic(getFramerateStatistic());
		statistics.addStatistic(getInstanceStatistic());
	}
	
	public void removeDrawable(IDrawable d) {
		drawables.remove(d);
	}
	public void addDrawable(IDrawable d) {
		drawables.add(d);
	}
	public QueuedArrayList<IDrawable> getDrawables(){
		return drawables;
	}
	public void drawAll(PApplet p) {
		drawables.update();
		for (IDrawable d : drawables)
			if(d != null && d.enabled())
				d.draw(p, this);
	}
	public ITextStatistic getFramerateStatistic() {
		return new ITextStatistic() {
			public int getLevel() { return 1; }
			public String getHeader() { return null; }
			public String[] getStatisticText() { return new String[] {String.format("FPS: %.3f", applet.frameRate)}; }
		};
	}
	public ITextStatistic getInstanceStatistic() {
		return new ITextStatistic() { 
			public int getLevel() { 
				return 2; 
			}
			public String getHeader() {
				return "Instance data:";
			}
			public String[] getStatisticText() {
				String drws = String.format("Drawables: %d +%d -%d", drawables.size(), drawables.addQueueSize(), drawables.removeQueueSize());
				String phys = String.format("PhysicsObjects: %d +%d -%d", physicsObjects.size(), physicsObjects.addQueueSize(), physicsObjects.removeQueueSize());
				String slvl = null;
				if(statistics != null) 
					slvl = String.format("Stat level: %d", statistics.level);
				return new String[] {drws, phys, slvl};
			}
		};
	}
	public boolean addStatistic(IStatistic stat) {
		if(statistics != null)
			return statistics.addStatistic(stat);
		return false;
	}
	public boolean removeStatistic(IStatistic stat) {
		if(statistics != null)
			return statistics.removeStatistic(stat);
		return false;
	}
}
