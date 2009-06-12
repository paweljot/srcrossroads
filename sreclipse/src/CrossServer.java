

import java.io.IOException;
import java.net.*;


/**
 *
 * @author paweljot
 */
public class CrossServer {

    private final static int listenPort = 2222;

    public static void main(String args[]) throws IOException, InterruptedException {

        //ustawienie GUI
        
        ServerSocket serverSocket = null;
        int clientCount = 0;
        Crossing cross = new Crossing();

        //tworzenie socketa serwerowego

        serverSocket = new ServerSocket(listenPort);


        //czekanie na podłączenie się 4 skrzyżowań
        while (clientCount < 4) {
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




    }
}
