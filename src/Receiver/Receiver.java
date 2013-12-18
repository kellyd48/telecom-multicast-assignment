package Receiver;
import Client.*;

public class Receiver {
	public static final String OUTPUT_FILE = "Receiver";
	public static enum RECEIVER_STATE {WAITING, RECEIVING_IMAGE, FINISHED_RECEIVING, ERROR};
	
	private ReceiverBuffer buffer;
	private RECEIVER_STATE state;
	private byte[] packetReceived;
	private Identifier ID;
	private Ack ack;
	
	public Receiver(Identifier ID){
		state = RECEIVER_STATE.WAITING;
		this.ID = new Identifier(ID);
	}

	public void run(){
		if(packetReceived != null){
			Multicast.PACKET_TYPE packetType = Multicast.getPacketType(packetReceived);
			switch(state){
			case WAITING:
				if(packetType == Multicast.PACKET_TYPE.IMAGE_METADATA){
					ack = new Ack(Multicast.getHeaderData(packetReceived));
					int sizeOfImage = Multicast.getImageSizeMetadataPacket(packetReceived);
					buffer = new ReceiverBuffer(sizeOfImage, ID.toString());
					state = RECEIVER_STATE.RECEIVING_IMAGE;
				}
				break;
			case RECEIVING_IMAGE:
				byte[] ack = Multicast.getHeaderData(packetReceived);
				if(Ack.equals(Ack.nextExpectedAck(this.ack), new Ack(ack))){
					this.ack.next();
					buffer.updateData(Multicast.getData(packetReceived));
					buffer.run();
					if(buffer.isComplete())
						state = RECEIVER_STATE.FINISHED_RECEIVING;
				}
				break;
			case FINISHED_RECEIVING:
				state = RECEIVER_STATE.WAITING;
				break;
			case ERROR:
				break;
			default:
				break;
			}
		}
	}
	
	public Ack getAck(){
		return ack;
	}
	
	/**
	 * Updates Packet Data read into the receiver.
	 * 
	 * @param packetData
	 */
	public void receivePacket(byte[] packetData){
		assert(packetData != null);
		packetReceived = new byte[Multicast.MTU];
		System.arraycopy(packetData, 0, packetReceived, 0, packetReceived.length);
	}
}
