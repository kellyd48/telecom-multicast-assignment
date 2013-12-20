package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import Client.Client.ClientState;
import Sender.*;
import tcdIO.*;

public class Sending extends Transmission implements Runnable {
	
	private Sender s;
	private String testingSenderFile = "";
	private DatagramPacket packet;

	/**
	 * Sending Constructor
	 * @param mSocket
	 * @param mAddress
	 * @param state
	 * @param clientNodeList
	 * @param senderNodeList
	 * @param ID
	 * @param terminal
	 * @param testingSenderFile
	 */
	public Sending (MulticastSocket mSocket, InetAddress mAddress,
			ClientState state, ClientNodeList clientNodeList,
			ClientNodeList senderNodeList, Identifier ID, Terminal terminal, String testingSenderFile) {
		super(mSocket, mAddress, state, clientNodeList, senderNodeList, ID, terminal);
		this.testingSenderFile = testingSenderFile;
		this.s = new Sender(ID);
	} // end Sending constructor

	/**
	 * Run method - State Machine for sending packets
	 */
	@Override
	public void run() {
		while(!state.equals(ClientState.State.CLOSED)) {
			switch(state.get()) {
				case JOIN_GROUP: {
					//sends hello packets
					println("Sender state: "+state.toString());
					sendHello();
					state.set(ClientState.State.LISTENING);
					println("Sender state: " + state.toString());
					break;
				} // end JOIN_GROUP case
				case LISTENING: {
					//waiting for image to send
					//send hello packet at certain intervals
					if(!testingSenderFile.equals("")){ // if image path is empty
						updateSenderNodeList();
						runSender(s);
						state.set(ClientState.State.SENDING_IMAGE);
						println("Sender state: "+state.toString());
						testingSenderFile = "";
					}
					break;
				} // end LISTENING case
				case SENDING_IMAGE: {
					//sends next image packet
					println("Sender state: "+state.toString());
					if(senderNodeList.checkAllAcks(s.getSequence())){
						println("Sending");
					}else{
						s.resend();
						println("Resending");
					}
					runSender(s);
					break;
				} // end SENDING_IMAGE case
				case RECEIVING_IMAGE:
					//does nothing
					break;
				default:
					break;
			}
			//sleep for a bit to see whats happening
			try {
				Thread.sleep(1000);
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		} // end while
	} // end run method
	
	/**
	 * Send hello packets
	 */
	public synchronized void sendHello() {
		try {
			byte[] helloPacket = Multicast.constructHelloPacket(ID);
			packet = new DatagramPacket(helloPacket, helloPacket.length, mAddress, Multicast.MCAST_PORT);
			for(int i = 0; i < Multicast.NUMBER_OF_PACKETS_PER_HELLO; i++) {
				mSocket.send(packet);
				println("Sent Hello Packet.");
				Thread.sleep(Multicast.HELLO_TIME_INTERVAL);
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	} // end sendHello method
	
	/**
	 * Sends image packets
	 * @param s
	 */
	public synchronized void runSender(Sender s){
		try {
			s.run(testingSenderFile);
			if(s.hasPacketToSend()){
				byte[] packetData = s.packetToSend();
				packet = new DatagramPacket(packetData, packetData.length, mAddress, Multicast.MCAST_PORT);
				mSocket.send(packet);
				println("Sent packet from Sender.");
			}	
		}
		catch (IOException e) {
			System.err.println("Error for Client ID: " + ID.toString());
			e.printStackTrace();
		}
	} // end runSender method
	
	/**
	 *	Updates the senderNodeList to contain the current state of the clientNodeList. 
	 */
	public synchronized void updateSenderNodeList(){
		senderNodeList = new ClientNodeList(clientNodeList);
	} // end updateSenderNodeList method
	
} // end Sending class
