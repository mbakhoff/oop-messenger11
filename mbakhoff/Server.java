package mbakhoff;

import java.util.Vector;
import java.util.Iterator;
import java.net.*;

public class Server {
	
	public static final int PORT = 1800;
	
	protected ServerSocket ssoc = null;
	protected Vector<Socket> connections = null;
	protected Object connections_lock = new Object();
	
	/**
	 * @brief Create a new ServerSocket and listen on port Server.PORT
	 * 
	 * A new thread is created to accept all incoming connections. Open sockets 
	 * are held in Vector<Socket> connections, closed connections are 
	 * automatically removed. 
	 */
	public Server() {
		connections = new Vector<Socket>();
		try {
			ssoc = new ServerSocket(Server.PORT);
			System.out.println("Server: opened ServerSocket on *:"+PORT);
		} catch (Exception e) {
			System.out.println("Server: failed to create ServerSocket: "+
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
				mgr.readPackets(it.next());
			}
		}
	}
	
	/**
	 * @brief Look for existing connections to addr, open new if necessary
	 * @param addr IP-address or hostname to get connection to
	 * @return Open socket to host or null if connection failed
	 */
	public Socket getSocketByAddr(String addr) {
		removeClosedConnections();
		String ip = null;
		// try to resolve addr
		try {
			ip = InetAddress.getByName(addr).getHostAddress();
		} catch (UnknownHostException e) {
			System.out.println("WARNING: failed to resolve: "+addr);
			return null;
		}
		// look for open sockets
		synchronized (connections_lock) {
			Iterator<Socket> it = connections.iterator();
			while (it.hasNext()) {
				Socket soc = it.next();
				if (soc.getInetAddress().getHostAddress().equals(ip)) {
					System.out.println("DEBUG: returned existing connection");
					return soc;
				}
			}
		}
		// try to create new connection
		System.out.println("DEBUG: not connected to "+addr+
			". Trying to connect..");
		try {
			Socket soc = new Socket(ip, Server.PORT);
			System.out.println("DEBUG: established connection to "+addr);
			addSocket(soc);
			return soc;
		} catch (Exception e) {
			System.out.println("ERROR: failed to connect to "+
					addr+": "+e.getMessage());
			return null;
		}
	}
	
	protected void addSocket(Socket soc) {
		removeClosedConnections();
		synchronized (connections_lock) {
			connections.add(soc);
		}
	}
	
	protected void acceptConnections() {
		while (true) {
			try {
				Socket soc = ssoc.accept();
				System.out.println("Server: connection from "+
						soc.getInetAddress().getHostAddress());
				synchronized (connections_lock) {
					connections.add(soc);
				}
			} catch (Exception e) {
				System.out.println("Server: failed to accept connection: "+
						e.getMessage());
			}
			removeClosedConnections();
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
