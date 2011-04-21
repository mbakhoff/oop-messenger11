package mbakhoff;

import java.util.Vector;
import java.util.Iterator;
import java.net.*;

public class SocketManager {
	
	public static final int PORT = 1800;
	
	protected ServerSocket ssoc = null;
	protected Vector<Socket> connections = null;
	protected Object connections_lock = new Object();
	
	/**
	 * @brief Create a new ServerSocket and listen on port SocketManager.PORT
	 * 
	 * A new thread is created to accept all incoming connections. Open sockets 
	 * are held in Vector<Socket> connections, closed connections are 
	 * automatically removed. 
	 */
	public SocketManager() {
		connections = new Vector<Socket>();
		try {
			ssoc = new ServerSocket(SocketManager.PORT);
			EventDispatch.get().debug(
					"SocketManager: opened ServerSocket on "+
					"*:"+SocketManager.PORT);
		} catch (Exception e) {
			EventDispatch.get().debug(
					"SocketManager: failed to create ServerSocket: "+
					e.getMessage());
		}
		new Thread(new Runnable() {
			public void run() {
				acceptConnections();
			}
		}).start();
	}
	
	/**
	 * @brief call mgr.readPackets(Socket) with every open socket
	 * @param mgr ConnectionManager to use
	 */
	public void readSockets(ConnectionManager mgr) {
		removeClosedConnections();
		synchronized (connections_lock) {
			Iterator<Socket> it = connections.iterator();
			while (it.hasNext()) {
				Socket soc = it.next();
				synchronized (soc) {
					if (soc.isConnected()) {
						mgr.readPackets(soc);
					}
				}
			}
		}
	}
	
	public void closeSocket(Socket s) {
		try {
			synchronized (s) {
				if (!s.isClosed()) {
					try {
						s.close();
					} catch (Exception e) {}
				}
			}
		} catch (Exception e) {}
	}
	
	public void shutDown() {
		try {
			ssoc.close();
		} catch (Exception e) {}
		synchronized (connections_lock) {
			Iterator<Socket> it = connections.iterator();
			while (it.hasNext()) {
				Socket soc = it.next();
				synchronized (soc) {
					if (!soc.isClosed()) {
						try {
							soc.close();
						} catch (Exception e) {}
					}
				}
				it.remove();
			}
		}
	}
	 
	/**
	 * @brief Look for existing connections to addr, open new if necessary
	 * @param addr IP-address or hostname to get connection to
	 * @return Open socket to host or null if connection failed
	 */
	public Socket getSocketByAddr(String addr, boolean allowNewConnection) {
		removeClosedConnections();
		String ip = null;
		// try to resolve addr
		try {
			ip = InetAddress.getByName(addr).getHostAddress();
		} catch (UnknownHostException e) {
			EventDispatch.get().debug("WARNING: failed to resolve: "+addr);
			return null;
		}
		// look for open sockets
		synchronized (connections_lock) {
			Iterator<Socket> it = connections.iterator();
			while (it.hasNext()) {
				Socket soc = it.next();
				if (soc.getInetAddress().getHostAddress().equals(ip)) {
					return soc;
				}
			}
		}
		if (allowNewConnection) {
			EventDispatch.get().debug("Trying to connect to "+addr);
			return makeConnection(ip, SocketManager.PORT);
		} else {
			return null;
		}
	}
	
	protected Socket makeConnection(String addr, int port) {
		try {
			Socket soc = new Socket();
			soc.connect(new InetSocketAddress(addr, SocketManager.PORT), 2000);
			EventDispatch.get().debug("established connection to "+addr);
			addSocket(soc);
			return soc;
		} catch (Exception e) {
			EventDispatch.get().debug("ERROR: failed to connect to "+
					addr+": "+e.getMessage());
			return null;
		}
	}
	
	protected void addSocket(Socket soc) {
		synchronized (connections_lock) {
			connections.add(soc);
		}
	}
	
	protected void acceptConnections() {
		while (!ssoc.isClosed()) {
			try {
				Socket soc = ssoc.accept();
				EventDispatch.get().debug("SocketManager: connection from "+
						soc.getInetAddress().getHostAddress()+
						":"+soc.getPort());
				Socket old = getSocketByAddr(
						soc.getInetAddress().getHostAddress(), false); 
				if (old != null) {
					EventDispatch.get().debug("SocketManager: "+
							"replacing old "+old.getRemoteSocketAddress()+
							" with new "+soc.getRemoteSocketAddress());
					closeSocket(old);
				}
				synchronized (connections_lock) {
					connections.add(soc);
				}
			} catch (Exception e) {
				EventDispatch.get().debug("SocketManager: "+
						"failed to accept connection: "+e.getMessage());
			}
		}
	}
	
	protected void removeClosedConnections() {
		synchronized (connections_lock) { 
			Iterator<Socket> it = connections.iterator();
			while (it.hasNext()) {
				Socket s = it.next();
				if (s.isClosed()) {
					it.remove();
				}
			}
		}
	}

}
