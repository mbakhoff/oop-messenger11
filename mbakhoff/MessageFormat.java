package mbakhoff;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class MessageFormat {

	protected static final byte PKT_MESSAGE = 1;
	protected static final byte PKT_ALIVE   = 2;
	protected static final byte[] ZERO3 = new byte[] {0,0,0};
	
	protected static byte[] intToBytes(int i) {
		return ByteBuffer.allocate(4).putInt(i).array();
	}
	
	protected static int bytesToInt(byte[] b) {
		return ByteBuffer.allocate(4).put(b).getInt(0);
	}
	
	protected static int readBytes(InputStream in, 
			byte[] buf, int len, long timeout) throws Exception 
	{
		if (buf == null)
			throw new Exception("MessageFormat.readBytes: unallocated buf");
		int read = 0;
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < timeout && read != len) {
			read += in.read(buf, read, len-read);
			Thread.sleep(25);
		}
		return read;
	}
	
	protected static byte[] readBlock(InputStream in, 
			int len, int timeout, String display)
	{
		byte[] buf = new byte[len];
		int r = -1;
		try {
			r = readBytes(in, buf, len, timeout);
			if (r < len) {
				System.out.println(String.format(
						"WARNING: MessageFormat: readBlock/%s failed: "+
						"timeout (read %d/%d in %d", display, r, len, timeout));
				return null;
			}
		} catch (Exception e) {
			System.out.println(String.format(
					"WARNING: MessageFormat: readBlock/%s failed: "+
					"exception occurred: %s", e.getMessage()));
			return null;
		}
		return buf;
	}
	
	public static byte[] createMessagePacket(String nick, String msg) {
		byte[] nickBytes = null;
		byte[] msgBytes = null;
		int ptr = 0;
		try {
			nickBytes = nick.getBytes("UTF8");
			msgBytes = msg.getBytes("UTF8");
		} catch (UnsupportedEncodingException e) {
			System.err.println("FATAL: UTF8 not supported. Get a OS, noob ");
			System.exit(1);
		}
		byte[] data = new byte[12 + nickBytes.length + msgBytes.length];
		data[0] = MessageFormat.PKT_MESSAGE;
		ptr += 1;
		System.arraycopy(intToBytes(nickBytes.length), 0, data, ptr, 4);
		ptr += 4;
		System.arraycopy(nickBytes, 0, data, ptr, nickBytes.length);
		ptr += nickBytes.length;
		System.arraycopy(intToBytes(msgBytes.length), 0, data, ptr, 4);
		ptr += 4;
		System.arraycopy(msgBytes, 0, data, ptr, msgBytes.length);
		ptr += msgBytes.length;
		System.arraycopy(ZERO3, 0, data, ptr, ZERO3.length);
		ptr += ZERO3.length;
		return data;
	}
	
	public static byte[] createAliveMessage() {
		return new byte[] { PKT_ALIVE, 0, 0, 0 };
	}
	
	public static boolean dissectStream(Socket soc, ConnectionManager mgr) {
		try {
			InputStream in = soc.getInputStream();
			byte type = (byte)in.read();
			switch (type) {
			case MessageFormat.PKT_MESSAGE: 
				if(dissectStreamMessage(soc, in, mgr))
					return true;
			case MessageFormat.PKT_ALIVE: 
				if(dissectStreamAlive(soc, in, mgr))
					return true;
			default: 
				System.out.println("DEBUG: MessageFormat: dissectStream: "+
						"EOF or invalid type byte: "+type);
			}
		} catch (Exception e) {
			System.out.println("DEBUG: MessageFormat: dissectStream: "+
					"could not read type byte: "+e.getMessage());
		}
		return false;
	}
	
	protected static boolean dissectStreamAlive(
			Socket soc, InputStream in, ConnectionManager mgr) 
	{
		System.out.println("DEBUG: MessageFormat: dissecting new alive "+
				"from "+soc.getRemoteSocketAddress());
		byte[] buf = MessageFormat.readBlock(in, 3, 300, "zero3");
		if (buf == null)
			return false;
		// check for end0; finalize
		if (Arrays.equals(buf, ZERO3)) {
			return true;
		} else {
			System.out.println(String.format("WARNING: MessageFormat: "+
					"zero3 = {%x,%x,%x}", buf[0], buf[1], buf[2]));
			return false;
		}
	}
	
	protected static boolean dissectStreamMessage(
			Socket soc, InputStream in, ConnectionManager mgr) 
	{
		System.out.println("DEBUG: MessageFormat: dissecting new message "+
				"from "+soc.getRemoteSocketAddress());
		byte[] buf = null;
		byte[] nickBytes = null;
		byte[] msgBytes = null;
		int nickLength = -1;
		int msgLength = -1;
		// read nick length
		buf = MessageFormat.readBlock(in, 4, 300, "nickLength");
		if (buf == null)
			return false;
		nickLength = bytesToInt(buf);
		// read nick
		nickBytes = MessageFormat.readBlock(in, nickLength, 1000, "nickBytes");
		if (nickBytes == null)
			return false;
		// read msg length
		buf = MessageFormat.readBlock(in, 4, 300, "msgLength");
		if (buf == null)
			return false;
		msgLength = bytesToInt(buf);
		// read msg
		msgBytes = MessageFormat.readBlock(in, msgLength, 1000, "msgBytes");
		if (msgBytes == null)
			return false;
		// read end0
		buf = MessageFormat.readBlock(in, 3, 300, "zero3");
		if (buf == null)
			return false;
		// check for end0; finalize
		if (Arrays.equals(buf, ZERO3)) {
			String nick = null;
			String msg = null;
			try {
				nick = new String(nickBytes, "UTF8");
				msg = new String(msgBytes, "UTF8");
			} catch (UnsupportedEncodingException e) {
				System.err.println("FATAL: UTF8 not supported. Get a OS, noob");
				System.exit(1);
			}
			// map nick
			mgr.mapNick(nick, soc.getInetAddress().getHostAddress());
			// announce message
			mgr.messageReceived(nick, msg);
			return true;
		} else {
			System.out.println(String.format("MessageFormat: error: "+
					"zero3 = {%x,%x,%x}", buf[0], buf[1], buf[2]));
			return false;
		}
	}
	
}
