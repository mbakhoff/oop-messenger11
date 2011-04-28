package erik;

import java.io.*;
import java.net.*;
import java.util.*;

public class MessengerMain {

    public static int com_port = 1800;
    public static String nick = "erik";
    public static ArrayList<Socket> socketList = new ArrayList<Socket>();
    public static ArrayList<String> nameList = new ArrayList<String>();
    Receive r  = null;
    ConsoleInterface ci;
    public static Socket s = null;
    private final Object locked = new Object();
    
// Main runtime //
    public MessengerMain() {
    	warmUpGUI();
        
        InputStream input = null;
                while(true) {
            try {
                socketList.add(new Socket("172.17.52.32", 1800));
                GraphicalInterface.appendText("Socket opened: " + socketList.get(0).getInetAddress().toString());
            } catch (Exception e) {

                    }
                    while(socketList.size() >0)
                        try {
                            for(int i=0; i<socketList.size(); i++) {
                                input = socketList.get(i).getInputStream();
                                if (input.available() > 0) {
                                    Receive.decodePacket(input);
                                    break;
                                }
                            }
                        }
                        catch(Exception e) {
                            System.out.println("failed sss: " + e.getMessage());
                        }
                    
                }
    }
    
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
    private void warmUpGUI() {
        GraphicalInterface gui = new GraphicalInterface();
        gui.appendText("[KONSOLE] Messenger .11 warming up...");
        gui.appendText("[KONSOLE] Checking for Internet connection...");
        if(checkConnection() == false)
            gui.appendText("[KONSOLE] There appears to be no Internet connection, please check your connection. Local area network might be operational");
        else
            gui.appendText("[KONSOLE] Internet connection up and running");
        gui.appendText("[KONSOLE] Starting incoming connection listener...");
        r=new Receive();
        gui.appendText("[KONSOLE] Starting command line interface...");
        ci = new ConsoleInterface();
        gui.appendText("[KONSOLE] Ready for operation. Please use command <help> for manual");
        gui.callOut();
    }
// Checks for internet connection //
    private boolean checkConnection() {
             return false;
    }

    public static void main(String[] args) {
        MessengerMain MessengerMain = new MessengerMain();
    }

}
