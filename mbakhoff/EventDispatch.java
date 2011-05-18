package mbakhoff;
import java.util.*;

// SINGLETON
public class EventDispatch {

	private static EventDispatch inst = null;
	private LinkedList<MEventListener> listeners = null;
	private boolean debugEnabled = false;
	
	private EventDispatch() {
		listeners = new LinkedList<MEventListener>();
	}
	
	public static EventDispatch get() {
		if (inst == null) {
			inst = new EventDispatch();
		}
		return inst;
	}
	
	public void addListener(MEventListener l) {
		if (!listeners.contains(l)) {
			listeners.add(l);
		}
	}
	
	public void message(String nick, String message) {
		MessageLog.get(nick).append(nick, message);
		for (MEventListener l : listeners) {
			l.messageReceived(nick, message);
		}
	}
	
	public void debug(String message) {
		if (debugEnabled) {
			for (MEventListener l : listeners) {
				l.messageDebug(message);
			}
		}
	}
	
	public void console(String message) {
		for (MEventListener l : listeners) {
			l.messageConsole(message);
		}
	}
	
	public void mapChanged() {
		for (MEventListener l : listeners) {
			l.peeringEvent();
		}
	}
	
	public synchronized boolean isDebugEnabled() {
		return debugEnabled;
	}
	
	public synchronized void setDebugEnabled(boolean enabled) {
		debugEnabled = enabled;
	}
	
}
