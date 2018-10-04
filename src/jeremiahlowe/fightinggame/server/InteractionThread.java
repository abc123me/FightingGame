package jeremiahlowe.fightinggame.server;

import java.util.Scanner;

import jeremiahlowe.fightinggame.Meta;
import jeremiahlowe.fightinggame.phys.*;
import net.net16.jeremiahlowe.shared.*;
import net.net16.jeremiahlowe.shared.math.*;

public class InteractionThread extends Thread{
	public final ServerInstance instance;
	public final FightingGameServerCLI fgs;
	
	public InteractionThread(ServerInstance instance, FightingGameServerCLI fgs) {
		this.instance = instance;
		this.fgs = fgs;
	}
	
	@Override
	public void run() {
		Scanner in = new Scanner(System.in);
		while(!Thread.interrupted()) {
			System.out.print("> ");
			while(!in.hasNextLine())
				Timing.sleep(100);
			String input = in.nextLine();
			handleUserInput(input);
		}
		in.close();
	}
	private void printHelp() {
		System.out.println("Commands:");
		System.out.println("help: Shows this help screen");
		System.out.println("exit: Stops the server");
		System.out.println("list: Lists all players");
		System.out.println("kick <uuid> [reason]: Kicks a player");
		System.out.println("close <uuid>: Closes a player's socket");
		System.out.println("debug <level>: Sets the debug level (0-3)");
		System.out.println("tps: Gets the TPS the server is running at");
		System.out.println("lag: Lags the server");
		System.out.println("lsphys: Lists the physicsobjects the server is handling");
		System.out.println("kickall [reason]: Kicks all players");
		System.out.println("ver: Prints out the server's version");
		System.out.println("say <msg>: Sends a chat message to all clients");
		System.out.println("tp <uuid> <x> <y>: Teleports a player to x, y");
		System.out.println("tp <uuid> <to_uuid>: Teleports a player to another player");
		System.out.println("hp <uuid>: Shows a players health");
		System.out.println("heal <uuid> [by]: Heals a player, negative health deals damage");
		System.out.println("kill <uuid>: Kills a player");
	}
	
