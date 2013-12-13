package multicastclient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 *
 * @author mashikag
 */
public class Client {
    
    public static void main(String[] args){
        try{
            MulticastSocket socket = new MulticastSocket(Constants.MCAST_PORT);
            InetAddress mcastAddress = InetAddress.getByName(Constants.MCAST_ADDR);
            socket.joinGroup(mcastAddress);
            
            if(args.length > 0){
                //Sends the message that was provided in argument
                System.out.println("Port number: "+socket.getLocalPort());
                new Thread(new Sender(socket,args[0])).start();
            }else{
                //Acts as a receiver
                new Thread(new Listener(socket)).start();
            }
                
        }
        catch(IOException e){e.printStackTrace();}
        
    }
}
