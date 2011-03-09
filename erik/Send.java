package erik;

import java.nio.ByteBuffer;

public class Send {

    MessengerMain mm;


    private byte[] intToByte(int in) {
        return ByteBuffer.allocate(4).putInt(in).array();
    }

    public byte[] encodePacket(String msg) {
        byte[] nickBytes = mm.nick.getBytes();
        byte[] msgBytes = msg.getBytes();
        int size = 1 + 4 + nickBytes.length + 4 + msgBytes.length + 3;
        byte[] packet = new byte[size];
        packet[0] = (byte) 0;
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

    public boolean sendPacket(String ip, Byte[] packet) {
        return false;
    }
}
