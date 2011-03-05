package kristina;
import java.util.Scanner;
import java.util.Vector;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.io.OutputStream;

public class UserSide implements Runnable{
	
	Vector<Socket> sockets = null;
	String nickname;
	Scanner scanner;
	Object lock;
	
	public UserSide(Scanner userScanner, Vector<Socket> sockets, String nickname, Object lock) {
		this.lock = lock;
		this.sockets = sockets;
		this.nickname = nickname;
		this.scanner = userScanner;
		new Thread(this).start();
	}
	
	public void run() {
		while (true) {
			if (scanner.hasNextLine()) {
				this.userInputParsingMethod();
			}
		}
	}
	
	public void userInputParsingMethod() {
		String line = scanner.nextLine();
		
		String[] messageArray = line.split(" ");
		
		if (messageArray[0].equals("-connect")) {
			if (line.indexOf(" ") != -1) {
				synchronized (lock) {
					try {
						Socket sock = new Socket(messageArray[1], 1800);
						sockets.add(sock);
						System.out.println("Connection established.");
						return;
					} catch (Exception e) {
						e.getMessage();
					}
				}
			} else {
				System.out.println("No ip-address was entered after '-connect'");
			}
		}
		
		else if (messageArray[0].equals("-quit")) {
			synchronized (lock) {
				for (Socket s : sockets) {
					try {
						s.close();
					} catch (Exception e) {
						e.getMessage();
					}
				}
				System.exit(0);
			}
		}
		
		else {
			byte id = 1;
			
			byte[] b_nick = nickname.getBytes();
			int nick_len = b_nick.length;
			byte[] b_nick_len = ByteBuffer.allocate(4).putInt(nick_len).array();
			
			byte[] msg = line.getBytes();
			int msg_len = msg.length;
			byte[] b_msg_len = ByteBuffer.allocate(4).putInt(msg_len).array();
			
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
			
			OutputStream out;
			
			synchronized (lock) {
				for (Socket s : sockets) {
					try {
						out = s.getOutputStream();
						out.write(b);
					} catch (Exception e) {
						e.getMessage();
					}
				}
			}
		}
	}
}
