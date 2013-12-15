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
	//Don't add this ID because it's the local client ID
	private Identifier myID;
	
	public ClientNodeList(Identifier ID){
		nodeList = new HashMap<Integer, ClientNode>();
		myID = new Identifier(ID);
	}
	
	public boolean clientNodeLookup(ClientNode node){
		/*
		 * Returns boolean for whether a ClientNode is contained in the list of nodes.
		 */
		return nodeList.containsKey(node.getID().getIdentifier());
	}
	
	public void add(ClientNode node){
		/*
		 * Adds a ClientNode to the list of nodes.
		 * Does nothing if node already present.
		 */
		if(!clientNodeLookup(node) && !myID.equals(node.getID())){
			nodeList.put(node.getID().getIdentifier(), node);
			System.out.println(node.toString());
		}
	}
	
	public void remove(ClientNode node){
		/*
		 * Removes a ClientNode from a list of nodes.
		 * Does nothing if node is not present.
		 */
		if(clientNodeLookup(node))
			nodeList.remove(node.getID().getIdentifier());
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
