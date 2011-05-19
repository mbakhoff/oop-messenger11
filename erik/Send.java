package erik;

/**
 * @brief This class contains methods necessary to encode and send messages to
 * others
 * @description Only methods needed are sendInput for sending messages and
 * sendAlive to check the connection. Rest of the methods are private and needed
 * only internally.
 *
 * @author erik
 * @version 0.1
 */

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.util.Calendar;

public class Send {

    private static OutputStream out = null;
    private static Calendar cal = Calendar.getInstance();
    private static DateFormat df = DateFormat.getDateTimeInstance(
                                           DateFormat.SHORT, DateFormat.MEDIUM);

// Sends packet to nick and than saves info to log //
    public static void sendInput(String msg, String receiver) {
        sendPacket(MessengerMain.getSocket(receiver), encodePacket(msg));
        
        MessengerMain.getDataStore(receiver).writeData(msg, df.format(cal.getTime()), MessengerMain.nick, receiver);
    }

// Sends the alive packet to check if connection is alive //
    public static void sendAlive(String receiver) {
        sendPacket(MessengerMain.getSocket(receiver), alivePacket());
    }

// Converts integer to byte array //
    private static byte[] intToByte(int in) {
        return ByteBuffer.allocate(4).putInt(in).array();
    }

// Assembles packet - syntax: //
//[packet type][nick length][nick][msg length][msg][endbytes] //
    private static byte[] encodePacket(String msg) {
        byte[] nickBytes = null;
        byte[] msgBytes = null;
        try {
            nickBytes = MessengerMain.nick.getBytes("UTF8");
            msgBytes = msg.getBytes("UTF8");
        } catch (UnsupportedEncodingException ex) {
            System.out.println("Invalid encoding: " + ex.getMessage());
        }
        int size = 1 + 4 + nickBytes.length + 4 + msgBytes.length + 3;
        byte[] packet = new byte[size];
        packet[0] = (byte) 1;
        byte[] packetend = {0x00, 0x00, 0x00};
        int pc = 1;

        System.arraycopy(intToByte(nickBytes.length), 0, packet, pc, 4);
        pc += 4;
        System.arraycopy(nickBytes, 0, packet, pc, nickBytes.length);
        pc += nickBytes.length;
        System.arraycopy(intToByte(msgBytes.length), 0, packet, pc, 4);
        pc += 4;
        System.arraycopy(msgBytes, 0, packet, pc, msgBytes.length);
        pc +=msgBytes.length;
        System.arraycopy(packetend, 0, packet, pc, 3);

        return packet;
}

// @@@TODO@@ Assembles packet to check if socket is alive - checkalive packet //
    private static byte[] alivePacket() {
        return new byte[] {2, 0, 0, 0};
    } 

// Sends user defined byte packet to user defined socket //
    private static void sendPacket(Socket sock, byte[] packet) {
        try {
            out = sock.getOutputStream();
            out.write(packet);
            //out.close();
            System.out.println("debug: packet away");
        } catch (IOException ex) {
            System.out.println("Failed to send packet: " + ex.getMessage());
        }
    }
}