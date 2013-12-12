package Client;

import java.net.InetAddress;

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
	
	
}
