package jeremiahlowe.fightinggame.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import jeremiahlowe.fightinggame.net.IClientListener;
import jeremiahlowe.fightinggame.net.Packet;

public class Server extends Thread implements IClientListener{
	public final int port, cnum;
	public final String host;
	public boolean debugPrinting = true;
	
	private boolean ready = false;
	private ArrayList<ClientWrapper> clients;
	private ArrayList<IClientListener> clientListeners;
	
	public Server(int port) {
		this(port, "127.0.0.1");
	}
	public Server(int port, String host) {
		this(port, host, 3);
	}
	public Server(int port, String host, int cnum) {
		clients = new ArrayList<ClientWrapper>();
		clientListeners = new ArrayList<IClientListener>();
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
				ClientWrapper cw = new ClientWrapper(UUID, csock);
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
			InetAddress haddr = InetAddress.getByName(host);
			return new ServerSocket(port, cnum, haddr);
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
	
	public void onDisconnect(ClientWrapper cw) {
		for(IClientListener c : clientListeners)
			if(c != null)
				c.onDisconnect(cw);
		clients.remove(cw);
	}
	public void onConnect(ClientWrapper cw) {
		for(IClientListener c : clientListeners)
			if(c != null)
				c.onConnect(cw);
	}
	public void onReceiveRequest(ClientWrapper cw, Packet p) {
		for(IClientListener c : clientListeners)
			if(c != null)
				c.onReceiveRequest(cw, p);
	}
	public void onReceiveUpdate(ClientWrapper cw, Packet p) {
		for(IClientListener c : clientListeners)
			if(c != null)
				c.onReceiveUpdate(cw, p);
	}
	public void onReceiveData(ClientWrapper cw, String data) {
		for(IClientListener c : clientListeners)
			if(c != null)
				c.onReceiveData(cw, data);
	}
	public void onReceiveUnknownPacket(ClientWrapper cw, Packet p) {
		for(IClientListener c : clientListeners)
			if(c != null)
				c.onReceiveUnknownPacket(cw, p);
	}
	public void addClientListener(IClientListener c) {
		clientListeners.add(c);
	}
	public void removeClientListener(IClientListener c) {
		clientListeners.remove(c);
	}
	
}
