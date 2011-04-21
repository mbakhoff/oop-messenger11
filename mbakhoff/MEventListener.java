package mbakhoff;

public interface MEventListener {

	public void messageReceived(String nick, String message);
	public void messageDebug(String message);
	public void messageConsole(String msg);
	public void peeringEvent();
	
}
