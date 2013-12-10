package Client;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import Receiver.Receiver;

public class Client {
	
	public Client() {
		/* Create and start the threads */
		Sender s = new Sender();
		s.start();
		Listener l = new Listener();
		l.start();
		
	}
	
	private class Sender extends Thread {
		Sender s = new Sender();
		
		@Override
		public void run() {
			s.sendPacket();
		}
	}
	
	
	private class Listener extends Thread {
		Receiver r = new Receiver();
		
		DatagramPacket p = new DatagramPacket(address);
		@Override
		public void run() {
			
			socket.receive(p);
			r.receivePacket(packet);
			
		}
	}

}
