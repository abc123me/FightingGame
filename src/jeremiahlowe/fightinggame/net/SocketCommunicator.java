package jeremiahlowe.fightinggame.net;

import java.io.*;
import java.net.*;

public class SocketCommunicator implements Closeable{
	private Socket base;
	private PrintWriter out;
	private BufferedReader in;
	
	public SocketCommunicator(Socket base) throws IOException{
		this.base = base;
		if(base == null)
			throw new NullPointerException("Base socket cannot be null!");
		this.out = new PrintWriter(new BufferedOutputStream(base.getOutputStream()));
		this.in = new BufferedReader(new InputStreamReader(base.getInputStream()));
	}

	public void println(String text) {
		out.println(text);
		out.flush();
	}
	public boolean hasNext() {
		try {
			return in.ready();
		} catch (IOException e) {
			close();
			return false;
		}
	}
	public String readLine() {
		try {
			String s = in.readLine(); 
			return s;
		} catch (IOException e) {
			close();
			return null;
		}
	}
	public boolean stillConnected() {
		if(!base.isConnected())
			return false;
		if(base.isClosed())
			return false;
		if(base.isInputShutdown())
			return false;
		if(base.isOutputShutdown())
			return false;
		return true;
	}
	
	public void close() {
		try{
			in.close();
			out.close();
			base.close();
		}catch(IOException ioe) {}
	}
}
