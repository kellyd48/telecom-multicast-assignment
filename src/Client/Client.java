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
	}
	
	/**
	 * Sender Thread
	 * Sends Packets on the socket, through the sender class
	 *
	 */
	private class Send extends Thread {
		Sender s = new Sender();
		
		@Override
		public void run() {
			try {
				if(s.hasPacketToSend())
					socket.send(s.packetToSend());
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Listener thread
	 * Receives Packets from the nodes and forwards them to the receiver class
	 *
	 */
	private class Listener extends Thread {
		Receiver r = new Receiver();
		
		DatagramPacket p = new DatagramPacket(address);
		@Override
		public void run() {
			for(;;) {
				socket.receive(p);
				r.receivePacket(packet);
			}
		}
	}

}
