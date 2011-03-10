package mbakhoff;

import java.util.Scanner;

public class CLI {

	protected ConnectionManager mgr = null;
	protected boolean active = true;
	
	public CLI(ConnectionManager mgr) {
		this.mgr = mgr;
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
	
	public void stop() {
		active = false;
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
		if (isMatch(key, "send", 1) && value != null)
			send(value);
		if (isMatch(key, "map", 1) && value != null)
			map(value);
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
		System.out.println("CLI: map nick ip");
		System.out.println("CLI: send nick/ip msg");
		System.out.println("CLI: quit");
	}
	
	protected void map(String s) {
		int pos = s.indexOf(" ");
		if (pos == -1)
			return; // no empty mappings
		String id = s.substring(0, pos);
		String ip = s.substring(pos+1);
		System.out.println("DEBUG: CLI: mapping "+id+":"+ip);
		mgr.mapNick(id, ip);
	}
	
	protected void send(String s) {
		int pos = s.indexOf(" ");
		if (pos == -1)
			return; // no empty strings from cli
		String id = s.substring(0, pos);
		String msg = s.substring(pos+1);
		System.out.println("DEBUG: CLI: sending \""+msg+"\" to "+id);
		mgr.send(id, MessageFormat.createMessagePacket("märt", msg));
	}
	
}
