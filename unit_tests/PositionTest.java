/*@author Agnes Aronsson [agar3573]*/
import java.util.Scanner;

public class PositionTest {
	
	public static void main(String args[]) {
		Scanner keyboard = new Scanner(System.in);
		char answer;
		int x = 0;
		int y = 0;
		
		do {
			System.out.print("x-coordinate: ");
			x = keyboard.nextInt();
			keyboard.nextLine();
		
			System.out.print("y-coordinate: ");
			y = keyboard.nextInt();
			keyboard.nextLine();
		
			Position p = new Position(x, y);
			System.out.println("Object x: " + p.getX());
			System.out.println("Object y: " + p.getY());
			System.out.println("Object string: " + p);
			System.out.println("Object hashcode: " + p.hashCode());
		
			System.out.println("Compare " + p + " to {50, 100}");
			System.out.println("They are equal: " + p.equals(new Position(50, 100)));
		
			System.out.print("Another time (Y/N)? ");
			answer = keyboard.next().charAt(0);
		} while(answer == 'y' || answer == 'Y');
		
		System.exit(0);
	}
}
