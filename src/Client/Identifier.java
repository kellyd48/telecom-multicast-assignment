package Client;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Identifier class can be used to store a unique id for each client based on a Timestamp.
 * Identifier is internally stored as an integer.
 */
public class Identifier {
	
	public static final int BYTES_PER_INT = 4;
	public static final int IDENTIFIER_LENGTH = 4;
	
	private int identifier;
	
	/**
	 * Identifier Constructor.
	 */
	public Identifier(){
		identifier = getTimestampID();
	} // end Identifier constructor
	
	/**
	 * Identifier Constructor.
	 * @param id
	 */
	public Identifier(Identifier id){
		this.identifier = id.getIdentifier();
	} // end Identifier constructor
	
	/**
	 * Identifier Constructor.
	 * @param identifier
	 */
	public Identifier(byte[] identifier){
		this(toInt(identifier));
	} // end Identifier constructor
	
	/**
	 * Identifier Constructor.
	 * @param identifier
	 */
	public Identifier(int identifier){
		this.identifier = identifier;
	} // end Identifier constructor
	
	/**
	 * Returns the identification code as an integer.
	 */
	public int getIdentifier(){
		return this.identifier;
	} // end getIdentifier method
	
	/**
	 * Returns id constructed from hash of current timestamp. (id is an integer)
	 */
	private int getTimestampID(){
		Date date = new Date();
		Timestamp timestamp = new Timestamp(date.getTime());
		return timestamp.hashCode();
	} // end getTimestampID method
	
	/**
	 * Takes array of Identifier bytes and converts it to integer form.
	 * @param byte array
	 * @return integer
	 */
	private static int toInt(byte[] bytes){
		assert(bytes.length == IDENTIFIER_LENGTH);
		int identifier = 0;
		int mask = 0xFF;
		for(int i = 0; i <bytes.length; i++){
			identifier = identifier + ((bytes[i] & mask) << (i * 8));
		}
		return identifier;
	} // end toInt method
	
	/**
	 * Converts id internally stored as an int into an array of bytes.
	 * @return byte array
	 */
	public byte[] toBytes(){
		byte[] identifier = new byte[BYTES_PER_INT];
		for (int i = 0; i < identifier.length; i++) {
		    identifier[i] = (byte)(this.identifier >> (i * 8));
		}
		return identifier;
	} // end toBytes method

	/**
	 * Checks if two Identifier objects are equal.
	 * @param Identifier otherID
	 * @return Returns boolean true or false whether the ID's are equal.
	 */
	public boolean equals(Identifier otherID){
		return this.identifier == otherID.getIdentifier();
	} // end equals method
	
	/**
	 * toString method
	 */
	public String toString(){
		return Integer.toString(identifier);
	} // end toString method
} // end Identifier class
