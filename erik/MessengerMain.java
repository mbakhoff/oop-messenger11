package erik;

import java.io.InputStream;
import java.net.*;
import java.util.*;

public class MessengerMain {

    public static int com_port = 1800;
    public static String nick = "erik";

    private static Map<String, Socket> socketMap = new HashMap<String, Socket>();
    private static Map<String, DataStore> dataLink = new HashMap<String, DataStore>();

    public static Socket s = null;
    private final Object locked = new Object();
    
// Main runtime //
    public MessengerMain() {

        InputStream input = null;

        //if(interf.equals("-gui"))
            warmUpGUI();
        //else if(interf.equals("-nogui"))
            //warmUpCLI();

        while(true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
            while(socketMap.size() > 0) {
                try {
                    for(String s:socketMap.keySet()) {
                        input = socketMap.get(s).getInputStream();
                        if(input.available() > 0) {
                            Receive.decodePacket(input, s);
                            break;
                        }
                    }
                    Thread.sleep(100);
                }
                catch(Exception e) {
                    System.out.println("Exception" + e.getMessage());
                }
            }
        }

        /*InputStream input = null;
                while(true) {
            try {
                socketList.add(new Socket("127.0.0.1", 1800));
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
                    
                }*/
    }
    
    private void warmUpCLI() {
        Random rand = new Random();
    	System.out.println("Messenger .11."+ rand.nextInt(8192) +" warming up...");
        rand = null;
        System.out.println("-Checking for Internet connection...");
        if(checkConnection() == false)
            System.out.println("There appears to be no Internet connection, please check your connection. Local area network might be operational");
        else
            System.out.println("Internet connection up and running");
        System.out.println("-Starting incoming connection listener...");
        Receive r =new Receive();
        System.out.println("-Starting command line interface...");
        ConsoleInterface ci = new ConsoleInterface();
        System.out.println("Ready for operation. Please use command <help> for manual");
    }

    private void warmUpGUI() {
        GraphicalInterface gui = new GraphicalInterface();
        GraphicalInterface.appendText("[KONSOLE] Messenger .11 warming up...");
        GraphicalInterface.appendText("[KONSOLE] Checking for Internet connection...");
        if(checkConnection() == false)
            GraphicalInterface.appendText("[KONSOLE] There appears to be no Internet connection, please check your connection. Local area network might be operational");
        else
            GraphicalInterface.appendText("[KONSOLE] Internet connection up and running");
        GraphicalInterface.appendText("[KONSOLE] Starting incoming connection listener...");
        Receive r=new Receive();
        GraphicalInterface.appendText("[KONSOLE] Starting command line interface...");
        ConsoleInterface ci = new ConsoleInterface();
        GraphicalInterface.appendText("[KONSOLE] Ready for operation. Please use command <help> for manual");
        GraphicalInterface.callOut();
    }

// Checks for internet connection //
    private boolean checkConnection() {
             return false;
    }

    public static void addDataLink(String name) {
        dataLink.put(name, new DataStore(name));
    }

    public static DataStore getDataStore(String name) {
        return dataLink.get(name);
    }

    public static boolean containsDSKey(String key) {
        return dataLink.containsKey(key);
    }

    public static void addSocket(String name, Socket socket) {
        socketMap.put(name, socket);
    }

    public static Socket getSocket(String name) {
        return socketMap.get(name);
    }

    public static boolean containsSocketKey(String key) {
        return socketMap.containsKey(key);
    }

    public static void replaceIPWithName(String ip, String name) {
        socketMap.put(name, socketMap.get(ip));
        socketMap.remove(ip);
        dataLink.put(name, dataLink.get(ip));
        System.out.println("Replaceing nick: " + ip + " with: " + name);
        GraphicalInterface.appendText("[KONSOLE] Replaceing nick: " + ip +
                                      " with: " + name);
    }

    public static void main(String[] args) {
        //MessengerMain MessengerMain = new MessengerMain(args[0]);
        MessengerMain MessengerMain = new MessengerMain();
    }

}
