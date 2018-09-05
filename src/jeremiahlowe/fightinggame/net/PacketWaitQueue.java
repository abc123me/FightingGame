package jeremiahlowe.fightinggame.net;

import jeremiahlowe.fightinggame.server.SocketWrapperThread;
import net.net16.jeremiahlowe.shared.QueuedArrayList;

public class PacketWaitQueue {
	private QueuedArrayList<PacketWaitRequest> requests;
	private final SocketWrapperThread swt;
	
	public PacketWaitQueue(SocketWrapperThread swt) {
		this.swt = swt;
		swt.addClientListener(getCustomListener());
	}
	
	public void queueRequest(PacketWaitRequest req) {
		requests.add(req);
	}
	public void dequeueRequest(PacketWaitRequest req) {
		requests.remove(req);
	}
	
	public void checkAll() {
		for(PacketWaitRequest req : requests) {
			if(req == null)
				continue;
			req.check();
			if(req.ended()) {
				requests.remove(req);
				continue;
			}
		}
	}

	private void onReceivePacket(Packet p) {
		for(PacketWaitRequest req : requests) {
			if(req == null)
				continue;
			req.check();
			if(req.ended()) {
				requests.remove(req);
				continue;
			}
			req.callReceivePacket(swt, p, req.timing.millisPassed());
		}
	}
	private ISocketListener getCustomListener() {
		return new SocketAdapter() {
			@Override
			public void onReceiveRequest(SocketWrapperThread cw, Packet p) {
				onReceivePacket(p);
			}
			@Override
			public void onReceiveUpdate(SocketWrapperThread cw, Packet p) {
				onReceivePacket(p);
			}
			@Override
			public void onReceiveUnknownPacket(SocketWrapperThread cw, Packet p) {
				onReceivePacket(p);
			}
		};
	}
}
