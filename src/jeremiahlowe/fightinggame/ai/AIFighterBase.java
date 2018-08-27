package jeremiahlowe.fightinggame.ai;

import jeremiahlowe.fightinggame.Fighter;
import jeremiahlowe.fightinggame.PhysicsObject;
import jeremiahlowe.fightinggame.Player;
import jeremiahlowe.fightinggame.ins.Instance;
import jeremiahlowe.fightinggame.util.Math;
import processing.core.PApplet;
import processing.core.PVector;

public abstract class AIFighterBase extends Player{
	public float fov = 0;
	public float lerpSpeed = Math.PI * 1.5f;
	
	protected AIAction lastAction, action;
	protected float gotoLookAngle = 0;
	protected float curLookAngle = 0;
	protected float dodgeTime = 0.75f;
	protected float dodgeTimeRem = 0;
	protected float chaseDist2, chaseDist;
	protected PVector dodgeDir;
	
	protected AIFighterBase(Instance instance) {
		super(instance);
		action = new AIAction();
		action.to = null;
		fov = Math.PI / 4;
	}

	public void lookAround() {
		setAction(new AIAction(EAIActionType.LookFor, null));
	}
	public void attack(Fighter f) {
		if (f == null)
			return;
		if (PVector.sub(pos, f.pos).magSq() > chaseDist2)
			setAction(new AIAction(EAIActionType.Chase, f));
		else
			setAction(new AIAction(EAIActionType.ShootAt, f));
	}
	public void startDodge(Fighter f) {
		dodgeTimeRem = dodgeTime;
		boolean x = Math.random(1) > 0.5;
		dodgeDir = new PVector(x ? 1 : -1, -1);
		action = new AIAction(EAIActionType.Dodge, f);
	}
	
	protected void aiUpdate(float dt) {
		if (action.type == null) {
			shooting = false;
			setAction(new AIAction(EAIActionType.Nothing, null));
			return;
		}
		if (action.type == EAIActionType.Nothing)
			return;
		if (action.type == EAIActionType.LookFor) {
			speedBoost = 1.0f;
			shooting = false;
			turn(lerpSpeed);
			for (PhysicsObject p : instance.getPhysicsObjects())
				if (p instanceof Fighter)
					if (inFOV(angleTo(p.pos)))
						attack((Fighter) p);
		}
		boolean hasTarget = action.to != null && action.to.alive();
		if (hasTarget)
			attackUpdate(dt);
		else {
			speedBoost = 1.0f;
			shooting = false;
		}
	}
	protected void attackUpdate(float dt) {
		if (action.type == EAIActionType.ShootAt) {
			speedBoost = 1.0f;
			shooting = true;
			keys = new PVector();
			float a = angleTo(action.to.pos);
			setLookRotation(a);
			if (PVector.sub(pos, action.to.pos).magSq() > chaseDist2) {
				setAction(new AIAction(EAIActionType.Chase, action.to));
				return;
			}
		}
		if (action.type == EAIActionType.Chase) {
			speedBoost = 1.5f;
			PVector dir = PVector.sub(action.to.pos, pos);
			if (dir.magSq() < (chaseDist2 - chaseDist2 / 4)) {
				setAction(new AIAction(EAIActionType.ShootAt, action.to));
				return;
			}
			keys = new PVector(1, 0);
			setLookRotation(dir.heading());
		}
		if (action.type == EAIActionType.Dodge) {
			shooting = false;
			keys = dodgeDir;
			dodgeTimeRem -= dt;
			if (dodgeTimeRem < 0)
				attack(action.to);
		}
	}

	protected boolean canAttack() {
		return action.type != EAIActionType.Dodge;
	}
	protected boolean inFOV(float a) {
		float top = fovTop(), bot = fovBot();
		float max = Math.max(top, bot), min = Math.min(top, bot);
		if (a >= min && a <= max)
			return true;
		return false;
	}
	protected boolean inFOV(PVector pos) {
		return inFOV(angleTo(pos));
	}
	protected float fovTop() {
		float a = getLookVector().heading();
		return a + fov / 2;
	}
	protected float fovBot() {
		float a = getLookVector().heading();
		return a - fov / 2;
	}
	protected void setAction(AIAction action) {
		lastAction = this.action;
		this.action = action;
	}
	protected void revertAction() {
		this.action = lastAction;
	}
	
	public void turn(float by) {
		gotoLookAngle += by;
	}
	public void setLookPosition(PVector pos) {
		setLookRotation(angleTo(pos));
	}
	public void setLookRotation(float angle) {
		gotoLookAngle = angle;
	}
	public float getLookRotation() {
		return curLookAngle;
	}
	public float getRequestedLookRotation() {
		return gotoLookAngle;
	}
	public float angleTo(PVector pos) {
		return pos.copy().sub(this.pos).heading();
	}
	public float getChaseDistance() {
		return chaseDist;
	}
	public void setChaseDistance(float chaseDist) {
		this.chaseDist = chaseDist;
		this.chaseDist2 = chaseDist * chaseDist;
	}
	
	private void lerpLookPosition(double dt) {
		float lerpAmount = (float) dt * lerpSpeed;
		gotoLookAngle = Math.normalizeAngle(gotoLookAngle);
		float diff = gotoLookAngle - curLookAngle;
		if (diff == 0)
			return;
		int m = 1;
		if (diff < 0) {
			m *= -1;
			diff *= -1;
		}
		if (diff > lerpAmount)
			diff = lerpAmount;
		curLookAngle += m * diff;
		super.setLookPosition(PVector.fromAngle(curLookAngle).add(pos));
	}
	
	@Override
	public void physics(Instance i, double dt) {
		aiUpdate((float) dt);
		lerpLookPosition(dt);
		super.physics(i, dt);
	}
	@Override
	public void draw(PApplet a, Instance i) {
		if (i.statistics.level > 2) {
			PVector tx = PVector.fromAngle(fovTop()).mult(100).add(pos);
			PVector bx = PVector.fromAngle(fovBot()).mult(100).add(pos);
			tx = i.world.transform(tx, i.screen);
			bx = i.world.transform(bx, i.screen);
			a.stroke(a.color(255, 200, 0));
			PVector p = i.world.transform(pos, i.screen);
			a.line(p.x, p.y, tx.x, tx.y);
			a.line(p.x, p.y, bx.x, bx.y);
		}
		super.draw(a, i);
	}
}
