package jeremiahlowe.fightinggame.server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import jeremiahlowe.fightinggame.ins.Instance;
import jeremiahlowe.fightinggame.net.IDataListener;
import jeremiahlowe.fightinggame.net.IDisconnectionListener;
import jeremiahlowe.fightinggame.net.SocketCommunicator;

public class ClientWrapper extends Thread{
	public final SocketCommunicator scomm;
	public final long UUID;
	
	private Instance instance;
	private ArrayList<IDisconnectionListener> disconnectListeners;
	private ArrayList<IDataListener> dataListeners;
	
	public ClientWrapper(long UUID, Instance instance, Socket baseSocket) throws IOException {
		checkSocketIntegrity(baseSocket);
		this.UUID = UUID;
		this.scomm = new SocketCommunicator(baseSocket);
		this.instance = instance;
		this.disconnectListeners = new ArrayList<IDisconnectionListener>();
		this.dataListeners = new ArrayList<IDataListener>();
	}
	
	public final static void checkSocketIntegrity(Socket baseSocket) throws IOException{
		if(baseSocket == null)
			throw new NullPointerException("Base socket is null!");
		if(baseSocket.isClosed())
			throw new IOException("Base socket cannot be closed!");
		if(!baseSocket.isConnected())
			throw new IOException("Base socket must be connected!");
	}

	@Override
	public void run() {
		in.useDelimiter("\n");
		while(!Thread.interrupted() && in.ioException() == null) {
			System.out.println("Waiting for data from client...");
			if(!in.hasNext()) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					break;
				}
			}
			String line = in.next();
			System.out.println("Got data from client: " + UUID);
			System.out.println(line);
			onData(line);
		}
		disconnect();
	}
	
	private void disconnect() {
		for(IDisconnectionListener dl : disconnectListeners)
			if(dl != null)
				dl.onDisconnect(this);
		try {
			out.close();
			in.close();
			baseSocket.close();
		}catch(IOException ioe) {}
	}
	private void onData(String data) {
		for(IDataListener d : dataListeners)
			if(d != null)
				d.onReceiveData(this, data);
	}

	public void addDisconnectionListener(IDisconnectionListener d) {
		disconnectListeners.add(d);
	}
	public void removeDisconnectionListener(IDisconnectionListener d) {
		disconnectListeners.remove(d);
	}
	public void addDataListener(IDataListener d) {
		dataListeners.add(d);
	}
	public void removeDataListener(IDataListener d) {
		dataListeners.remove(d);
	}
}
