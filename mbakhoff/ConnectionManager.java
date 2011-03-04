package mbakhoff;

import java.util.HashMap;
import java.net.Socket;

public class ConnectionManager {
	
	protected Server server = null;
	protected HashMap<String,String> nickMappings = null;

	public static void main(String[] args) {
		new ConnectionManager();
	}
	
	/**
	 * Create new server instance and wait for new packets. Blocks indefinately. 
	 */
	public ConnectionManager() {
		server = new Server();
		nickMappings = new HashMap<String, String>();
		/*
		sendToIP("127.0.0.1", 
				MessageFormat.createMessagePacket("me", "hello world"));
		sendToIP("localhost", 
				MessageFormat.createMessagePacket("me", "hello world2"));
		*/
		while (true) {
			try {
				server.readSockets(this);
				Thread.sleep(200);
			} catch (Exception e) {
				System.out.println("ConnectionManager: "+e.getMessage());
			}
		}
	}
	
	/**
	 * @brief Try to read a packet from the socket
	 * @param soc Socket to read from
	 * 
	 * Try to read a packet, if bytes are available. 
	 * ConnectionManager.*received() is called when reading is successful, else
	 * the socket is closed.  
	 */
	public void readPackets(Socket soc) {
		try {
			if (soc.getInputStream().available() > 0) {
				System.out.println("DEBUG: reading from socket");
				MessageFormat.dissectStream(soc, this);
			}
		} catch (Exception e) {
			System.out.println("ERROR: inputstream died? "+e.getMessage());
		}
	}
	
	/**
	 * @brief Send a packet to given IP/hostname
	 * @param addr IP-address or hostname of recipent
	 * @param pkt byte[] created by MessageFormat.create*()
	 * @return true on success, else false
	 */
	public boolean sendToIP(String addr, byte[] pkt) {
		Socket soc = server.getSocketByAddr(addr);
		if (soc != null) {
			try {
				soc.getOutputStream().write(pkt);
				return true;
			} catch (Exception e) {
				System.out.println("ERROR: failed to send to "+addr+": "+
						e.getMessage());
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * @brief Send a packet to given nick
	 * @param nick nickname of recipent
	 * @param pkt byte[] created by MessageFormat.create*()
	 * @return true on success, else false
	 */
	public boolean sendToNick(String nick, byte[] pkt) {
		String addr = nickMappings.get(nick);
		if (addr == null) {
			System.out.println("DEBUG: could not send message: "+
					"nick \""+nick+"\" not mapped to ip");
			return false;
		} else {
			return sendToIP(addr, pkt);
		}
	}
	
	// TODO: collisions?
	// TODO: clear closed connections
	/**
	 * @brief Bind ip-address to nickname
	 * @param nick nickname to bind
	 * @param ip address to bind
	 */
	public void mapNick(String nick, String ip) {
		String current = nickMappings.get(nick);
		if (current == null) {
			System.out.println("ConnectionManager: "+
					"mapNick "+nick+":"+ip);
			nickMappings.put(nick, ip);
		} else if (!current.equals(ip)) {
			System.out.println("ConnectionManager: "+
					"nickname collision: \""+nick+"\": "+
					"current:"+current+"; new:"+ip);
		}
	}
	
	/**
	 * @brief Event handler for new messages
	 * @param nick nickname of sender
	 * @param msg message contents
	 */
	public void messageReceived(String nick, String msg) {
		// TODO: eventlistener arch 
		System.out.println("MSG: "+nick+" says: "+msg);
		// TODO: echo server; remove later
		System.out.println("DEBUG: echo-ing: "+msg);
		sendToNick(nick, MessageFormat.createMessagePacket("you", msg));
	}
	
}
