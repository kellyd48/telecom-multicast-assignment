package Client;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import Receiver.Receiver;
import Sender.Sender;

public class Client {
	public static final String MCAST_ADDR = "230.0.0.1"; // hardcoded address for the multicast group
	public static final int MCAST_PORT = 9013; // hardcoded port number for the multicast group
	public static final int HELLO_TIME_INTERVAL = 10;
	public static final int NUMBER_OF_PACKETS_PER_HELLO = 2;
	public static enum CLIENT_STATE {JOIN_GROUP, LISTENING, SENDING_IMAGE, RECEIVING_IMAGE};
	
	private MulticastSocket mSocket;
	private InetAddress address;
	private CLIENT_STATE state;
	private ClientNodeList clientNodeList;
	private Identifier ID;
	private Send s;
	private Listener l;
	
	/**
	 * Client Constructor
	 */
	public Client() {
		ID = new Identifier();
		state = CLIENT_STATE.JOIN_GROUP;
		clientNodeList = new ClientNodeList(ID);
		// create send and listener objects
		s = new Send();
		l = new Listener();
		try {
			address = InetAddress.getByName(MCAST_ADDR);
			mSocket = new MulticastSocket(MCAST_PORT);
			mSocket.joinGroup(address);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	} // end constructor
	
	public void run(){
		try {
			/* Start the threads for sending and receiving*/
			s.start();
			l.start();	
		}
		catch(Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public static void main(String[] args){
		Client client1 = new Client();
		client1.run();
		Client client2 = new Client();
		client2.run();
	}
	
	/**
	 * Sender Thread
	 * Sends Packets on the socket, through the sender class
	 *
	 */
	private class Send extends Thread {
		private Sender s;
		private DatagramPacket packet;
		
		public Send(){
			s = new Sender();
		}
		
		@Override
		public void run() {
			System.out.println("Client ID: " + ID.toString());
			switch(state){
			case JOIN_GROUP:
				sendHello();
				break;
			case LISTENING:
				break;
			case SENDING_IMAGE:
				runSender(s);
			case RECEIVING_IMAGE:
				break;
			default:
				break;
			}
			runSender(s);
		} // end run
		
		public synchronized void sendHello(){
			try{
				byte[] helloPacket = Multicast.constructHelloPacket(ID);
				DatagramPacket packet = new DatagramPacket(helloPacket, helloPacket.length, address, MCAST_PORT);
				for(int i = 0; i < NUMBER_OF_PACKETS_PER_HELLO; i++){
					mSocket.send(packet);
					System.out.println("Client ID: " + ID.toString() + " Sent Hello Packet.");
					sleep(HELLO_TIME_INTERVAL);
				}
			}catch(IOException e){
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		public synchronized void runSender(Sender s){
			try {
				s.run();
				if(s.hasPacketToSend()){
					byte[] packetData = s.packetToSend();
					packet = new DatagramPacket(packetData, packetData.length, address, MCAST_PORT);
					mSocket.send(packet);
				}	
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	} // end Send
	
	/**
	 * Listener thread
	 * Receives Packets from the nodes and forwards them to the receiver class
	 *
	 */
	private class Listener extends Thread {
		Receiver r = new Receiver();
		@Override
		public void run() {
			System.out.println("Client receiver with ID: " + ID.toString());
			receivePacket();			
		} // end run
		
		public synchronized void receivePacket(){
			try {
				DatagramPacket p = null;
				byte[] data= new byte[Multicast.MTU];  
				for(;;) {
					p = new DatagramPacket(data, data.length);
					mSocket.receive(p);
					byte[] packetData = p.getData();
					switch(Multicast.getPacketType(packetData)){
					case HELLO:
						Identifier identifier = new Identifier(Multicast.getClientIdentifier(packetData));
						receiveHello(p.getAddress(), p.getPort(), identifier);
						break;
					case IMAGE:
					case IMAGE_METADATA:
						r.receivePacket(p.getData());
						break;
					default:
						break;
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public void receiveHello(InetAddress address, int port, Identifier identifier){
			ClientNode node = new ClientNode(address, port, identifier);
			clientNodeList.add(node);
		}
		
	} // end Listener 

} // end Client
