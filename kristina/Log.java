package kristina;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.io.*;
import java.util.Date;

public class Log {
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	ArrayList<File> files = new ArrayList<File>();
	StringBuilder beginning = new StringBuilder();
	
	protected String currentDate() {
		return "[" + dateFormat.format(new Date()) + "]";
	}
	
	protected File getFile(String nickname) {
		String fileName = "log_" + nickname;
		for (File f : files) {
			if (f.getName().equals(fileName)) {
				return f;
			}
		}
		File newFile = new File(fileName);
		files.add(newFile);
		return newFile;
	}
	
	protected void write(String nickname, String text) {
		File file = getFile(nickname);
		try {
			FileWriter out = new FileWriter(file, true);
			out.write(text);
			out.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
