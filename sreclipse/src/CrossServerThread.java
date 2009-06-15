
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTextArea;

/**
 *
 * @author paweljot
 */
public class CrossServerThread extends Thread {

    private Socket socket = null;
    //private int id = -1; potrzebne ?
    PrintWriter out = null;
    BufferedReader in = null;
    Crossing cross = null;
    JTextArea log;

    public CrossServerThread(Socket socket,Crossing cross, JTextArea log) {
        super();
        this.socket = socket;
        this.log = log;
        this.cross = cross;

        try {
            //strumień wejściowy - od serwera
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException ex) {
            System.out.println("Błąd io wątek - "+ex.getMessage());
        }


    }

    @Override
    public void run() {
        log.append("Stworzono nowy wątek dla klienta");
        while (true) {

        }
    }
}
