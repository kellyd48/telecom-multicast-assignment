package Client;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import Receiver.Receiver;
import Sender.Sender;

public class Client {
	public static final String MCAST_ADDR = "230.0.0.1"; // hardcoded address for the multicast group
	public static final int MCAST_PORT = 9013; // hardcoded port number for the multicast group
	public static final int HELLO_TIME_INTERVAL = 10;
	public static final int NUMBER_OF_PACKETS_PER_HELLO = 2;
	public static enum CLIENT_STATE {JOIN_GROUP, LISTENING, SENDING_IMAGE, RECEIVING_IMAGE};
	
	private MulticastSocket socket;
	private InetAddress address;
	private CLIENT_STATE state;
	
	/**
	 * Client Constructor
	 */
	public Client() {
		state = CLIENT_STATE.JOIN_GROUP;
		try {
			address = InetAddress.getByName(MCAST_ADDR);
			socket = new MulticastSocket(MCAST_PORT);
			socket.joinGroup(address);
			
			/* Create and start the threads */
			Send s = new Send();
			s.start();
			Listener l = new Listener();
			l.start();	
		}
		catch(Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	} // end constructor
	
	/**
	 * Sender Thread
	 * Sends Packets on the socket, through the sender class
	 *
	 */
	private class Send extends Thread {
		Sender s = new Sender();
		DatagramPacket packet;
		@Override
		public void run() {
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
		
		public void sendHello(){
			try{
				byte[] helloPacket = Multicast.constructHelloPacket();
				DatagramPacket packet = new DatagramPacket(helloPacket, helloPacket.length, address, MCAST_PORT);
				for(int i = 0; i < NUMBER_OF_PACKETS_PER_HELLO; i++){
					socket.send(packet);
					sleep(HELLO_TIME_INTERVAL);
				}
			}catch(IOException e){
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		public void runSender(Sender s){
			try {
				s.run();
				if(s.hasPacketToSend()){
					byte[] packetData = s.packetToSend();
					packet = new DatagramPacket(packetData, packetData.length, address, MCAST_PORT);
					socket.send(packet);
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
			try {
				DatagramPacket p = null;
				byte[] data= new byte[Multicast.MTU];  
				for(;;) {
					p = new DatagramPacket(data, data.length);
					socket.receive(p);
					r.receivePacket(p.getData());
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		} // end run
	} // end Listener 

} // end Client
