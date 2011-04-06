package mbakhoff;
import java.util.LinkedList;

// SINGLETON
public class EventDispatch {

	private static EventDispatch inst = null;
	private LinkedList<MEventListener> listeners = null;
	
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
		for (MEventListener l : listeners) {
			l.messageReceived(nick, message);
		}
	}
	
	public void debug(String message) {
		for (MEventListener l : listeners) {
			l.messageDebug(message);
		}
	}
	
	public void mapChanged() {
		for (MEventListener l : listeners) {
			l.peeringEvent();
		}
	}
	
}
