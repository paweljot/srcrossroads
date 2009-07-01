import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

 
/**
 *
 * @author paweljot
 */
public class Crossing {

    private Road[] roads = new Road[4];
    private CrossServerThread[] clients = new CrossServerThread[4];
    private CrossServer owner;
    private long nextLightChange;
    private Timer timer = new Timer();
    
    public Crossing(CrossServer owner) {
		// droga z góry na dół
		roads[0] = new Road(Road.Orientation.VERTICAL, Car.Direction.DOWN,70,50,0);
		// droga z lewej na prawo
		roads[1] = new Road(Road.Orientation.HORIZONTAL, Car.Direction.RIGHT,70,100,1);
		roads[1].light = Road.LightColor.GREEN;
		// droga z dołu do góry
		roads[2] = new Road(Road.Orientation.VERTICAL, Car.Direction.UP,120,100,2);
		// droga z prawej na lewo
		roads[3] = new Road(Road.Orientation.HORIZONTAL, Car.Direction.LEFT,120,50,3);
		roads[3].light = Road.LightColor.GREEN;
		this.owner = owner;
		Timer timer = new Timer();
	    lightControl lc = new lightControl();
	    timer.scheduleAtFixedRate(lc, 0, 7000);		
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
    
    public String getHello() {
    	String msg = new String();
    	for (int i=0;i<4;i++) {
    		if (roads[i].occupation==Road.Occupation.FREE)
    			msg+="0";
    		else
    			msg+="1";
    		if (roads[i].light==Road.LightColor.RED)
    			msg+="0";
    		else
    			msg+="1";
    		msg+=",";
    	}
    	return msg;
    }
    
    public String getNextLightChangeMsg() {
		String msg = Character.toString(CrossServerThread.T_LIGHTCH);
		msg+=Long.toString(nextLightChange);
		return msg;
    }
    
	/**
	 * Sluzy do zmiany swiatel.
	 */
	public void lightChange() {
		owner.log.append("zmiana Swiatel");
		for (int i=0;i<4;i++) {
			if (roads[i].light==Road.LightColor.GREEN)
				roads[i].light=Road.LightColor.RED;
			else
				roads[i].light=Road.LightColor.GREEN;
		}
	}
	
    class lightControl extends TimerTask {
		public void run() {
			nextLightChange = System.currentTimeMillis()+6000;
			String msg = Character.toString(CrossServerThread.T_LIGHTCH);
			msg+=Long.toString(nextLightChange);
			owner.broadcast(msg);
			//u siebie tez zmianiam swiatla
			timer.schedule(new lightChange(), new Date(nextLightChange));
		}
	}
	/**
	 * klasa do zmiany swiatla
	 * @author ojciec
	 *
	 */
    class lightChange extends TimerTask {
		public void run() {
			lightChange();
		}
	}        
}