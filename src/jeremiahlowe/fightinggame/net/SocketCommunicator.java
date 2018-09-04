package jeremiahlowe.fightinggame.net;

import java.io.*;
import java.net.*;
import java.nio.BufferOverflowException;
import java.util.*;

public class SocketCommunicator implements Closeable{
	public static final int BUFFER_SIZE = 3000; //3KB per client should be PLENTY
	
	private Socket base;
	private PrintWriter out;
	private InputStream in;
	
	public SocketCommunicator(String host, int port) throws UnknownHostException, IOException {
		this(new Socket(host, port));
	}
	public SocketCommunicator(Socket base) throws IOException{
		this.base = base;
		if(base == null)
			throw new NullPointerException("Base socket cannot be null!");
		this.out = new PrintWriter(new BufferedOutputStream(base.getOutputStream()));
		this.in = base.getInputStream();
	}

	public void print(String text) {
		out.print(text.length() + " " + text);
		out.flush();
	}
	public void println(String text) {
		print(text + "\n");
	}
	public boolean hasNext() {
		try{
			return in.available() > 0;
		}catch(IOException ioe) {
			return false;
		}
	}
	public String readLine() {
		try{
			String head = "";
			int a = in.available();
			if(a > BUFFER_SIZE) {
				System.err.println("Assface sending huge ass packets / trying to D.O.S., forcefully disconnecting the piece of shit");
				close();
				return null;
			}
			if(a == 0)
				return null;
			int r = 0;
			for(int i = 0; i < (a > 10 ? 10 : a); i++) {
				int j = in.read();
				if(j < 0) return null;
				if(j == ' ')
					break;
				head += (char)j;
			}
			int len = Integer.parseInt(head);
			int ttl = a - r;
			if(len > ttl)
				throw new RuntimeException("Message is too small for size provided: got " + ttl + ", expected " + len);
			String out = "";
			for(int i = 0; i < ttl; i++) {
				int j = in.read();
				if(j < 0) return null;
				out += (char)j;
				if(j == '\n')
					break;
			}
			return out;
		}catch(IOException ioe) {
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