	private void tp(String[] parts) {
		if(parts.length != 3 && parts.length != 4)
			System.out.println("Usage: tp <player> <to player, x> <y>");
		else {
			Player p = null;
			Vector pos = new Vector();
			try{ p = getPlayer(parts[1]); }catch(InvalidArgumentException e) { System.out.println(e); return; }
			if(parts.length == 2) {
				try{
					pos = getPlayer(parts[2]).pos.copy();
				}catch(InvalidArgumentException e) { System.out.println(e); return; }
			} else {
				try{ pos.x = Float.parseFloat(parts[2]); } catch(Exception e) { System.out.println("Invalid X coordinate!"); return; }
				try{ pos.y = Float.parseFloat(parts[3]); } catch(Exception e) { System.out.println("Invalid Y coordinate!"); return; }
			}
			instance.teleportPlayerTo(p, pos);
			System.out.println("Teleported player " + p.uuid + " to " + pos);
		}
	}
	private void kill(String[] parts) {
		if(parts.length != 2)
			System.out.println("Usage: kill <uuid>");
		else {
			try {
				getPlayer(parts[1]).kill(instance, "server");
			} catch(InvalidArgumentException ie) {
				System.out.println(ie);
			}
		}
	}
	private void hp(String[] parts) {
		if(parts.length != 2)
			System.out.println("Usage: hp <uuid>");
		else {
			try {
				Player p = getPlayer(parts[1]);
				System.out.printf("%f / %f%s", p.health, p.maxHealth, System.lineSeparator());
			} catch(InvalidArgumentException ie) {
				System.out.println(ie);
			}
		}
	}
	private void heal(String[] parts) {
		if(parts.length != 2 && parts.length != 3)
			System.out.println("Usage: heal <uuid> [by]");
		else {
			try {
				Player p = getPlayer(parts[1]);
				if(parts.length == 2)
					p.heal(instance, "server");
				if(parts.length == 3)
					p.heal(instance, Float.parseFloat(parts[2]));
			} catch(InvalidArgumentException ie) {
				System.out.println(ie);
			}
		}
	}
	private void say(String[] parts) {
		if(parts.length < 2) 
			System.out.println("Usage: say <message>");
		else {
			String msg = "";
			for(int i = 1; i < parts.length; i++)
				msg += parts[i];
			instance.scm.serverSay(msg);
		}
	}
	private void exit() {
		System.out.println("Exiting now!");
		System.exit(0);
	}
	private void listPlayers() {
		System.out.println("Player list (" + instance.getPlayerList().length + "):");
		for(Player p : instance.getPlayerList()) {
			if(p != null) System.out.println("\tPlayer: " + p.uuid + " with name " + p.name);
			else System.out.println("\tNull player!");
		}
	}
	private void debug(String[] parts) {
		if(parts.length != 2) 
			System.out.println("Debug needs a debug level parameter!");
		else{
			try {
				Logger.level = Integer.parseInt(parts[1]);
				System.out.println("Set debug level to " + parts[1]);
			} catch(Exception e) { System.out.println("Invalid debug level \"" + parts[1] + "\""); }
		}
	}
	private void kickAll(String[] parts) {
		String reason = "You were kicked, GG";
		if(parts.length >= 2) {
			reason = "";
			for(int i = 1; i < parts.length; i++)
				reason += " " + parts[i];
			reason = reason.substring(1);
		}
		for(Player p : instance.getPlayerList())
			instance.kickPlayerWithUUID(p.uuid, reason);
	}
	private void kick(String[] parts, boolean close) {
		long uuid = 0;
		if(parts.length < 2) {
			System.out.println("Usage: kick <uuid>!");
			return;
		}
		try {
			uuid = getUUID(parts[1]);
			String reason = "You were kicked, GG";
			if(parts.length > 2) {
				reason = "";
				for(int i = 2; i < parts.length; i++)
					reason += " " + parts[i];
				reason = reason.substring(1);
			}
			if(close)
				instance.getWrapperWithUUID(uuid).close();
			else
				instance.kickPlayerWithUUID(uuid, reason);
		} catch(Exception e) { 
			System.out.println("Error during kicking player with UUID \"" + parts[1] + "\": " + e); 
		}
	}
	private void toggleLag() {
		fgs.toggleLagg();
		System.out.println("Server lag " + (fgs.lagg() ? "en" : "dis") + "abled!");
	}
	private void version() {
		System.out.println("Version: " + Meta.VERSION);
		System.out.println("Version ID: " + Meta.VERSION_ID);
	}
	private void listPhysics() {
		System.out.println("Physics objects:");
		for(PhysicsObject po : instance.getPhysicsObjects()) {
			if(po == null) continue;
			System.out.println("\tPhysicsObject: " + po);
			System.out.println("\t\tPosition: " + po.pos);
			System.out.println("\t\tVelocity: " + po.vel);
			System.out.println("\t\tEnabled: " + po.enabled());
		}
	}
	public void handleUserInput(String input) {
		input = input.trim();
		String[] parts = input.split(" ");
		if(input.equals("exit")) 
			exit();
		else if(input.equals("list")) 
			listPlayers();
		else if(input.equals("help")) 
			printHelp();
		else if(input.startsWith("debug")) 
			debug(parts);
		else if(input.equals("kickall")) 
			kickAll(parts);
		else if(input.startsWith("kick")) 
			kick(parts, false);
		else if(input.startsWith("close"))
			kick(parts, true);
		else if(input.equals("tps")) 
			System.out.println("TPS: " + fgs.tps());
		else if(input.equals("lag")) 
			toggleLag();
		else if(input.equals("lsphys")) 
			listPhysics();
		else if(input.equals("ver")) 
			version();
		else if(input.startsWith("say"))
			say(parts);
		else if(input.startsWith("tp"))
			tp(parts);
		else if(input.startsWith("hp"))
			hp(parts);
		else if(input.startsWith("heal"))
			heal(parts);
		else if(input.startsWith("kill"))
			kill(parts);
		else System.out.println("Unknown command, type \"help\" for help!");
	}
	private long getUUID(String from) throws InvalidArgumentException{
		try{
			return Long.parseLong(from);
		}catch(NumberFormatException e) {
			Player pl = instance.getPlayerWithName(from);
			if(pl == null)
				throw new InvalidArgumentException("Invalid name: " + from);
			throw new InvalidArgumentException("Invalid UUID: " + from);
		}
	}
	private Player getPlayer(String from) throws InvalidArgumentException{
		return getPlayer(from, true);
	}
	private Player getPlayer(String from, boolean noNull) throws InvalidArgumentException{
		long uuid = getUUID(from);
		Player p = instance.getPlayerWithUUID(uuid);
		if(noNull && p == null)
			throw new InvalidArgumentException("No player with name/uuid: " + from);
		return p;
	}
}
class InvalidArgumentException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	
	public InvalidArgumentException(String msg) {
		super(msg);
	}
	public InvalidArgumentException() {
		super("Invalid argument");
	}
	@Override
	public String toString() {
		return super.getMessage();
	}
}
