 
/**
 *
 * @author paweljot
 */
public class Crossing {

    private Road[] roads = new Road[4];
    private CrossServerThread[] clients = new CrossServerThread[4];
    private CrossServer owner;
    
    public Crossing(CrossServer owner) {
		// droga z góry na dół
		roads[0] = new Road(Road.Orientation.VERTICAL, Car.Direction.DOWN,70,50,0);
		// droga z lewej na prawo
		roads[1] = new Road(Road.Orientation.HORIZONTAL, Car.Direction.RIGHT,70,100,1);
		// droga z dołu do góry
		roads[2] = new Road(Road.Orientation.VERTICAL, Car.Direction.UP,120,100,2);
		// droga z prawej na lewo
		roads[3] = new Road(Road.Orientation.HORIZONTAL, Car.Direction.LEFT,120,50,3);
		this.owner = owner;
    }

    public synchronized boolean occupyRoad(int number, CrossServerThread watekKlienta) {
    	if (roads[number].occupation!=Road.Occupation.OCCUPIED) {
    		roads[number].occupation=Road.Occupation.OCCUPIED;
    		clients[number] = watekKlienta;
    		String msg = Character.toString(watekKlienta.T_OCCUPY);
    		msg += Integer.toString(number); 
    		owner.broadcastWithout(watekKlienta, msg);
    		return true;
    	}
    	return false;
    }
}