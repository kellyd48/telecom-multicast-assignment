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
	public static final String MCAST_ADDR = "230.0.0.1"; // hardcoded address for the multicast group
	public static final int MCAST_PORT = 9013; // hardcoded port number for the multicast group
	public static final int DATAGRAM_PORT = 9014;
	public static final int HELLO_TIME_INTERVAL = 200;
	public static final int NUMBER_OF_PACKETS_PER_HELLO = 2;
	public static enum CLIENT_STATE {JOIN_GROUP, LISTENING, SENDING_IMAGE, RECEIVING_IMAGE, CLOSED};

	private MulticastSocket mSocket;
	DatagramSocket dSocket;
	private InetAddress address;
	private CLIENT_STATE state;
	private ClientNodeList clientNodeList;
	private Identifier ID;
	private Send s;
	private Listener l;
	private Terminal terminal;

	//variable just for testing sending an image.
	private String testingSenderFile = "";

	public Client(String testingSenderFile){
		this(testingSenderFile, DATAGRAM_PORT);
	}

	/**
	 * Client Constructor
	 */
	public Client(String testingSenderFile, int port) {
		this.testingSenderFile = testingSenderFile;
		ID = new Identifier();
		terminal = new Terminal("Client ID: " + ID.toString());
		state = CLIENT_STATE.JOIN_GROUP;
		clientNodeList = new ClientNodeList(ID,terminal);
		// create send and listener objects
		s = new Send();
		l = new Listener();
		try {
			dSocket = new DatagramSocket(port);
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


	public synchronized void println(String message){
		try {
			sleep(20);
			terminal.println(message);
		} catch (InterruptedException e) {
			System.err.println("terminal problem");
		}
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
			while(state != CLIENT_STATE.CLOSED){
				switch(state){
					case JOIN_GROUP:
						println("Sender state: "+state.toString());
						sendHello();
						state = CLIENT_STATE.LISTENING;
						println("Sender state: "+state.toString());
						break;
					case LISTENING:
						if(!testingSenderFile.equals("")){ // if image path is empty
							//runSender(s);
							state = CLIENT_STATE.SENDING_IMAGE;
							println("Sender state: "+state.toString());
							testingSenderFile = "";
						}
						break;
					case SENDING_IMAGE:
						println("testing");
						//					if(senderNodeList.checkForAck(Ack.getPrevious(s.getSequence())))
						//						s.resend();
						runSender(s);
						break;
					case RECEIVING_IMAGE:
						break;
					default:
						break;
				}
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
					println("Sent Hello Packet.");
					sleep(HELLO_TIME_INTERVAL);
				}
			} catch(IOException e){
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
					packet = new DatagramPacket(packetData, packetData.length, address, MCAST_PORT);
					mSocket.send(packet);
					println("Sent packet from Sender.");
				}	
			}
			catch (IOException e) {
				System.err.println("Error for Client ID: " + ID.toString());
				e.printStackTrace();
			}
		} // end runSender
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
			assert(p != null);

			byte[] packetData = p.getData();
			Identifier identifier = new Identifier(Multicast.getClientIdentifier(packetData));
			//checks if the packet received originated from the local client
			if(!identifier.equals(ID)) {
				switch(Multicast.getPacketType(packetData)) {
					case HELLO: {
						ClientNode node = new ClientNode(address, p.getPort(), identifier);
						clientNodeList.add(node);
						println("Received Hello Packet from " +identifier.toString() + " " +p.getAddress());
						break;
					} // end HELLO
					case IMAGE_METADATA: {
						if(state != CLIENT_STATE.RECEIVING_IMAGE){
							println("Received Metadata for Image");
							state = CLIENT_STATE.RECEIVING_IMAGE;
						}
					} // end IMAGE_METADATA
					case IMAGE: {
						if(state == CLIENT_STATE.RECEIVING_IMAGE){
							println("Received Image Packet");
							r.receivePacket(packetData);
							r.run();
							sendAckResponse(identifier, Ack.nextExpectedAck(r.getAck()));
							state = CLIENT_STATE.RECEIVING_IMAGE;
						}
						break;
					} // end IMAGE
					case ACK: {
						//					if(state == CLIENT_STATE.SENDING_IMAGE){
						//						senderNodeList.updateAck(Multicast.getClientIdentifier(packetData), 
						//								new Ack(Multicast.getHeaderData(packetData)));
						//					}	
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

		public synchronized void printState(){
			try {
				sleep(20);
				println("Sender state: "+state.toString());
			} catch (InterruptedException e) {
				System.err.println("terminal problem");
			}
		}

		/**
		 * Sends an ACK over a datagram socket
		 * @param ID
		 * @param ackToSend
		 */
		public synchronized void sendAckResponse(Identifier ID, Ack ackToSend) {
			assert(ID != null && ackToSend != null);
			ClientNode node = clientNodeList.getClientNode(ID.getIdentifier());
			byte[] data = Multicast.constructAckPacket(ID, ackToSend);
			try {
				DatagramPacket packet = new DatagramPacket(data, Multicast.MTU, node.getAddress(), node.getPort());
				dSocket.send(packet);
				println("Sent ack ("+ackToSend.getAck()[0]+") to address: "+
						node.getAddress().toString()+" port: "+node.getPort());
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		} // end sendAckResponse
	} // end Listener 

} // end Client
