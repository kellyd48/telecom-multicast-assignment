package Client;

import java.net.InetAddress;

/**
 * Represents a client that is a member of the multicast group.
 *
 */
public class ClientNode {
	private InetAddress address;
	private Identifier id;
	private Ack ack;

	/**
	 * Constructor for Client.
	 * 
	 * @param address
	 * @param port
	 * @param id
	 */
	public ClientNode(InetAddress address, Identifier id){
		this.address = address;
		this.id = new Identifier(id);
		ack = null;
	} // end ClientNode constructor
	
	/**
	 * set and ack
	 * @param ack
	 */
	public void setAck(Ack ack){
		this.ack = new Ack(ack);
	} // end setAck method
	
	/**
	 * returns the ack
	 * @return
	 */
	public Ack getAck(){
		return ack;
	} // end getAck method
	
	/**
	 * @return InetAddress of Client.
	 */
	public InetAddress getAddress() {
		return address;
	} // end getAddress method
	
	/**
	 * @return Identifier of client.
	 */
	public Identifier getID(){
		return id;
	} // end getID method
	
	public void resetAck(){
		ack = null;
	} // end resetAck method

	public String toString(){
		return "Client Node ID: " + id.toString() + " Address: " + address.getHostAddress() + 
					(ack != null ? "Ack: "+ack.toString():"");
	} // end toString method
} // end ClientNode class
