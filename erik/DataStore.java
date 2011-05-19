package erik;

/**
 * @brief This class contains methods necessary to save data to log
 * @description ???
 *
 * @author erik
 * @version 0.1
 */

import java.io.*;

public class DataStore {

    private static String fileName = null;

// Constructor //
    public DataStore(String fileNameI) {
       fileName = fileNameI;
    }
    
// Method to save logs to file //
    public static void writeData(String msg, String time,
                                 String sender, String receiver) {
        BufferedWriter out = null;

        try {
            out = new BufferedWriter(new FileWriter(fileName + ".log", true));
            out.newLine();
            out.write(time + " " + sender + " >> " + receiver +
                     " " + msg);
            out.close();
            out = null;
        }
        catch(IOException e) {
            e.getMessage();
        }
    }
}