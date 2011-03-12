package kristina;
import java.util.Scanner;
import java.net.Socket;
import java.lang.System;
import java.util.Vector;
import java.io.InputStream;

// pane utf-8 encodingusse
// tee nii, et sõnumeid saaks saata üksikutele inimestele eraldi

public class Program {
	
	public static void main(String[] args) {
		
		System.out.println("Enter you nickname:");
		Scanner scan = new Scanner(System.in);
		String nickname = scan.nextLine();
		
		Object lock = new Object();
		
		Vector<Socket> sockets = new Vector<Socket>();
		ReceivingSide r = new ReceivingSide(sockets, lock);
		
		Scanner userScanner = new Scanner(System.in, "UTF-8");
		UserSide u = new UserSide(userScanner, sockets, nickname, lock);
		//System.out.println("made it this far");
		
		while (true) {
			synchronized (lock) {
				for (Socket s : sockets) {
					try {
						InputStream input = s.getInputStream();
						if (input.available() != 0) {
							ReceivingSide.incomingMessagesParser(input);
						}
						//if (messageScanner.hasNext()) {
							//System.out.println("There was something here");
							//ReceivingSide.incomingMessagesParser(messageScanner);
						//}
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}
			}
		}
	}
}