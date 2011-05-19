package erik;

import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author erik
 */
public class ConsoleInterface extends Execution implements Runnable{

    private String[] commands = {"exit", "help", "setnick", "msg", "anosock", "setport", "bind", "getip", "curport", "clear"};
    private Scanner scn = null;
    MessengerMain mm;
    Thread asd = null;

    public ConsoleInterface() {
        asd = new Thread(this);
        asd.start();
        System.out.println("Command line interface thread started");
    }


    public void run() {
        while(true) {
            scn = new Scanner(System.in, "UTF8");
                try {
                    interrupt(scn);
                }
                catch(Exception e) {

                }
            }
    }

    public void interrupt(Scanner scn){
        int command = -1;
        String[] tokens = getFirstToken(scn.nextLine());

        if(tokens[0].length() > 0)
            command = idCommand(tokens[0]);
        if(command == -1)
            System.out.println("Command not recognized. Please use command <help> for manual");
        else {
            if(tokens.length == 2)
                    execute(command, tokens[1]);
            else
                    execute(command, null);
    }
    }

// Command identification method //
    public int idCommand(String input) {
        for(int i=0; i<commands.length; i++) {
            if(input.equals(commands[i])) {
                return i;
            }
        }
        return -1;
    }

    public void execute(int id, String info) {
    	switch(id) {
    	// Commands execution here //
            case 0:
                systemExit();
            case 1:

            case 2:

            case 3:
                if(info == null)
                    System.out.println("Correct syntax is: msg <reveicer nickname> <message>");
                else {
                        String[] tokens = getFirstToken(info);
                        //Send.sendPacket(MessengerMain.socketList.get(1), Send.encodePacket(tokens[1]));
                }

            case 4:
                String[] token = getFirstToken(info);
                openAndAddSocket(token[0], mm.com_port);
            case 5:

            case 6:

            case 7:

            case 8:

            case 9:

            case 10:


        }
    }


}
