package Sender;

import Client.*;

public class Sender {

	public static enum SENDER_STATE {WAIT_FOR_IMAGE, SEND_METADATA, SEND_IMAGE, ERROR};
	
	private byte[] packetToSend = null;
	private byte[] lastPacketSent = null;
	private byte[] ackToSend = new byte[Multicast.ACK_LENGTH];
	private SENDER_STATE state;
	
	public Sender(){
		state = SENDER_STATE.WAIT_FOR_IMAGE;
		//initialise ack byte array
		for(int i = 0; i < ackToSend.length; i++)
			ackToSend[i] = 0;
	}
	
	public void run(){
		switch(state){
		case WAIT_FOR_IMAGE:
			break;
		case SEND_METADATA:
			break;
		case SEND_IMAGE:
			break;
		case ERROR:
			break;
		default:
			break;
		}
		
	}
	
	public boolean hasPacketToSend(){
		return (packetToSend == null ? false:true);
	}
	
	public byte[] packetToSend(){
		lastPacketSent = packetToSend;
		packetToSend = null;
		return lastPacketSent;
	}
}
