package jeremiahlowe.fightinggame.server;

import java.io.IOException;

public class ClientListenerThread extends Thread{
	public final ClientWrapper client;
	
	public ClientListenerThread(ClientWrapper client) throws IOException {
		ClientWrapper.checkSocketIntegrity(client.baseSocket);
		this.client = client;
	}
	
	@Override
	public void run() {
		while(!Thread.interrupted()) {
			
		}
	}
	
	public void onConnectionClosed() {
		
	}
}
