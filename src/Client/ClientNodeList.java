package Client;

import java.util.ArrayList;
import java.util.HashMap;

public class ClientNodeList {
	private HashMap nodeList;
	
	public ClientNodeList(){
		nodeList = new HashMap<ClientNode, Integer>();
	}
	
	public boolean clientNodeLookup(ClientNode node){
		//search for ClientNode
	}
	
	public void addNode(ClientNode node, Integer key){
		nodeList.put(node, key);
	}
	
	private Integer generateKey(ClientNode node){
		int key = node.getPort() + node.getAddress().getHostAddress();
	}
	
}
