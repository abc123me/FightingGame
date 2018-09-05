package jeremiahlowe.fightinggame.net;

import jeremiahlowe.fightinggame.server.SocketWrapperThread;
import net.net16.jeremiahlowe.shared.Timing;

public abstract class PacketWaitRequest {
	public final int timeout;
	public final Timing timing;
	private boolean cancelled;
	
	public PacketWaitRequest(int timeout) {
		this.timeout = timeout;
		this.timing = new Timing();
		timing.start();
		cancelled = false;
	}
	public void startWait() {
		timing.start();
	}
	
	/**
	 * Called whenever a packet of any type is received, if this returns true then the PacketWaitRequest is cancelled
	 * @return Whether or not the packet cancels the timeout
	 */
	public final void callReceivePacket(SocketWrapperThread sc, Packet p, long time) {
		if(onReceivePacket(sc, p, time))
			cancel();
	}
	public abstract boolean onReceivePacket(SocketWrapperThread sc, Packet p, long time);
	public void onTimeout() {}
	public void onCancel() {}
	
	public void cancel() {
		cancelled = true;
		onCancel();
	}
	public boolean cancelled() {
		return cancelled;
	}
	public boolean timedOut() {
		return timing.millisPassed() > timeout;
	}
	public boolean ended() {
		return cancelled || timedOut();
	}
	public void check() {
		if(timedOut())
			onTimeout();
	}
}
