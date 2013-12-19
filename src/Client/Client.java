package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

import Receiver.Receiver;
import Sender.Sender;
import tcdIO.*;

public class Client {
	public static final String MCAST_ADDR = "230.0.0.1"; // hardcoded address for the multicast group
	public static final int MCAST_PORT = 9013; // hardcoded port number for the multicast group
	public static final int HELLO_TIME_INTERVAL = 200;
	public static final int NUMBER_OF_PACKETS_PER_HELLO = 2;
	public static enum CLIENT_STATE {JOIN_GROUP, LISTENING, SENDING_IMAGE, RECEIVING_IMAGE};

	private MulticastSocket mSocket;
	DatagramSocket dSocket;
	private InetAddress address;
	private CLIENT_STATE state;
	private ClientNodeList clientNodeList;
	// Keeps track of nodes sending image to
	private ClientNodeList senderNodeList;
	private Identifier ID;
	private Send s;
	private Listener l;
	private Terminal terminal;

	//variable just for testing sending an image.
	private String testingSenderFile = "";

	/**
	 * Client Constructor
	 */
	public Client(String testingSenderFile) {
		this.testingSenderFile = testingSenderFile;
		ID = new Identifier();
		terminal = new Terminal("Client ID: " + ID.toString());
		state = CLIENT_STATE.JOIN_GROUP;
		clientNodeList = new ClientNodeList(ID,terminal);
		// create send and listener objects
		s = new Send();
		l = new Listener();
		try {
			dSocket = new DatagramSocket(MCAST_PORT);
			address = InetAddress.getByName(MCAST_ADDR);
			mSocket = new MulticastSocket(MCAST_PORT);
			mSocket.joinGroup(address);
		} catch (Exception e) {
			e.printStackTrace();
		}

	} // end constructor

	/**
	 * Run method, starts the sending and receiving threads
	 */
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
		Client client1 = new Client("");
		client1.run();
		Client client2 = new Client("");
		client2.run();
		Client client3 = new Client("doge.jpeg");
		client3.run();
	}

	/**
	 * Sender Thread
	 * Sends Packets on the socket, through the sender class
	 *
	 */
	private class Send extends Thread {
		private Sender s;
		private DatagramPacket packet;

		/**
		 * Send Constructor
		 */
		public Send(){
			s = new Sender(ID);
		} // end constructor

		@Override
		public void run() {
			while(true){
				switch(state){
				case JOIN_GROUP:
					sendHello();
					state = CLIENT_STATE.SENDING_IMAGE;
					break;
				case LISTENING:
					break;
				case SENDING_IMAGE:
					if(senderNodeList != null){
						if(s.getSequence() != null && senderNodeList.checkForAck(Ack.getPrevious(s.getSequence())))
							s.resend();
					}
					runSender(s);
				case RECEIVING_IMAGE:

					break;
				default:
					break;
				}
				runSender(s);
			}
		} // end run

		/**
		 * Send hello packets
		 */
		public synchronized void sendHello(){
			try{
				byte[] helloPacket = Multicast.constructHelloPacket(ID);
				DatagramPacket packet = new DatagramPacket(helloPacket, helloPacket.length, address, MCAST_PORT);
				for(int i = 0; i < NUMBER_OF_PACKETS_PER_HELLO; i++){
					mSocket.send(packet);
					terminal.println("Sent Hello Packet.");
					sleep(HELLO_TIME_INTERVAL);
				}
			}catch(IOException e){
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} // end sendHello

		/**
		 * Sends image packets
		 * @param s
		 */
		public synchronized void runSender(Sender s){
			try {
				s.run(testingSenderFile);
				if(s.hasPacketToSend()){
					byte[] packetData = s.packetToSend();
					if(Multicast.getPacketType(packetData) == Multicast.PACKET_TYPE.IMAGE_METADATA)
						updateSenderNodeList();
					packet = new DatagramPacket(packetData, packetData.length, address, MCAST_PORT);
					mSocket.send(packet);
					terminal.println("Sent packet from Sender.");
				}	
			}
			catch (IOException e) {
				System.out.println("Error for Client ID: " + ID.toString());
				e.printStackTrace();
			}
		} // end runSender

		public synchronized void updateSenderNodeList(){
			senderNodeList = new ClientNodeList(clientNodeList);
		}
	} // end Send

	/**
	 * Listener thread
	 * Receives Packets from the nodes and forwards them to the receiver class
	 *
	 */
	private class Listener extends Thread {
		Receiver r;

		/**
		 * Listener Constructor
		 */
		public Listener() {
			r = new Receiver(ID);
		} // end constructor

		/**
		 * Listens for packets on the mSocket
		 */
		@Override
		public void run() {
			try {
				DatagramPacket p = null;
				byte[] data= new byte[Multicast.MTU];  
				while(true) {
					p = new DatagramPacket(data, data.length);
					// listen for packet 
					mSocket.receive(p);	
					// process it
					receivePacket(p);
				} // end while
			}
			catch(IOException e) {
				System.err.println("Socket closed prematurely");
			}
		} // end run

		/**
		 * Processes a packet
		 * @param p
		 * @throws IOException
		 */
		public synchronized void receivePacket(DatagramPacket p) throws IOException {
			byte[] packetData = p.getData();
			Identifier identifier = new Identifier(Multicast.getClientIdentifier(packetData));
			if(!identifier.equals(ID)) {
				switch(Multicast.getPacketType(packetData)) {
					case HELLO: {
						ClientNode node = new ClientNode(address, p.getPort(), identifier);
						clientNodeList.add(node);
						terminal.println("Received Hello Packet from " +identifier.toString() + " " +p.getAddress());
						break;
					} // end HELLO
					case IMAGE_METADATA: {
						if(state != CLIENT_STATE.RECEIVING_IMAGE){
							terminal.println("Received Metadata for Image");
							state = CLIENT_STATE.RECEIVING_IMAGE;
						}
					} // end IMAGE_METADATA
					case IMAGE: {
						if(state == CLIENT_STATE.RECEIVING_IMAGE){
							terminal.println("Received Image Packet");
							r.receivePacket(packetData);
							r.run();
							sendAckResponse(identifier, Ack.nextExpectedAck(r.getAck()));
							state = CLIENT_STATE.RECEIVING_IMAGE;
						}
						break;
					} // end IMAGE
					case ACK: {
						if(state == CLIENT_STATE.SENDING_IMAGE){
							senderNodeList.updateAck(Multicast.getClientIdentifier(packetData), 
									new Ack(Multicast.getHeaderData(packetData)));
						}	
						break;
					} // end ACK
					default: {
						break;
					}
				} // end switch
			} // end if
			else {
				// ignore packets received by this node sent to itself over multicast
			}
		} // end receivePacket
		
		/**
		 * Sends an ACK over a datagram socket
		 * @param ID
		 * @param ackToSend
		 */
		public synchronized void sendAckResponse(Identifier ID, Ack ackToSend) {
			ClientNode node = clientNodeList.getClientNode(ID.getIdentifier());
			byte[] data = Multicast.constructAckPacket(ID, ackToSend);
			try {
				DatagramPacket packet = new DatagramPacket(data, Multicast.MTU, node.getAddress(), node.getPort());
				dSocket.send(packet);
				terminal.println("Sent ack ("+ackToSend.getAck()[0]+") to address: "+
						node.getAddress().toString()+" port: "+node.getPort());
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		} // end sendAckResponse
	} // end Listener 

} // end Client
