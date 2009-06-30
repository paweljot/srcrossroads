 
/**
 *
 * @author paweljot
 */
public class Crossing {

    private Road[] roads = new Road[4];

    public Crossing() {
		// droga z góry na dół
		roads[0] = new Road(Road.Orientation.VERTICAL, Car.Direction.DOWN,70,50,0);
		// droga z lewej na prawo
		roads[1] = new Road(Road.Orientation.HORIZONTAL, Car.Direction.RIGHT,70,100,1);
		// droga z dołu do góry
		roads[2] = new Road(Road.Orientation.VERTICAL, Car.Direction.UP,120,100,2);
		// droga z prawej na lewo
		roads[3] = new Road(Road.Orientation.HORIZONTAL, Car.Direction.LEFT,120,50,3);
    }

}
