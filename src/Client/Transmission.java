package Client;
import java.net.InetAddress;
import java.net.MulticastSocket;
import GUI.GraphicalUserInterface;

import Client.Client.ClientState;

public abstract class Transmission {
	
	public static enum CLIENT_STATE {JOIN_GROUP, LISTENING, SENDING_IMAGE, RECEIVING_IMAGE, CLOSED};
	
	protected MulticastSocket mSocket;
	protected InetAddress mAddress;
	protected ClientState state;
	protected ClientNodeList clientNodeList;
	protected ClientNodeList senderNodeList;
	protected Identifier ID;
	private static int progress = 0;
	//gui
	private GraphicalUserInterface gui;
	
	/**
	 * Transmission Constructor
	 * @param mSocket
	 * @param mAddress
	 * @param state
	 * @param clientNodeList
	 * @param senderNodeList
	 * @param ID
	 */
	public Transmission(MulticastSocket mSocket, InetAddress mAddress, ClientState state,
			ClientNodeList clientNodeList, ClientNodeList senderNodeList, Identifier ID) {
		this.mSocket = mSocket;
		this.mAddress = mAddress;
		this.state = state;
		this.clientNodeList = clientNodeList;
		this.senderNodeList = senderNodeList;
		this.ID = ID;
		//gui
		gui = new GraphicalUserInterface(ID.toString());
	} // end Transmission method
	
	/**
	 * @return Returns the amount of progress.
	 */
	public int getProgress(){
		return progress;
	}
	
	/**
	 * Sets the progress variable (should be a percentage.)
	 * @param progress
	 */
	public synchronized void setProgress(int progress){
		Transmission.progress = progress;
	}

	public synchronized void updateGUI(){
		gui.setProgress(ClientState.getPercentageProgress(state, getProgress()));
		gui.setMessage(ClientState.getProgressMessage(state));
	}
	
	/**
	 * Prints a message to the terminal
	 * 20 millisecond sleep, to allow threads to print to terminal properly
	 * @param message
	 */
	public synchronized void println(String message) {
		try {
			Thread.sleep(20);
			//terminal.println(message);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	} // end println method
} // end Transmission class
