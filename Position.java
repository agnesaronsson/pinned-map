/*@author Agnes Aronsson [agar3573]*/
package Inlupp2;

public class Position {
	private int x;
	private int y;
	
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}

	public String toString() {
		return "{" + Integer.toString(x) + ", " + Integer.toString(y) + "}";
	}
	
	@Override
	public int hashCode() {
		return x * 100 + y;
	}
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof Position) {
			Position p = (Position)other;
			if(x == p.getX() && y == p.getY()) {
				return true;
			}
		}
		return false;
	}
}
