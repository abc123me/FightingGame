package jeremiahlowe.fightinggame.ai;

import jeremiahlowe.fightinggame.Fighter;

public class AIAction {
	public EAIActionType type;
	public Fighter to;

	public AIAction() {
		type = EAIActionType.Nothing;
		to = null;
	}

	public AIAction(EAIActionType type, Fighter to) {
		this.type = type;
		this.to = to;
	}

	@Override
	public String toString() {
		return "Action(" + type + ", " + to + ")";
	}
}