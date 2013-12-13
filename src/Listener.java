package multicastclient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

public class Listener implements Runnable{
    private final MulticastSocket socket;
    
    public Listener(MulticastSocket socket){
        this.socket = socket;
    }
    
    public byte[] listen() throws IOException{
        byte[] buffer = new byte[Constants.MAX_BUFFER];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        return buffer;
    }

    @Override
    public void run() {
        try{
        while(true){
            System.out.println(listen().toString());
        }
        }catch(IOException e){e.printStackTrace();}
    }
}
