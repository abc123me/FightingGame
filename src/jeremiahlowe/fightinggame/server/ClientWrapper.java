package jeremiahlowe.fightinggame.server;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientWrapper implements Closeable{
	public final Socket baseSocket;
	
	private OutputStream out;
	private InputStream in;
	
	public ClientWrapper(Socket baseSocket) throws IOException {
		checkSocketIntegrity(baseSocket);
		this.baseSocket = baseSocket;
		this.out = baseSocket.getOutputStream();
		this.in = baseSocket.getInputStream();
	}
	
	public boolean stillConnected() {
		if(baseSocket == null)
			return false;
		if(!baseSocket.isConnected())
			return false;
		if(baseSocket.isClosed())
			return false;
		if(baseSocket.isInputShutdown())
			return false;
		if(baseSocket.isOutputShutdown())
			return false;
		return true;
	}
	
	public void sendMessage(String msg) throws IOException{
		out.write(msg.getBytes(), 0, msg.length());
		out.write(0);
	}
	public void println(String msg) throws IOException{
		sendMessage(msg + '\n');
	}
	public String readln() throws IOException {
		return readUntil('\n');
	}
	public String readUntil(char end) throws IOException {
		String out = "";
		int c;
		while(true) {
			c = in.read();
			if(c < 0 || c == end) break;
			out += c;
		}
		return out;
	}
	public void close() throws IOException{
		out.close();
		in.close();
		baseSocket.close();
	}
	
	public final static void checkSocketIntegrity(Socket baseSocket) throws IOException{
		if(baseSocket == null)
			throw new NullPointerException("Base socket is null!");
		if(baseSocket.isClosed())
			throw new IOException("Base socket cannot be closed!");
		if(!baseSocket.isConnected())
			throw new IOException("Base socket must be connected!");
	}
}
