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
	private Terminal terminal;
	
	/**
	 * Transmission Constructor
	 * @param mSocket
	 * @param mAddress
	 * @param state
	 * @param clientNodeList
	 * @param senderNodeList
	 * @param ID
	 * @param terminal
	 */
	public Transmission(MulticastSocket mSocket, InetAddress mAddress, CLIENT_STATE state,
			ClientNodeList clientNodeList, ClientNodeList senderNodeList, Identifier ID, Terminal terminal) {
		this.mSocket = mSocket;
		this.mAddress = mAddress;
		this.state = CLIENT_STATE.JOIN_GROUP;
		this.clientNodeList = clientNodeList;
		this.senderNodeList = senderNodeList;
		this.ID = ID;
		this.terminal = terminal;
	} // end Transmission method
	
	/**
	 * Prints a message to the terminal
	 * 20 millisecond sleep, to allow threads to print to terminal properly
	 * @param message
	 */
	public synchronized void println(String message) {
		try {
			Thread.sleep(20);
			terminal.println(message);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	} // end println method
} // end Transmission class
