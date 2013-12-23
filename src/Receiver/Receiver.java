package Receiver;

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
		outputFileName = ID.toString()+".jpeg";
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
							JFrame frame = new JFrame("Client ID: " + id.toString());
							ImageDisplay panel = new ImageDisplay(outputFileName);
							frame.getContentPane().add(panel);
							frame.setSize(500, 500);
							frame.setVisible(true);
							state = RECEIVER_STATE.FINISHED_RECEIVING;
						}
					}
					break;
				default:
					break;
			}
		}
	}

	public Ack getAck(){
		return ack;
	}

	public RECEIVER_STATE getState(){
		return state;
	}
}
