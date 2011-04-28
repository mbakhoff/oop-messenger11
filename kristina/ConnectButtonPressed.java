package kristina;

import java.awt.event.*;
import java.net.Socket;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

public class ConnectButtonPressed implements ActionListener {
	
	Session session;
	
	public ConnectButtonPressed(Session session) {
		this.session = session;
	}
	
	public void actionPerformed(ActionEvent e){
		String ip = JOptionPane.showInputDialog("Enter IP to connect to:"); 
		if (ip != null) {
			synchronized (session.lock) {
				try {
					Socket sock = new Socket(ip, 1800);
					session.sockets.add(sock);
					JTabbedPane tabPane = session.tabPane;
					if (tabPane.getTitleAt(0).equals("Main")) {
						tabPane.remove(0);
					}
					tabPane.addTab("ip", new mainPane(session));
					JOptionPane.showMessageDialog(null, "Connection established.");
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}
			}
		}
	}
}
