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
						System.out.println(e.getMessage());
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
						System.out.println(e.getMessage());
					}
				}
				System.exit(0);
			}
		}
		
		else if (sockets.size() == 0) {
			System.out.println("You are not currently connected to anybody.");
		}
		
		else if (messageArray[0].equals("-alive")) {
			byte id = 2;
			byte[] b = new byte[4];
			b[0] = id;
			b[1] = 0;
			b[2] = 0;
			b[3] = 0;
			
			OutputStream out;
			
			synchronized (lock) {
				for (int i = 0; i < sockets.size(); i++) {
					try {
						out = sockets.elementAt(i).getOutputStream();
						out.write(b);
						out.flush();
					} catch (Exception e) {
						System.out.println(e.getMessage());
						sockets.remove(i);
					}
				}
			}
		}
		
		else {
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
			
			OutputStream out;
			
			synchronized (lock) {
				for (int i = 0; i < sockets.size(); i++) {
					try {
						out = sockets.elementAt(i).getOutputStream();
						out.write(b);
					} catch (Exception e) {
						System.out.println(e.getMessage());
						sockets.remove(i);
					}
				}
			}
		}
	}
}
