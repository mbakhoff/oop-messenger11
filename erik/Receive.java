package erik;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

public class Receive implements Runnable {

    ServerSocket servSock = null;
    MessengerMain mm;

    public Receive() {
        try {
            servSock = new ServerSocket(1800);
            new Thread(this).start();
            System.out.println("Listening thread started. Now listening on port: " + MessengerMain.com_port);
        }
        catch (IOException ex) {
        	System.out.println("ServerSocket not started:" + ex.getMessage());
        }
    }

// Converts byte array to integer (used to get the length of nickname and message) //
    private static int byteToInt(byte[] in) {
        return ByteBuffer.wrap(in).getInt();
    }
// Disassembles packet - array syntax: [packet type][nick length][nick][msg length][msg][endbytes] //
    public static void decodePacket(InputStream in) {
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
                if(inBuf[9+nickLen+msgLen] == 0 && inBuf[10+nickLen+msgLen] == 0 && inBuf[11+nickLen+msgLen] == 0 && 12+nickLen+msgLen == inBuf.length) {
                    String nick = new String(nickBuf);
                    String msg = new String(msgBuf);
                    System.out.println(nick + " says: " + msg);
                }
                else
                    System.out.println("EndBytes not correct");
        }
        else if(inBuf[0] == 2)
            ;
    }
// Main loop - listens for incoming connections //
    public void run() {
        while(true) {
            try {
                Socket s = servSock.accept();
                MessengerMain.socketList.add(s);
                //System.out.println(MessengerMain.socketList.size());
                System.out.println("Incoming socket from: " + s.getInetAddress() + " port: " + s.getPort());
            }
            catch (Exception ex) {
                System.out.println("Error:" + ex.getMessage());
            }
        }
    }
}
