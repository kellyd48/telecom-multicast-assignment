package Sender;

import Client.*;

public class Sender {

	public static enum SENDER_STATE {WAIT_FOR_IMAGE, SEND_METADATA, SEND_IMAGE, RESEND, COMPLETED, ERROR,};
	
	private byte[] packetToSend = null;
	private byte[] lastPacketSent = null;
	private byte[] dataToSend;
	private int dataBytesSent = 0;
	private SENDER_STATE state;
	private Identifier ID;
	private Ack ack;
	
	public Sender(Identifier ID){
		state = SENDER_STATE.WAIT_FOR_IMAGE;
		//initialise ack
		this.ack = new Ack();
		this.ID = new Identifier(ID);
	}
	
	public void run(){
		switch(state){
		case WAIT_FOR_IMAGE:
			if(checkForNewImage())
				state = SENDER_STATE.SEND_METADATA;
			break;
		case SEND_METADATA:
			assert(dataToSend != null);
			packetToSend = Multicast.constructImageMetadataPacket(ID, ack.getAck(), dataToSend.length);
			break;
		case SEND_IMAGE:
			if(isComplete())
				state = SENDER_STATE.COMPLETED;
			else
				packetToSend = Multicast.constructImagePacket(ID, ack.getAck(), getBytesFromImage());
			break;
		case RESEND:
			packetToSend = lastPacketSent;
			state = SENDER_STATE.SEND_IMAGE;
			break;
		case COMPLETED:
			dataToSend = null;
			dataBytesSent = 0;
			state = SENDER_STATE.WAIT_FOR_IMAGE;
			break;
		case ERROR:
			break;
		default:
			break;
		}
		//Change ack to next ack.
		ack.next();
	}
	
	public boolean hasPacketToSend(){
		return (packetToSend == null ? false:true);
	}
	
	private boolean checkForNewImage(){
		//check for image in a directory
		//if present put it in the byte array of dataToSend.
		return false;
	}
	
	public void resend(){
		this.state = SENDER_STATE.RESEND;
	}
	
	private byte[] getBytesFromImage(){
		byte[] imageBytes = new byte[Multicast.DATA];
		System.arraycopy(dataToSend, dataBytesSent, imageBytes, 0, dataToSend.length);
		return imageBytes;
	}
	
	private boolean isComplete(){
		return dataBytesSent >= dataToSend.length;
	}
	
	public byte[] packetToSend(){
		lastPacketSent = packetToSend;
		packetToSend = null;
		return lastPacketSent;
	}
}
