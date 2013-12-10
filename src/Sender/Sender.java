package Sender;
import java.net.DatagramPacket;

import Client.*;

public class Sender {

	private boolean hasPacketToSend = false;
	DatagramPacket packetToSend = null;
	
	public Sender(){
		
	}
	
	public boolean hasPacketToSend(){
		return hasPacketToSend;
	}
	
	public DatagramPacket packetToSend(){
		return packetToSend;
	}
	
}
