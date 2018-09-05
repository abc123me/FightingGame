package jeremiahlowe.fightinggame.server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.sun.xml.internal.ws.Closeable;

import jeremiahlowe.fightinggame.net.ISocketListener;
import jeremiahlowe.fightinggame.net.Packet;
import jeremiahlowe.fightinggame.net.PacketWaitQueue;
import jeremiahlowe.fightinggame.net.SocketCommunicator;

public class SocketWrapperThread extends Thread implements Closeable{
	private static final Gson gson = new Gson();
	
	public final long UUID;
	public final PacketWaitQueue waitQueue;
	
	private SocketCommunicator scomm;
	private ArrayList<ISocketListener> clientListeners;
	private Thread baseThread = null;
	
	public SocketWrapperThread(long UUID, Socket baseSocket) throws IOException {
		this.scomm = new SocketCommunicator(baseSocket);
		this.clientListeners = new ArrayList<ISocketListener>();
		this.UUID = UUID;
		waitQueue = new PacketWaitQueue(this);
	}
	
	@Override
	public void run() {
		baseThread = Thread.currentThread();
		connect();
		while(!Thread.interrupted() && scomm.stillConnected()) {
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
	
	public void close() {
		disconnect();
	}
	
	public void addClientListener(ISocketListener d) {
		clientListeners.add(d);
	}
	public void removeClientListener(ISocketListener d) {
		clientListeners.remove(d);
	}
	public void sendPacket(Packet p) {
		String j = gson.toJson(p);
		scomm.println(j);
	}
}
