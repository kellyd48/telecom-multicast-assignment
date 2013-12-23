package Client;

import java.net.InetAddress;
import java.net.MulticastSocket;

import Client.Transmission.*;
import tcdIO.*;

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
    }

	private MulticastSocket mSocket;
	private InetAddress mAddress;
	private ClientState state;
	private ClientNodeList clientNodeList;
	private ClientNodeList senderNodeList;
	private Identifier ID;
	private Terminal terminal;
	private String testingSenderFile;
	
	public static void main(String[] args) {
		new Thread(new Client()).start();
		new Thread(new Client()).start();
		new Thread(new Client()).start();
	}
	
	/**
	 * Client Constructor
	 * @param testingSenderFile
	 */
	public Client() {
		ID = new Identifier();
		state = new ClientState();
		terminal = new Terminal("Client ID: " + ID.toString());
		clientNodeList = new ClientNodeList(ID,terminal);
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
				clientNodeList, ID, terminal)).start();
		new Thread(new Listening(mSocket, mAddress, state, clientNodeList, 
				clientNodeList, ID, terminal)).start();	
	} // end run method

} // end Client abstract class
