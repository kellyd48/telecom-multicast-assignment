package multicastclient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class Sender implements Runnable{
    private MulticastSocket socket;
    private InetAddress address;
    private int port;
    private String message;
   
    /*THIS CONSTRUCTOR IS ONLY FOR TESTING PURPOSES*/
    public Sender(MulticastSocket socket,String message){
        this(socket);
        this.message = message;
    }
    /***********************************************/
    
    public Sender(MulticastSocket socket){
        try{
            this.socket = socket;
            this.address = InetAddress.getByName(Constants.MCAST_ADDR);
            this.port = Constants.MCAST_PORT;
        }catch(UnknownHostException e){e.printStackTrace();}
    }
    
    public void send(String message) throws IOException {
        if(!message.isEmpty()){
            socket.send(new DatagramPacket(message.getBytes(),message.length(),address, port));
            System.out.println("Msg sent");
        }
    }

    @Override
    public void run(){
        try{send(message);}catch(IOException e){e.printStackTrace();}
    }
}
