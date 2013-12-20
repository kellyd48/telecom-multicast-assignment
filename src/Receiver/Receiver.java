package Receiver;
import Client.*;

public class Receiver {
	public static enum RECEIVER_STATE {RECEIVING_IMAGE, FINISHED_RECEIVING, ERROR};

	private ReceiverBuffer buffer;
	private RECEIVER_STATE state;
	private Ack ack;
	private String outputFileName;

	public Receiver(Identifier ID){
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
						if(buffer.isComplete())
							state = RECEIVER_STATE.FINISHED_RECEIVING;
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
