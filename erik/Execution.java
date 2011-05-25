package erik;

import java.net.*;

/**
 * @brief This class contains methods necessary to execute the will of the user
 * @description Both user interfaces - command line and graphical will inherit
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
        Send.sendInput(msg, dest);
    }

// Opens a socket and adds it to the socket socketMap //
    protected void openAndAddSocket(String ip, int port) {
        Socket sock = null;
        try {
            sock = new Socket(ip, port);
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }

    	MessengerMain.addSocket(ip, sock);

        System.out.println("Socket opened: " + 
                    MessengerMain.getSocket(ip).getInetAddress().toString());
        GraphicalInterface.appendText("[KONSOLE] Socket opened: " +
                    MessengerMain.getSocket(ip).getInetAddress().toString());
    }

// Binds IP aadress to the specified nickname //
    protected void bindIpToNick(String ip, String nick) {
        MessengerMain.replaceIPWithName(ip, nick);
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
        GraphicalInterface.appendText("[KONSOLE] System shutting down...");
        System.exit(0);
    }

// Splits on space and get the first value, return rest in original syntax //
// kammoon
	public String[] getFirstToken(String input) {
		input = input.trim();
		if (input.contains(" ")) {
			int spos = input.indexOf(" ");
			return new String[] {
					input.substring(0, spos),
					input.substring(spos+1)
			};
		} else {
			return new String[] {input};
		}
	}
}
