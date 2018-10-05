package jeremiahlowe.fightinggame.net.sockets;

import java.util.ArrayList;

import jeremiahlowe.fightinggame.net.Packet;

public class PacketQueue {
	private ArrayList<Packet> toSend;
	private int waiting = 0;
	
	public PacketQueue() {
		toSend = new ArrayList<Packet>();
		waiting = 0;
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
		if(waiting >= toSend.size()) {
			System.err.println("Invalid packets waiting value, getting last");
			toSend.clear();
			waiting = toSend.size();
		}
		waiting--;
		return toSend.remove(waiting);
	}
	public boolean hasNextPacket() {
		return waiting > 0;
	}
}
