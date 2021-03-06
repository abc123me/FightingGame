package jeremiahlowe.fightinggame.server;

import java.io.*;
import java.net.*;
import java.util.*;
import jeremiahlowe.fightinggame.net.*;
import jeremiahlowe.fightinggame.net.sockets.*;

public class Server extends Thread implements ISocketListener{
	public final int port, cnum;
	public final String host;
	public boolean debugPrinting = true;
	
	private boolean ready = false;
	private ArrayList<SocketWrapperThread> clients;
	private ArrayList<ISocketListener> clientListeners;
	
	public Server(int port) {
		this(port, null);
	}
	public Server(int port, String host) {
		this(port, host, 3);
	}
	public Server(int port, String host, int cnum) {
		clients = new ArrayList<SocketWrapperThread>();
		clientListeners = new ArrayList<ISocketListener>();
		this.port = port;
		this.host = host;
		this.cnum = cnum;
	}
	
	@Override
	public void run() {
		ServerSocket servsock = createServerSocket();
		if(servsock == null) return;
		System.out.println("Server started!");
		long UUID = 0;
		while(!Thread.interrupted()) {
			try {
				System.out.println("Waiting for a client!");
				Socket csock = servsock.accept();
				System.out.println("Accepted client from: " + csock.getInetAddress());
				SocketWrapperThread cw = new SocketWrapperThread(UUID, csock);
				cw.addClientListener(this);
				cw.start();
				clients.add(cw);
				UUID++;
				
			} catch (IOException e) {
				System.err.println("Failed to accept client: " + e);
			}
		}
		try {servsock.close();}
		catch(IOException ioe) {}
	}
	private ServerSocket createServerSocket() {
		try {
			if(host != null) {
				InetAddress haddr = InetAddress.getByName(host);
				return new ServerSocket(port, cnum, haddr);
			} else return new ServerSocket(port);                
		} catch (UnknownHostException uhe) {
			System.err.println("Invalid host: " + host + "(" + uhe + ")");
			uhe.printStackTrace();
		} catch (IOException ioe) {
			System.err.println("Couldn't start server (most likely an invalid port/host): " + ioe);
			ioe.printStackTrace();
		}
		return null;
	}
	public void serverStop() {
		interrupt();
	}
	public boolean ready() {
		return ready;
	}
	
	public void broadcast(Packet msg) {
		for(SocketWrapperThread t : clients)
			if(t != null)
				t.sendPacket(msg);
	}
	public void broadcastAllBut(Packet msg, long ignoreUUID) {
		for(SocketWrapperThread t : clients) 
			if(t != null && t.UUID != ignoreUUID) 
				t.sendPacket(msg);
	}
	public void onDisconnect(SocketWrapperThread cw) {
		for(ISocketListener c : clientListeners)
			if(c != null)
				c.onDisconnect(cw);
		clients.remove(cw);
	}
	public void onConnect(SocketWrapperThread cw) {
		for(ISocketListener c : clientListeners)
			if(c != null)
				c.onConnect(cw);
	}
	public void onReceiveRequest(SocketWrapperThread cw, Packet p) {
		for(ISocketListener c : clientListeners)
			if(c != null)
				c.onReceiveRequest(cw, p);
	}
	public void onReceiveUpdate(SocketWrapperThread cw, Packet p) {
		for(ISocketListener c : clientListeners)
			if(c != null)
				c.onReceiveUpdate(cw, p);
	}
	public void onReceiveData(SocketWrapperThread cw, String data) {
		for(ISocketListener c : clientListeners)
			if(c != null)
				c.onReceiveData(cw, data);
	}
	public void onReceiveUnknownPacket(SocketWrapperThread cw, Packet p) {
		for(ISocketListener c : clientListeners)
			if(c != null)
				c.onReceiveUnknownPacket(cw, p);
	}
	public void addClientListener(ISocketListener c) {
		clientListeners.add(c);
	}
	public void removeClientListener(ISocketListener c) {
		clientListeners.remove(c);
	}
}
