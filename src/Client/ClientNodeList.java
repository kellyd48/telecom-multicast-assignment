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
	
	public ClientNodeList(ClientNodeList otherList){
		this.terminal = otherList.getTerminal();
		this.myID = getMyID();
		nodeList = new HashMap<Integer, ClientNode>();
		nodeList.putAll(otherList.getMap());
	}

	/**
	 * Returns ClientNode object mapped to the key passed.
	 * @param key
	 * @return
	 */
	public ClientNode getClientNode(int key){
		return nodeList.get(key);
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
			terminal.println("Client added " + node.getID().toString());
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

	/**
	 * Returns the Identifier myID
	 * 
	 * @return
	 */
	public Identifier getMyID(){
		return myID;
	}

	/**
	 * Returns the HashMap nodeList
	 * @return
	 */
	public HashMap<Integer, ClientNode> getMap(){
		return nodeList;
	}

	/**
	 * Returns reference to terminal.
	 * @return
	 */
	public Terminal getTerminal(){
		return terminal;
	}

	public void updateAck(Identifier ID, Ack ack){
		ClientNode node = nodeList.get(ID.getIdentifier());
		node.setAck(ack);
	}
	
	/**
	 * Checks for ack with the value provided.
	 * Returns true if a client has an ack with the same value
	 * 
	 * @param ack
	 * @return
	 */
	public boolean checkForAck(Ack ack){
		@SuppressWarnings("rawtypes")
		Set nodes = nodeList.entrySet();
		@SuppressWarnings("rawtypes")
		Iterator iterator = nodes.iterator();
		while(iterator.hasNext()){
			@SuppressWarnings("unchecked")
			Map.Entry<Integer, ClientNode> node = (Map.Entry<Integer, ClientNode>)iterator.next();
			if(node.getValue().getAck() != null && Ack.equals(node.getValue().getAck(), ack))
				return true;
		}
		return false;
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
