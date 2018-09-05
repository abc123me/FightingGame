package jeremiahlowe.fightinggame.server;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.gson.Gson;
import com.sun.xml.internal.ws.Closeable;

import jeremiahlowe.fightinggame.net.EPacketIdentity;
import jeremiahlowe.fightinggame.net.ISocketListener;
import jeremiahlowe.fightinggame.net.Packet;
import jeremiahlowe.fightinggame.net.SocketAdapter;
import jeremiahlowe.fightinggame.net.SocketCommunicator;
import net.net16.jeremiahlowe.shared.Timing;

public class SocketWrapperThread extends Thread implements Closeable{
	private static final Gson gson = new Gson();
	
	public final long UUID;
	
	private SocketCommunicator scomm;
	private CopyOnWriteArrayList<ISocketListener> clientListeners;
	private Thread baseThread = null;
	private Packet waitTransfer = null;
	
	public SocketWrapperThread(long UUID, Socket baseSocket, boolean startWaitQueue) throws IOException {
		this.scomm = new SocketCommunicator(baseSocket);
		this.clientListeners = new CopyOnWriteArrayList<ISocketListener>();
		this.UUID = UUID;
	}
	public SocketWrapperThread(long UUID, Socket baseSocket) throws IOException {
		this(UUID, baseSocket, true);
	}
	
	@Override
	public void run() {
		baseThread = Thread.currentThread();
		connect();
		while(!Thread.interrupted() && scomm.stillConnected()) {
			String line = scomm.readLine();
			onData(line);
			parseData(line);
			if(!scomm.hasNext()) 
				Timing.sleep(10);
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

	public Packet waitForRequest(int time, EPacketIdentity ident) {
		return waitForPacket(time, Packet.createRequest(ident));
	}
	public Packet waitForUpdate(int time, EPacketIdentity ident) {
		return waitForPacket(time, Packet.createUpdate(ident, null));
	}
	private Packet waitForPacket(int time, Packet basedOff) {
		waitTransfer = null;
		final Thread t = Thread.currentThread();
		ISocketListener isl = new SocketAdapter() {
			public void onPacket(Packet p) {
				if(p.identity == basedOff.identity && p.type == basedOff.type) {
					waitTransfer = p;
					t.interrupt();
				}
			}
			@Override public void onReceiveRequest(SocketWrapperThread cw, Packet p) { onPacket(p); }
			@Override public void onReceiveUpdate(SocketWrapperThread cw, Packet p) { onPacket(p); }
		};
		addClientListener(isl);
		Packet out = null;
		try {
			Thread.sleep(time); //If this is finished then out is not set to waitTransfer so we return null
		} catch (InterruptedException e) {
			out = waitTransfer; //This means the SocketAdapter above interrupts are wait and we have the requested packet
		} finally {
			removeClientListener(isl); //If we don't do this then bad shit happens so we must do this
		}
		return out;
	}
}
