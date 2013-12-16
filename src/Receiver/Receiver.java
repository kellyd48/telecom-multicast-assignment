package Receiver;
import Client.*;

public class Receiver {
	public static final String OUTPUT_FILE = "Receiver";
	public static enum RECEIVER_STATE {WAITING, RECEIVING_IMAGE, FINISHED_RECEIVING, ERROR};
	
	private ReceiverBuffer buffer;
	private RECEIVER_STATE state;
	private byte[] packetReceived;
	private Identifier ID;
	private Ack currentAck;
	
	public Receiver(Identifier ID, Ack ack){
		state = RECEIVER_STATE.WAITING;
		this.ID = new Identifier(ID);
		currentAck = new Ack(ack);
	}

	public void run(){
		if(packetReceived != null){
			Multicast.PACKET_TYPE packetType = Multicast.getPacketType(packetReceived);
			switch(state){
			case WAITING:
				if(packetType == Multicast.PACKET_TYPE.IMAGE_METADATA){
					byte[] header = Multicast.getHeader(packetReceived);
					byte[] ack = Multicast.getHeaderData(header);
					currentAck.setAck(ack);
					byte[] metadata = Multicast.getData(packetReceived);
					int sizeOfImage = Multicast.getImageSizeFromMetadata(metadata);
					buffer = new ReceiverBuffer(sizeOfImage, ID.toString());
					state = RECEIVER_STATE.RECEIVING_IMAGE;
				}
				break;
			case RECEIVING_IMAGE:
				byte[] header = Multicast.getHeader(packetReceived);
				byte[] ack = Multicast.getHeaderData(header);
				if(Ack.equals(currentAck.nextExpectedAck(), ack)){
					currentAck.next();
					buffer.updateData(Multicast.getData(packetReceived));
					buffer.run();
					if(buffer.isComplete())
						state = RECEIVER_STATE.FINISHED_RECEIVING;
				}
				break;
			case FINISHED_RECEIVING:
				currentAck.setEnabled(false);
				state = RECEIVER_STATE.WAITING;
				break;
			case ERROR:
				break;
			default:
				break;
			}
		}
	}
	
	public void receivePacket(byte[] packetData){
		assert(packetData != null);
		packetReceived = new byte[Multicast.DATA];
		System.arraycopy(packetData, 0, packetReceived, 0, packetReceived.length);
	}
}
