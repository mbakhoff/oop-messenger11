package erik;

import java.net.*;

/**
 * @brief This class contains methods necessary to execute the will of the user
 * @discription Both user interfaces - command line and graphical will inherit
 * this class and use it to execute commands given by the user
 *
 * @author erik
 * @version 0.1
 */
public class Execution {

    MessengerMain mm;

// Can change your nickname //
    protected void setNick(String nickname) {

    }
// Can set new listening port //
    protected void setPort(int port) {

    }
// Prints out the socket information for nickname or IP //
    protected void getSocketInfo(String in) {

    }
// Prints out the current listening port //
    protected void getPort() {

    }
// Encodes the message into a packet and sends it to a socket //
// (socket is tied to a nickname) //
    protected void sendMessage(String dest, String msg) {

    }

// Opens a socket and adds it to the socket ArrayList //
    protected void openAndAddSocket(String ip, int port) {
        Socket sock = null;
        try {
            sock = new Socket(ip, port);
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }

    	mm.socketList.add(sock);
    	mm.nameList.add(sock.getInetAddress().toString());
    }

// Binds IP aadress to the specified nickname //
// Returns whether the operation was a success or not //
    protected void bindIpToNickname(String ip, String nick) {

    }

// Clears the promt of old messages //
    protected void clear() {
        
    }

// Prints commands and syntaxes on screen //
    protected void help() {

    }

// System exit method //
    protected void systemExit() {
        System.out.println("System shutting down...");
        System.exit(0);
    }


// Splits on space and get the first value, return rest in original syntax //
    public String[] getFirstToken(String input) {
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
}
