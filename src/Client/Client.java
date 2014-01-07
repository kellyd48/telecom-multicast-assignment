package Client;

import java.net.InetAddress;
import java.net.MulticastSocket;

public class Client implements Runnable {

	public static final String IMAGE_FILENAME = "image.jpg";

	/**
	 * Class to represent the state of the client.
	 *
	 */
	public static class ClientState{
		public enum State {JOIN_GROUP, LISTENING, SENDING_IMAGE, RECEIVING_IMAGE, CLOSED};
		public State state;

		public ClientState(){
			this(State.JOIN_GROUP);
		}

		public ClientState(State state){
			this.state = state;
		}

		public boolean equals(State state){
			return (this.state == state);
		}

		public State get(){
			return state;
		}

		public void set(State state){
			this.state = state;
		}

		public String toString(){
			return state.toString();        
		}

		/**
		 * @param state
		 * @param transmission
		 * @return Returns progress as a percentage based on the current state of the program.
		 */
		 public static int getPercentageProgress(ClientState state, int imageProgress){
			switch(state.get()){
				case JOIN_GROUP:
					return 20;
				case LISTENING:
					return 40;
				case SENDING_IMAGE:
				case RECEIVING_IMAGE:
					return imageProgress;
				case CLOSED:
					return 100;
				default:
					return 0;
			}
		 }

		 /**
		  * @param state
		  * @param transmission
		  * @return Returns a String indicating the current progress of the program.
		  */
		 public static String getProgressMessage(ClientState state){
			 switch(state.get()){
				 case JOIN_GROUP:
					 return "Joining local Snapchat group...";
				 case LISTENING:
					 return "Listening for other snapchat users...";
				 case SENDING_IMAGE:
					 return "Sending Image";
				 case RECEIVING_IMAGE:
					 return "Receiving Image";
				 case CLOSED:
					 return "Closed";
				 default:
					 return "";
			 }
		 }
	}

	private MulticastSocket mSocket;
	private InetAddress mAddress;
	private ClientState state;
	private ClientNodeList clientNodeList;
	@SuppressWarnings("unused")
	private ClientNodeList senderNodeList;
	private Identifier ID;

	public static void main(String[] args) {
		new Thread(new Client()).start();
	}

	/**
	 * Client Constructor
	 * @param testingSenderFile
	 */
	public Client() {
		ID = new Identifier();
		state = new ClientState();
		clientNodeList = new ClientNodeList(ID);
		try {
			mAddress = InetAddress.getByName(Multicast.MCAST_ADDR);
			mSocket = new MulticastSocket(Multicast.MCAST_PORT);
			mSocket.joinGroup(mAddress);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	} // end Client constructor

	/**
	 * Run method starts listener and sender threads
	 */
	@Override
	public void run() {
		// create and start send and listener threads
		new Thread(new Sending(mSocket,mAddress, state, clientNodeList, 
				clientNodeList, ID)).start();
		new Thread(new Listening(mSocket, mAddress, state, clientNodeList, 
				clientNodeList, ID)).start();	
	} // end run method

} // end Client abstract class
