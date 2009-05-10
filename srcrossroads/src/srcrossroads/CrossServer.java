
package srcrossroads;

import java.io.IOException;
import java.net.*;


/**
 *
 * @author paweljot
 */
public class CrossServer {

    private final static int listenPort = 2222;
    
    public static void main(String args[]) throws IOException, InterruptedException {
        ServerSocket serverSocket = null;
        int clientCount = 0;
        Crossing crossing = new Crossing();

        //tworzenie socketa serwerowego
        
        serverSocket = new ServerSocket(listenPort);
        

        //czekanie na podłączenie się 4 skrzyżowań
        while(clientCount<4) {
            new CrossServerThread(serverSocket.accept(),clientCount,crossing).start();
            clientCount++;
        }
        System.out.println("4 skrzyżowania podłączone!");

        while (true) {
            Thread.sleep(10000);
            System.out.println("STAN SKRZYŻOWANIA:");
            for (int i=0;i<4;i++) {
                System.out.println("Droga "+i+": "+crossing.getState(i));
            }
            System.out.println("------------------------");
            System.out.println();
        }
        
        

       
    }

}
