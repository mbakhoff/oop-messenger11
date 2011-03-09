package erik;

import java.nio.ByteBuffer;

public class Receive {


    private int byteToInt(byte[] in) {
        return ByteBuffer.wrap(in).getInt();
    }

    public String decodePacket(Byte[] packet) {
        String msg = null;

        return msg;
    }
}
