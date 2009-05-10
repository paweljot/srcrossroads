package srcrossroads;

import java.io.IOException;
import java.net.*;
import javax.swing.*;

/**
 *
 * @author paweljot
 */
public class CrossServer {

    private final static int listenPort = 2222;

    public static void main(String args[]) throws IOException, InterruptedException {

        //ustawienie GUI
        CrossServerGUI gui = new CrossServerGUI();
        gui.setVisible(true);

        ServerSocket serverSocket = null;
        int clientCount = 0;
        Crossing crossing = new Crossing();

        //tworzenie socketa serwerowego

        serverSocket = new ServerSocket(listenPort);


        //czekanie na podłączenie się 4 skrzyżowań
        while (clientCount < 4) {
            new CrossServerThread(serverSocket.accept(), clientCount, crossing).start();
            clientCount++;
        }
        System.out.println("4 skrzyżowania podłączone!");

        JLabel labels[] = new JLabel[4];
        labels[0]=gui.labelRoad0;
        labels[1]=gui.labelRoad1;
        labels[2]=gui.labelRoad2;
        labels[3]=gui.labelRoad3;

        while (true) {

            Thread.sleep(50);
            System.out.println("STAN SKRZYŻOWANIA:");
            for (int i = 0; i < 4; i++) {
                int state = crossing.getState(i);
                labels[i].setText(Integer.toString(state));
                System.out.println("Droga " + i + ": " + state);

            }
            System.out.println("------------------------");
            System.out.println();
            
        }




    }
}
