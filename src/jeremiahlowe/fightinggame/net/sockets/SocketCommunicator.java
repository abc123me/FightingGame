package jeremiahlowe.fightinggame.net.sockets;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import jeremiahlowe.fightinggame.net.Packet;

public class SocketCommunicator extends Thread implements Closeable{
	private Socket base;
	private PrintWriter out;
	private Scanner in;
	
	public SocketCommunicator(Socket base) throws IOException{
		if(base == null)
			throw new NullPointerException("Base socket cannot be null!");
		//base.setSoTimeout(1000);
		this.base = base;
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
		int a = 0;
		try { a = base.getInputStream().available();
		}catch(Exception e) { a = 0; }
		return a > 0;
	}
	public String readLine() {
		String s = in.nextLine(); 
		IOException ioe = in.ioException();
		if(ioe != null) {
			System.out.println("ioe in readLine(): " + ioe);
			close();
			return null;
		}
		return s;
	}
	public boolean stillConnected() {
		if(in.ioException() != null)
			return false;
		try {
			byte[] dat = Packet.createConnCheck().toJSON().getBytes();
			base.getOutputStream().write(dat);
			base.getOutputStream().write('\n');
			return true;
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
