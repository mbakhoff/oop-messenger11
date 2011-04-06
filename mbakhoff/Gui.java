package mbakhoff;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.util.Vector;

public class Gui implements MEventListener {

	private ConnectionManager mgr = null;
	
	private JFrame frame = null;
	private JTabbedPane pane = null;
	private JPanel ctl = null;
	
	// control panel elements
	private DefaultListModel mdl = null; 
	private JTextArea log = null;
	private JList list = null;
	private JTextField tfNick = null;
	private JTextField tfAddr = null;
	private JButton bAdd = null;
	
	public Gui(ConnectionManager mgr) {
		this.mgr = mgr;
		EventDispatch.get().addListener(this);
		// frame
		frame = new JFrame("oop-messenger");
		pane = new JTabbedPane();
		// control panel
		ctl = new JPanel();
		buildPeerView(ctl);
		pane.addTab("Peers", ctl);
		// frame layout
		frame.add(pane);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		// focus on textfield
		tfNick.requestFocus();
	}
	
	protected void buildPeerView(JPanel p) {
		// log textarea
		log = new JTextArea("");
		log.setColumns(60);
		log.setRows(30);
		log.setEditable(false);
		log.setBorder(BorderFactory.createTitledBorder("Log"));
		// list + model
		mdl = new DefaultListModel();
		peeringEvent();
		list = new JList(mdl);
		//list.setCellRenderer(new MyCellRenderer());
		list.setFixedCellWidth(150);
		list.setBorder(BorderFactory.createTitledBorder("Peers"));
		list.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {}
			public void keyPressed(KeyEvent ev) {
				if (ev.getKeyCode() == KeyEvent.VK_ENTER)
					peerSelected((String)list.getSelectedValue());
			}
		});
		list.addMouseListener(new MouseListener() {
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 1) {
					peerSelected((String)list.getSelectedValue());
				}
			}
		});
		// encapsulate JList in viewport
		JViewport list_view = new JViewport();
		list_view.add(list);
		tfNick = new JTextField();
		tfNick.setColumns(8);
		tfNick.setToolTipText("Insert nickname");
		tfAddr = new JTextField();
		tfAddr.setColumns(10);
		tfAddr.setToolTipText("Insert ip/hostname");
		tfAddr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connectionRequestEvent();
			}
		});
		bAdd = new JButton("Connect");
		bAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connectionRequestEvent();
			}
		});
		// create layout
		// haha edu desifeerimisel
		GroupLayout gl = new GroupLayout(p);
		p.setLayout(gl);
		gl.setAutoCreateContainerGaps(true);
		gl.setAutoCreateGaps(true);
		gl.linkSize(SwingConstants.VERTICAL, tfNick, tfAddr, bAdd);
		gl.setHorizontalGroup(
				gl.createSequentialGroup()
					.addGroup(gl.createParallelGroup()
							.addComponent(list_view)
							.addGroup(gl.createSequentialGroup()
									.addComponent(tfNick)
									.addComponent(tfAddr)
									.addComponent(bAdd)))
					.addComponent(log));
		gl.setVerticalGroup(
				gl.createParallelGroup()
					.addGroup(gl.createSequentialGroup()
							.addComponent(list_view)
							.addGroup(gl.createParallelGroup()
									.addComponent(tfNick)
									.addComponent(tfAddr)
									.addComponent(bAdd)))
					.addComponent(log));
	}
	
	public void connectionRequestEvent() {
		String nick = tfNick.getText();
		String addr = tfAddr.getText();
		tfNick.setText("");
		tfAddr.setText("");
		EventDispatch.get().debug("gui connectionReq: "+nick+":"+addr);
		mgr.mapNick(nick, addr);
		// refocus
		tfNick.requestFocus();
	}
	
	public void peerSelected(String peer) {
		EventDispatch.get().debug("gui selected peer: "+peer);
	}

	// MEventListener
	public void messageReceived(String nick, String message) {
		log.append(nick+" says: "+message+"\n");
	}

	// MEventListener
	public void messageDebug(String message) {
		log.append("DEBUG: "+message+"\n");
	}

	// MEventListener
	public void peeringEvent() {
		Vector<String> map = mgr.getMap();
		mdl.clear();
		for (String s : map) {
			mdl.addElement(s);
		}
		mdl.addElement("");
	}
	
	/*
	private class MyCellRenderer implements ListCellRenderer {

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			JLabel item = new JLabel();
			if (value != null && value.toString().length() > 0) {
				item.setText((String)value);
			} else {
				item.setText(" ");
			}
			if (isSelected) {
				item.setBackground(Color.lightGray);
			}
			return item;
		}
		
	}
	*/
		
}
