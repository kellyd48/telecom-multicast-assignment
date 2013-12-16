package Client;

public class Ack {
	private byte[] ackBytes = new byte[Multicast.ACK_LENGTH];
	
	/**
	 * Ack Constructor Method
	 */
	public Ack(){
		ackBytes = constructAck((byte) 1);
	}
	
	/**
	 * Constructs ack from another Ack object.
	 * @param ack
	 */
	public Ack(Ack ack){
		ackBytes = ack.getAck();
	}
	
	/**
	 * Constructs ack from byte array
	 * @param ack
	 */
	public Ack(byte[] ack){
		assert(ack.length == Multicast.ACK_LENGTH);
		System.arraycopy(ack, 0, ackBytes, 0, ackBytes.length);
	}
	
	/**
	 * Just creates a new ack with the sequence number provided.
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] constructAck(byte sequence){
		return new byte[]{sequence, 0, 0};
	}
	
	/**
	 * Returns byte array of next ack.
	 * @param ack
	 * @return
	 */
	public static byte[] nextAck(byte[] ack){
		if(ack[0] == 1)
			return constructAck((byte)0);
		else
			return constructAck((byte)1);
	}
	
	/**
	 * Returns previous ack.
	 * 
	 * @param ack
	 * @return
	 */
	public Ack getPrevious(Ack ack){
		return new Ack(Ack.nextAck(ack.getAck()));
	}
	
	/**
	 * Updates internally stored ack to the next Ack.
	 */
	public void next(){
		ackBytes = nextAck(ackBytes);
	}
	
	/**
	 * Returns Ack object of next expected ack.
	 * 
	 * @param ack
	 * @return
	 */
	public static Ack nextExpectedAck(Ack ack){
		return new Ack(Ack.nextAck(ack.getAck()));
	}
	
	/**
	 * Checks if ack1 is internally equal to ack2
	 * 
	 * @param ack1
	 * @param ack2
	 * @return
	 */
	public static boolean equals(Ack ack1, Ack ack2){
		for(int i = 0; i < Multicast.ACK_LENGTH; i++){
			if(ack1.getAck()[i] == ack2.getAck()[i])
				return false;
		}
		return true;
	}
	
	/**
	 * Returns byte array of internally stored ack.
	 * @return
	 */
	public byte[] getAck(){
		return ackBytes;
	}
}
