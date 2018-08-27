package jeremiahlowe.fightinggame.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Server extends Thread{
	public final int port, cnum;
	public final String host;
	public boolean debugPrinting = true;
	
	private boolean ready = false;
	private ArrayList<ClientListenerThread> clientListenerThreads;
	
	public Server(int port) {
		this(port, "127.0.0.1");
	}
	public Server(int port, String host) {
		this(port, host, 3);
	}
	public Server(int port, String host, int cnum) {
		clientListenerThreads = new ArrayList<ClientListenerThread>();
		this.port = port;
		this.host = host;
		this.cnum = cnum;
	}
	
	@Override
	public void run() {
		ServerSocket servsock = createServerSocket();
		if(servsock == null) return;
		System.out.println("Server started!");
		while(!Thread.interrupted()) {
			try {
				System.out.println("Waiting for a client!");
				Socket csock = servsock.accept();
				System.out.println("Accepted client from: " + csock.getInetAddress());
				ClientWrapper cw = new ClientWrapper(csock);
				ClientListenerThread clt = new ClientListenerThread(cw);
				clientListenerThreads.add(clt);
				clt.start();
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
}
