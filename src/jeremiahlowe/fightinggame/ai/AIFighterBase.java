package jeremiahlowe.fightinggame.ai;

import java.util.Random;

import jeremiahlowe.fightinggame.ins.GraphicalInstance;
import jeremiahlowe.fightinggame.ins.Instance;
import jeremiahlowe.fightinggame.phys.Fighter;
import jeremiahlowe.fightinggame.phys.PhysicsObject;
import jeremiahlowe.fightinggame.phys.Player;
import jeremiahlowe.fightinggame.ui.IStatistic.IDrawableStatistic;
import net.net16.jeremiahlowe.shared.math.GeneralMath;
import net.net16.jeremiahlowe.shared.math.Vector;
import processing.core.PApplet;

public abstract class AIFighterBase extends Player implements IDrawableStatistic{
	public float fov = 0;
	public float lerpSpeed = (float) (Math.PI * 1.5f);
	
	protected AIAction lastAction, action;
	protected float gotoLookAngle = 0;
	protected float curLookAngle = 0;
	protected float dodgeTime = 0.75f;
	protected float dodgeTimeRem = 0;
	protected float chaseDist2, chaseDist;
	protected Vector dodgeDir;
	
	protected Random rng;
	
	protected AIFighterBase(Instance instance) {
		super(instance);
		rng = new Random(System.nanoTime());
		action = new AIAction();
		action.to = null;
		fov = (float) (Math.PI / 4);
		setChaseDistance(2);
		if(instance instanceof GraphicalInstance) 
			((GraphicalInstance) instance).addStatistic(this);
	}

	public void lookAround() {
		setAction(new AIAction(EAIActionType.LookFor, null));
	}
	public void attack(Fighter f) {
		if (f == null)
			return;
		if (Vector.sub(pos, f.pos).magSq() > chaseDist2)
			setAction(new AIAction(EAIActionType.Chase, f));
		else
			setAction(new AIAction(EAIActionType.ShootAt, f));
	}
	public void startDodge(Fighter f) {
		dodgeTimeRem = dodgeTime;
		boolean x = rng.nextFloat() > 0.5;
		dodgeDir = new Vector(x ? 1 : -1, -1);
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
			keys = new Vector();
			float a = angleTo(action.to.pos);
			setLookRotation(a);
			if (pos.distSq(action.to.pos) > chaseDist2) {
				setAction(new AIAction(EAIActionType.Chase, action.to));
				return;
			}
		}
		if (action.type == EAIActionType.Chase) {
			speedBoost = 1.5f;
			if (pos.distSq(action.to.pos) < (chaseDist2 - chaseDist2 / 4)) {
				setAction(new AIAction(EAIActionType.ShootAt, action.to));
				return;
			}
			keys = new Vector(0, 1);
			setLookRotation(Vector.sub(action.to.pos, pos).atan2());
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
	protected boolean inFOV(Vector pos) {
		return inFOV(angleTo(pos));
	}
	protected float fovTop() {
		float a = getLookVector().atan2();
		return a + fov / 2;
	}
	protected float fovBot() {
		float a = getLookVector().atan2();
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
	public void setLookPosition(Vector pos) {
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
	public float angleTo(Vector pos) {
		return pos.copy().sub(this.pos).atan2();
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
		gotoLookAngle = GeneralMath.normalizeAngle(gotoLookAngle);
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
		super.setLookPosition(Vector.fromAngle(curLookAngle).add(pos));
	}
	
	@Override
	public void physics(Instance i, double dt) {
		aiUpdate((float) dt);
		lerpLookPosition(dt);
		super.physics(i, dt);
	}
	@Override
	public void draw(PApplet a, GraphicalInstance i) {
		super.draw(a, i);
	}
	
	public void drawStatistic(PApplet a, GraphicalInstance i) {
		Vector tx = Vector.fromAngle(fovTop()).mult(100).add(pos);
		Vector bx = Vector.fromAngle(fovBot()).mult(100).add(pos);
		tx = i.world.transform(tx, i.screen);
		bx = i.world.transform(bx, i.screen);
		a.stroke(a.color(255, 200, 0));
		Vector p = i.world.transform(pos, i.screen);
		a.line(p.x, p.y, tx.x, tx.y);
		a.line(p.x, p.y, bx.x, bx.y);
	}
	public int getLevel() {
		return 3;
	}
}
