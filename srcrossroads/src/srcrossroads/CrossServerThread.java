
package srcrossroads;

import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author paweljot
 */
public class CrossServerThread extends Thread{
    private Socket socket = null;
    private int id = 0;

    public CrossServerThread(Socket socket,int id) {
        super();
        this.socket = socket;
        this.id = id;
    }

    @Override
    public void run() {
        System.out.print("Stworzono nowy wątek dla klienta "+id);
        while(true) {
            System.out.println("Wątek działa...");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(CrossServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
