import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JApplet;
import javax.swing.JButton;



public class CrossClient extends JApplet {
	private CrossingK cross;
	private Road myRoad;
	private final int serverPort=2222;
	private final String serverHost = "127.0.0.1";
	private DataOutputStream output;
	private DataInputStream input;
	private Socket server;
	private Connection conn;
	private Roader buttonListener; 
	
	//tokeny dla komunikatów:
	
	public final char T_OCCUPY = 0x11;
	public final char T_SHELO = 0x01;
	public final char T_OKOCC = 0x21;
	public final char T_FLDOCC = 0x22;	
	public final char T_NEWCAR = 0x23;	
	public final char T_LIGHTCH = 0x12;
	
	//token dla okupacji drogi
	
	private boolean occupyResponse;
	
	public void init() {
		try {
			javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					initGui();
					connect();
				}
			});
		} catch (Exception e) {
			System.err.println("createGUI didn't successfully complete: "
					+ e.getMessage());
		}

	}

	public void connect() {
		try {
			server = new Socket(serverHost, serverPort);
			output = new DataOutputStream(new BufferedOutputStream(server.getOutputStream()));
			input = new DataInputStream(new BufferedInputStream(server.getInputStream()));
			conn = new Connection();
			conn.start();
		} catch (UnknownHostException e) {
			//TODO obsluga błędów.
			e.printStackTrace();
		} catch (IOException e) {
			//TODO obsluga błędów.			
			e.printStackTrace();
		}
	}
	
	public void initGui() {
		getContentPane().setLayout(new java.awt.BorderLayout());
		
		javax.swing.JButton but = new javax.swing.JButton("Throw a car!");
		but.setPreferredSize(new Dimension(300, 40));
		but.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1,
				Color.black));
		but.addActionListener(new Thrower());
		getContentPane().add(but, BorderLayout.NORTH);
		this.cross = new CrossingK();
		getContentPane().add(cross, BorderLayout.CENTER);
		buttonListener = new Roader();
		this.cross.addListeners(buttonListener);
		}

	public synchronized boolean occupyRoad(int number) {
		//poinformuj serwer:
		String msg = Character.toString(T_OCCUPY);
		msg+=Integer.toString(number);
		conn.sendMessage(msg);
		try {
			wait();
		} catch (InterruptedException e1) {
			//TODO lost communication with server
		}
		return occupyResponse;
	}
	
	class Thrower implements java.awt.event.ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Random rand = new Random();
			//myRoad = cross.roads[rand.nextInt(4)];
			if (myRoad!=null) {
				Car newcar = myRoad.newCar(rand.nextInt(10) + 1);
				
				
				//wysylanie wiadomości do serwera
				String msg = Character.toString(T_NEWCAR);
				msg+=","+Integer.toString(myRoad.roadNumber);
				msg+=","+Integer.toString(newcar.startSpeed);
				
				
				conn.sendMessage(msg);
			}

		}

	}
	
	class Roader implements java.awt.event.ActionListener {
		public synchronized void actionPerformed(ActionEvent e) {
			//System.out.println(((RoadButton)e.getSource()).getBackground());
			Road selectedRoad = ((RoadButton)e.getSource()).connectedRoad;
			if (myRoad==null && selectedRoad.occupation==Road.Occupation.FREE) {
				//poinformuj serwer:
				String msg = Character.toString(T_OCCUPY);
				msg+=Integer.toString(selectedRoad.roadNumber);
				conn.sendMessage(msg);
				try {
					wait();
				} catch (InterruptedException e1) {
					//TODO lost communication with server
				}
				
				if (occupyResponse) {
					((RoadButton)e.getSource()).setBackground(new Color(0,255,0));
					myRoad = selectedRoad;
				}
			}
/*			else if (myRoad == ((RoadButton)e.getSource()).connectedRoad) {
				myRoad=null;
				((RoadButton)e.getSource()).setBackground(new Color(238,238,238));				
			}
			*/
		}
	
	}
	class Connection extends Thread {
		public void sendMessage(String msg) {
			try {
				output.writeUTF(msg);
				output.flush();
			}
			catch (IOException e) {
				//TODO poinformowanie o takowym fakcie klienta i zamkniecie aplikacji.
			}
		}	
		public void run() {
			try {
				while(true) {
					String msg = input.readUTF();
					synchronized (buttonListener) {
						char token = msg.charAt(0);

						switch(token) {
						case T_SHELO:
							String args[] = (msg.substring(1)).split(",");
							cross.setUp(args);
							break;
						case T_OKOCC:
							occupyResponse = true;
							buttonListener.notify();
							break;
						case T_FLDOCC:
							occupyResponse = false;
							buttonListener.notify();
							break;
						case T_OCCUPY:
							int roadNumber = Integer.parseInt(msg.substring(1));
							cross.roadOccupied(roadNumber);
							break;
						case T_NEWCAR:
							String tmpmsg[] = msg.split(",");

							roadNumber = Integer.parseInt(tmpmsg[1]);
							int carspeed = Integer.parseInt(tmpmsg[2]);
							cross.roads[roadNumber].newCar(carspeed);
							break;
						case T_LIGHTCH:
							long when = Long.parseLong(msg.substring(1));
							if (when < System.currentTimeMillis())
								//jezeli sie spoznilem to wykonuje to natychmiast!
								//TODO jakas informacja o duzym latency !
								cross.lightChange();
							else {
								Timer timer = new Timer();
								timer.schedule(new lightChange(), new Date(when));
							}
							break;
						}
					}
				}
			} catch (IOException e) {
				//TODO
			} finally {
				//TODO
			}
		}
	}
	/**
	 * klasa do zmiany swiatla
	 * @author ojciec
	 *
	 */
    class lightChange extends TimerTask {
		public void run() {
			cross.lightChange();
		}
	}    

}

