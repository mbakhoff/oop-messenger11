package mbakhoff;
import javax.swing.*;
import java.awt.event.*;

public class GuiTab extends JPanel {

	protected ConnectionManager mgr = null;
	protected Gui gui = null;
	protected String nick = null;
	
	protected JTextArea log = null;
	protected JTextField input = null;
	protected JButton sendButton = null;
	
	public GuiTab(ConnectionManager mgr, Gui gui, String nick) {
		this.nick = nick;
		this.mgr = mgr;
		this.gui = gui;
		initComponents();
		doTabLayout();
	}

	protected void removeSelf() {
		gui.removeTab(this);
	}
	
	public void messageReceived(String msg) {
		synchronized (log) {
			log.append(nick+": "+msg+"\n");
		}
	}
	
	public String getNick() {
		return nick;
	}
	
	protected void initComponents() {
		ActionListener sendAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendEvent();
			}
		};
		log = new JTextArea("");
		log.setRows(20);
		log.setColumns(60);
		log.setEditable(false);
		input = new JTextField();
		input.setColumns(60);
		input.addActionListener(sendAction);
		sendButton = new JButton("Send");
		sendButton.addActionListener(sendAction);
	}
	
	protected void doTabLayout() {
		GroupLayout gl = new GroupLayout(this);
		setLayout(gl);
		gl.linkSize(SwingConstants.VERTICAL, input, sendButton);
		JScrollPane logWrapper = new JScrollPane();
		logWrapper.setViewportView(log);
		gl.setAutoCreateContainerGaps(true);
		gl.setAutoCreateGaps(true);
		gl.setHorizontalGroup(
				gl.createParallelGroup()
					.addComponent(logWrapper)
					.addGroup(gl.createSequentialGroup()
							.addComponent(input)
							.addComponent(sendButton)));
		gl.setVerticalGroup(
				gl.createSequentialGroup()
					.addComponent(logWrapper)
					.addGroup(gl.createParallelGroup()
							.addComponent(input)
							.addComponent(sendButton)));
	}
	
	protected void sendEvent() {
		String msg = input.getText();
		input.setText("");
		EventDispatch.get().debug("gui: sending to "+nick+": "+msg);
		mgr.send(nick, MessageFormat.createMessagePacket("märt", msg));
		synchronized (log) {
			log.append("märt: "+msg+"\n");
		}
	}
	
}
