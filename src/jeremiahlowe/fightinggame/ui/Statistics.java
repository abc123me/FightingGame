package jeremiahlowe.fightinggame.ui;

import java.util.Comparator;

import jeremiahlowe.fightinggame.ins.GraphicalInstance;
import jeremiahlowe.fightinggame.ui.IStatistic.*;
import net.net16.jeremiahlowe.shared.QueuedArrayList;
import processing.core.PApplet;

public class Statistics implements IDrawable {
	public int level = 0;
	
	private QueuedArrayList<IStatistic> stats;
	
	private static final Comparator<IStatistic> statCmp;
	static {
		statCmp = new Comparator<IStatistic>() {
			public int compare(IStatistic a, IStatistic b) {
				int al = a.getLevel(), bl = b.getLevel();
				if(al > bl) return 1;
				if(al < bl) return -1;
				return 0;
			}
		};
	}
	
	public Statistics() {
		stats = new QueuedArrayList<IStatistic>(statCmp);
	}

	public void incrStatLevel() {
		if (level < 100)
			level++;
	}
	public void decrStatLevel() {
		if (level > 0)
			level--;
	}
	
	public void draw(PApplet p, GraphicalInstance gi) {
		p.textAlign(PApplet.LEFT, PApplet.BOTTOM);
		p.stroke(0); p.fill(0);
		stats.update();
		float x = 0, y = 0, h = p.textAscent();
		for(int i = stats.size() - 1; i >= 0; i--) {
			IStatistic stat = stats.get(i);
			if(stat == null)
				continue;
			int l = stat.getLevel();
			if(l > level)
				continue;
			if(stat instanceof ITextStatistic) {
				ITextStatistic tstat = (ITextStatistic) stat;
				String t = tstat.getHeader();
				if(t != null) {
					p.text(t, x, y += h);
					x += 10;
				}
				for(String s : tstat.getStatisticText())
					if(s != null)
						p.text(s, x, y += h);
				if(t != null) 
					x -= 10;
			}
			else if(stat instanceof IDrawableStatistic) 
				((IDrawableStatistic) stat).drawStatistic(p, gi);
		}
	}
	public int getDrawPriority() {
		return GraphicalInstance.STATISTICS_DRAW_PRIORITY;
	}
	public boolean enabled() {
		return level > 0;
	}

	public boolean addStatistic(IStatistic stat) {
		return stats.add(stat);
	}
	public boolean removeStatistic(IStatistic stat) {
		return stats.remove(stat);
	}
}
