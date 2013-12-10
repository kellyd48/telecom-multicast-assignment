package Client;
import java.util.Arrays;


public class Packet {
	public static final int MTU = 1024;
	public static final int HEADER = 4;
	public static final int DATA = MTU - HEADER;
	//A hello packet contains this byte array in the header
	public static final byte[] HELLO = {'*', 'H', 'I', '*'};
	public static enum PACKET_TYPE {HELLO, IMAGE, UNKNOWN};
	
	public static byte[] getHeader(byte[] packetData){
		/*
		 * Returns the header from the data contained in a packet.
		 */
		byte[] result = new byte[HEADER];
		System.arraycopy(packetData, 0, result, 0, HEADER);
		return result;
	}
	
	public static byte[] getData(byte[] packetData){
		/*
		 * Returns the data minus the header from the packet data.
		 */
		byte[] result = new byte[DATA];
		System.arraycopy(packetData, 0, result, 0, DATA);
		return result;
	}
	
	public static PACKET_TYPE getPacketType(byte[] packetData){
		/*
		 * Returns the type of packet based on the packet data
		 */
		if(isHelloHeader(getHeader(packetData)))
			return PACKET_TYPE.HELLO;
		else if(isImageData(getHeader(packetData)))
			return PACKET_TYPE.IMAGE;
		else return PACKET_TYPE.UNKNOWN;
	}

	public static boolean isHelloHeader(byte[] header){
		/*
		 * Checks if header from packet data is a "hello" header.
		 */
		assert(header.length == HEADER);
		return Arrays.equals(header, HELLO);
	}
	
	public static boolean isImageData(byte[] header){
		/*
		 * If the header isn't a "hello" header then it's an image
		 */
		return !isHelloHeader(header);
	}
}
