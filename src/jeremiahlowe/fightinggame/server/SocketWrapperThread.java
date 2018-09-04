package jeremiahlowe.fightinggame.server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import com.google.gson.Gson;

import jeremiahlowe.fightinggame.net.IClientListener;
import jeremiahlowe.fightinggame.net.Packet;
import jeremiahlowe.fightinggame.net.SocketCommunicator;

public class SocketWrapperThread extends Thread{
	private static final Gson gson = new Gson();
	
	public final long UUID;
	
	private SocketCommunicator scomm;
	private ArrayList<IClientListener> clientListeners;
	private Thread baseThread = null;
	
	public SocketWrapperThread(long UUID, Socket baseSocket) throws IOException {
		this.scomm = new SocketCommunicator(baseSocket);
		this.clientListeners = new ArrayList<IClientListener>();
		this.UUID = UUID;
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
				for(IClientListener c : clientListeners)
					if(c != null)
						c.onReceiveRequest(this, p);
			}
			else if(p.type == Packet.UPDATE) {
				for(IClientListener c : clientListeners)
					if(c != null)
						c.onReceiveUpdate(this, p);
			}
			else {
				for(IClientListener c : clientListeners)
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
		for(IClientListener c : clientListeners)
			if(c != null)
				c.onConnect(this);
	}
	private void disconnect() {
		for(IClientListener c : clientListeners)
			if(c != null)
				c.onDisconnect(this);
		scomm.close();
		if(baseThread!= null && baseThread.isAlive())
			baseThread.interrupt();
	}
	private void onData(String data) {
		for(IClientListener c : clientListeners)
			if(c != null)
				c.onReceiveData(this, data);
	}
	
	public void addClientListener(IClientListener d) {
		clientListeners.add(d);
	}
	public void removeClientListener(IClientListener d) {
		clientListeners.remove(d);
	}
	public void sendPacket(Packet p) {
		scomm.println(gson.toJson(p));
	}
}