class CrossingK extends javax.swing.JPanel {

	public Road roads[];
	public JButton batons[];
	
	public CrossingK() {
		super();
		roads = new Road[4];
		
		// droga z góry na dół
		roads[0] = new Road(Road.Orientation.VERTICAL, Car.Direction.DOWN,-30,-30,0);
		// droga z lewej na prawo
		roads[1] = new Road(Road.Orientation.HORIZONTAL, Car.Direction.RIGHT,-30,20,1);
		// droga z dołu do góry
		roads[2] = new Road(Road.Orientation.VERTICAL, Car.Direction.UP,20,20,2);
		// droga z prawej na lewo
		roads[3] = new Road(Road.Orientation.HORIZONTAL, Car.Direction.LEFT,20,-30,3);

		//przyciski obslugi obsadzenia:
		batons = new JButton[4];
		setLayout(new BorderLayout());
		batons[0] = new RoadButton(roads[0], "Take this road");
		batons[0].setPreferredSize(new Dimension(20,20));
		add(batons[0],BorderLayout.PAGE_START);
		batons[1] = new RoadButton(roads[1], "Take this road");
		batons[1].setPreferredSize(new Dimension(20,20));
		add(batons[1],BorderLayout.WEST);
		batons[2] = new RoadButton(roads[2], "Take this road");
		batons[2].setPreferredSize(new Dimension(20,20));
		add(batons[2],BorderLayout.SOUTH);
		batons[3] = new RoadButton(roads[3], "Take this road");
		batons[3].setPreferredSize(new Dimension(20,20));
		add(batons[3],BorderLayout.EAST);
		
		Mover mover = new Mover(this);
		mover.start();
		
		//ustawienie timera, żeby zmieniał światła
/*	    Timer timer = new Timer();
	    lightControl lc = new lightControl(roads);
	    timer.scheduleAtFixedRate(lc, 0, 7000);
	   */
	}

	public void addListeners(ActionListener listener) {
		for (int i=0; i<4; i++) {
			batons[i].addActionListener(listener);
		}
	}
	/**
	 *  SLuzy do oznaczenia drogi jako zjaeta - jesli dostaniemyu taka informacje od serwera.
	 * @param number
	 */
	public void roadOccupied(int number) {
		roads[number].occupation = Road.Occupation.OCCUPIED;
		batons[number].setBackground(new Color(255,0,0));
	}
	/**
	 * Sluzy do zmiany swiatel.
	 */
	public void lightChange() {
		for (int i=0;i<4;i++) {
			if (roads[i].light==Road.LightColor.GREEN)
				roads[i].light=Road.LightColor.RED;
			else
				roads[i].light=Road.LightColor.GREEN;
		}
	}
	/**
	 * Sluzy do ustawienia skrzyzowanie po podlaczeniu do serwera
	 */
	
	public void setUp(String[] args) {
		int counter=0;
		for (int i=0;i<args.length;i++) {
			if (args[i].length()==2) {
				//poprawny argument:
				if (args[i].substring(0, 1).equals("1")) {
					//zajęta droga:
					roads[counter].occupation=Road.Occupation.OCCUPIED;
					//button na czerwono:
					batons[counter].setBackground(new Color(255,0,0));
				}
				
				if (args[i].substring(1,2).equals("0")) {
					//czerwone:
					roads[counter].light = Road.LightColor.RED;
				}
				else 
					//zielone
					roads[counter].light = Road.LightColor.GREEN;
				
				counter++;
			}
		}
	}
	
	public void paint(Graphics g) {
		// tlo:
		g.setColor(new Color(195, 195, 195));
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		g.setColor(new Color(255, 255, 255));
		// drogi:
		int width = this.getWidth();
		int height = this.getHeight();
		roads[0].paint(g, width / 2, 0, height / 2);
		roads[1].paint(g, 0, height / 2, width / 2);
		roads[2].paint(g, width / 2, height / 2, height / 2);
		roads[3].paint(g, width / 2, height / 2, width / 2);

		roads[0].drawCars(g, width / 2, 0 - Car.length, height / 2);
		roads[1].drawCars(g, 0 - Car.length, height / 2+Car.length, width / 2);
		roads[2].drawCars(g, width / 2+Car.length, height, height / 2);
		roads[3].drawCars(g, width, height / 2, width / 2);
		
		roads[0].drawSignalization(g,this.getWidth(),this.getHeight());
		roads[1].drawSignalization(g,this.getWidth(),this.getHeight());
		roads[2].drawSignalization(g,this.getWidth(),this.getHeight());
		roads[3].drawSignalization(g,this.getWidth(),this.getHeight());
		
		
		this.paintComponents(g);
	}

	class Mover extends Thread {
		private javax.swing.JPanel canvas;

		public Mover(javax.swing.JPanel holder) {
			this.canvas = holder;
		}

		public void run() {
			while (true) {
				roads[0].moveCars();
				roads[1].moveCars();
				roads[2].moveCars();
				roads[3].moveCars();
				try {
					this.canvas.repaint();
					sleep(67);
				} catch (InterruptedException e) {
					System.out.println("Mover Exception" + e.getMessage());
				}
			}
		}
		
	}
}

class RoadButton extends JButton {
	public Road connectedRoad;
	
	public boolean occupied;
	//TODO rysowanie zaleznie od tego argumentu (background)
	
	public RoadButton(Road road, String text) {
		super(text);
		this.connectedRoad = road;
	}
}

