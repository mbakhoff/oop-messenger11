package mbakhoff;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MessageLog {
	
	public static final SimpleDateFormat fmt = 
		new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	protected static final String suffix = "-oop.log";
	protected static Map<String,File> fileMap = new HashMap<String,File>();
	
	protected static File getFile(String target) {
		synchronized (MessageLog.fileMap) {
			File f = MessageLog.fileMap.get(target);
			if (f == null) {
				f = new File(target+suffix);
				MessageLog.fileMap.put(target, f);
			}
			try {
				if (!f.exists()) {
					f.createNewFile();
				} else if (!f.isFile()) {
					throw new Exception("not a file");
				} else if (!f.canRead() || !f.canWrite()) {
					throw new Exception("check permissions");
				}
			} catch (Exception e) {
				EventDispatch.get().debug(String.format(
						"Failed to get log file %s: %s",
						f.getName(), e.getMessage()));
				return null;
			}
			return f;
		}
	}
	
	public static MessageLog get(String target) {
		MessageLog log = new MessageLog();
		log.logFile = MessageLog.getFile(target);
		return log;
	}
	
	
	
	protected File logFile = null;
	
	protected MessageLog() {
	}
	
	public void append(String nick, String msg) {
		append(String.format("[%s] %s: %s\n", 
				MessageLog.fmt.format(new Date()), nick, msg));
	}
	
	public void append(String raw) {
		if (logFile == null) return;
		synchronized (logFile) {
			try {
				FileWriter out = new FileWriter(logFile, true);
				out.append(raw);
				out.close();
			} catch (Exception e) {
				EventDispatch.get().debug(String.format(
						"log failed: %s: %s\n", 
						logFile.getName(), e.getMessage()));
			}
		}
	}
	 
	public List<String> tail(int max_lines) {
		if (logFile == null) { 
			return new ArrayList<String>();
		}
		LinkedList<String> buf = new LinkedList<String>();
		synchronized (logFile) {
			try {
				BufferedReader in = new BufferedReader(new FileReader(logFile));
				String line;
				while ((line = in.readLine()) != null) {
					while (buf.size() >= max_lines) {
						buf.removeFirst();
					}
					buf.addLast(line);
				}
				in.close();
			} catch (Exception e) {
				EventDispatch.get().debug(String.format(
						"log failed: %s: %s\n", 
						logFile.getName(), e.getMessage()));
			}
		}
		return buf;
	}

}
