package Client;
import java.net.InetAddress;
import java.net.MulticastSocket;

import tcdIO.*;

public abstract class Transmission {
	
	public static enum CLIENT_STATE {JOIN_GROUP, LISTENING, SENDING_IMAGE, RECEIVING_IMAGE, CLOSED};
	
	protected MulticastSocket mSocket;
	protected InetAddress mAddress;
	protected CLIENT_STATE state;
	protected ClientNodeList clientNodeList;
	protected ClientNodeList senderNodeList;
	protected Identifier ID;
	protected Terminal terminal;
	
	public Transmission(MulticastSocket mSocket, InetAddress mAddress, CLIENT_STATE state,
			ClientNodeList clientNodeList, ClientNodeList senderNodeList, Identifier ID, Terminal terminal) {
		this.mSocket = mSocket;
		this.mAddress = mAddress;
		this.state = CLIENT_STATE.JOIN_GROUP;
		this.clientNodeList = clientNodeList;
		this.senderNodeList = senderNodeList;
		this.ID = ID;
		this.terminal = terminal;
	}
	
	public synchronized void println(String message) {
		try {
			Thread.sleep(20);
			terminal.println(message);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
