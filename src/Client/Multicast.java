package Client;

/**
 * Contains important constants and useful methods for the Multicast Implementation.
 *
 */
public class Multicast {
	/*
	 * Data In Packet looks like this
	 * | HEADER |      DATA     |
	 * 
	 * HEADER = 8 bytes
	 * | PACKET_IDENTIFIER 1 byte | HEADER_DATA 3 bytes | CLIENT_IDENTIFIER 4 bytes
	 * 
	 * DATA = remaining 1020 bytes
	 * 
	 */
	
	// Length in bytes of the various components of the packet.
	public static final int MTU = 1024;
	public static final int HEADER_INDEX = 0;
	public static final int HEADER = 8;
	public static final int PACKET_IDENTIFIER_INDEX = 0;
	public static final int PACKET_IDENTIFIER = 1;
	public static final int HEADER_DATA_INDEX = 1;
	public static final int HEADER_DATA = 3;
	public static final int CLIENT_IDENTIFIER_INDEX = 4;
	public static final int CLIENT_IDENTIFIER = 4;
	public static final int ACK_LENGTH = HEADER_DATA;
	public static final int DATA_INDEX = 8;
	public static final int DATA = MTU - HEADER;
	//A hello packet contains this byte array in the header
	public static final byte[] HELLO = {'H', 'I', '!'};
	// Constants for identifying the type of packet.
	public static enum PACKET_TYPE {HELLO, IMAGE, IMAGE_METADATA, UNKNOWN};
	public static final byte HELLO_IDENTIFIER = 'H';
	public static final byte IMAGE_IDENTIFIER = 'I';
	public static final byte IMAGE_METADATA_IDENTIFIER = 'M';
	

	public static PACKET_TYPE getPacketType(byte[] packetData){
		/*
		 * Returns the type of packet based on the packet data
		 */
		switch(getPacketIdentifier(getHeaderData(packetData))){
		case HELLO_IDENTIFIER:
			return PACKET_TYPE.HELLO;
		case IMAGE_IDENTIFIER:
			return PACKET_TYPE.IMAGE;
		case IMAGE_METADATA_IDENTIFIER:
			return PACKET_TYPE.IMAGE_METADATA;
		default:
			return PACKET_TYPE.UNKNOWN;
		}
	}

	public static byte[] constructHelloPacket(Identifier ID){
		// returns byte array to be sent as a hello packet
		byte[] packet = new byte[MTU];
		//Insert Packet Identifier.
		packet[PACKET_IDENTIFIER_INDEX] = HELLO_IDENTIFIER;
		//Insert Hello Code
		System.arraycopy(HELLO, 0, packet, HEADER_DATA_INDEX, HEADER_DATA);
		//Insert Client Identifier
		System.arraycopy(ID.toBytes(), 0, packet, CLIENT_IDENTIFIER_INDEX, CLIENT_IDENTIFIER);
		return packet;
	}

	public static byte[] constructImagePacket(Identifier ID, byte[] ack, byte[] imageData){
		/*
		 * Constructs an image packet when given an ack and the image data.
		 */
		byte[] packet = new byte[MTU];
		//Insert Packet Identifier.
		packet[PACKET_IDENTIFIER_INDEX] = IMAGE_IDENTIFIER;
		//Insert ack
		assert(ack.length == ACK_LENGTH);
		System.arraycopy(ack, 0, packet, HEADER_DATA_INDEX, ACK_LENGTH);
		//Insert Client Identifier
		System.arraycopy(ID.toBytes(), 0, packet, CLIENT_IDENTIFIER_INDEX, CLIENT_IDENTIFIER);
		//Insert Image Data
		assert(imageData.length == DATA);
		System.arraycopy(imageData, 0, packet, DATA_INDEX, DATA);
		return packet;
	}

	public static byte[] constructImageMetadataPacket(Identifier ID, byte[] ack, int imageSize){
		/*
		 * Takes an ack and metadata (image length) and constructs an image metadata packet.
		 */
		byte[] packet = new byte[MTU];
		//Insert Packet Identifier.
		packet[PACKET_IDENTIFIER_INDEX] = IMAGE_METADATA_IDENTIFIER;
		//Insert ack
		assert(ack.length == ACK_LENGTH);
		System.arraycopy(ack, 0, packet, HEADER_DATA_INDEX, ACK_LENGTH);
		//Insert Client Identifier
		System.arraycopy(ID.toBytes(), 0, packet, CLIENT_IDENTIFIER_INDEX, CLIENT_IDENTIFIER);
		//Insert metadata
		byte[] metadata = (Integer.toString(imageSize)).getBytes();
		System.arraycopy(metadata, 0, packet, HEADER, metadata.length);
		return packet;
 	}
	
	public static int getImageSizeFromMetadata(byte[] metadata){
		// returns the size of an image in bytes from the metadata given.
		return Integer.valueOf(new String(metadata, 0, metadata.length).trim()).intValue();
	}

	public static byte[] getHeader(byte[] packetData){
		/*
		 * Returns the header from the data contained in a packet.
		 */
		byte[] header = new byte[HEADER];
		System.arraycopy(packetData, HEADER_INDEX, header, 0, HEADER);
		return header;
	}
	
	public static byte getPacketIdentifier(byte[] header){
		// returns the byte identifying what the type of packet is
		return header[PACKET_IDENTIFIER_INDEX];
	}
	
	public static byte[] getClientIdentifier(byte[] packetData){
		byte[] clientIdentifier = new byte[CLIENT_IDENTIFIER];
		System.arraycopy(packetData, CLIENT_IDENTIFIER_INDEX, clientIdentifier, 0, CLIENT_IDENTIFIER);
		return clientIdentifier;
	}
	
	public static byte[] getHeaderData(byte[] header){
		//returns the data from the header (ie. an ack or hello message)
		byte[] headerData = new byte[HEADER_DATA];
		System.arraycopy(header, HEADER_DATA_INDEX, headerData, 0, HEADER_DATA);
		return headerData;
	}
	
	public static byte[] getData(byte[] packetData){
		/*
		 * Returns the data minus the header from the packet data.
		 */
		byte[] data = new byte[DATA];
		System.arraycopy(packetData, DATA_INDEX, data, 0, DATA);
		return data;
	}

	public static byte[] nextAck(byte[] ack){
		byte[] newAck = new byte[ack.length];
		if(ack[0] == 0)
			ack[0] = 1;
		else
			ack[0] = 0;
		return newAck;
	}
}
