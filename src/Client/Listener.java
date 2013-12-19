package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import tcdIO.Terminal;
import Client.Client.CLIENT_STATE;
import Receiver.Receiver;

/**
 * Listener thread
 * Receives Packets from the nodes and forwards them to the receiver class
 *
 */
public class Listener extends Thread {
	private Receiver r;
	private Identifier ID;
	private CLIENT_STATE state;
	private MulticastSocket mSocket;
	private InetAddress mAddress;
	private Terminal terminal;
	private ClientNodeList clientNodeList;
	private ClientNodeList senderNodeList;

	/**
	 * Listener Constructor
	 */
	public Listener(Identifier ID, Client.CLIENT_STATE state, MulticastSocket mSocket, InetAddress mAddress,
			Terminal terminal, ClientNodeList clientNodeList, ClientNodeList senderNodeList) {
		this.ID = ID;
		this.mSocket = mSocket;
		this.mAddress = mAddress;
		this.terminal = terminal;
		this.clientNodeList = clientNodeList;
		this.senderNodeList = senderNodeList;
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
					ClientNode node = new ClientNode(mAddress, identifier);
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
	 * Sends an ACK over the multicast socket.
	 * @param ID
	 * @param ackToSend
	 */
	public synchronized void sendAckResponse(Identifier ID, Ack ackToSend) {
		assert(ID != null && ackToSend != null);
		byte[] data = Multicast.constructAckPacket(ID, ackToSend);
		try {
			DatagramPacket packet = new DatagramPacket(data, Multicast.MTU, mAddress, Multicast.MCAST_PORT);
			mSocket.send(packet);
			println("Sent ack ("+ackToSend.getAck()[0]+")");
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	} // end sendAckResponse

	/**
	 * Special print method needed for printing with the Terminal in while loops/threaded apps.
	 * 
	 * @param message
	 */
	private synchronized void println(String message){
		try {
			sleep(20);
			terminal.println(message);
		} catch (InterruptedException e) {
			System.err.println("terminal problem");
		}
	}
} // end Listener 