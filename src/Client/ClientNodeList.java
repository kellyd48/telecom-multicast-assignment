package Client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import tcdIO.*;

/**
 * Interface to manage a group of ClientNode members on a multicast network.
 *
 */
public class ClientNodeList {
	private HashMap<Integer, ClientNode> nodeList;
	// The local client ID
	private Identifier myID;
	private Terminal terminal;

	/**
	 * ClientNodeList Constructor.
	 * @param ID
	 */
	public ClientNodeList(Identifier ID, Terminal terminal){
		this.terminal = terminal;
		nodeList = new HashMap<Integer, ClientNode>();
		myID = new Identifier(ID);
	}

	/**
	 * Returns boolean for whether a ClientNode is contained in the list of nodes.
	 * @param node
	 * @return
	 */
	public boolean clientNodeLookup(ClientNode node){
		return nodeList.containsKey(node.getID().getIdentifier());
	}

	/**
	 * Adds a ClientNode to the list of nodes.
	 * Does nothing if node already present.
	 * @param node
	 */
	public void add(ClientNode node){
		if(!clientNodeLookup(node) && !myID.equals(node.getID())){
			nodeList.put(node.getID().getIdentifier(), node);
			terminal.println("RECEIVED");
			terminal.println(node.toString());
		}
	}

	/**
	 * Removes a ClientNode from a list of nodes.
	 * Does nothing if node is not present.
	 * @param node
	 */
	public void remove(ClientNode node){
		if(clientNodeLookup(node))
			nodeList.remove(node.getID().getIdentifier());
	}

	/**
	 * Returns the node at a certain index
	 * @param index
	 * @return
	 */
	public ClientNode get(int index){
		@SuppressWarnings("rawtypes")
		Set nodes = nodeList.entrySet();
		@SuppressWarnings("rawtypes")
		Iterator iterator = nodes.iterator();
		for(int i = 0; iterator.hasNext(); i++){
			if(i == index){
				@SuppressWarnings("unchecked")
				Map.Entry<Integer, ClientNode> node = (Map.Entry<Integer, ClientNode>)iterator.next();
				return (ClientNode)node.getValue();
			}
		}
		return null;
	}

	/**
	 * Returns the size of the list
	 * @return
	 */
	public int size(){
		return nodeList.size();
	}

	public String toString(){
		String toString = "";
		@SuppressWarnings("rawtypes")
		Set nodes = nodeList.entrySet();
		@SuppressWarnings("rawtypes")
		Iterator iterator = nodes.iterator();
		while(iterator.hasNext()){
			@SuppressWarnings("unchecked")
			Map.Entry<Integer, ClientNode> node = (Map.Entry<Integer, ClientNode>)iterator.next();
			toString += "Key: " + node.getKey() + " Client: " + node.getValue().toString() + "\n";
		}
		return toString;
	}
}
