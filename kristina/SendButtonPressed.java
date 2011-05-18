package kristina;
import java.awt.*;
import java.awt.event.*;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;

public class SendButtonPressed implements ActionListener{
	
	Session session;
	
	public SendButtonPressed(Session session) {
		this.session = session;
	}
	
	public void actionPerformed(ActionEvent e){
		String text = null;
		JButton currentButton = (JButton) e.getSource();
		JPanel parentPanel = (JPanel) currentButton.getParent();
		JPanel writingArea1 = (JPanel) parentPanel.getParent();
		Component[] comps = writingArea1.getComponents();
		for (Component comp : comps) {
			if (comp instanceof JScrollPane) {
				JTextArea area = (JTextArea) ((JScrollPane) comp).getViewport().getView();
				text = area.getText();	// get text from bottom text area
				area.replaceRange("", 0, text.length()); // empty bottom text area
				break;
			}
		}
		JPanel entirePane = (JPanel) writingArea1.getParent();
		JPanel largePanel = (JPanel) entirePane.getComponent(1);
		JTextArea upperTextArea = (JTextArea) ((JScrollPane) largePanel.getComponent(1)).getViewport().getView();
		// write text from bottom text area to upper text area
		String output = "you: " + text + "\n";
		upperTextArea.append(" " + output);
		
		int index = session.tabPane.indexOfComponent(entirePane);
		String nickname = session.tabPane.getTitleAt(index);
		
		if (!nickname.equals("Main")) {
			if (nickname.equals("ip")) {
				session.log.beginning.append(session.log.currentDate() + " " +
						output);
			} else {
				session.log.write(nickname, session.log.currentDate() + " " +
						output);
			}
			byte[] bytes = UserSide.composeMessage(text, session.nickname);
			Socket s;
			synchronized (session.lock) {
				try {
					s = session.sockets.elementAt(index);
					UserSide.sendMessage(bytes, s);
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
					session.sockets.remove(index);
					JOptionPane.showMessageDialog(null, "Connection to " + session.tabPane.getTitleAt(index) + " lost!");
					session.tabPane.remove(index);
				}
			}
		}
	}
}
