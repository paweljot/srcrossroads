
import java.io.*;
import java.net.*;

import javax.swing.JTextArea;

/**
 *
 * @author paweljot
 */
public class CrossServerThread extends Thread {

    private Socket socket = null;
    //private int id = -1; potrzebne ?
    DataOutputStream out = null;
    DataInputStream in = null;
    Crossing cross = null;
    JTextArea log;
    
	//tokeny dla komunikatów:
	//TODO - w jakiejs wspolne klaise / pliku konfiguracyjnym
	public final char T_OCCUPY = 0x11;
	public final char T_SHELO = 0x01;
	public final char T_OKOCC = 0x21;
	public final char T_FLDOCC = 0x22;	

    public CrossServerThread(Socket socket,Crossing cross, JTextArea log) {
        super();
        this.socket = socket;
        this.log = log;
        this.cross = cross;

        try {
            //strumień wejściowy - od serwera
    		out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    		in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

        } catch (IOException ex) {
            System.out.println("Błąd io wątek - "+ex.getMessage());
        }


    }

    @Override
    public void run() {
        log.append("Stworzono nowy wątek dla klienta");
		try {
			out.writeUTF(new String(Character.toString(T_SHELO)));
			out.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        while (true) {
        	try {
				String msg = in.readUTF();
				char token = msg.charAt(0);
				switch (token) {
					case T_OCCUPY:
						out.writeUTF(new String(Character.toString(T_OKOCC)));
						out.flush();
						log.append("Klient zajął drogę " +msg.substring(1)+"\n");						
				}
			} catch (IOException e) {
				// TODO odlaczenie klienta, zabicie watku.
			}
        }
    }
}
