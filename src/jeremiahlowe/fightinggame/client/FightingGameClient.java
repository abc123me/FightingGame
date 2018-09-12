package jeremiahlowe.fightinggame.client;

import jeremiahlowe.fightinggame.Meta;
import jeremiahlowe.fightinggame.client.chat.Chat;
import jeremiahlowe.fightinggame.client.chat.RemoteChatManager;
import jeremiahlowe.fightinggame.net.Packet;
import jeremiahlowe.fightinggame.net.sockets.ISocketListener;
import jeremiahlowe.fightinggame.net.sockets.SocketWrapperThread;
import jeremiahlowe.fightinggame.phys.Player;
import net.net16.jeremiahlowe.shared.math.Vector;
import net.net16.jeremiahlowe.shared.math.VectorMath;
import net.net16.jeremiahlowe.shared.math.Viewport;
import processing.core.PApplet;
import processing.event.MouseEvent;

public class FightingGameClient extends PApplet implements ISocketListener{

	public static void main(String[] args) {
		Meta.setServerside(false);
		PApplet.main(FightingGameClient.class, args);
	}
	
	public float worldSize = 10;
	public boolean hax = true;
	public String host = "localhost";
	public String name = "Unnamed";
	public int port = 1234;
	public boolean followLocalPlayer = false;
	public int simulatedNetworkLag = 0;
	
	private Chat chat;
	private RemoteChatManager rcm;
	private GameClientInstance instance;
	private Player localPlayer;
	private boolean chatControl = false;
	
	@Override
	public void settings() {
		parseArgs(this.args);
		instance = new GameClientInstance(this);
		instance.screen = new Viewport(width, -height, width / 2, height / 2);
		instance.world = new Viewport(worldSize * instance.screen.aspRatio(), worldSize, 0, 0);
		if(!instance.connectToServer(host, port))
			System.exit(1);
		instance.setNetworkLag(simulatedNetworkLag);
		instance.sendVersionData();
		localPlayer = instance.getLocalPlayerFromServer();
		if(localPlayer == null) {
			System.err.println("Was unable to retrieve player from the server?!");
			System.exit(-1);
		}
		instance.sendName(name);
		localPlayer.name = name;
		instance.localPlayer = localPlayer;
		chat = new Chat(instance);
		instance.addAll(instance.getNetworkStatistics(), localPlayer, chat);
		instance.getPlayerList();
		rcm = new RemoteChatManager(instance, chat);
		chat.addChatListener(rcm);
		instance.add(rcm);
	}
	@Override
	public void setup() {
		if (hax)
			instance.statistics.level = 9000;
		frameRate(60);
	}
	@Override
	public void draw() {
		background(255);
		instance.physicsUpdate((1 / frameRate));
		instance.drawAll(this);
		if(followLocalPlayer) {
			instance.world.x = localPlayer.pos.x;
			instance.world.y = localPlayer.pos.y;
		}
	}
	
	@Override
	public void mouseMoved() {
		Vector newLookPos = instance.screen.transform(new Vector(mouseX, mouseY), instance.world);
		Vector lookPos = localPlayer.look;
		localPlayer.setLookPosition(newLookPos);
		if(VectorMath.dist2(newLookPos, lookPos) > 1)
			instance.updateLocalPlayer();
	}
	@Override
	public void mousePressed() {
		localPlayer.shooting = true;
		instance.updateLocalPlayer();
	}
	@Override
	public void mouseReleased() {
		localPlayer.shooting = false;
		instance.updateLocalPlayer();
	}
	@Override
	public void mouseDragged() {
		Vector newLookPos = instance.screen.transform(new Vector(mouseX, mouseY), instance.world);
		Vector lookPos = localPlayer.look;
		localPlayer.setLookPosition(newLookPos);
		localPlayer.shoot();
		if(VectorMath.dist2(newLookPos, lookPos) > 1)
			instance.updateLocalPlayer();
	}
	@Override
	public void mouseWheel(MouseEvent m) {
		Viewport world = instance.world;
		float a = m.getCount();
		float val = Math.min(world.w, world.h);
		if(val + a <= 0)
			return;
		world.zoom(a, instance.screen.aspRatio());
	}
	@Override
	public void keyPressed() {
		char k = Character.toLowerCase(key);
		if(key == PApplet.ENTER) {
			chatControl = !chatControl;
			if(chatControl) chat.startTyping();
			else chat.stopTyping(true);
			return;
		}
		if(chatControl) {
			chat.typeChar(key);
		} else {
			if (keyCode == SHIFT)
				localPlayer.setFastMovement(true);
			else if (k == 'w')
				localPlayer.keys.y = 1;
			else if (k == 's')
				localPlayer.keys.y = -1;
			else if (k == 'a')
				localPlayer.keys.x = 1;
			else if (k == 'd')
				localPlayer.keys.x = -1;
			else if (k == '=')
				instance.statistics.incrStatLevel();
			else if (k == '-')
				instance.statistics.decrStatLevel();
			else if (k == ' ')
				localPlayer.shooting = true;
			else
				return;
			instance.updateLocalPlayer();
		}
	}
	@Override
	public void keyReleased() {
		if(!chatControl) {
			char k = Character.toLowerCase(key);
			if (keyCode == SHIFT)
				localPlayer.setFastMovement(false);
			else if (k == 'w' || k == 's')
				localPlayer.keys.y = 0;
			else if (k == 'a' || k == 'd')
				localPlayer.keys.x = 0;
			else if (k == ' ')
				localPlayer.shooting = false;
			else
				return;
			instance.updateLocalPlayer();
		}
	}
	public void onConnect(SocketWrapperThread cw) {}
	public void onReceiveRequest(SocketWrapperThread cw, Packet p) {}
	public void onReceiveUpdate(SocketWrapperThread cw, Packet p) {}
	public void onDisconnect(SocketWrapperThread cw) {
		exit();
	}
	public void onReceiveData(SocketWrapperThread cw, String data) {}
	public void onReceiveUnknownPacket(SocketWrapperThread cw, Packet p) {}
	public void exit() {
		System.out.println("Exiting now!");
		System.exit(0);
	}
	private void parseArgs(String[] args) {
		int w = 500, h = 500;
		boolean full = false;
		if(args != null) {
			System.out.println("Got arguments: ");
			for(String arg : args)
				System.out.print(arg + " ");
			System.out.println();
			for(int i = 1; i < args.length + 1; i++) {
				String arg = args[i - 1].trim();
				String next = "";
				if(i < args.length)
					next = args[i].trim();
				if(arg.equals("--full-screen"))
					full = true;
				if(arg.equals("--width"))
					w = Integer.parseInt(next);
				if(arg.equals("--height"))
					h = Integer.parseInt(next);
				if(arg.equals("--host"))
					host = next;
				if(arg.equals("--port"))
					port = Integer.parseInt(next);
				if(arg.equals("--hax"))
					this.hax = true;
				if(arg.equals("--name"))
					name = next;
				if(arg.equals("--follow")) 
					followLocalPlayer = true;
				if(arg.equals("--ping"))
					simulatedNetworkLag = Integer.parseInt(next);
			}
		} else System.out.println("No arguments given :(");
		if(w <= 10)
			w = 500;
		if(h <= 10)
			h = 500;
		size(w, h);
		if(full) {
			fullScreen();
			size(displayWidth, displayHeight);
		}
	}
}
