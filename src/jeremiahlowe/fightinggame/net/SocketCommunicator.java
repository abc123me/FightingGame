package jeremiahlowe.fightinggame.net;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class SocketCommunicator implements Closeable{
	private Socket base;
	private PrintWriter out;
	private Scanner in;
	
	public SocketCommunicator(Socket base) throws IOException{
		this.base = base;
		if(base == null)
			throw new NullPointerException("Base socket cannot be null!");
		this.out = new PrintWriter(new BufferedOutputStream(base.getOutputStream()));
		this.in = new Scanner(base.getInputStream());
	}

	public void println(String text) {
		out.println(text);
		out.flush();
	}
	public boolean hasNext() {
		return in.hasNext();
	}
	public String readLine() {
		return in.nextLine(); 
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
		if(in.ioException() != null)
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
