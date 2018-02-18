/*@author Agnes Aronsson [agar3573]*/
package Inlupp2;

import java.awt.*;
import javax.swing.*;

public class Form extends JPanel{
	protected JTextField nameField = new JTextField(15);
	protected JTextField descField = new JTextField(30);

	public String getNameInput() {
		return nameField.getText();
	}
	
	public void setNameField(String name) {
		nameField.setText(name);
	}
}	

class NamedPlaceForm extends Form{
		
	public NamedPlaceForm () {		
		JPanel form = new JPanel();
		add(form);
		
		form.add(new JLabel("Name: ", SwingConstants.RIGHT));
		form.add(nameField);
	}
}

class DescPlaceForm extends Form{
	
	public DescPlaceForm() {
		JPanel form = new JPanel();
		form.setLayout(new GridLayout(2,2));
		add(form);
		
		JPanel firstRow = new JPanel();
		form.add(firstRow);
		firstRow.add(new JLabel("Name: ", SwingConstants.RIGHT));
		firstRow.add(nameField);
		
		JPanel secondRow = new JPanel();
		form.add(secondRow);
		secondRow.add(new JLabel("Description: ", SwingConstants.RIGHT));
		secondRow.add(descField);
	}
	
	public String getDescInput() {
		return descField.getText();
	}
}
	
class NamedPlaceInfo extends Form{
		
	public NamedPlaceInfo() {
		JPanel nform = new JPanel();
		add(nform);
		
		nform.add(new JLabel("Name: ", SwingConstants.RIGHT));
		nform.add(nameField);
	}
}
	
class DescPlaceInfo extends Form{
		
	public DescPlaceInfo() {
		JPanel dform = new JPanel();
		dform.setLayout(new GridLayout(2,2));
		add(dform);
		
		JPanel firstRow = new JPanel();
		dform.add(firstRow);
		firstRow.add(new JLabel("Name: ", SwingConstants.RIGHT));
		firstRow.add(nameField);
		
		JPanel secondRow = new JPanel();
		dform.add(secondRow);
		secondRow.add(new JLabel("Description: ", SwingConstants.RIGHT));
		secondRow.add(descField);
	}
		
	public void setDescField(String desc) {
		descField.setText(desc);
	}
}

class PositionForm extends Form {
	private JTextField xField = new JTextField(5);
	private JTextField yField = new JTextField(5);
 
	public PositionForm() {
		JPanel form = new JPanel();
		add(form);
		form.add(new JLabel("x: "));
		form.add(xField);
		
		form.add(new JLabel("y: "));
		form.add(yField);
	}
	
	public int getXField() {
		return Integer.parseInt(xField.getText());
	}
	
	public int getYField() {
		return Integer.parseInt(yField.getText());
	}
	
	public void setXField() {
		xField.setText("");
	}
	
	public void setYField() {
		yField.setText("");
	}
}
