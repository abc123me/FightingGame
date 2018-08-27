package jeremiahlowe.fightinggame.ui;

import java.util.Comparator;

import jeremiahlowe.fightinggame.Instance;
import processing.core.PApplet;

public interface IDrawable{
	public static final Comparator<IDrawable> PRIORITY_SORT = new Comparator<IDrawable>() {
		public int compare(IDrawable a, IDrawable b) {
			int ap = a.getDrawPriority();
			int bp = b.getDrawPriority();
			if (ap < bp)
				return -1;
			if (ap > bp)
				return 1;
			return 0;
		}
	};
	
	public void draw(PApplet p, Instance i);
	public int getDrawPriority();
	public boolean enabled();
}