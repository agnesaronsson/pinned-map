/*@author Agnes Aronsson [agar3573]*/
package Inlupp2;

import javax.swing.*;
import java.awt.*;
import javax.swing.JPanel;

public class MapImg extends JPanel{
	private ImageIcon mapImg;
	
	public MapImg(String file) {
		mapImg = new ImageIcon(file);
		int w = mapImg.getIconWidth();
		int h = mapImg.getIconHeight();
		setPreferredSize(new Dimension(w,h));
		setMaximumSize(new Dimension(w,h));
		setMinimumSize(new Dimension(w,h));	
		setSize(new Dimension(w,h));
		setLayout(null);
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(mapImg.getImage(), 0, 0, getWidth(), getHeight(), this);
	}
}
