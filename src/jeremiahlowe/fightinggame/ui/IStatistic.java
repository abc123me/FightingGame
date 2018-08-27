package jeremiahlowe.fightinggame.ui;

import jeremiahlowe.fightinggame.Instance;
import processing.core.PApplet;

public interface IStatistic {
	public boolean shouldDisplay();
	public String getStatisticText();
	public void drawStatistic(PApplet a, Instance i, int statLevel);
}
