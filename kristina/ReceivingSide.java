package kristina;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Vector;
import java.io.InputStream;

public class ReceivingSide implements Runnable{
	
	Vector<Socket> sockets = null;
	ServerSocket servSock = null;
	Object lock = null;
	
	public ReceivingSide(Vector<Socket> sockets, Object lock) {
		this.lock = lock;
		synchronized (this.lock) {
			try {
				this.sockets = sockets;
				servSock = new ServerSocket(1800);
				new Thread(this).start();
			} catch (Exception e) {
				e.getMessage();
			}
		}
	}
	
	public void run() {
		while (true) {
			try {
				Socket newSocket = servSock.accept();
				synchronized (lock) {
					for (Socket s : sockets) {
						if (s.getInetAddress().getHostAddress().equals( 
							newSocket.getInetAddress().getHostAddress())) {
								continue;
						}
					}
					sockets.add(newSocket);
				}
				System.out.println("Incoming connection from " + 
						newSocket.getInetAddress().getHostAddress() + 
						". Connection established.");
			} catch (Exception e) {
				e.getMessage();
			}
		}
	}
	
	public static void incomingMessagesParser(InputStream input) {
		
		int id;
		byte[] b_id = new byte[1];
		int nick_len;
		byte[] b_nick_len = new byte[4];
		String nick;
		int msg_len;
		byte[] b_msg_len = new byte[4];
		String msg;
		
		while (true) {
			try {
				if (input.available() >= 1) {
					input.read(b_id, 0, 1);
					break;
				}
				else {
					try {
						Thread.sleep(200);
					} catch (Exception e) {
						e.getMessage();
					}
				}
			} catch (Exception e) {
					e.getMessage();
			}
		}
		
		id = b_id[0];
		
		if (id == 1) {
			try{
			while (true) {
				if (input.available() >= 4) {
					input.read(b_nick_len, 0, 4);
					break;
				} else {
					try {
						Thread.sleep(200);
					} catch (Exception e) {
						e.getMessage();
					}
				}
			}
			
			nick_len = ByteBuffer.allocate(4).put(b_nick_len).getInt(0);
			
			byte[] b_nick = new byte[nick_len];
			while (true) {
				if (input.available() >= nick_len) {
					input.read(b_nick, 0, nick_len);
					break;
				} else {
					try {
						Thread.sleep(200);
					} catch (Exception e) {
						e.getMessage();
					}
				}
			}
			
			nick = new String(b_nick);
			
			while (true) {
				if (input.available() >= 4) {
					input.read(b_msg_len, 0, 4);
					break;
				} else {
					try {
						Thread.sleep(200);
					} catch (Exception e) {
						e.getMessage();
					}
				}
			}
			
			msg_len = ByteBuffer.allocate(4).put(b_msg_len).getInt(0);
			
			byte[] b_msg = new byte[msg_len];
			while (true) {
				if (input.available() >= msg_len) {
					input.read(b_msg, 0, msg_len);
					break;
				} else {
					try {
						Thread.sleep(200);
					} catch (Exception e) {
						e.getMessage();
					}
				}
			}
			
			msg = new String(b_msg);
			
			int ctr = 0;
			while (ctr < 3) {
				if (input.available() >= 1) {
					if (input.read() != 0) {
						System.out.println("Error: The message did not arrive properly.");
						return;
					}
					ctr++;
				} else {
					try {
						Thread.sleep(200);
					} catch (Exception e) {
						e.getMessage();
					}
				}
			}
			
			System.out.println(nick + " says: " + msg);
			} catch (Exception e) {
				e.getMessage();
			}
		}
	}
}
