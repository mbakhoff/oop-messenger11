package mbakhoff;

import java.util.HashMap;
import java.net.Socket;

public class ConnectionManager {
	
	protected SocketManager server = null;
	protected HashMap<String,String> nickMappings = null;

	public static void main(String[] args) {
		ConnectionManager mgr = new ConnectionManager();
		mgr.mainLoop();
	}
	
	/**
	 * Create new SocketManager and initialize ConnectionManager
	 */
	public ConnectionManager() {
		Runnable shutDownHook = new Runnable() {
			public void run() {
				server.shutDown();
			}
		};
		server = new SocketManager();
		Runtime.getRuntime().addShutdownHook(new Thread(shutDownHook));
		new CLI(this);
		nickMappings = new HashMap<String, String>();
	}
	
	/**
	 * ConnectionManager main loop. Blocks indefinately
	 */
	protected void mainLoop() {
		while (true) {
			try {
				server.readSockets(this);
				Thread.sleep(100);
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
		synchronized (soc) {
			try {
				if (soc.getInputStream().available() > 0) {
					System.out.println("DEBUG: reading from socket");
					MessageFormat.dissectStream(soc, this);
				}
			} catch (Exception e) {
				System.out.println("ERROR: socket died? "+e.getMessage());
				server.closeSocket(soc);
			}
		}
	}
	
	/**
	 * @brief Send a packet to given IP/hostname
	 * @param addr IP-address or hostname of recipent
	 * @param pkt byte[] created by MessageFormat.create*()
	 * @return true on success, else false
	 */
	public boolean sendToIP(String addr, byte[] pkt) {
		Socket soc = server.getSocketByAddr(addr, true);
		if (soc != null) {
			try {
				synchronized (soc) {
					soc.getOutputStream().write(pkt);
				}
				return true;
			} catch (Exception e) {
				System.out.println("ERROR: failed to send to "+
						soc.getRemoteSocketAddress()+": "+e.getMessage());
				server.closeSocket(soc);
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * @brief Send a packet to given nick
	 * @param id nickname or IP of recipent
	 * @param pkt byte[] created by MessageFormat.create*()
	 * @return true on success, else false
	 */
	public boolean send(String id, byte[] pkt) {
		String addr = nickMappings.get(id);
		if (addr == null) {
			return sendToIP(id, pkt);
		} else {
			boolean ret = sendToIP(addr, pkt);
			if (!ret)
				unmapNick(id);
			return ret;
		}
	}
	
	// TODO: collisions?
	// TODO: clear closed connections
	// TODO: sync
	/**
	 * @brief Bind ip-address to nickname
	 * @param nick nickname to bind
	 * @param ip address to bind
	 */
	public void mapNick(String nick, String ip) {
		String current = nickMappings.get(nick);
		// map if not defined
		if (current == null) {
			System.out.println("ConnectionManager: "+
					"mapNick "+nick+":"+ip);
			synchronized (nickMappings) {
				if (server.getSocketByAddr(ip, true) != null)
					nickMappings.put(nick, ip);
			}
		// if defined, check if connection still alive
		} else if (!current.equals(ip)) {
			if (checkAlive(current)) {
				System.out.println("ConnectionManager: "+
						"nickname collision: \""+nick+"\": "+
						"current:"+current+"; new:"+ip);
			} else {
				System.out.println("ConnectionManager: "+
						"dead nickname mapping: "+nick+":"+current);
				System.out.println("ConnectionManager: "+
						"remapping "+nick+":"+ip);
				synchronized (nickMappings) {
					nickMappings.remove(nick);
					if (server.getSocketByAddr(ip, true) != null)
						nickMappings.put(nick, ip);
				}
			}
		}
	}
	
	public void unmapNick(String nick) {
		synchronized (nickMappings) {
			nickMappings.remove(nick);
		}
	}
	
	public boolean checkAlive(String addr) {
		return sendToIP(addr, MessageFormat.createAliveMessage());
	}
	
	/**
	 * @brief Event handler for new messages
	 * @param nick nickname of sender
	 * @param msg message contents
	 */
	public void messageReceived(String nick, String msg) {
		// TODO: eventlistener arch 
		System.out.println("MSG: "+nick+" says: "+msg);
	}
	
}
