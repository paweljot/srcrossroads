package srcrossroads;

import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author paweljot
 */
public class CrossClient {

    private final static int port = 2222;

    public static void main(String args[]) throws IOException {

        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        //tworzenie gniazdka
        try {
            socket = new Socket("localhost", port);
        } catch (UnknownHostException ex) {
            Logger.getLogger(CrossClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CrossClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            //strumień wejściowy - od serwera
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException ex) {
            Logger.getLogger(CrossClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        while (true) {
            int traffic = generateRandomTraffic();
            out.println(traffic);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(CrossClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }





    }

    private static int generateRandomTraffic() {

        Random r = new Random();
        return r.nextInt(50);
    }
}
