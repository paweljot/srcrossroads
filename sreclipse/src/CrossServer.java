import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


/**
 *
 * @author paweljot
 */
public class CrossServer extends JFrame {

    private final static int listenPort = 2222;
    private JTextArea log;
    private int clientCount;
    
    private ArrayList<CrossServerThread> clients;
    
    public CrossServer() throws IOException, InterruptedException {
        initGui();
		//poprawne wychodzenie:
	      addWindowListener(new WindowAdapter()
	      {
	         public void windowClosing(WindowEvent e)
	         {
	           dispose();
	           System.exit(0); //calling the method is a must
	         }
	      });
	      
        ServerSocket serverSocket = null;
        clientCount = 0;
        clients = new ArrayList<CrossServerThread>();
        Crossing cross = new Crossing(this);

        //tworzenie socketa serwerowego

        serverSocket = new ServerSocket(listenPort);

		while (true) {
			Socket client = serverSocket.accept();
			log.append("Accepted from "+client.getInetAddress()+"\n");
			CrossServerThread h = new CrossServerThread(this,client, cross, log);
			clients.add(h);
			h.start();
		}
  
    }
    
    public void broadcast(String msg) {
    	Iterator<CrossServerThread> i = clients.iterator();
    	while (i.hasNext()) {
    		i.next().sendMessage(msg);
    	}
    }
    
    public void broadcastWithout(CrossServerThread notThis, String msg) {
    	Iterator<CrossServerThread> i = clients.iterator();
    	while (i.hasNext()) {
    		CrossServerThread watek = i.next();
    		if (!watek.equals(notThis))
    			watek.sendMessage(msg);    			
    	}    	
    }
    
    public static void main(String args[]) throws IOException, InterruptedException {
    	new CrossServer();
    }
    
    private void initGui() {
		setTitle("SR-Server");
		setSize(200,600);
		getContentPane().add(new JScrollPane(log = new JTextArea()));
		setVisible(true);
		log.setEditable(false);
		log.append("Hello!\n");

    }    
}
