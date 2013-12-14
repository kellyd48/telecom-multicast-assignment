package Client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Interface to manage a group of ClientNode members on a multicast network.
 *
 */
public class ClientNodeList {
	private HashMap<Integer, ClientNode> nodeList;
	
	public ClientNodeList(){
		nodeList = new HashMap<Integer, ClientNode>();
	}
	
	public boolean clientNodeLookup(ClientNode node){
		/*
		 * Returns boolean for whether a ClientNode is contained in the list of nodes.
		 */
		return nodeList.containsKey(generateKey(node));
	}
	
	public void add(ClientNode node){
		/*
		 * Adds a ClientNode to the list of nodes.
		 * Does nothing if node already present.
		 */
		if(!clientNodeLookup(node))
			nodeList.put(generateKey(node), node);
	}
	
	public void remove(ClientNode node){
		/*
		 * Removes a ClientNode from a list of nodes.
		 * Does nothing if node is not present.
		 */
		if(clientNodeLookup(node))
			nodeList.remove(generateKey(node));
	}
	
	public ClientNode get(int index){
		/*
		 * Returns the node at a certain index
		 */
		Set nodes = nodeList.entrySet();
		Iterator iterator = nodes.iterator();
		for(int i = 0; iterator.hasNext(); i++){
			if(i == index){
				Map.Entry<Integer, ClientNode> node = (Map.Entry<Integer, ClientNode>)iterator.next();
				return (ClientNode)node.getValue();
			}
		}
		return null;
	}
	
	public int size(){
		/*
		 * Returns the size of the list
		 */
		return nodeList.size();
	}
	
	private Integer generateKey(ClientNode node){
		/*
		 * Generates a key to map to a value.
		 */
		byte[] address = node.getAddress().getAddress();
		return address[2] + address[3] + node.getPort();
	}
	
	public String toString(){
		/*
		 * Returns string representation of object.
		 */
		String toString = "";
		Set nodes = nodeList.entrySet();
		Iterator iterator = nodes.iterator();
		while(iterator.hasNext()){
			Map.Entry<Integer, ClientNode> node = (Map.Entry<Integer, ClientNode>)iterator.next();
			toString += "Key: " + node.getKey() + " Client: " + node.getValue().toString() + "\n";
		}
		return toString;
	}
}
