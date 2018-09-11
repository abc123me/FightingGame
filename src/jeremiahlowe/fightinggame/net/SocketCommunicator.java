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
		if(in.ioException() != null) {
			System.out.println("ioe in hasNext()");
			close();
			return false;
		}
		return in.hasNext();
	}
	public String readLine() {
		String s = in.nextLine(); 
		if(in.ioException() != null) {
			System.out.println("ioe in readLine()");
			close();
			return null;
		}
		return s;
	}
	public boolean stillConnected() {
		try {
			int i = base.getInputStream().read();
			return i >= 0;
		}catch(Exception e) {
			return false;
		}
	}
	
	public void close() {
		try{
			in.close();
			out.close();
			base.close();
		}catch(IOException ioe) {}
	}
}
