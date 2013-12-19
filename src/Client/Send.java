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
	private String testingSenderFile = "";

	/**
	 * Send Constructor
	 */
	public Send(Identifier ID, Client.CLIENT_STATE state, MulticastSocket mSocket, InetAddress mAddress, Terminal terminal, String testingSenderFile){
		this.terminal = terminal;
		this.mSocket = mSocket;
		this.ID = ID;
		this.state = state;
		this.testingSenderFile = testingSenderFile;
		this.mAddress = mAddress;
		s = new Sender(ID);
	} // end constructor

	@Override
	public void run() {
		while(state != Client.CLIENT_STATE.CLOSED){
			switch(state){
				case JOIN_GROUP:
					println("Sender state: "+state.toString());
					sendHello();
					state = Client.CLIENT_STATE.LISTENING;
					println("Sender state: "+state.toString());
					break;
				case LISTENING:
					if(!testingSenderFile.equals("")){ // if image path is empty
						//runSender(s);
						state = Client.CLIENT_STATE.SENDING_IMAGE;
						println("Sender state: "+state.toString());
						testingSenderFile = "";
					}
					break;
				case SENDING_IMAGE:
					//					if(senderNodeList.checkForAck(Ack.getPrevious(s.getSequence())))
					//						s.resend();
					runSender(s);
					break;
				case RECEIVING_IMAGE:
					break;
				default:
					break;
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

	private synchronized void println(String message){
		try {
			sleep(20);
			terminal.println(message);
		} catch (InterruptedException e) {
			System.err.println("terminal problem");
		}
	}
} // end Send