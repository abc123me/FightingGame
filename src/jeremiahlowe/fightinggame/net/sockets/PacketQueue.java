package jeremiahlowe.fightinggame.net.sockets;

import java.util.ArrayList;

import jeremiahlowe.fightinggame.net.Packet;

public class PacketQueue {
	private ArrayList<Packet> toSend;
	private int waiting = 0;
	
	public PacketQueue() {
		toSend = new ArrayList<Packet>();
	}
	
	public int packetsWaiting() {
		return waiting;
	}
	public void pushPacket(Packet p) {
		toSend.add(0, p);
		waiting++;
	}
	public Packet nextPacket() {
		if(waiting <= 0)
			return null;
		waiting--;
		return toSend.remove(waiting);
	}
	public boolean hasNextPacket() {
		return waiting > 0;
	}
}
