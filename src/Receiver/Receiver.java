package Receiver;
import Client.*;

import java.net.DatagramPacket;

public class Receiver {
	
	public static enum RECEIVER_STATE {RECEIVING_IMAGE, FINISHED_RECEIVING, ERROR};
	
	public Receiver(){
		
	}
	
	public void receivePacket(DatagramPacket packet){
		byte[] packetData = packet.getData();
		Multicast.PACKET_TYPE packetType = Multicast.getPacketType(packet.getData());
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
