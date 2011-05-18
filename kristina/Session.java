package kristina;
import java.net.Socket;
import java.lang.System;
import java.util.Vector;
import java.io.InputStream;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

// ping button
// same socket can be added multiple times, fix it (in ReceivingSide)
// pressing Enter in addition to the send button

public class Session {
	
	String nickname = null;
	Vector<Socket> sockets = new Vector<Socket>();
	JTabbedPane tabPane = new JTabbedPane();
	Object lock = new Object();
	Log log = new Log();
	
	public Session() {
		JFrame frame = new JFrame("Messenger");
		frame.setSize(800,600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		MainPane fullPanel = new MainPane(this);
		tabPane.addTab("Main", fullPanel);
		frame.add(tabPane);
		frame.setVisible(true);
		while (true) {
			nickname = JOptionPane.showInputDialog("Enter nickname:");
			if (nickname != null) {
				break;
			}
		}
	}
	
	public static void main(String[] args) {
		
		Session session = new Session();
		ReceivingSide r = new ReceivingSide(session);
		
		while (true) {
			synchronized (session.lock) {
				for (Socket s : session.sockets) {
					try {
						InputStream input = s.getInputStream();
						if (input.available() != 0) {
							int tabIndex = session.sockets.indexOf(s);
							r.incomingMessagesParser(input, tabIndex);
						}
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}
			}
		}
	}
}