package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import Receiver.Receiver;
import Sender.Sender;
import tcdIO.*;

public class Client extends Thread {
	
	public static enum CLIENT_STATE {JOIN_GROUP, LISTENING, SENDING_IMAGE, RECEIVING_IMAGE, CLOSED};

	private MulticastSocket mSocket;
	private InetAddress mAddress;
	private CLIENT_STATE state;
	private ClientNodeList clientNodeList;
	private ClientNodeList senderNodeList;
	private Identifier ID;
	private Send s;
	private Listener l;
	private Terminal terminal;

	/**
	 * Client Constructor
	 */
	public Client(String testingSenderFile, int port) {
		ID = new Identifier();
		terminal = new Terminal("Client ID: " + ID.toString());
		state = CLIENT_STATE.JOIN_GROUP;
		clientNodeList = new ClientNodeList(ID,terminal);
		try {
			mAddress = InetAddress.getByName(Multicast.MCAST_ADDR);
			mSocket = new MulticastSocket(Multicast.MCAST_PORT);
			mSocket.joinGroup(mAddress);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// create send and listener objects
		s = new Send(ID, state, mSocket, mAddress, terminal, clientNodeList, senderNodeList, testingSenderFile);
		l = new Listener(ID, state, mSocket, mAddress, terminal, clientNodeList, senderNodeList);
	} // end constructor

	/**
	 * Run method, starts the sending and receiving threads
	 */
	@Override
	public void run(){
		try {
			/* Start the threads for sending and receiving */
			l.start();
			s.start();
		}
		catch(Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	} // end run

	public static void main(String[] args){
		Client client1 = new Client("", 9014);
		client1.start();
		Client client2 = new Client("", 9015);
		client2.start();
		Client client3 = new Client("doge.jpeg", 9016);
		client3.start();
	}
} // end Client
