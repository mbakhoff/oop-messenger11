package kristina;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.io.OutputStream;

public class UserSide {
	
	public static byte[] composeMessage(String message, String nickname) {
		String line = message;
		
		byte id = 1;
		
		byte[] b_nick = null;
		int nick_len = -1;
		byte[] b_nick_len = null;
		byte[] msg = null;
		int msg_len = -1;
		byte[] b_msg_len = null;
		
		try{
			b_nick = nickname.getBytes("UTF-8");
			nick_len = b_nick.length;
			b_nick_len = ByteBuffer.allocate(4).putInt(nick_len).array();
			
			msg = line.getBytes("UTF-8");
			msg_len = msg.length;
			b_msg_len = ByteBuffer.allocate(4).putInt(msg_len).array();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		int total_len = 1 + 4 + nick_len + 4 + msg_len + 3;
		
		byte[] b = new byte[total_len];
		b[0] = id;
		System.arraycopy(b_nick_len, 0, b, 1, 4);
		System.arraycopy(b_nick, 0, b, 5, nick_len);
		int b_pos = 1 + 4 + nick_len;
		System.arraycopy(b_msg_len, 0, b, b_pos, 4);
		b_pos += 4;
		System.arraycopy(msg, 0, b, b_pos, msg_len);
		b_pos += msg_len;
		b[b_pos] = (byte) 0;
		b[b_pos+1] = (byte) 0;
		b[b_pos+2] = (byte) 0;
		
		return b;
	}
		
	public static void sendMessage(byte[] message, Socket s) {
		OutputStream out;
		try {
			out = s.getOutputStream();
			out.write(message);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
