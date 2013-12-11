package Receiver;
import Client.*;

public class Receiver {
	
	public static enum RECEIVER_STATE {RECEIVING_IMAGE, FINISHED_RECEIVING, ERROR};
	
	public Receiver(){
		
	}
	
	public void receivePacket(byte[] packetData){
		Multicast.PACKET_TYPE packetType = Multicast.getPacketType(packetData);
		switch(packetType){
		case IMAGE: 
			image(packetData);
			break;
		case HELLO:
			hello();
			break;
		case UNKNOWN:
			break;
		default:
			break;
		}
	}
	
	private void image(byte packetData[]){
		//code for receiving image
	}
		
	private void hello(){
		// code for hello
	}
	
}
