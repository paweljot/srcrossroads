
package srcrossroads;

import java.io.IOException;
import java.net.*;


/**
 *
 * @author paweljot
 */
public class CrossServer {

    private final static int listenPort = 2222;
    
    public static void main(String args[]) throws IOException {
        ServerSocket serverSocket = null;
        int clientCount = 0;

        //tworzenie socketa serwerowego
        try {
            serverSocket = new ServerSocket(listenPort);
        } catch (IOException ex) {
            System.err.println("Could not listen on port: "+listenPort);
            System.exit(-1);
        }

        //czekanie na podłączenie się 4 skrzyżowań
        while(clientCount<4) {
            new CrossServerThread(serverSocket.accept(),clientCount+1).start();
            clientCount++;
        }
        System.out.println("4 skrzyżowania podłączone!");
        
        
        

       
    }

}
