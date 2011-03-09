package erik;

import java.io.*;
import java.net.*;
import java.util.*;

public class MessengerMain {

    public static int com_port = 0;
    public static String nick = "erik";
    private Scanner scn = new Scanner(System.in);
    private String[] commands = {"exit", "help", "setnick", "msg", "msgip", "setport", "bind", "getip", "currentport", "clear"};

// Main runtime //
    private MessengerMain() {

        
        System.out.println("Messenger .11 preparing...");
        System.out.println("Checking Internet connection...");
        if(checkConnection() == false)
            System.out.println("There appears to be no Internet connection, please check your connection. Local area network may be operational.");
        else
            System.out.println("Internet connection up and running.");
        System.out.println("Ready for operation. Please use command \"help\" for manual");
        while(true) {
            int command = -1;
            String[] tokens = getFirstToken(scn.nextLine());

            if(tokens[0].length() > 0)
                command = idCommand(tokens[0]);
            if(command == -1)
                System.out.println("Command not recognized. Please use command \"help\" for manual");
            else {
                switch(command) {
// Commands execution here //
                    case 0:
                        systemExit();
                    case 1:

                    case 2:

                    case 3:
                        if(tokens.length < 2)
                            System.out.println("Please insert message you want to send");
                        else
                            System.out.println(tokens[1]);
                    case 4:

                }
            }
        }
    }
// Splits on space and get the first value, return rest in original syntax //
    private String[] getFirstToken(String input) {
        String[] tolkens = input.split(" ");
        String[] returnValues = new String[1];
        String[] returnValuesB = new String[2];

        returnValues[0] = tolkens[0];
        returnValuesB[0] = tolkens[0];
        returnValuesB[1] = "";

        if (tolkens.length > 1) {
            for(int i=1; i<tolkens.length; i++) {
                returnValuesB[1] = returnValuesB[1] + tolkens[i] + " ";
                returnValuesB[1].trim();
            }
            return returnValuesB;
        }
        else {
            return returnValues;
        }
    }
// Command identification method //
    private int idCommand(String input) {
        for(int i=0; i<commands.length; i++) {
            if(input.equals(commands[i])) {
                return i;
            }
        }
        return -1;
    }
// Checks for internet connection //
    private boolean checkConnection() {
             return false;
    }
    
    public void gotMsg(String nick, String msg) {
        
    }

    public Socket openSocket(String ip, int port) {

        try {
            Socket sock = new Socket(ip, port);
            return sock;
        }
        catch (UnknownHostException e) {
            System.out.println("Connection failed: " + e.getMessage());
            return null;
        }
        catch (IOException e2) {
            System.out.println("Connection failed: " + e2.getMessage());
            return null;
        }
    }




// System exit method //
    private void systemExit() {
        System.out.println("System shutting down...");
        System.exit(0);
    }

    public static void main(String[] args) {
        MessengerMain MessengerMain = new MessengerMain();
    }

}
