package Receiver;
import Client.*;

public class ReceiverBuffer {
	public static enum BUFFER_STATE{WRITING, FULL};
	private byte[] fileBuffer;
}
