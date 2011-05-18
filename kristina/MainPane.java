package kristina;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.*;
import javax.swing.border.*;

public class MainPane extends JPanel{
	
	public MainPane(Session session) {
		
		JButton button1 = new JButton("New connection");
		JButton button2 = new JButton("Show log");
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new GridLayout(11, 1, 12, 12));
		leftPanel.add(button1);
		leftPanel.add(button2);
		
		ConnectButtonPressed listener1 = new ConnectButtonPressed(session);
		button1.addActionListener(listener1);
		LogButtonPressed listener2 = new LogButtonPressed(session, this);
		button2.addActionListener(listener2);		
		JTextArea textBox = new JTextArea();
//		textBox.setLineWrap(true);
		textBox.setEditable(false);
		textBox.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
		JScrollPane box = new JScrollPane(textBox);
		box.setBorder(new LineBorder(Color.black, 1, true));
		JPanel messagePanel = new JPanel();
		messagePanel.setLayout(new BorderLayout(0,10));
		messagePanel.add(new JLabel("Messages:"), BorderLayout.NORTH);
		messagePanel.add(box, BorderLayout.CENTER);
		
		JTextArea writingArea = new JTextArea();
		writingArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
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
