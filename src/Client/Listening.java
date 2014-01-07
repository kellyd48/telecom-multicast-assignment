package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import Client.Client.ClientState;
import GUI.GraphicalUserInterface;
import Receiver.*;

public class Listening extends Transmission implements Runnable {

	private Receiver r;
	private Identifier receivingFrom;
	
	/**
	 * Listening Constructor
	 * @param mSocket
	 * @param mAddress
	 * @param state
	 * @param clientNodeList
	 * @param senderNodeList
	 * @param ID
	 * @param gui
	 */
	public Listening(MulticastSocket mSocket, InetAddress mAddress,
			ClientState state, ClientNodeList clientNodeList,
			ClientNodeList senderNodeList, Identifier ID, GraphicalUserInterface gui) {
		super(mSocket, mAddress, state, clientNodeList, senderNodeList, ID, gui);
		this.r = new Receiver(ID);
	} // end Listening constructor

	/**
	 * Run method listens for packets on the socket and processes them
	 */
	@Override
	public void run() {
		updateGUI();
		try {
			DatagramPacket p = null;
			byte[] data= new byte[Multicast.MTU];  
			while(true) {
				p = new DatagramPacket(data, data.length);
				// listen for packet 
				mSocket.receive(p);	
				// process it
				receivePacket(p);
				System.out.println("State: (Listener)"+state.toString());
			} // end while
		}
		catch(IOException e) {
			System.err.println("Socket closed prematurely");
		}
	} // end run method
	
	/**
	 * Processes a packet
	 * @param p
	 * @throws IOException
	 */
	public synchronized void receivePacket(DatagramPacket p) throws IOException {
		assert(p != null) : "Processed packet is null";
		byte[] packetData = p.getData();
		Identifier identifier = new Identifier(Multicast.getClientIdentifier(packetData));
		//checks if the packet received originated from the local client
		if(!identifier.equals(ID)) {
			switch(Multicast.getPacketType(packetData)) {
				case HELLO: {
					ClientNode node = new ClientNode(mAddress, identifier);
					clientNodeList.add(node);
					System.out.println("Received Hello Packet from " +identifier.toString() + " " +p.getAddress());
					break;
				} // end HELLO case
				case IMAGE_METADATA: {
					if(!state.equals(ClientState.State.RECEIVING_IMAGE) && receivingFrom == null){
						System.out.println("Received Metadata for Image");
						receivingFrom = new Identifier(identifier);
						state.set(ClientState.State.RECEIVING_IMAGE);
					}
				} // end IMAGE_METADATA case
				case IMAGE: {
					// Checks that the packet originated from the expected sender.
					if(receivingFrom != null && identifier.equals(receivingFrom)){
						System.out.println("Received Image Packet");
						r.run(packetData);
						sendAckResponse(Ack.nextExpectedAck(r.getAck()));
						if(r.getState() == Receiver.RECEIVER_STATE.FINISHED_RECEIVING){
							receivingFrom = null;
							this.state.set(ClientState.State.JOIN_GROUP);
						}
					}
					//update amount of progress
					setProgress(r.getPercentageProgress());
					break;
				} // end IMAGE case
				case ACK: {
					System.out.println("Received Ack Packet.");
					if(state.equals(ClientState.State.SENDING_IMAGE)){
						senderNodeList.updateAck(Multicast.getClientIdentifier(packetData), 
								new Ack(Multicast.getHeaderData(packetData)));
						System.out.println(senderNodeList.toString());
					}	
					break;
				} // end ACK case
				default: {
					break;
				}
			} // end switch
		} // end if
		else {
			// ignore packets received by this node sent to itself over multicast
		}
	} // end receivePacket method

	/**
	 * Sends an ACK over the multicast socket.
	 * @param ID
	 * @param ackToSend
	 */
	public synchronized void sendAckResponse(Ack ackToSend) {
		assert(ackToSend != null) : "Sending ack Response error";
		byte[] data = Multicast.constructAckPacket(ID, ackToSend);
		try {
			DatagramPacket packet = new DatagramPacket(data, Multicast.MTU, mAddress, Multicast.MCAST_PORT);
			mSocket.send(packet);
			System.out.println("Sent ack ("+ackToSend.toString()+")");
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	} // end sendAckResponse method
	
} // end Listening class
