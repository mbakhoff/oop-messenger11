package kristina;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.io.InputStream;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JOptionPane;

public class ReceivingSide implements Runnable{
	
	ServerSocket servSock = null;
	Session session = null;
	
	public ReceivingSide(Session session) {
		this.session = session;
		synchronized (session.lock) {
			try {
				servSock = new ServerSocket(1800);
				new Thread(this).start();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	public void run() {
		while (true) {
			try {
				Socket newSocket = servSock.accept();
				synchronized (session.lock) {
					for (Socket s : session.sockets) {
						if (s.getInetAddress().getHostAddress().equals( 
							newSocket.getInetAddress().getHostAddress())) {
								continue;
						}
					}
					session.sockets.add(newSocket);
					if (session.tabPane.getTitleAt(0).equals("Main")) {
						session.tabPane.remove(0);
					}
					session.tabPane.addTab("ip", new mainPane(session));
				}
				JOptionPane.showMessageDialog(null, "Incoming connection from " + 
						newSocket.getInetAddress().getHostAddress() + 
						". Connection established.");
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	public static void incomingMessagesParser(InputStream input, JTabbedPane tabbedPane, int tabIndex) {
		
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
						System.out.println(e.getMessage());
					}
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
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
						System.out.println(e.getMessage());
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
						System.out.println(e.getMessage());
					}
				}
			}
			
			nick = new String(b_nick, "UTF-8");
			if (tabbedPane.getTitleAt(tabIndex).equals("ip")) {
				tabbedPane.setTitleAt(tabIndex, nick);
			}
			
			while (true) {
				if (input.available() >= 4) {
					input.read(b_msg_len, 0, 4);
					break;
				} else {
					try {
						Thread.sleep(200);
					} catch (Exception e) {
						System.out.println(e.getMessage());
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
						System.out.println(e.getMessage());
					}
				}
			}
			
			msg = new String(b_msg, "UTF-8");
			
			int ctr = 0;
			while (ctr < 3) {
				if (input.available() >= 1) {
					if (input.read() != 0) {
						System.out.println("Error: A message did not arrive properly.");
						return;
					}
					ctr++;
				} else {
					try {
						Thread.sleep(200);
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}
			}
			
			JPanel panel = (JPanel) tabbedPane.getComponentAt(tabIndex);
			JPanel subPanel = (JPanel) panel.getComponent(1);
			JTextArea textArea = (JTextArea) ((JScrollPane) subPanel.getComponent(1)).getViewport().getView();
			textArea.append(nick + ": " + msg + "\n");
			
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
		else if (id == 2) {
			try {
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
							System.out.println(e.getMessage());
						}
					}
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
}
