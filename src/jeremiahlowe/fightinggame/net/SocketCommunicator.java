package jeremiahlowe.fightinggame.net;

import java.io.*;
import java.net.*;
import java.util.*;

public class SocketCommunicator implements Closeable{
	private Socket base;
	private PrintWriter out;
	private Scanner in;
	
	@SuppressWarnings("resource") //Will be closed since this object is closeable
	public SocketCommunicator(String host, int port) throws UnknownHostException, IOException {
		this(new Socket(host, port));
	}
	public SocketCommunicator(Socket base) throws IOException{
		this.base = base;
		if(base == null)
			throw new NullPointerException("Base socket cannot be null!");
		this.out = new PrintWriter(new BufferedOutputStream(base.getOutputStream()));
		this.in = new Scanner(new BufferedInputStream(base.getInputStream()));
		this.in.useDelimiter("\n");
	}

	public void print(String text) {
		out.print(text);
	}
	public void println(String text) {
		out.println(text);
	}
	public String readLine() {
		return in.next();
	}
	public boolean hasNext() {
		return in.hasNext();
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
	
	@Override
	public void close() {
		try{
			in.close();
			out.close();
			base.close();
		}catch(IOException ioe) {}
	}
	
}
