package mbakhoff;

import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class MessageFormat {

	protected static final byte PKT_MESSAGE = 1;
	
	protected static byte[] intToBytes(int i) {
		return ByteBuffer.allocate(4).putInt(i).array();
	}
	
	protected static int bytesToInt(byte[] b) {
		return ByteBuffer.allocate(4).put(b).getInt(0);
	}
	
	protected static int readBytes(InputStream in, 
			byte[] buf, int len, long timeout) throws Exception {
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
	
	public static byte[] createMessagePacket(String nick, String msg) {
		int nickBytes = nick.getBytes().length;
		int msgBytes = msg.getBytes().length;
		int len = 1 + 4 + nickBytes + 4 + msgBytes + 3;
		byte[] data = new byte[len];
		data[0] = MessageFormat.PKT_MESSAGE;
		System.arraycopy(
				intToBytes(nickBytes), 0, 
				data, 1, 4);
		System.arraycopy(
				nick.getBytes(), 0, 
				data, 5, nickBytes);
		System.arraycopy(
				intToBytes(msgBytes), 0, 
				data, 5+nickBytes, 4);
		System.arraycopy(
				msg.getBytes(), 0, 
				data, 5+nickBytes+4, msgBytes);
		data[len-3] = 0;
		data[len-2] = 0;
		data[len-1] = 0;
		return data;
	}
	
	public static void dissectStream(Socket soc, ConnectionManager mgr) {
		try {
			InputStream in = soc.getInputStream();
			byte type = (byte)in.read();
			switch (type) {
			case MessageFormat.PKT_MESSAGE: {
					if(!dissectStreamMessage(soc, mgr)) {
						System.out.println("MessageFormat: killing socket "+
								soc.getRemoteSocketAddress());
						soc.close();
					} 
					break;
				}
			default: {
					System.out.println("MessageFormat:dissectStream: "+
							"EOF or invalid type byte: "+type+"; killing "+
							"socket "+soc.getRemoteSocketAddress());
					soc.close();
				}
			}
		} catch (Exception e) {
			System.out.println("MessageFormat:dissectStream: "+
					"could not read type byte: "+e.getMessage());
		}
	}
	
	@SuppressWarnings("unused") // eclipse is dumb
	protected static boolean dissectStreamMessage(
			Socket soc, ConnectionManager mgr) {
		InputStream in = null;
		try {
			in = soc.getInputStream();
		} catch (Exception e) {
			System.out.println();
		}
		byte[] intBuf = new byte[4];
		byte[] nick = null, msg = null;
		int nickBytes = -1;
		int msgBytes = -1;
		int r = 0;
		// read nick length
		try {
			r = readBytes(in, intBuf, 4, 300);
			if (r < 4) {
				System.out.println("MessageFormat:dissectStreamMessage: "+
						"could not read nick length: timeout (read "+r+"/4)");
				return false;
			}
		} catch (Exception e) {
			System.out.println("MessageFormat:dissectStreamMessage: "+
					"could not read nick length: exception: "+e.getMessage());
			return false;
		}
		nickBytes = bytesToInt(intBuf);
		nick = new byte[nickBytes];
		// read nick
		try {
			r = readBytes(in, nick, nickBytes, 1000);
			if (r < nickBytes) {
				System.out.println("MessageFormat:dissectStreamMessage: "+
					"could not read nick: timeout (read "+r+"/"+nickBytes+")");
				return false;
			}
		} catch (Exception e) {
			System.out.println("MessageFormat:dissectStreamMessage: "+
				"could not read nick: exception: "+e.getMessage());
			return false;
		}
		// read msg length
		try {
			r = readBytes(in, intBuf, 4, 300);
			if (r < 4) {
				System.out.println("MessageFormat:dissectStreamMessage: "+
					"could not read msg length: timeout (read "+r+"/4)");
				return false;
			}
		} catch (Exception e) {
			System.out.println("MessageFormat:dissectStreamMessage: "+
				"could not read msg length: exception: "+e.getMessage());
			return false;
		}
		msgBytes = bytesToInt(intBuf);
		msg = new byte[msgBytes];
		// read msg
		try {
			r = readBytes(in, msg, msgBytes, 1000);
			if (intBuf == null) {
				System.out.println("MessageFormat:dissectStreamMessage: "+
					"could not read msg: timeout (read "+r+"/"+msgBytes+")");
				return false;
			}
		} catch (Exception e) { 
			System.out.println("MessageFormat:dissectStreamMessage: "+
				"could not read msg: exception: "+e.getMessage());
			return false;
		}
		// read end0
		try {
			r = readBytes(in, intBuf, 3, 300);
			if (r < 3) {
				System.out.println("MessageFormat:dissectStreamMessage: "+
					"could not read end0: timeout (read "+r+"/3)");
				return false;
			}
		} catch (Exception e) {
			System.out.println("MessageFormat:dissectStreamMessage: "+
				"could not read end0: exception: "+e.getMessage());
			return false;
		}
		// check for end0
		if (intBuf[0]==0 && intBuf[1]==0 && intBuf[2]==0) {
			// map nick
			mgr.mapNick(new String(nick), soc.getInetAddress().getHostAddress());
			// announce message
			mgr.messageReceived(new String(nick), new String(msg));
			return true;
		} else {
			System.out.println(
					String.format("MessageFormat:dissectStreamMessage: "+
							"end0 missing (%x,%x,%x). stream out of sync? ", 
							intBuf[0], intBuf[1], intBuf[2]));
			return false;
		}
	}
	
}
