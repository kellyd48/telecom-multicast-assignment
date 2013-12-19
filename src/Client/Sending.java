package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import Sender.*;
import tcdIO.*;

public class Sending extends Transmission implements Runnable {
	
	private Sender s;
	protected Terminal terminal;
	private String testingSenderFile = "";
	private DatagramPacket packet;

	public Sending(MulticastSocket mSocket, InetAddress mAddress,
			CLIENT_STATE state, ClientNodeList clientNodeList,
			ClientNodeList senderNodeList, Identifier ID, Terminal terminal, String testingSenderFile) {
		super(mSocket, mAddress, state, clientNodeList, senderNodeList, ID, terminal);
		this.terminal = terminal;
		this.testingSenderFile = testingSenderFile;
		this.s = new Sender(ID);
	}

	@Override
	public void run() {
		while(state != CLIENT_STATE.CLOSED) {
			switch(state) {
				case JOIN_GROUP:
					//sends hello packets
					println("Sender state: "+state.toString());
					sendHello();
					state = CLIENT_STATE.LISTENING;
					println("Sender state: "+state.toString());
					break;
				case LISTENING:
					//waiting for image to send
					//send hello packet at certain intervals
					if(!testingSenderFile.equals("")){ // if image path is empty
						updateSenderNodeList();
						runSender(s);
						state = CLIENT_STATE.SENDING_IMAGE;
						println("Sender state: "+state.toString());
						testingSenderFile = "";
					}
					break;
				case SENDING_IMAGE:
					//sends next image packet
					println("Sender state: "+state.toString());
					if(senderNodeList.checkForAck(Ack.getPrevious(s.getSequence())) || senderNodeList.checkForAck(null)){
						s.resend();
						println("Resending");
					}
					runSender(s);
					println("Sending");
					break;
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
		}
	}
	
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
				packet = new DatagramPacket(packetData, packetData.length, mAddress, Multicast.MCAST_PORT);
				mSocket.send(packet);
				println("Sent packet from Sender.");
			}	
		}
		catch (IOException e) {
			System.err.println("Error for Client ID: " + ID.toString());
			e.printStackTrace();
		}
	} // end runSender
	
	/**
	 *	Updates the senderNodeList to contain the current state of the clientNodeList. 
	 */
	public synchronized void updateSenderNodeList(){
		senderNodeList = new ClientNodeList(clientNodeList);
	}
	
}
