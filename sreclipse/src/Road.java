import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

class Road extends java.awt.Rectangle {
	// orientacja drogi
	public enum Orientation {
		HORIZONTAL, VERTICAL
	};
	//kolory świateł
	public enum LightColor {
		RED,ORANGE,GREEN
	};
	//zajetosc :)
	public enum Occupation {
		OCCUPIED, FREE;
	}
	

	// position samochodu to liczba od 1 do 100.
	private ArrayList<Car> cars;

	public Orientation orientation;
	public Car.Direction startDirection;
	public Occupation occupation;
	
	public LightColor light = LightColor.RED;
	private int lightPosX;
	private int lightPosY;
	public int roadNumber;
	private final int size = 30;
	

	//TODO konstruktor dla drogi bez tych orientacji itd - to co potrzebuje serwer.
	public Road(Orientation orientation, Car.Direction startDirection, int lightX, int lightY, int number) {
		cars = new ArrayList<Car>();
		this.orientation = orientation;
		this.startDirection = startDirection;
		this.roadNumber = number;
		this.lightPosX = lightX;
		this.lightPosY = lightY;
		this.occupation = Occupation.FREE;
	}

	public void paint(Graphics g, int x, int y, int length) {
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
