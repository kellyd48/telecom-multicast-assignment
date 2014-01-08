package Client;

import java.net.InetAddress;
import java.net.MulticastSocket;

import Client.Client.ClientState;
import GUI.GraphicalUserInterface;

public abstract class Transmission {

	public static enum CLIENT_STATE {JOIN_GROUP, LISTENING, SENDING_IMAGE, RECEIVING_IMAGE, CLOSED};
	public static final int GUI_UPDATE_INTERVAL = 1000;

	protected MulticastSocket mSocket;
	protected InetAddress mAddress;
	protected ClientState state;
	protected ClientNodeList clientNodeList;
	protected ClientNodeList senderNodeList;
	protected Identifier ID;
	private static int progress = 0;
	//gui
	private GraphicalUserInterface gui;
	private long lastTimeUpdated = 0;

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
			ClientNodeList clientNodeList, ClientNodeList senderNodeList, Identifier ID, GraphicalUserInterface gui) {
		this.mSocket = mSocket;
		this.mAddress = mAddress;
		this.state = state;
		this.clientNodeList = clientNodeList;
		this.senderNodeList = senderNodeList;
		this.ID = ID;
		this.gui = gui;
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
	
	public synchronized String getImageToSend(){
		
	}

	public synchronized void updateGUI(){
		//Interval put in place to avoid some weird thread problems I don't understand.
		long now = System.currentTimeMillis();
		long timeSinceLastUpdate = now - lastTimeUpdated;
		if(timeSinceLastUpdate >= GUI_UPDATE_INTERVAL){
			try {
				String msg = ClientState.getProgressMessage(state);
				int progress = ClientState.getPercentageProgress(state, getProgress());
				Thread.sleep(20);
				gui.setProgress(progress);
				Thread.sleep(20);
				gui.setMessage(msg);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			lastTimeUpdated = now;
		}
	}
} // end Transmission class
