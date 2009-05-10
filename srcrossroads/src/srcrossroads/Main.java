

package srcrossroads;

/**
 *
 * @author paweljot
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new CrossServer();

        for (int i=0;i<4;i++) {

            new CrossClient();

        }
    }

}
