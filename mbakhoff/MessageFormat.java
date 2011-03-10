package mbakhoff;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.ByteBuffer;

public class MessageFormat {

	protected static final byte PKT_MESSAGE = 1;
	protected static final byte PKT_ALIVE   = 2;
	
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
		byte[] nickBytes = null;
		byte[] msgBytes = null;
		try {
			nickBytes = nick.getBytes("UTF8");
			msgBytes = msg.getBytes("UTF8");
		} catch (UnsupportedEncodingException e) {
			System.err.println("FATAL: UTF8 not supported. Get a OS, noob ");
			System.exit(1);
		}
		int len = 1+4+nickBytes.length+4+msgBytes.length+3;
		byte[] data = new byte[len];
		data[0] = MessageFormat.PKT_MESSAGE;
		System.arraycopy(
				intToBytes(nickBytes.length), 0, 
				data, 1, 4);
		System.arraycopy(
				nickBytes, 0, 
				data, 5, nickBytes.length);
		System.arraycopy(
				intToBytes(msgBytes.length), 0, 
				data, 5+nickBytes.length, 4);
		System.arraycopy(
				msgBytes, 0, 
				data, 5+nickBytes.length+4, msgBytes.length);
		data[len-3] = 0;
		data[len-2] = 0;
		data[len-1] = 0;
		return data;
	}
	
	public static byte[] createAliveMessage() {
		return new byte[] { PKT_ALIVE, 0, 0, 0 };
	}
	
	public static void dissectStream(Socket soc, ConnectionManager mgr) {
		try {
			InputStream in = soc.getInputStream();
			byte type = (byte)in.read();
			switch (type) {
			case MessageFormat.PKT_MESSAGE: {
					if(!dissectStreamMessage(soc, in, mgr)) {
						System.out.println("MessageFormat: killing socket "+
								soc.getRemoteSocketAddress());
						soc.close();
					} 
					break;
				}
			case MessageFormat.PKT_ALIVE: {
				if(!dissectStreamAlive(soc, in, mgr)) {
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
	
	protected static boolean dissectStreamAlive(
			Socket soc, InputStream in, ConnectionManager mgr) 
	{
		System.out.println("DEBUG: MessageFormat: dissecting new alive "+
				"from "+soc.getRemoteSocketAddress());
		byte[] buf = new byte[3];
		int r = 0;
		try {
			r = readBytes(in, buf, 3, 300);
			if (r < 4) {
				System.out.println("MessageFormat:dissectStreamAlive: "+
						"could not read zero3: timeout (read "+r+"/3)");
				return false;
			}
		} catch (Exception e) {
			System.out.println("MessageFormat:dissectStreamAlive: "+
					"could not read zero3: exception: "+e.getMessage());
			return false;
		}
		if (buf[0] == 0 && buf[1] == 0 && buf[2] == 0) {
			return true;
		} else {
			System.out.println("MessageFormat:dissectStreamAlive: "+
					"zero3 != {0,0,0}");
			return false;
		}
	}
	
	// TODO: refactor badly needed
	@SuppressWarnings("unused") // eclipse is dumb
	protected static boolean dissectStreamMessage(
			Socket soc, InputStream in, ConnectionManager mgr) 
	{
		System.out.println("DEBUG: MessageFormat: dissecting new message "+
				"from "+soc.getRemoteSocketAddress());
		byte[] buf = new byte[4];
		byte[] nickBytes = null;
		byte[] msgBytes = null;
		int nickLength = -1;
		int msgLength = -1;
		int r = 0;
		// read nick length
		try {
			r = readBytes(in, buf, 4, 300);
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
		nickLength = bytesToInt(buf);
		nickBytes = new byte[nickLength];
		// read nick
		try {
			r = readBytes(in, nickBytes, nickLength, 1000);
			if (r < nickLength) {
				System.out.println("MessageFormat:dissectStreamMessage: "+
					"could not read nick: timeout (read "+r+"/"+nickLength+")");
				return false;
			}
		} catch (Exception e) {
			System.out.println("MessageFormat:dissectStreamMessage: "+
				"could not read nick: exception: "+e.getMessage());
			return false;
		}
		// read msg length
		try {
			r = readBytes(in, buf, 4, 300);
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
		msgLength = bytesToInt(buf);
		msgBytes = new byte[msgLength];
		// read msg
		try {
			r = readBytes(in, msgBytes, msgLength, 1000);
			if (buf == null) {
				System.out.println("MessageFormat:dissectStreamMessage: "+
					"could not read msg: timeout (read "+r+"/"+msgLength+")");
				return false;
			}
		} catch (Exception e) { 
			System.out.println("MessageFormat:dissectStreamMessage: "+
				"could not read msg: exception: "+e.getMessage());
			return false;
		}
		// read end0
		try {
			r = readBytes(in, buf, 3, 300);
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
		if (buf[0]==0 && buf[1]==0 && buf[2]==0) {
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
			System.out.println(
					String.format("MessageFormat:dissectStreamMessage: "+
							"end0 missing (%x,%x,%x). stream out of sync? ", 
							buf[0], buf[1], buf[2]));
			return false;
		}
	}
	
}
