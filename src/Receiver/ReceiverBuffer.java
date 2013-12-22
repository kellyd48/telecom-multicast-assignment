package Receiver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import Receiver.Receiver.RECEIVER_STATE;

/**
 * The ReceiverBuffer class takes care of storing the data being received in packets.
 * When the buffer is full it writes the buffer out to file.
 *
 */
public class ReceiverBuffer {
	public static enum BUFFER_STATE{READING, FULL, COMPLETE};
	private byte[] fileBuffer;
	private int sizeOfData = 0;
	private int dataReceivedSize = 0;
	private BUFFER_STATE state;
	private String outputFilename = "";
	
	public ReceiverBuffer(int sizeOfData, String outputFile){
		state = BUFFER_STATE.READING;
		this.sizeOfData = sizeOfData;
		this.fileBuffer = new byte[sizeOfData];
		this.outputFilename = outputFile;
	}
	
	public void run(byte[] currentData){
		// iterates through state machine for the buffer
		switch(state){
			case READING:
				readData(currentData);
			case FULL:
				if(state == BUFFER_STATE.FULL){
					writeToFile();
					state = BUFFER_STATE.COMPLETE;
				}
				break;
			case COMPLETE:
				break;
		}
	}
	
	private void readData(byte data[]){
		/* Reads the data byte array and adds it to the filebuffer
		 * Which will later be written out to file.
		 */
		if(dataReceivedSize < sizeOfData){
			System.arraycopy(data, 0, fileBuffer, dataReceivedSize, 
					(sizeOfData-dataReceivedSize < data.length ? sizeOfData-dataReceivedSize:data.length));
			dataReceivedSize += data.length;
		}
		if(dataReceivedSize >= sizeOfData){
			state = BUFFER_STATE.FULL;
		}
	}
	
	private void writeToFile(){
		//writes out data received to file
		File file = new File(outputFilename);
		try {
			FileOutputStream fout = new FileOutputStream(file);
			fout.write(fileBuffer, 0, fileBuffer.length);
			fout.flush();
			fout.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isComplete(){
		// Checks if buffer is finished reading the data and has written out the file.
		return state == BUFFER_STATE.COMPLETE;
	}
	
}
