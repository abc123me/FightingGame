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
		if(waiting < 0)
			waiting = 0;
		if(waiting == 0) 
			return null;
		waiting--;
		try{
			return toSend.remove(waiting);
		}catch(IndexOutOfBoundsException ioobe) {
			System.err.println(ioobe);
			ioobe.printStackTrace(System.err);
		}
		return null;
	}
	public boolean hasNextPacket() {
		return waiting > 0;
	}
}
