package Client;

import java.net.InetAddress;

/**
 * Represents a client that is a member of the multicast group.
 *
 */
public class ClientNode {
	private InetAddress address;
	private int port;

	public ClientNode(InetAddress address, int port){
		this.address = address;
		this.port = port;
	}
	
	public InetAddress getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}
	
	public String toString(){
		return "Address: " + address.getHostAddress() + " Port: " + port;
	}
}
