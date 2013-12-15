package Client;

import java.net.InetAddress;

/**
 * Represents a client that is a member of the multicast group.
 *
 */
public class ClientNode {
	private InetAddress address;
	private int port;
	private Identifier id;

	public ClientNode(InetAddress address, int port, Identifier id){
		this.address = address;
		this.port = port;
		this.id = new Identifier(id);
	}
	
	public InetAddress getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}
	
	public Identifier getID(){
		return id;
	}
	
	public String toString(){
		return "Client Node ID: " + id.toString() + " Address: " + address.getHostAddress() + " Port: " + port;
	}
}
