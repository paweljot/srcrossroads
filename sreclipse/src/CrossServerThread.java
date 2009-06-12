
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author paweljot
 */
public class CrossServerThread extends Thread {

    private Socket socket = null;
    private int id = -1;
    PrintWriter out = null;
    BufferedReader in = null;
    Crossing cross = null;

    public CrossServerThread(Socket socket, int id,Crossing cross) {
        super();
        this.socket = socket;
        this.id = id;
        this.cross = cross;

        try {
            //strumień wejściowy - od serwera
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException ex) {
            Logger.getLogger(CrossClient.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    @Override
    public void run() {
        System.out.print("Stworzono nowy wątek dla klienta " + id);
        while (true) {
            try {

                System.out.println("Wątek działa...");
                //czekam..
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(CrossServerThread.class.getName()).log(Level.SEVERE, null, ex);
                }
                String traffic = null;
                while ((traffic = in.readLine()) != null) {

                    System.out.println("Wątek " + id + " otrzymał ruch " + traffic);
                    cross.updateState(id, Integer.parseInt(traffic));
                    

                }
            } catch (IOException ex) {
                Logger.getLogger(CrossServerThread.class.getName()).log(Level.SEVERE, null, ex);
            } 


        }
    }
}
