
package srcrossroads;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author paweljot
 */
public class CrossClient {


    private final static int port = 2222;
    

    public static void main(String args[]) {

        Socket socket = null;
        try {
            socket = new Socket("localhost", port);
        } catch (UnknownHostException ex) {
            Logger.getLogger(CrossClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CrossClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        

        

    }

}
