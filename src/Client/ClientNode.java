package Client;

import java.net.InetAddress;

public class ClientNode {
	private InetAddress address;

	public InetAddress getAddress() {
		return address;
	}

	public ClientNode(InetAddress address){
		this.address = address;
	}
}
