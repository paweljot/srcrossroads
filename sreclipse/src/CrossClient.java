import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JApplet;
import javax.swing.JButton;



import com.sun.xml.internal.bind.v2.util.CollisionCheckStack;

public class CrossClient extends JApplet {
	private CrossingK cross;
	private Road myRoad;
	private final int serverPort=2222;
	private final String serverHost = "127.0.0.1";
	private DataOutputStream output;
	private DataInputStream input;
	private Socket server;
	
	public void init() {
		try {
			javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					initGui();
					//connect();
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
		this.cross.addListeners(new Roader());
		}

	class Thrower implements java.awt.event.ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Random rand = new Random();
			myRoad = cross.roads[rand.nextInt(4)];

			myRoad.newCar(rand.nextInt(10) + 1);

		}

	}
	
	class Roader implements java.awt.event.ActionListener {
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked.");
			if (myRoad==null) {
				e.getSource();
			}
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
		roads[0] = new Road(Road.Orientation.VERTICAL, Car.Direction.DOWN,70,50);
		// droga z lewej na prawo
		roads[1] = new Road(Road.Orientation.HORIZONTAL, Car.Direction.RIGHT,70,100);
		// droga z dołu do góry
		roads[2] = new Road(Road.Orientation.VERTICAL, Car.Direction.UP,120,100);
		// droga z prawej na lewo
		roads[3] = new Road(Road.Orientation.HORIZONTAL, Car.Direction.LEFT,120,50);

		//przyciski obslugi obsadzenia:
		batons = new JButton[4];
		setLayout(new BorderLayout());
		batons[0] = new JButton("Take this road");
		batons[0].setPreferredSize(new Dimension(20,20));
		add(batons[0],BorderLayout.PAGE_START);
		batons[1] = new JButton("Take this road");
		batons[1].setPreferredSize(new Dimension(20,20));
		add(batons[1],BorderLayout.SOUTH);
		batons[2] = new JButton("Take this road");
		batons[2].setPreferredSize(new Dimension(20,20));
		add(batons[2],BorderLayout.WEST);
		batons[3] = new JButton("Take this road");
		batons[3].setPreferredSize(new Dimension(20,20));
		add(batons[3],BorderLayout.EAST);
		
		Mover mover = new Mover(this);
		mover.start();
	}

	public void addListeners(ActionListener listener) {
		for (int i=0; i<4; i++) {
			batons[i].addActionListener(listener);
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
		
		roads[0].drawSignalization(g);
		roads[1].drawSignalization(g);
		roads[2].drawSignalization(g);
		roads[3].drawSignalization(g);
		
		
		this.paintComponents(g);
	}

	class RoadButton extends JButton {
		private Road connectedRoad;
		
		public RoadButton(Road road, String text) {
			super(text);
			this.connectedRoad = road;
		}
	}
	
	class Mover extends Thread {
		private javax.swing.JPanel canvas;

		public Mover(javax.swing.JPanel holder) {
			this.canvas = holder;
		}

		public void run() {
			
			//ustawienie timera, żeby zmieniał światła
		    Timer timer = new Timer();
		    lightControl lc = new lightControl(roads);
		    timer.scheduleAtFixedRate(lc, 0, 7000);
		       
			
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
		
		
		//klasa wywoływana przez timer do zmieniania świateł co określony czas
		class lightControl extends TimerTask {
			private Road roads[];
			private int counter = 0;
			lightControl(Road roads[]) {
				super();
				this.roads = roads;
			}
			public void run() {

				
				for (int i = 0; i<4; i++) {
					if (i==counter || i== (counter+2)%4) {
						roads[i].light = Road.LightColor.GREEN;
					} else {
						roads[i].light = Road.LightColor.RED;
					}
				}
				counter++;
				counter = counter%4;
			}
		}
	}
}

class Road extends java.awt.Rectangle {
	// position samochodu to liczba od 1 do 100.
	private ArrayList<Car> cars;
	public boolean selected=false; 
	// orientacja drogi
	public enum Orientation {
		HORIZONTAL, VERTICAL
	};

	public Orientation orientation;
	public Car.Direction startDirection;
	
	//kolory świateł
	public enum LightColor {
		RED,ORANGE,GREEN
	};
	public LightColor light = LightColor.RED;
	private int lightPosX;
	private int lightPosY;
	
	
	private final int size = 30;

	public Road(Orientation orientation, Car.Direction startDirection, int lightX, int lightY) {
		cars = new ArrayList<Car>();
		this.orientation = orientation;
		this.startDirection = startDirection;
		
		this.lightPosX = lightX;
		this.lightPosY = lightY;
	}

	public void paint(Graphics g, int x, int y, int length) {
		if (selected)
			g.setColor(new Color(0,0,255));
		else
			g.setColor(new Color(255, 255, 255));
		if (orientation == Orientation.HORIZONTAL)
			// poziom:
			g.fillRect(x, y - size / 2, length, size);
		else
			// pion:
			g.fillRect(x - size / 2, y, size, length);
		
	}
	
	public void drawSignalization(Graphics g) {
		if (this.light == LightColor.RED) 
			g.setColor(new Color(255,0,0));
		else if (this.light == LightColor.GREEN) {
			g.setColor(new Color(0,255,0));
		} else if (this.light == LightColor.ORANGE) {
			g.setColor(new Color(255,255,0));
		}
		
		g.fillOval(lightPosX, lightPosY, 10, 10);
		
	}

	public void drawCars(Graphics g, int x, int y, int length) {
		java.util.Iterator<Car> i = cars.iterator();
		while (i.hasNext()) {

			Car car = i.next();

			g.setColor(car.color);
			if (this.orientation == Orientation.HORIZONTAL) {

				int carX = x + car.pos * length / 100;
				g.fillRect(carX, y - 10, Car.length, Car.length);

				// wyrzucamy samochody, które są poza planszą
				if (carX > length * 2 || carX < 0 - Car.length * 2)
					i.remove();

			} else {

				int carY = y + car.pos * length / 100;
				g.fillRect(x - 10, carY, Car.length, Car.length);

				// wyrzucamy samochody, które są poza planszą
				if (carY - Car.length > length * 2 || carY < 0 - Car.length * 2)
					i.remove();
			}
		}
	}

	public void newCar(int speed) {
		cars.add(new Car(speed, startDirection));
	}

	public void moveCars() {
		java.util.Iterator<Car> i = cars.iterator();

		checkCollisions();
		
		

		while (i.hasNext()) {
			Car tmp = i.next();

			if (Math.abs(tmp.pos) >= 80 && Math.abs(tmp.pos) < 90 && this.light==LightColor.RED) {
				tmp.speed = 0;
				if (tmp.pos<0) tmp.pos=-80; else tmp.pos=80;
			} else if (tmp.speed == 0 && this.light==LightColor.GREEN) {
				tmp.speed = 1;
			}


			tmp.move();
			
		}
	}

	public void checkCollisions() {
		java.util.Iterator<Car> i = cars.iterator();
		ArrayList<Car> carsUp = new ArrayList<Car>();
		ArrayList<Car> carsDown = new ArrayList<Car>();
		ArrayList<Car> carsLeft = new ArrayList<Car>();
		ArrayList<Car> carsRight = new ArrayList<Car>();

		Car car;

		while (i.hasNext()) {
			car = i.next();
			switch (car.direction) {
			case UP:
				carsUp.add(car);
			case DOWN:
				carsDown.add(car);
			case LEFT:
				carsLeft.add(car);
			case RIGHT:
				carsRight.add(car);
			}
		}

		 collisions(carsUp);
		 collisions(carsDown);
		 collisions(carsLeft);
		 collisions(carsRight);

	}

	private void collisions(ArrayList<Car> cars) {
		java.util.ListIterator<Car> i = cars.listIterator();

		if (!i.hasNext())
			return;

		Car cur;
		Car prev = i.next();

		do {
			// pobranie samochodu który wjechał na skrzyżowanie
			if (!i.hasNext())
				return;
			// i następnego po nim
			cur = i.next();


			
			
			
			switch (cur.direction) {
			case UP:
				if (cur.pos - cur.speed < prev.pos+Car.length) {
					cur.pos = prev.pos+Car.length;
					cur.speed = prev.speed;
				}
				break;
			case DOWN:
				if (cur.pos + cur.speed > prev.pos-Car.length) {
					cur.pos = prev.pos-Car.length;
					cur.speed = prev.speed;
				}
				break;
			case LEFT:
				
				if (cur.pos - cur.speed < prev.pos+Car.length) {
					cur.pos = prev.pos+Car.length;
					cur.speed = prev.speed;
				}
				break;
			case RIGHT:
				if (cur.pos + cur.speed > prev.pos - Car.length) {
					cur.pos = prev.pos - Car.length;
					cur.speed = prev.speed;
				}
				break;

			}
			

			prev = cur;
		} while (i.hasNext());

		return;
	}
	
	

}
