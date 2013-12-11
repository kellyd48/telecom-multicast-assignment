package Client;

public class Multicast {
	/*
	 * Contains important constants and useful methods for the Multicast Implementation.
	 */
	
	/*
	 * Data In Packet looks like this
	 * | HEADER |      DATA     |
	 * 
	 * HEADER = 4 bytes
	 * | PACKET_IDENTIFIER 1 byte long | HEADER_DATA 3 bytes long | 
	 * 
	 * DATA = remaining 1020 bytes
	 * 
	 */
	
	// Length in bytes of the various components of the packet.
	public static final int MTU = 1024;
	public static final int HEADER = 4;
	public static final int PACKET_IDENTIFIER = 1;
	public static final int HEADER_DATA = HEADER - PACKET_IDENTIFIER;
	public static final int ACK_LENGTH = HEADER_DATA;
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

	public static byte[] constructHelloPacket(){
		// returns byte array to be sent as a hello packet
		byte[] packet = new byte[MTU];
		packet[0] = HELLO_IDENTIFIER;
		System.arraycopy(HELLO, 0, packet, PACKET_IDENTIFIER, HEADER_DATA);
		return packet;
	}

	public static byte[] constructImagePacket(byte[] ack, byte[] imageData){
		/*
		 * Constructs an image packet when given an ack and the image data.
		 */
		byte[] imagePacket = new byte[MTU];
		imagePacket[0] = IMAGE_IDENTIFIER;
		System.arraycopy(ack, 0, imagePacket, PACKET_IDENTIFIER, ACK_LENGTH);
		System.arraycopy(imageData, 0, imagePacket, HEADER, DATA);
		return imagePacket;
	}

	public static byte[] constructImageMetadataPacket(byte[] ack, int imageSize){
		/*
		 * Takes an ack and metadata (image length) and constructs an image metadata packet.
		 */
		byte[] metadataPacket = new byte[MTU];
		metadataPacket[0] = IMAGE_METADATA_IDENTIFIER;
		System.arraycopy(ack, 0, metadataPacket, PACKET_IDENTIFIER, ACK_LENGTH);
		byte[] metadata = (Integer.toString(imageSize)).getBytes();
		System.arraycopy(metadata, 0, metadataPacket, HEADER, metadata.length);
		return metadataPacket;
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
		System.arraycopy(packetData, 0, header, 0, HEADER);
		return header;
	}
	
	public static byte getPacketIdentifier(byte[] header){
		// returns the byte identifying what the type of packet is
		return header[0];
	}
	
	public static byte[] getHeaderData(byte[] header){
		//returns the data from the header (ie. an ack or hello message)
		byte[] headerData = new byte[HEADER_DATA];
		System.arraycopy(header, PACKET_IDENTIFIER, headerData, 0, headerData.length);
		return headerData;
	}
	
	public static byte[] getData(byte[] packetData){
		/*
		 * Returns the data minus the header from the packet data.
		 */
		byte[] data = new byte[DATA];
		System.arraycopy(packetData, HEADER, data, 0, DATA);
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
