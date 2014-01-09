package Receiver;

import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import Client.*;

public class Receiver extends JPanel{
	// eclipse added this
	private static final long serialVersionUID = 1L;

	public static enum RECEIVER_STATE {RECEIVING_IMAGE, FINISHED_RECEIVING, ERROR};

	private ReceiverBuffer buffer;
	private RECEIVER_STATE state;
	private Ack ack;
	private String outputFileName;
	private Identifier id;

	public Receiver(Identifier ID){
		this.id = ID;
	}

	public void run(byte[] packetReceived){
		if(packetReceived != null){
			assert(packetReceived.length == Multicast.MTU);
			Multicast.PACKET_TYPE packetType = Multicast.getPacketType(packetReceived);
			switch(packetType){
				case IMAGE_METADATA:
					if(state != RECEIVER_STATE.RECEIVING_IMAGE){
						ack = new Ack(Multicast.getHeaderData(packetReceived));
						int sizeOfImage = Multicast.getImageSizeMetadataPacket(packetReceived);
						outputFileName = newOutputFilename();
						buffer = new ReceiverBuffer(sizeOfImage, outputFileName);
						state = RECEIVER_STATE.RECEIVING_IMAGE;
					}
					break;
				case IMAGE:
					byte[] ack = Multicast.getHeaderData(packetReceived);
					if(Ack.equals(Ack.nextExpectedAck(this.ack), new Ack(ack))){
						this.ack.next();
						buffer.run(Multicast.getData(packetReceived));
						if(buffer.isComplete()) {
							displayImageJPanel();
							state = RECEIVER_STATE.FINISHED_RECEIVING;
						}
					}
					break;
				default:
					break;
			}
		}
	}
	
	/**
	 * Returns the received image from the buffer as an ImageIcon.
	 * @return
	 */
	public ImageIcon getReceivedImageIcon(){
		return buffer.getReceivedImage();
	}
	
	/**
	 * Creates a popup window with the received image.
	 */
	private void displayImageJPanel(){
		JFrame frame = new JFrame("Client ID: " + id.toString());
		ImageDisplay panel = new ImageDisplay(outputFileName);
		frame.getContentPane().add(panel);
		frame.setSize(500, 500);
		frame.setVisible(true);
	}

	private String newOutputFilename(){
		assert(this.id != null);
		File file;
		int i = 0;
		do{
			String append = (i == 0 ? "":"_"+i);
			file = new File(this.id.toString() + append + ".jpg");
			i++;
		}while(file.isFile());
		return file.getPath();
	}
	
	public Ack getAck(){
		return ack;
	}

	public RECEIVER_STATE getState(){
		return state;
	}
	
	/**
	 * @return Returns progress as a percentage of the amount of bytes received.
	 */
	public int getPercentageProgress(){
		return buffer.getPercentageProgress();
	}
}
