package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import tcdIO.Terminal;
import Client.*;
import Sender.Sender;

/**
 * Sender Thread
 * Sends Packets on the socket, through the sender class
 *
 */
public class Send extends Thread {
	private Sender s;
	private DatagramPacket packet;
	private Terminal terminal;
	private MulticastSocket mSocket;
	private Identifier ID;
	private Client.CLIENT_STATE state;
	private InetAddress mAddress;
	private ClientNodeList clientNodeList;
	private ClientNodeList senderNodeList;
	private String testingSenderFile = "";

	/**
	 * Send Constructor
	 */
	public Send(Identifier ID, Client.CLIENT_STATE state, MulticastSocket mSocket, InetAddress mAddress, 
							Terminal terminal, ClientNodeList clientNodeList, ClientNodeList senderNodeList,
										String testingSenderFile){
		this.terminal = terminal;
		this.mSocket = mSocket;
		this.ID = ID;
		this.state = state;
		this.testingSenderFile = testingSenderFile;
		this.mAddress = mAddress;
		this.clientNodeList = clientNodeList;
		this.senderNodeList = senderNodeList;
		s = new Sender(ID);
	} // end constructor

	@Override
	public void run() {
		while(state != Client.CLIENT_STATE.CLOSED){
			switch(state){
				case JOIN_GROUP:
					//sends hello packets
					println("Sender state: "+state.toString());
					sendHello();
					state = Client.CLIENT_STATE.LISTENING;
					println("Sender state: "+state.toString());
					break;
				case LISTENING:
					//waiting for image to send
					//send hello packet at certain intervals
					if(!testingSenderFile.equals("")){ // if image path is empty
						updateSenderNodeList();
						runSender(s);
						state = Client.CLIENT_STATE.SENDING_IMAGE;
						println("Sender state: "+state.toString());
						testingSenderFile = "";
					}
					break;
				case SENDING_IMAGE:
					//sends next image packet
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
				sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	} // end run

	/**
	 * Send hello packets
	 */
	public synchronized void sendHello(){
		try{
			byte[] helloPacket = Multicast.constructHelloPacket(ID);
			DatagramPacket packet = new DatagramPacket(helloPacket, helloPacket.length, mAddress, Multicast.MCAST_PORT);
			for(int i = 0; i < Multicast.NUMBER_OF_PACKETS_PER_HELLO; i++){
				mSocket.send(packet);
				println("Sent Hello Packet.");
				sleep(Multicast.HELLO_TIME_INTERVAL);
			}
		} catch(IOException e){
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
	
	/**
	 * Special print method needed for printing with the Terminal in while loops/threaded apps.
	 * 
	 * @param message
	 */
	private synchronized void println(String message){
		assert(message != null):"Null String passed to terminal";
		try {
			sleep(20);
			terminal.println(message);
		} catch (InterruptedException e) {
			System.err.println("terminal problem");
		}
	}
} // end Send