package Sender;

import java.io.File;
import java.io.FileInputStream;

import Client.*;

public class Sender {

	public static enum SENDER_STATE {WAIT_FOR_IMAGE, SEND_METADATA, SEND_IMAGE, RESEND, COMPLETED, ERROR,};
	
	private byte[] packetToSend = null;
	private byte[] lastPacketSent = null;
	private byte[] dataToSend;
	private int dataBytesSent = 0;
	private SENDER_STATE state;
	private Identifier ID;
	private Ack sequence;
	
	public Sender(Identifier ID){
		state = SENDER_STATE.WAIT_FOR_IMAGE;
		//initialise ack
		this.sequence = new Ack();
		this.ID = new Identifier(ID);
	}
	
	public void run(String inputFileTest){
		switch(state){
		case WAIT_FOR_IMAGE:
			if(!inputFileTest.equals("")){
				state = SENDER_STATE.SEND_METADATA;
				getImageFromFile(inputFileTest);
				inputFileTest = "";
			}
		case SEND_METADATA:
			if(state == SENDER_STATE.SEND_METADATA){
				assert(dataToSend != null);
				packetToSend = Multicast.constructImageMetadataPacket(ID, sequence.getAck(), dataToSend.length);
				state = SENDER_STATE.SEND_IMAGE;
				sequence.next();
			}
			break;
		case SEND_IMAGE:
			if(isComplete())
				state = SENDER_STATE.COMPLETED;
			else{
				packetToSend = Multicast.constructImagePacket(ID, sequence.getAck(), getBytesFromImage());
				sequence.next();
			}
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
	}
	
	/**
	 * Checks if sender has a packet to send.
	 * @return
	 */
	public boolean hasPacketToSend(){
		return (packetToSend == null ? false:true);
	}
	
	private void getImageFromFile(String filename){
		try {
			File file = new File(filename);
			//Create buffer to be length of file
			dataToSend = new byte[(int) file.length()];
			FileInputStream fileInput = new FileInputStream(file);
			fileInput.read(dataToSend);
			fileInput.close();
			dataBytesSent = 0;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean checkForNewImage(){
		//check for image in a directory
		//if present put it in the byte array of dataToSend.
		return false;
	}
	
	/**
	 * Changes the sender state to RESEND.
	 * Can be accessed from outside the class.
	 */
	public void resend(){
		this.state = SENDER_STATE.RESEND;
	}
	
	/**
	 * Returns ack
	 * 
	 * @return
	 */
	public Ack getSequence(){
		return sequence;
	}
	
	private byte[] getBytesFromImage(){
		byte[] imageBytes = new byte[Multicast.DATA];
		int bytesToSend = (dataToSend.length - dataBytesSent > Multicast.DATA ? Multicast.DATA:dataToSend.length - dataBytesSent);
		System.arraycopy(dataToSend, dataBytesSent, imageBytes, 0, bytesToSend);
		dataBytesSent += bytesToSend;
		return imageBytes;
	}
	
	/**
	 * Checks if Image is fully sent
	 * @return
	 */
	private boolean isComplete(){
		return dataBytesSent >= dataToSend.length;
	}
	
	/**
	 * Returns the packet to currently send
	 * 
	 * @return
	 */
	public byte[] packetToSend(){
		lastPacketSent = packetToSend;
		packetToSend = null;
		return lastPacketSent;
	}

	/**
	 * Returns the state of the Sender.
	 * @return
	 */
	public SENDER_STATE getState(){
		return state;
	}
}
