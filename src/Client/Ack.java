package Client;

public class Ack {
	
	private byte[] currentAck = new byte[Multicast.ACK_LENGTH];
	private boolean enabled;
	
	public Ack(){
		currentAck[0] = 1;
		for(int i = 1; i < currentAck.length; i++){
			currentAck[i] = 0;
		}
		enabled = false;
	}
	
	public Ack(Ack ack){
		for(int i = 0; i < ack.getAck().length; i++){
			currentAck[i] = ack.getAck()[i];
		}
		enabled = false;
	}
	
	public Ack(byte[] ack){
		assert(ack.length == Multicast.ACK_LENGTH);
		for(int i = 0; i < ack.length; i++){
			currentAck[i] = ack[i];
		}
		enabled = false;
	}
	
	public boolean isEnabled(){
		return enabled;
	}
	
	public void setEnabled(boolean enable){
		enabled = enable;
	}
	
	public void setAck(byte[] ack){
		assert(ack.length == Multicast.ACK_LENGTH);
		for(int i = 0; i < ack.length; i++){
			currentAck[i] = ack[i];
		}
	}
	
	public byte[] getAck(){
		return currentAck;
	}

	public byte[] nextExpectedAck(){
		return getNextAck(this.getAck());
	}
	
	public void next(){
		currentAck = getNextAck(currentAck);
	}
	
	private static byte[] getNextAck(byte[] currentAck){
		byte[] nextAck = new byte[Multicast.ACK_LENGTH];
		if(currentAck[0] == 0)
			nextAck[0] = 1;
		else
			nextAck[0] = 0;
		return nextAck;
	}
	
	public boolean equals(byte[] ack){
		for(int i = 0; i < Multicast.ACK_LENGTH; i++){
			if(this.currentAck[i] != ack[i])
				return false;
		}
		return true;
	}
	
	public static boolean equals(byte[] ack1, byte[] ack2){
		for(int i = 0; i < Multicast.ACK_LENGTH; i++){
			if(ack1[i] != ack2[i])
				return false;
		}
		return true;
	}
}
