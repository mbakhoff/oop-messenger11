package erik;

import java.io.*;
import java.net.*;
import java.util.*;

public class MessengerMain {

    public static int com_port = 1800;
    public static String nick = "erik";
    public static ArrayList<Socket> socketList = new ArrayList<Socket>();
    public static ArrayList<String> nameList = new ArrayList<String>();
    InputStream input = null;
    static Socket s = null;
    Receive r  = null;
    ConsoleInterface ci;
    
// Main runtime //
    public MessengerMain() {
    	warmUp();
        try {
            s = new Socket("127.0.0.1", 1800);
        }
        catch(Exception e) {
            System.out.println("failed:" + e.getMessage());
        }
        
        Send.sendPacket(s, Send.encodePacket("debug: selfmessage works"));
        while(true) {
            try {
            	if(socketList.size() > 0) {
            		input = socketList.get(0).getInputStream();
            		if (input.available() > 0) {
            			System.out.println("getting info from: " + socketList.get(0));
            			Receive.decodePacket(input);
            		}
            	}
        }
            
        catch(Exception e) {
            System.out.println("failed sss: " + e.getMessage());
        }
        }
    }
//Warms up the messenger and starts up threads //
// Starts up stuff //
    private void warmUp() {
        Random rand = new Random();
    	System.out.println("Messenger .11."+ rand.nextInt(8192) +" warming up...");
        rand = null;
        System.out.println("-Checking for Internet connection...");
        if(checkConnection() == false)
            System.out.println("There appears to be no Internet connection, please check your connection. Local area network might be operational");
        else
            System.out.println("Internet connection up and running");
        System.out.println("-Starting incoming connection listener...");
        r=new Receive();
        System.out.println("-Starting command line interface...");
        ci = new ConsoleInterface();
        System.out.println("Ready for operation. Please use command <help> for manual");
    }

// Checks for Internet connection //
    private boolean checkConnection() {
             return false;
    }

    public static void main(String[] args) {
        MessengerMain MessengerMain = new MessengerMain();
    }

}
