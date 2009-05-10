
package srcrossroads;

/**
 *
 * @author paweljot
 */
public class Crossing {

    private int[] roads = new int[4];

    public Crossing() {
        for (int i=0;i<roads.length;i++) {
            roads[i]=0;
        }
    }

    public synchronized void updateState(int roadNum,int newTraffic) {
        roads[roadNum] += newTraffic;
    }

    public int getState(int roadNum) {
        return roads[roadNum];
    }

}
