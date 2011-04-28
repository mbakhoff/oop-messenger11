package mbakhoff;

import java.util.HashMap;
import java.util.Vector;
import java.net.Socket;

public class ConnectionManager {
	
	protected SocketManager server = null;
	protected HashMap<String,String> nickMappings = null;
	
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
		nickMappings = new HashMap<String, String>();
	}
	
	/**
	 * ConnectionManager main loop. Blocks indefinately
	 */
	protected void mainLoop() {
		while (true) {
			try {
				server.readSockets(this);
				Thread.sleep(300);
			} catch (Exception e) {
				EventDispatch.get().debug(
						"ConnectionManager: "+e.getMessage());
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
		boolean ok = true;
		synchronized (soc) {
			try {
				if (soc.getInputStream().available() > 0) {
					EventDispatch.get().debug("reading from socket");
					if (!MessageFormat.dissectStream(soc, this)) {
						EventDispatch.get().debug("WARNING: read failed; "+
								"killing socket "+soc.getRemoteSocketAddress());
						ok = false;
					}
				}
			} catch (Exception e) {
				EventDispatch.get().debug(
						"ERROR: socket died? "+e.getMessage());
				ok = false;
			}
		}
		// close broken connection
		if (!ok)
			server.closeSocket(soc);
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
					soc.getOutputStream().flush();
				}
				return true;
			} catch (Exception e) {
				EventDispatch.get().debug("ERROR: failed to send to "+
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
			EventDispatch.get().debug("ConnectionManager: "+
					"mapNick "+nick+":"+ip);
			synchronized (nickMappings) {
				if (server.getSocketByAddr(ip, true) != null)
					nickMappings.put(nick, ip);
			}
			EventDispatch.get().mapChanged();
		// if defined, check if connection still alive
		} else if (!current.equals(ip)) {
			if (checkAlive(current)) {
				EventDispatch.get().debug("ConnectionManager: "+
						"nickname collision: \""+nick+"\": "+
						"current:"+current+"; new:"+ip);
			} else {
				EventDispatch.get().debug("ConnectionManager: "+
						"dead nickname mapping: "+nick+":"+current);
				EventDispatch.get().debug("ConnectionManager: "+
						"remapping "+nick+":"+ip);
				synchronized (nickMappings) {
					nickMappings.remove(nick);
					if (server.getSocketByAddr(ip, true) != null)
						nickMappings.put(nick, ip);
				}
				EventDispatch.get().mapChanged();
			}
		}
	}
	
	public void unmapNick(String nick) {
		synchronized (nickMappings) {
			nickMappings.remove(nick);
		}
		EventDispatch.get().mapChanged();
	}
	
	public String getIpByNick(String nick) {
		synchronized (nickMappings) {
			return nickMappings.get(nick);
		}
	}
	
	public Vector<String> getMap() {
		return new Vector<String>(nickMappings.keySet());
	}
	
	public boolean checkAlive(String addr) {
		boolean ok = sendToIP(addr, MessageFormat.createAliveMessage());
		EventDispatch.get().debug("pinging "+addr+": "+(ok?"UP":"DOWN"));
		return ok;
	}
	
}
