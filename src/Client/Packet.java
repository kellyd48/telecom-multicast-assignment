
public class Packet {
	public static final int MTU = 1500;
	public static final int HEADER = 4;
	public static final int DATA = MTU - HEADER;
	
	public static byte[] getHeader(byte[] packetData){
		byte[] result = new byte[HEADER];
		System.arraycopy(packetData, 0, result, 0, HEADER);
		return result;
	}
	
	public static byte[] getData(byte[] packetData){
		byte[] result = new byte[DATA];
		System.arraycopy(packetData, 0, result, 0, DATA);
		return result;
	}

}
