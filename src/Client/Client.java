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
	
	private MulticastSocket socket;
	private InetAddress address;
	
	/**
	 * Client Constructor
	 */
	public Client() {
		try {
			address = InetAddress.getByName(MCAST_ADDR);
			socket = new MulticastSocket(MCAST_PORT);
			socket.joinGroup(address);
			
			/* Create and start the threads */
			Send t1 = new Send();
			t1.start();
			Listener t2 = new Listener();
			t2.start();	
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
		} // end run
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
