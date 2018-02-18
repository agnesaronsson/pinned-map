/*@author Agnes Aronsson [agar3573]*/
import javax.swing.*;
import java.awt.*;

public class Place extends JComponent implements Comparable<Place>{
	private String name;
	private Position position;
	private String category;
	private boolean pin = true;
	private boolean highlight = false;
	
	public Place(String name, Position position, String category) {
		this.name = name;
		this.position = position;
		this.category = category;
		setBounds((position.getX() - 10), (position.getY() - 20), 20, 20);
	}
	
	public String getName() {
		return name;
	}
	
	public Position getPosition() {
		return position;
	}
	
	public String getCategory() {
		if(category == null) {
			category = "None";
		}
		return category;
	}
	
	public boolean getPin() {
		return pin;
	}
	
	public boolean getHighlight() {
		return highlight;
	}
	
	public void setPin(boolean b){
		pin = b;
	}
	
	public void setHighlight(boolean b) {
		highlight = b;
	}
	
	public void paintPin(Graphics g) {
		int[] xPoints = {0, 10, 20}, yPoints = {0, 20, 0};
		int totPoints = 3;
		
		if(category.equals("Bus")) {
			g.setColor(Color.RED);
			g.fillPolygon(xPoints, yPoints, totPoints);
		} else if(category.equals("Underground")){
			g.setColor(Color.BLUE);
			g.fillPolygon(xPoints, yPoints, totPoints);
		} else if(category.equals("Train")){
			g.setColor(Color.GREEN);
			g.fillPolygon(xPoints, yPoints, totPoints);
		} else {
			g.setColor(Color.BLACK);
			g.fillPolygon(xPoints, yPoints, totPoints);
		}
	}
	
	public void paintHighlight(Graphics g) {
		g.setColor(Color.RED);
		g.drawRect(0, 0, 19, 19);
	}
	
@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		if(pin) {
			paintPin(g);
		}
		
		if(highlight) {
			paintHighlight(g);
		}
	}

@Override
	public int compareTo(Place p) {
		return name.compareTo(p.getName());
	}
}

class NamedPlace extends Place {
	public NamedPlace(String name, Position position, String category) {
		super(name, position, category);
	}
	
	public String toString() {
		return "Named," + getCategory() + "," + getPosition().getX() + "," + getPosition().getY() + "," + getName();
	}
}
	
class DescribedPlace extends Place {
	private String description;
		
	public DescribedPlace(String name, Position position, String category, String description) {
		super(name, position, category);
		this.description = description;
	}
		
	public String getDescription() {
		return description;
	}
	
	public String toString() {
		return "Described," + getCategory() + "," + getPosition().getX() + "," + getPosition().getY() + "," + getName() + "," + description;
	}
}
