package jeremiahlowe.fightinggame.net.struct;

import com.google.gson.Gson;

import jeremiahlowe.fightinggame.net.EPacketIdentity;
import jeremiahlowe.fightinggame.net.Packet;
import jeremiahlowe.fightinggame.phys.Player;

public class AttackData {
	private final transient Gson gson = new Gson();
	
	private long attackerUUID, victimUUID;
	private boolean hasAttacker, victimKilled;
	private float attackerHp, victimHp;
	private float attackerMaxHp, victimMaxHp;
	
	private AttackData(Player attacker, Player victim) {
		if(victim == null)
			throw new NullPointerException("Null victim, victum must be non-null!");
		victimKilled = !victim.alive();
		victimUUID = victim.uuid;
		setAttacker(attacker);
		victimHp = victim.health;
		victimMaxHp = victim.maxHealth;
	}
	
	public boolean isAttackerKnown() {
		return hasAttacker;
	}
	public long getAttackerUUID() {
		return attackerUUID;
	}
	public long getVictimUUID() {
		return victimUUID;
	}
	
	public void copyTo(Player attacker, Player victim) {
		if(hasAttacker && attacker != null) {
			attacker.health = attackerHp;
			attacker.maxHealth = attackerMaxHp;
		}
		if(victim != null) {
			victim.alive = true;
			if(victimKilled) 
				victim.alive = false;
			victim.health = victimHp;
			victim.maxHealth = victimMaxHp;
		}
	}
	public Packet toPacket() {
		return Packet.createUpdate(EPacketIdentity.ATTACK_UPDATE, gson.toJson(this));
	}
	
	public static AttackData createDamage(Player from, Player to) {
		return new AttackData(from, to);
	}
	public static AttackData createMurder(Player attacker, Player victim) {
		AttackData a = new AttackData(attacker, victim);
		a.victimKilled = true;
		return a;
	}
	public void setAttacker(Player attacker) {
		hasAttacker = attacker != null;
		if(hasAttacker) 
			attackerUUID = attacker.uuid;
		if(hasAttacker) {
			attackerHp = attacker.health;
			attackerMaxHp = attacker.maxHealth;
		}
	}
	public String toString() {
		return toString("Unknown");
	}
	public String toString(String uname) {
		String aname = hasAttacker ? String.valueOf(attackerUUID) : uname;
		return toString(aname, String.valueOf(victimUUID));
	}
	public String toString(String aname, String vname) {
		if(victimKilled)
			return aname + " killed " + vname;
		else
			return aname + " attacked " + vname;
	}

	public float getAttackerHealth() {
		return attackerHp;
	}
	public float getVictimHealth() {
		return victimHp;
	}
}
