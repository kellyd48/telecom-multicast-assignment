package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import Client.Client.ClientState;
import GUI.GraphicalUserInterface;
import Sender.*;

public class Sending extends Transmission implements Runnable {

	private Sender s;
	private DatagramPacket packet;

	/**
	 * Sending Constructor
	 * @param mSocket
	 * @param mAddress
	 * @param state
	 * @param clientNodeList
	 * @param senderNodeList
	 * @param ID
	 * @param gui
	 */
	public Sending (MulticastSocket mSocket, InetAddress mAddress,
			ClientState state, ClientNodeList clientNodeList,
			ClientNodeList senderNodeList, Identifier ID, GraphicalUserInterface gui) {
		super(mSocket, mAddress, state, clientNodeList, senderNodeList, ID, gui);
		this.s = new Sender(ID);
	} // end Sending constructor

	/**
	 * Run method - State Machine for sending packets
	 */
	@Override
	public void run() {
		long lastTime = 0;
		while(!state.equals(ClientState.State.CLOSED)) {
			//ouput
			updateGUI();
			switch(state.get()) {
				case JOIN_GROUP: {
					//sends hello packets
					//ouput
					updateGUI();
					System.out.println("Sender state: "+state.toString());
					//send hello packet
					sendHello();
					state.set(ClientState.State.LISTENING);
					//ouput
					updateGUI();
					System.out.println("Sender state: " + state.toString());
					lastTime = System.currentTimeMillis();
					break;
				} // end JOIN_GROUP case
				case LISTENING: {
					//waiting for image to send
					//send hello packet at certain intervals
					if(checkForImage()){
						setImageToSend();
						updateSenderNodeList();
						runSender();
						state.set(ClientState.State.SENDING_IMAGE);
						//ouput
						updateGUI();
						System.out.println("Sender state: "+state.toString());
					}
					else{
						long now = System.currentTimeMillis();
						if(now - lastTime >= Multicast.HELLO_TIME_INTERVAL){
							this.state.set(ClientState.State.JOIN_GROUP);
							System.out.println("Hello message time interval reached.");
						}
					}
					break;
				} // end LISTENING case
				case SENDING_IMAGE: {
					//sends next image packet
					//ouput
					updateGUI();
					System.out.println("Sender state: "+state.toString());
					if(senderNodeList.checkAllAcks(s.getSequence())){
						if(s.getState() == Sender.SENDER_STATE.COMPLETED){
							this.state.set(ClientState.State.JOIN_GROUP);
						}else{
							System.out.println("Sending");
						}
					}else{
						s.resend();
						System.out.println("Resending");
					}
					runSender();
					//update progress
					setProgress(s.getPercentageProgress());
					break;
				} // end SENDING_IMAGE case
				case RECEIVING_IMAGE:
					//does nothing
					break;
				default:
					break;
			}
			//sleep for a bit to see whats happening
			// try {
			// 	Thread.sleep(1000);
			// } 
			// catch (InterruptedException e) {
			// 	e.printStackTrace();
			// }
		} // end while
	} // end run method

	/**
	 * Gets shared image file from the gui and passes it to the sender.
	 */
	public void setImageToSend(){
		s.getImageFromFile(gui.getSharedImageFile());
		gui.setState(GUI.GraphicalUserInterface.DEFAULT_STATE);
	}
	
	/**
	 * Checks the gui if there is an image to send.
	 * @return Boolean
	 */
	private boolean checkForImage(){
		return (gui.getState() == GUI.GraphicalUserInterface.IMAGE_SHARED_STATE);
	}
	
//	private boolean checkForLocalImage(){
//		//check for image in local directory
//		//if present put it in the byte array of dataToSend.
//		File imageFile = new File(Client.IMAGE_FILENAME);
//		if(imageFile.exists() && s.getState() == Sender.SENDER_STATE.WAIT_FOR_IMAGE){
//			s.getImageFromFile(Client.IMAGE_FILENAME);
//			File renamedFile;
//			int i = 0;
//			do{
//				String append = (i == 0 ? "":"_"+i);
//				renamedFile = new File(this.ID.toString() + append + ".jpg");
//				i++;
//			}while(renamedFile.isFile());
//			imageFile.renameTo(renamedFile);
//			return true;
//		}
//		return false;
//	}

	/**
	 * Send hello packets
	 */
	public synchronized void sendHello() {
		try {
			byte[] helloPacket = Multicast.constructHelloPacket(ID);
			packet = new DatagramPacket(helloPacket, helloPacket.length, mAddress, Multicast.MCAST_PORT);
			for(int i = 0; i < Multicast.NUMBER_OF_PACKETS_PER_HELLO; i++) {
				mSocket.send(packet);
				System.out.println("Sent Hello Packet.");
				Thread.sleep(Multicast.HELLO_PACKET_TIME_INTERVAL);
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
	public synchronized void runSender(){
		try {
			s.run();
			if(s.hasPacketToSend()){
				byte[] packetData = s.packetToSend();
				packet = new DatagramPacket(packetData, packetData.length, mAddress, Multicast.MCAST_PORT);
				mSocket.send(packet);
				System.out.println("Sent packet from Sender.");
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
