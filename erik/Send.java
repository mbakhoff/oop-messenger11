package erik;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

public class Send {

    public static OutputStream out = null;
    
// Converts integer to byte array //
    private static byte[] intToByte(int in) {
        return ByteBuffer.allocate(4).putInt(in).array();
    }
 // Assembles packet - syntax: [packet type][nick length][nick][msg length][msg][endbytes] //
    public static byte[] encodePacket(String msg) {
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
// Assembles packet to check if socket is alive - checkalive packet //
    public static byte[] alivePacket() {
        return new byte[] {2, 0, 0, 0};
    } 
// Sends user defined byte packet to user defined socket //
    public static void sendPacket(Socket sock, byte[] packet) {
        try {
            out = sock.getOutputStream();
            out.write(packet);
            System.out.println("Packet sent to: " + sock.getInetAddress().toString() + " port: " + sock.getPort()
            		+ " from: " + sock.getLocalAddress() + " port: " + sock.getLocalPort());
        } catch (IOException ex) {
            System.out.println("Failed to send packet: " + ex.getMessage());
        }
    }
}
