package Client;

import java.net.InetAddress;
import java.net.MulticastSocket;

import Client.Transmission.*;
import tcdIO.*;

public class Client implements Runnable {

	private MulticastSocket mSocket;
	private InetAddress mAddress;
	private CLIENT_STATE state;
	private ClientNodeList clientNodeList;
	private ClientNodeList senderNodeList;
	private Identifier ID;
	private Terminal terminal;
	private String testingSenderFile;
	
	public static void main(String[] args) {
		new Thread(new Client()).start();
		new Thread(new Client()).start();
		new Thread(new Client("doge.jpeg")).start();
	}
	
	/**
	 * Default Constructor
	 */
	public Client() {
		this("");
	} // end Client constructor
	
	/**
	 * Client Constructor
	 * @param testingSenderFile
	 */
	public Client(String testingSenderFile) {
		ID = new Identifier();
		this.testingSenderFile = testingSenderFile;
		state = CLIENT_STATE.JOIN_GROUP;
		terminal = new Terminal("Client ID: " + ID.toString());
		clientNodeList = new ClientNodeList(ID,terminal);
		try {
			mAddress = InetAddress.getByName(Multicast.MCAST_ADDR);
			mSocket = new MulticastSocket(Multicast.MCAST_PORT);
			mSocket.joinGroup(mAddress);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	} // end Client constructor

	/**
	 * Run method starts listener and sender threads
	 */
	@Override
	public void run() {
		// create and start send and listener threads
		new Thread(new Sending(mSocket,mAddress, state, clientNodeList, 
				clientNodeList, ID, terminal, testingSenderFile)).start();
		new Thread(new Listening(mSocket, mAddress, state, clientNodeList, 
				clientNodeList, ID, terminal)).start();	
	} // end run method

} // end Client abstract class
