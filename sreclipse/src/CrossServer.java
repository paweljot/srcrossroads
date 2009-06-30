import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

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
        Crossing cross = new Crossing();

        //tworzenie socketa serwerowego

        serverSocket = new ServerSocket(listenPort);

		while (true) {
			Socket client = serverSocket.accept();
			log.append("Accepted from "+client.getInetAddress()+"\n");
			CrossServerThread h = new CrossServerThread(client, cross, log);
			clients.add(h);
			h.start();
		}

        //czekanie na podłączenie się 4 skrzyżowań
        /*while (clientCount < 4) {
            new CrossServerThread(serverSocket.accept(), clientCount, cross).start();
            clientCount++;
        }
        System.out.println("4 skrzyżowania podłączone!");



        while (true) {

            Thread.sleep(50);
            System.out.println("STAN SKRZYŻOWANIA:");
            for (int i = 0; i < 4; i++) {
                int state = cross.getState(i);

                System.out.println("Droga " + i + ": " + state);

            }
            System.out.println("------------------------");
            System.out.println();
            
        }
        */        
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
