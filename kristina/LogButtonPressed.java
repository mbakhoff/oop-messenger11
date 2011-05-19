package kristina;
import java.awt.event.*;
import javax.swing.JTabbedPane;
import java.io.File;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class LogButtonPressed implements ActionListener{
	
	Session session;
	MainPane pane;
	
	public LogButtonPressed(Session session, MainPane pane) {
		this.session = session;
		this.pane = pane;
	}
	
	public void actionPerformed(ActionEvent e) {
		JTabbedPane tabs = session.tabPane;
		for (int i = 0; i < tabs.getTabCount(); i++) {
			MainPane tab = (MainPane) session.tabPane.getComponentAt(i);
			if (tab.equals(pane)) {
				String nickname = tabs.getTitleAt(i);
				File file = new File("log_" + nickname);
				if (!file.exists()) {
					JOptionPane.showMessageDialog(null, "This person's log " +
							"file doesn't exist yet!");
					break;
				}
				JFrame frame = new JFrame("Log for " + nickname);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.setSize(600, 400);
				StringBuilder logText = new StringBuilder();
				try {
					BufferedReader reader = new BufferedReader(new FileReader(file));
					while (reader.ready()) {
						logText.append(reader.readLine() + "\n");
					}
				} catch (Exception ex) {
					System.out.println(ex);
				}
				String text = logText.toString();
				JTextArea textArea = new JTextArea(text);
				textArea.setEditable(false);
				textArea.setLineWrap(true);
				JScrollPane scrollPane = new JScrollPane(textArea);
				frame.add(scrollPane);
				frame.setVisible(true);
				break;
			}
		}
	}
}
