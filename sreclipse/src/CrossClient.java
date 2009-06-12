import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JApplet;

import com.sun.xml.internal.bind.v2.util.CollisionCheckStack;

public class CrossClient extends JApplet {
	private CrossingK cross;
	private Road myRoad;

	public void init() {
		try {
			javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					initGui();
					// myRoad = cross.roads[0];
				}
			});
		} catch (Exception e) {
			System.err.println("createGUI didn't successfully complete: "
					+ e.getMessage());
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

	}

	class Thrower implements java.awt.event.ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Random rand = new Random();
			myRoad = cross.roads[rand.nextInt(4)];

			myRoad.newCar(rand.nextInt(10) + 1);

		}

	}

}

class CrossingK extends javax.swing.JPanel {

	public Road roads[];

	public CrossingK() {
		super();
		roads = new Road[4];
		// droga z góry na dół
		roads[0] = new Road(Road.Orientation.VERTICAL, Car.Direction.DOWN);
		// droga z lewej na prawo
		roads[1] = new Road(Road.Orientation.HORIZONTAL, Car.Direction.RIGHT);
		// droga z dołu do góry
		roads[2] = new Road(Road.Orientation.VERTICAL, Car.Direction.UP);
		// droga z prawej na lewo
		roads[3] = new Road(Road.Orientation.HORIZONTAL, Car.Direction.LEFT);

		Mover mover = new Mover(this);
		mover.start();
	}

	public void paint(Graphics g) {
		// tlo:
		g.setColor(new Color(255, 0, 0));
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
		roads[1].drawCars(g, 0 - Car.length, height / 2, width / 2);
		roads[2].drawCars(g, width / 2, height, height / 2);
		roads[3].drawCars(g, width, height / 2, width / 2);

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

class Road extends java.awt.Rectangle {
	// position samochodu to liczba od 1 do 100.
	private ArrayList<Car> cars;

	// orientacja drogi
	public enum Orientation {
		HORIZONTAL, VERTICAL
	};

	public Orientation orientation;
	public Car.Direction startDirection;
	private final int size = 30;

	public Road(Orientation orientation, Car.Direction startDirection) {
		cars = new ArrayList<Car>();
		this.orientation = orientation;
		this.startDirection = startDirection;
	}

	public void paint(Graphics g, int x, int y, int length) {
		g.setColor(new Color(255, 255, 255));
		if (orientation == Orientation.HORIZONTAL)
			// poziom:
			g.fillRect(x, y - size / 2, length, size);
		else
			// pion:
			g.fillRect(x - size / 2, y, size, length);
		// this.drawCars(g, x,y,length);
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
			i.next().move();
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
