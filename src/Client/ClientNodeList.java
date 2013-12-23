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
	} // end ClientNodeList constructor
	
	/**
	 * ClientNodeList Constructor
	 * @param otherList
	 */
	public ClientNodeList(ClientNodeList otherList){
		this.terminal = otherList.getTerminal();
		this.myID = getMyID();
		nodeList = new HashMap<Integer, ClientNode>();
		nodeList.putAll(otherList.getMap());
	} // end ClientNodeList constructor

	/**
	 * Returns ClientNode object mapped to the key passed.
	 * @param key
	 * @return
	 */
	public ClientNode getClientNode(int key){
		return nodeList.get(key);
	} // end getClientNode method
	
	/**
	 * Returns boolean for whether a ClientNode is contained in the list of nodes.
	 * @param node
	 * @return
	 */
	public boolean clientNodeLookup(ClientNode node){
		return nodeList.containsKey(node.getID().getIdentifier());
	} // end clientNodeLookup method

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
		} // end if
	} // end add method

	/**
	 * Removes a ClientNode from a list of nodes.
	 * Does nothing if node is not present.
	 * @param node
	 */
	public void remove(ClientNode node){
		if(clientNodeLookup(node))
			nodeList.remove(node.getID().getIdentifier());
	} // add remove method
	
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
			} // end if
		} // end for
		return null;
	} // end get method

	/**
	 * Returns the size of the list
	 * @return
	 */
	public int size(){
		return nodeList.size();
	} // end size method

	/**
	 * Returns the Identifier myID
	 * 
	 * @return
	 */
	public Identifier getMyID(){
		return myID;
	} // end getMyID method

	/**
	 * Returns the HashMap nodeList
	 * @return
	 */
	public HashMap<Integer, ClientNode> getMap(){
		return nodeList;
	} // end HashMap method

	/**
	 * Returns reference to terminal.
	 * @return
	 */
	public Terminal getTerminal(){
		return terminal;
	} // end getTerminal method

	/**
	 * Updates the ack stored in the ClientNode mapped to ID.
	 * @param ID
	 * @param ack
	 */
	public void updateAck(Identifier ID, Ack ack){
		ClientNode node = nodeList.get(ID.getIdentifier());
		node.setAck(ack);
	} // end updateAck method
	
	/**
	 * Checks all acks if they have the value passed.
	 * Returns true only if all ClientNodes have the same value ack.
	 * 
	 * @param ack
	 * @return
	 */
	public boolean checkAllAcks(Ack ack){
		@SuppressWarnings("rawtypes")
		Set nodes = nodeList.entrySet();
		@SuppressWarnings("rawtypes")
		Iterator iterator = nodes.iterator();
		while(iterator.hasNext()){
			@SuppressWarnings("unchecked")
			Map.Entry<Integer, ClientNode> node = (Map.Entry<Integer, ClientNode>)iterator.next();
			if(node.getValue().getAck() == null)
				return false;
			if(!Ack.equals(node.getValue().getAck(), ack))
				return false;
		}
		return true;
	} // end checkForAck method
	
	/**
	 * toString method
	 */
	public String toString(){
        String toString = "\nNodeList:\n";
        @SuppressWarnings("rawtypes")
        Set nodes = nodeList.entrySet();
        @SuppressWarnings("rawtypes")
        Iterator iterator = nodes.iterator();
        while(iterator.hasNext()){
                @SuppressWarnings("unchecked")
                Map.Entry<Integer, ClientNode> node = (Map.Entry<Integer, ClientNode>)iterator.next();
                toString += node.getValue().toString()+"\n";
        }
        return toString;
	}//end toString method
} // end ClientNodeList class
