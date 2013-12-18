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
	public static final int DATAGRAM_PORT = 9014;
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
		client1.run();
		Client client2 = new Client("", 9015);
		client2.run();
		Client client3 = new Client("doge.jpeg", 9016);
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

		@Override
		public void run() {
			try {
				receivePacket();		
			}
			catch(IOException e) {
				mSocket.close();
				System.out.println("Socket closed prematurely");
			}
		} // end run

		/**
		 * Receive packets from the multicast socket 
		 * @throws IOException
		 */
		public synchronized void receivePacket() throws IOException{
			DatagramPacket p = null;
			byte[] data= new byte[Multicast.MTU];  
			while(true){
				p = new DatagramPacket(data, data.length);
				mSocket.receive(p);
				byte[] packetData = p.getData();
				Identifier identifier = new Identifier(Multicast.getClientIdentifier(packetData));
				if(!identifier.equals(ID)){
					switch(Multicast.getPacketType(packetData)){
					case HELLO:
						receiveHello(p.getAddress(), p.getPort(), identifier);
						terminal.println("Received Hello Packet from " +identifier.toString() + " " +p.getAddress());
						break;
					case IMAGE_METADATA:
						if(state != CLIENT_STATE.RECEIVING_IMAGE){
							terminal.println("Received Metadata for Image");
							state = CLIENT_STATE.RECEIVING_IMAGE;
						}
					case IMAGE:
						if(state == CLIENT_STATE.RECEIVING_IMAGE){
							terminal.println("Received Image Packet");
							r.receivePacket(packetData);
							r.run();
							sendAckResponse(identifier, Ack.nextExpectedAck(r.getAck()));
							state = CLIENT_STATE.RECEIVING_IMAGE;
						}
						break;
					case ACK:
						if(state == CLIENT_STATE.SENDING_IMAGE){
							senderNodeList.updateAck(Multicast.getClientIdentifier(packetData), new Ack(Multicast.getHeaderData(packetData)));
						}	
						break;
					default:
						break;
					}
				}
			}
		}

		/**
		 * Hello Packet Handler - Creates a new node in the membership list
		 * @param address
		 * @param port
		 * @param identifier
		 */
		public synchronized void receiveHello(InetAddress address, int port, Identifier identifier){
			ClientNode node = new ClientNode(address, port, identifier);
			clientNodeList.add(node);
		}

		public synchronized void sendAckResponse(Identifier ID, Ack ackToSend){
			ClientNode node = clientNodeList.getClientNode(ID.getIdentifier());
			byte[] data = Multicast.constructAckPacket(ID, ackToSend);
			try {
				DatagramPacket packet = new DatagramPacket(data, Multicast.MTU, node.getAddress(), node.getPort());
				dSocket.send(packet);
				terminal.println("Sent ack ("+ackToSend.getAck()[0]+") to address: "+
						node.getAddress().toString()+" port: "+node.getPort());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	} // end Listener 

} // end Client
