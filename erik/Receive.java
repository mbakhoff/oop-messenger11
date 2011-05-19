package erik;

/**
 * @brief This class contains a thread to monitor incoming connections and
 * various methods to decode received package and deal with it.
 * @description ???
 *
 * @author erik
 * @version 0.1
 */

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.util.Calendar;

public class Receive implements Runnable {

    private ServerSocket servSock = null;
    private final Object locked = new Object();
    private static boolean check = false;
    private static boolean check2 = false;
    private static Calendar cal = Calendar.getInstance();
    private static DateFormat df = DateFormat.getDateTimeInstance(
                                    DateFormat.SHORT, DateFormat.MEDIUM);
    private static String time = null;

// Main constructor of the class, starts up monitoring thread //
    public Receive() {
        try {
            servSock = new ServerSocket(MessengerMain.com_port);
            new Thread(this).start();
            System.out.println("Listening thread started. Now listening on port: " + MessengerMain.com_port);
            GraphicalInterface.appendText("[KONSOLE] Listening thread started. Now listening on port: " + MessengerMain.com_port);
        }
        catch (IOException ex) {
        }
    }

// Converts byte array to integer //
    private static int byteToInt(byte[] in) {
        return ByteBuffer.wrap(in).getInt();
    }

// Disassembles packet - array syntax: //
// [packet type][nick length][nick][msg length][msg][endbytes] //
    public static void decodePacket(InputStream in, String ip) {

        byte[] inBuf = null;
        int nickLen = -1;
        byte[] nickBuf = null;
        int msgLen = -1;
        byte[] msgBuf = null;

        try {
             inBuf = new byte[in.available()];
             in.read(inBuf);
        }
        catch (Exception e) {
            System.out.println("InputStream reading failed: " + e.getMessage());
        }
        if(inBuf[0] == 1) {
            nickBuf = new byte[4];
            for(int i=0;i<4;i++) {
                nickBuf[i] = inBuf[i+1];
            }
            nickLen = byteToInt(nickBuf);
            nickBuf = new byte[nickLen];
            for(int i=0;i<nickLen;i++) {
                nickBuf[i] = inBuf[i+5];
            }
            msgBuf = new byte[4];
            for(int i=0;i<4;i++) {
                msgBuf[i] = inBuf[i+5+nickLen];
            }
            msgLen = byteToInt(msgBuf);
            msgBuf = new byte[msgLen];
            for(int i=0;i<msgLen;i++) {
                msgBuf[i] = inBuf[i+9+nickLen];
            }
            if(inBuf[9+nickLen+msgLen] == 0 && inBuf[10+nickLen+msgLen] == 0 && inBuf[11+nickLen+msgLen] == 0 /*&& 12+nickLen+msgLen == inBuf.length*/) {
                postResults(new String(msgBuf), new String(nickBuf), ip);
                //postResults(msgBuf.toString(), nickBuf.toString());
            }
            else {
                System.out.println("EndBytes not correct");
            }
        }
        else if(inBuf[0] == 2)
            ;
    }

// Prints out results to the screen and sends results to log //
    private static void postResults(String msg, String sender, String ip) {
        check = MessengerMain.containsSocketKey(ip);
        check2 = MessengerMain.containsSocketKey(sender);
        time = df.format(cal.getTime());

        if (check == true && check2 == false) {
            MessengerMain.replaceIPWithName(ip, sender);
        }
        System.out.println(time + " - " + sender + " says: " + msg);
        GraphicalInterface.appendText(sender + " says: " + msg);
        MessengerMain.getDataStore(sender).writeData(
                    msg, time, sender, MessengerMain.nick);
    }

// Starts monitoring thread //
    public void run() {
        while(true) {
            try {
                Socket s = servSock.accept();
                MessengerMain.addSocket(s.getInetAddress().toString(), s);
                System.out.println("Incoming socket from: " + s.getInetAddress()
                + " port: " + s.getPort() + " current nick set to: "
                + s.getInetAddress().toString());
                GraphicalInterface.appendText("[KONSOLE] Incoming socket from: "
                                              + s.getInetAddress()
                + " port: " + s.getPort() + " current nick set to: "
                + s.getInetAddress().toString());
            }
            catch (Exception ex) {
                System.out.println("Error accepting incoming socket: " + ex.getMessage());
            }
        }
    }
}