
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
    CrossServer server = null;
    
	//tokeny dla komunikatów:
	//TODO - w jakiejs wspolne klaise / pliku konfiguracyjnym
	public final static char T_OCCUPY = 0x11;
	public final static char T_SHELO = 0x01;
	public final static char T_OKOCC = 0x21;
	public final static char T_FLDOCC = 0x22;	
	public final static char T_NEWCAR = 0x23;
	public final static char T_LIGHTCH = 0x12;
	

    public CrossServerThread(CrossServer server,Socket socket,Crossing cross, JTextArea log) {
        super();
        this.socket = socket;
        this.log = log;
        this.cross = cross;
        this.server = server;

        try {
            //strumień wejściowy - od serwera
    		out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    		in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

        } catch (IOException ex) {
            System.out.println("Błąd io wątek - "+ex.getMessage());
        }


    }
    
    public void sendMessage(String msg) {
		try {
			out.writeUTF(msg);
			out.flush(); 
		} catch (IOException e) {
			// TODO zamkniecie klienta dla serwera.
		}   	
    }

    @Override
    public void run() {
        log.append("Stworzono nowy wątek dla klienta\n");
        String hello = Character.toString(T_SHELO);
        hello+=cross.getHello();
        sendMessage(hello);
        //wysylam tez informacje o nastepnej zmianie swiatla:
        sendMessage(cross.getNextLightChangeMsg());
        log.append("Wysłano hello: "+hello+"\n");        
        while (true) {
        	try {
				String msg = in.readUTF();
				char token = msg.charAt(0);
				switch (token) {
					case T_OCCUPY:
						int roadNumber = Integer.parseInt(msg.substring(1));
						if (cross.occupyRoad(roadNumber, this)) {
							sendMessage(Character.toString(T_OKOCC));
							log.append("Klient zajął drogę " +msg.substring(1)+"\n");
						}
						else {
							sendMessage(Character.toString(T_FLDOCC));
							log.append("Klient nie może zająć drogi " +msg.substring(1)+"\n");
						}
						break;
					case T_NEWCAR:
						//przyszedł nowy samochód na daną drogę
						String tmpmsg[] = msg.split(",");

						roadNumber = Integer.parseInt(tmpmsg[1]);
						int carspeed = Integer.parseInt(tmpmsg[2]);
						
						log.append("Przyszedł nowy samochód na drogę nr "+roadNumber+"" +
								" z prędkością " +carspeed+
								"\n");
						
						server.broadcastWithout(this, msg);
						
						
						break;

				}
			} catch (IOException e) {
				// TODO odlaczenie klienta, zabicie watku.
			}
        }
    }
}
