package jeremiahlowe.fightinggame.server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import com.google.gson.Gson;

import jeremiahlowe.fightinggame.net.EPacketIdentity;
import jeremiahlowe.fightinggame.net.ISocketListener;
import jeremiahlowe.fightinggame.net.Packet;
import jeremiahlowe.fightinggame.net.SocketCommunicator;
import net.net16.jeremiahlowe.shared.Timing;

public class SocketWrapperThread extends Thread{
	private static final Gson gson = new Gson();
	
	public final long UUID;
	
	private SocketCommunicator scomm;
	private ArrayList<ISocketListener> clientListeners;
	private Thread baseThread = null;
	
	public SocketWrapperThread(long UUID, Socket baseSocket) throws IOException {
		this.scomm = new SocketCommunicator(baseSocket);
		this.clientListeners = new ArrayList<ISocketListener>();
		this.UUID = UUID;
	}

	public Packet waitForPacket(long timeout, EPacketIdentity identity) {
		sendPacket(Packet.createRequest(identity));
		if(!waitForServer(1000)) {
			System.err.println("Server didn't respond to request for \"" + identity + "\" after " + timeout + "ms");
			return null;
		}
		String fromServer = scomm.readLine();
		Packet p = gson.fromJson(fromServer, Packet.class);
		if(p.type != Packet.UPDATE){
			System.err.println("Server responded to \"" + identity + "\" request with an invalid packet type of: " + p.type);
			return null;
		}
		if(p.identity != identity){
			System.err.println("Server responded to \"" + identity + "\" request with an invalid packet identity of \"" + p.identity + "\"");
			return null;
		}
		return p;
	}
	private boolean waitForServer(long timeout) {
		return waitForServer(timeout, 10);
	}
	private boolean waitForServer(long timeout, long step) {
		long time = 0;
		while(!scomm.hasNext()) {
			if(!Timing.sleep(step))
				return false;
			time += step;
			if(time > timeout)
				return false;
		}
		return true;
	}
	
	@Override
	public void run() {
		baseThread = Thread.currentThread();
		connect();
		while(!Thread.interrupted() && scomm.stillConnected()) {
			if(!scomm.hasNext()) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					break;
				}
			}
			String line = scomm.readLine();
			onData(line);
			parseData(line);
		}
		disconnect();
	}
	
	private void parseData(String data) {
		try {
			Packet p = gson.fromJson(data, Packet.class);
			if(p == null)
				return;
			if(p.type == Packet.REQUEST) {
				for(ISocketListener c : clientListeners)
					if(c != null)
						c.onReceiveRequest(this, p);
			}
			else if(p.type == Packet.UPDATE) {
				for(ISocketListener c : clientListeners)
					if(c != null)
						c.onReceiveUpdate(this, p);
			}
			else {
				for(ISocketListener c : clientListeners)
					if(c != null)
						c.onReceiveUnknownPacket(this, p);
			}
		}catch(Exception e){
			System.err.println("Error parsing packet from client!");
			System.err.println(e);
			e.printStackTrace();
		}
	}
	private void connect() {
		for(ISocketListener c : clientListeners)
			if(c != null)
				c.onConnect(this);
	}
	private void disconnect() {
		for(ISocketListener c : clientListeners)
			if(c != null)
				c.onDisconnect(this);
		scomm.close();
		if(baseThread!= null && baseThread.isAlive())
			baseThread.interrupt();
	}
	private void onData(String data) {
		for(ISocketListener c : clientListeners)
			if(c != null)
				c.onReceiveData(this, data);
	}
	
	public void addClientListener(ISocketListener d) {
		clientListeners.add(d);
	}
	public void removeClientListener(ISocketListener d) {
		clientListeners.remove(d);
	}
	public void sendPacket(Packet p) {
		scomm.println(gson.toJson(p));
	}
}
