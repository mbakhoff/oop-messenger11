package kristina;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.*;
import javax.swing.border.*;

public class mainPane extends JPanel{
	
	public mainPane(Session session) {
		
		JButton button1 = new JButton("New connection");
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.add(button1);
		
		ConnectButtonPressed listener1 = new ConnectButtonPressed(session);
		button1.addActionListener(listener1);
		
		JTextArea textBox = new JTextArea();
//		textBox.setLineWrap(true);
		textBox.setEditable(false);
		JScrollPane box = new JScrollPane(textBox);
		box.setBorder(new LineBorder(Color.black, 1, true));
		JPanel messagePanel = new JPanel();
		messagePanel.setLayout(new BorderLayout(0,10));
		messagePanel.add(new JLabel("Messages:"), BorderLayout.NORTH);
		messagePanel.add(box, BorderLayout.CENTER);
		
		JTextArea writingArea = new JTextArea();
//		writingArea.setLineWrap(true);
		JScrollPane write = new JScrollPane(writingArea);
		write.setBorder(new LineBorder(Color.black, 1, true));
		JButton sendButton = new JButton("Send");
		SendButtonPressed listener = new SendButtonPressed(session);
		sendButton.addActionListener(listener);
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(sendButton);
		JPanel writingPanel = new JPanel();
		writingPanel.setLayout(new BorderLayout(10,10));
		writingPanel.add(new JLabel("Write your message here:"), BorderLayout.NORTH);
		writingPanel.add(write, BorderLayout.CENTER);
		writingPanel.add(buttonPanel, BorderLayout.EAST);
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		setLayout(gridbag);
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5,5,5,5);
		
		c.gridheight = 5;
		gridbag.setConstraints(leftPanel, c);
		add(leftPanel);
		
		c.gridheight = GridBagConstraints.RELATIVE;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.weighty = 5.0;
		gridbag.setConstraints(messagePanel, c);
		add(messagePanel);
		
		c.weighty = 1.0;
		c.gridheight = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(writingPanel, c);
		add(writingPanel);
	}
}
