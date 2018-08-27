package jeremiahlowe.fightinggame.ui;

import jeremiahlowe.fightinggame.ins.GraphicalInstance;
import processing.core.PApplet;

public interface IStatistic {
	public int getLevel();
	public interface IDrawableStatistic extends IStatistic{
		public void drawStatistic(PApplet a, GraphicalInstance i);
	}
	public interface ITextStatistic extends IStatistic{
		public String[] getStatisticText();
		public String getHeader();
	}
}
