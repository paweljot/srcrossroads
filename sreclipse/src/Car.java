import java.awt.Color;
import java.util.Random;

public class Car {
	public final static int length = 10;
	public int pos;
	public int speed;
	public Color color;

	public enum Direction {
		UP, DOWN, LEFT, RIGHT
	}

	public Direction direction;

	public Car(int speed, Direction direction) {
		pos = 0;
		this.speed = speed;
		this.direction = direction;
		Random rand = new Random();
		
		this.color = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
		
	}

	public void move() {
		
		Random rand = new Random();
		double factor = 0.02;
		if (speed<2) factor = 0.2;
		
		if (rand.nextDouble() < factor) {
			speed = rand.nextInt(10);
		}

		if (pos < 100 || true) {

			switch (direction) {
			case UP:
				pos-=speed;
				break;
			case DOWN:
				pos+=speed;
				break;
			case LEFT:
				pos-=speed;
				break;
			case RIGHT:
				pos+=speed;
				break;
			}

		}
	}

	public void draw() {

	}
}
