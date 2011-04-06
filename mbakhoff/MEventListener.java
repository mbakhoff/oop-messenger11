package mbakhoff;

public interface MEventListener {

	public void messageReceived(String nick, String message);
	public void messageDebug(String message);
	public void peeringEvent();
	
}
