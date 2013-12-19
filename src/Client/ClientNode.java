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
	}
	
	public void setAck(Ack ack){
		this.ack = new Ack(ack);
	}
	
	public Ack getAck(){
		return ack;
	}
	
	/**
	 * @return InetAddress of Client.
	 */
	public InetAddress getAddress() {
		return address;
	}
	
	/**
	 * @return Identifier of client.
	 */
	public Identifier getID(){
		return id;
	}
	
	public void resetAck(){
		ack = null;
	}

	public String toString(){
		return "Client Node ID: " + id.toString() + " Address: " + address.getHostAddress();
	}
}
