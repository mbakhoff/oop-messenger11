package mbakhoff;

import java.util.Scanner;

public class CLI implements MEventListener {
	
	public static void main(String[] args) {
		// parse cli args
		for (String s : args) {
			if (s.equals("-d")) {
				EventDispatch.get().setDebugEnabled(true);
			}
		}
		// run
		ConnectionManager mgr = new ConnectionManager();
		new CLI(mgr, true);
		mgr.mainLoop();
	}

	protected ConnectionManager mgr = null;
	protected boolean active = true;
	
	public CLI(ConnectionManager mgr, boolean interactive) {
		this.mgr = mgr;
		if (interactive) {
			EventDispatch.get().addListener(this);
			help();
			new Thread(new Runnable() {
				public void run() {
					Scanner in = new Scanner(System.in);
					while (active) {
						try {
							checkInputs(in);
							Thread.sleep(50);
						} catch (Exception e) {
						}
					}
				}
			}).start();
		}
	}
	
	public void stop() {
		active = false;
	}
	
	public void messageReceived(String nick, String message) {
		System.out.println(nick+" says: "+message);
	}

	public void messageDebug(String message) {
		System.out.println("DEBUG: "+message);
	}
	
	public void messageConsole(String msg) {
		System.out.println(msg);
	}
	
	public void peeringEvent() {
	}
	
	protected void checkInputs(Scanner in) {
		try {
			if (in.hasNextLine()) {
				String line = in.nextLine();
				parse(line.trim());
			}
		} catch (Exception e) {
		}
	}
	
	// watch out for duplicate cmd name starts
	protected void parse(String cmd) {
		int pos = cmd.indexOf(' ');
		String key = cmd.substring(0, pos != -1 ? pos : cmd.length());
		String value = pos == -1 ? null : cmd.substring(pos+1);
		if (isMatch(key, "help", 1))
			help();
		if (isMatch(key, "get-map", 1))
			viewNicktable();
		if (isMatch(key, "debug", 1))
			toggleDebug();
		if (isMatch(key, "send", 1) && value != null)
			send(value);
		if (isMatch(key, "map", 1) && value != null)
			map(value);
		if (isMatch(key, "ping-nick", 6) && value != null)
			pingNick(value);
		if (isMatch(key, "ping-ip", 6) && value != null)
			pingIP(value);
		if (isMatch(key, "quit", 1))
			System.exit(0);
	}
	
	protected boolean isMatch(String test, String sample, int min) {
		if (test.length() > sample.length() || test.length() < min) {
			return false;
		}
		if (sample.substring(0, test.length()).equalsIgnoreCase(test)) {
			return true;
		} else {
			return false;
		}
	}
	
	protected void help() {
		EventDispatch.get().console("CLI: map nick ip");
		EventDispatch.get().console("CLI: send nick/ip msg");
		EventDispatch.get().console("CLI: ping-ip ip");
		EventDispatch.get().console("CLI: ping-nick nick");
		EventDispatch.get().console("CLI: get-map");
		EventDispatch.get().console("CLI: debug");
		EventDispatch.get().console("CLI: quit");
	}
	
	protected void viewNicktable() {
		for (String s : mgr.getMap()) {
			EventDispatch.get().console("Mapped: "+s);
		}
	}
	
	protected void pingIP(String ip) {
		if (mgr.checkAlive(ip)) {
			EventDispatch.get().console(ip + " is UP");
		} else {
			EventDispatch.get().console(ip + " is DOWN");
		}
	}
	
	protected void pingNick(String nick) {
		String ip = mgr.getIpByNick(nick);
		if (ip == null) {
			EventDispatch.get().console(nick + " is not mapped");
		} else if (mgr.checkAlive(ip)) {
			EventDispatch.get().console(ip + " is UP");
		} else {
			EventDispatch.get().console(ip + " is DOWN");
		}
	}
	
	protected void map(String s) {
		int pos = s.indexOf(" ");
		if (pos == -1)
			return; // no empty mappings
		String id = s.substring(0, pos);
		String ip = s.substring(pos+1);
		EventDispatch.get().debug("CLI: mapping "+id+":"+ip);
		mgr.mapNick(id, ip);
	}
	
	protected void send(String s) {
		int pos = s.indexOf(" ");
		if (pos == -1)
			return; // no empty strings from cli
		String id = s.substring(0, pos);
		String msg = s.substring(pos+1);
		EventDispatch.get().debug("CLI: sending \""+msg+"\" to "+id);
		mgr.send(id, MessageFormat.createMessagePacket("märt", msg));
	}
	
	protected void toggleDebug() {
		boolean state = EventDispatch.get().isDebugEnabled();
		if (state == true) {
			EventDispatch.get().console("disabling debugging");
			EventDispatch.get().setDebugEnabled(false);
		} else {
			EventDispatch.get().console("enabling debugging");
			EventDispatch.get().setDebugEnabled(true);
		}
	}
	
}
