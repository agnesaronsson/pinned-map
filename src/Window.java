/*@author Agnes Aronsson [agar3573]*/
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;

public class Window extends JFrame {
	private JFileChooser jfc = new JFileChooser();
	private MapImg mapImg = null;
	private JLabel imageLabel;
	private JTextField searchField;
	private JList<String> categoryList;
	private JButton add, search, hide, remove, coordinates, hideCategory;
	private JRadioButton named, described;
	private boolean enableNew = false;
	private boolean enableClick = true;
	private TreeMap<String, HashSet<Place>> placesByName = new TreeMap<>();
	private HashMap<String, HashSet<Place>> placesByCategory = new HashMap<>();
	private HashMap<Position, Place> placesByPosition = new HashMap<>();
	private HashSet<Place> highlightedPlaces = new HashSet<>();
	
	public Window() {
		super("Map");

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		JMenu menu = new JMenu("Archive");
		menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);

		JMenuItem newMap = new JMenuItem("New Map", KeyEvent.VK_N);
		menu.add(newMap);
		newMap.addActionListener(new NewMapListener());
		
		JMenuItem loadItem = new JMenuItem("Load Places", KeyEvent.VK_N);
		menu.add(loadItem);
		loadItem.addActionListener(new LoadListener());
			
		JMenuItem saveItem = new JMenuItem("Save", KeyEvent.VK_N);
		menu.add(saveItem);
		saveItem.addActionListener(new SaveListener());
		
		JMenuItem exitItem = new JMenuItem("Exit", KeyEvent.VK_N);
		menu.add(exitItem);
		
		JPanel centerPanel = new JPanel();
		add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new GridLayout());
		imageLabel = new JLabel();
		JScrollPane scroll = new JScrollPane(imageLabel);
		centerPanel.add(scroll);
		
		JPanel northPanel = new JPanel();
		add(northPanel, BorderLayout.NORTH);

		add = new JButton("New");
		northPanel.add(add);
		add.addActionListener(new NewPlaceListener());
		
		named = new JRadioButton("Named", true);
		described = new JRadioButton("Described");	
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		buttonPanel.add(named);
		buttonPanel.add(described);
		ButtonGroup options = new ButtonGroup();
		options.add(named);
		options.add(described);
		northPanel.add(buttonPanel);
		
		searchField = new JTextField("Search", 15);
		northPanel.add(searchField);
		searchField.addMouseListener(new SearchFieldListener());
		
		search = new JButton("Search");
		northPanel.add(search);
		search.addActionListener(new SearchButtonListener());
		
		hide = new JButton("Hide");
		northPanel.add(hide);
		hide.addActionListener(new HideListener());
		
		remove = new JButton("Remove");
		northPanel.add(remove);
		remove.addActionListener(new RemoveListener());
		
		coordinates = new JButton("Coordinates");
		northPanel.add(coordinates);
		coordinates.addActionListener(new CoordinatesListener());
		
		JPanel eastPanel = new JPanel();
		eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.Y_AXIS));
		add(eastPanel, BorderLayout.EAST);
		eastPanel.add(new JLabel("Categories"));
		
		String[] categories = {"Bus", "Underground", "Train"};
		categoryList = new JList<>(categories);
		categoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		categoryList.addListSelectionListener(new ShowCatListener());
		JScrollPane categoryField = new JScrollPane(categoryList);
		eastPanel.add(categoryField);
		
		hideCategory = new JButton("Hide category");
		eastPanel.add(hideCategory);
		hideCategory.addActionListener(new HideCatListener());
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				exit();
			}
		});
		setSize(700,400);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void addPlace(Place newPlace) {
		String category = newPlace.getCategory();
		if(placesByCategory.containsKey(category)) {
			placesByCategory.get(category).add(newPlace);
		} else {
			HashSet<Place> categoryPlace = new HashSet<>();
			categoryPlace.add(newPlace);
			placesByCategory.put(category, categoryPlace);
		}
		
		String name = newPlace.getName();
		if(placesByName.containsKey(name)) {
			placesByName.get(name).add(newPlace);
		} else {
			HashSet<Place> namePlace = new HashSet<>();
			namePlace.add(newPlace);
			placesByName.put(name, namePlace);
		}
		
		placesByPosition.put(newPlace.getPosition(), newPlace);
		
		newPlace.addMouseListener(new ClickListener());
		mapImg.add(newPlace);
		validate();
		repaint();
		categoryList.clearSelection();
	}
	
	public void savePlaces() {
		FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
		jfc.setFileFilter(filter);
		jfc.setSelectedFile(new File("map.places"));
		if(!placesByPosition.isEmpty()) {
			int response = jfc.showSaveDialog(Window.this);
			if(response != JFileChooser.APPROVE_OPTION) {
				jfc.resetChoosableFileFilters();
				return;
			}
			try{
				String fileName = jfc.getSelectedFile() + ".txt";
				FileWriter outputFile = new FileWriter(fileName);
				PrintWriter out = new PrintWriter(outputFile);
			
				for(Place place : placesByPosition.values()) {
					out.println(place);
				}
				
				out.close();
			} catch(IOException e) {
				JOptionPane.showMessageDialog(Window.this, "Error " + e.getMessage());
			}
		} else {
			JOptionPane.showMessageDialog(null, "No places to save yet.", "Error", 
					JOptionPane.ERROR_MESSAGE);
		}
		jfc.resetChoosableFileFilters();
	}
	
	public void removePlaces() {
		for(Place place : highlightedPlaces) {
			String category = place.getCategory();
			String name = place.getName();
			
			if(placesByCategory.get(category).contains(place)) {
				placesByCategory.get(category).remove(place);
				if(placesByCategory.get(category).isEmpty()) {
					placesByCategory.remove(category);
				}
			}
			
			if(placesByName.get(name).contains(place)) {
				placesByName.get(name).remove(place);
				if(placesByName.get(name).isEmpty()) {
					placesByName.remove(name);
				}
			}
			
			if(placesByPosition.containsKey(place.getPosition())) {
				placesByPosition.values().remove(place);
				place.setVisible(false);
			}
		}
		highlightedPlaces.clear();
	}
	
	public void exit() {
		if(!placesByPosition.isEmpty()) {
			int i = JOptionPane.showConfirmDialog(null, "Do you want to save your places before exiting?", "Exit", 
					JOptionPane.YES_NO_CANCEL_OPTION);
			if(i == JOptionPane.YES_OPTION) {
				savePlaces();
			} else if(i == JOptionPane.NO_OPTION){
				dispose();
			} else {
				return;
			}
		} else {
			dispose();
		}
	}
	
	class NewMapListener implements ActionListener {
		public void addNewMap() {
			jfc.setSelectedFile(new File(""));
			int response = jfc.showOpenDialog(Window.this);
			if(response != JFileChooser.APPROVE_OPTION) {
				return;
			}
			placesByPosition.values().forEach(place -> highlightedPlaces.add(place));
			removePlaces();
			File myFile = jfc.getSelectedFile();
			String path = myFile.getAbsolutePath();

			mapImg = new MapImg(path);
			imageLabel.removeAll();
			imageLabel.add(mapImg);
			imageLabel.setPreferredSize(mapImg.getSize());
			validate();
			repaint();
		}
		
		public void actionPerformed(ActionEvent act) {
			if(placesByPosition.isEmpty()) {
				addNewMap();
			} else {
				int i = JOptionPane.showConfirmDialog(null, "Do you want to save current places?", "Exit", 
						JOptionPane.YES_NO_CANCEL_OPTION);
				if(i == JOptionPane.YES_OPTION) {
					savePlaces();
				} else if(i == JOptionPane.NO_OPTION) {
					addNewMap();
				} else {
					return;
				}
			}	
		}
	}
	
	class LoadListener implements ActionListener {
		public void loadPlaces() {
			FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
			jfc.setFileFilter(filter);
			jfc.setSelectedFile(new File(""));
			int response = jfc.showOpenDialog(Window.this);
			if(response != JFileChooser.APPROVE_OPTION) {
				jfc.resetChoosableFileFilters();
				return;
			}
			try{
				File myFile = jfc.getSelectedFile();
				FileReader inputFile = new FileReader(myFile);
				BufferedReader in = new BufferedReader(inputFile);
				placesByPosition.values().forEach(place -> highlightedPlaces.add(place));
				removePlaces();
				
				String line;
				while((line = in.readLine()) != null) {
					String[] tokens = line.split(",");
					String type = tokens[0];
					String category = tokens[1];
					Position position = new Position(Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]));
					String name = tokens[4];
					String desc = null;
				
					if(type.equals("Named")) {
						NamedPlace nplace = new NamedPlace(name, position, category);
						addPlace(nplace);
					} else {
						desc = tokens[5];
						DescribedPlace dplace = new DescribedPlace(name, position, category, desc);
						addPlace(dplace);
					}
				}
                in.close();
				inputFile.close();
			
			} catch(FileNotFoundException e) {
				JOptionPane.showMessageDialog(Window.this, "Cannot open file!");
			} catch(IOException e) {
				JOptionPane.showMessageDialog(Window.this, "Error " + e.getMessage());
			}
			jfc.resetChoosableFileFilters();
		}
		
		public void actionPerformed(ActionEvent act) {
			if(mapImg != null) {
				if(placesByPosition.isEmpty()) {
					loadPlaces();
				} else {
					int i = JOptionPane.showConfirmDialog(null, "Do you want to save current places?", "Exit", 
							JOptionPane.YES_NO_CANCEL_OPTION);
					if(i == JOptionPane.YES_OPTION) {
						savePlaces();
					} else if(i == JOptionPane.NO_OPTION) {
						loadPlaces();
					} else {
						return;
					}
				}
			} else {
				JOptionPane.showMessageDialog(null, "Please insert a map before loading places.", "Error", 
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	class SaveListener implements ActionListener {
		public void actionPerformed(ActionEvent act) {
			savePlaces();
		}
	}

	class ExitListener implements ActionListener {
		public void actionPerformed(ActionEvent act) {
			exit();
		}
	}

	class ClickListener extends MouseAdapter{
		private Place existingPlace;
		
		public void namedPlaceInfo(Place place) {
			NamedPlaceInfo ninfo = new NamedPlaceInfo();
			ninfo.setNameField(place.getName() + " " + place.getPosition().toString());
			
			JOptionPane.showMessageDialog(null, ninfo, "Named place info:", 
					JOptionPane.INFORMATION_MESSAGE);
		}
		
		public void descPlaceInfo(DescribedPlace place) {
			DescPlaceInfo dinfo = new DescPlaceInfo();
			
			dinfo.setNameField(place.getName() + " " + place.getPosition().toString());
			dinfo.setDescField(place.getDescription());
			
			JOptionPane.showMessageDialog(null, dinfo, "Described place info:", 
					JOptionPane.INFORMATION_MESSAGE);
		}
	
		public void leftButtonClick() {
			if(existingPlace.getPin()) {
				if(existingPlace.getHighlight()) {
					existingPlace.setHighlight(false);
					mapImg.add(existingPlace);
					highlightedPlaces.remove(existingPlace);
				} else {
					existingPlace.setHighlight(true);
					mapImg.add(existingPlace);
					highlightedPlaces.add(existingPlace);
				}
				repaint();
			}
		}
	
		public void rightButtonClick() {
			if(existingPlace instanceof NamedPlace) {
				namedPlaceInfo(existingPlace);
			} else {
				DescribedPlace d = (DescribedPlace)existingPlace;					
				descPlaceInfo(d);	
			}
		}
		
		@Override
		public void mouseClicked(MouseEvent mev) {
			existingPlace = (Place)mev.getSource();
			
			if(enableClick) {
				if(existingPlace != null) {
					if(mev.getButton() == MouseEvent.BUTTON1){
						leftButtonClick();
					} else {
						rightButtonClick();
					}
				}
			}
		}
	}
	
	class NewPlaceListener implements ActionListener {
		public void actionPerformed(ActionEvent act) {	
			if(mapImg != null) {
				enableNew = true;
				enableClick = false;
				mapImg.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				mapImg.addMouseListener(new PlaceListener());
			}
		}
	}
	
	class PlaceListener extends MouseAdapter{
		private String name;
		private int x;
		private int y;
		private Position position;
		private String category;
		
		public boolean uniquePosition(Position key) {
			if(placesByPosition.containsKey(key)) {
				return false;
			}
			return true;
		}
		
		public boolean validateInput(String s) {
			if(s == null || s.isEmpty()) {
				JOptionPane.showMessageDialog(null, "Field can not be empty!", "ERROR", 
						JOptionPane.ERROR_MESSAGE);
				return false;
			} else {
				return true;
			}
		}
		
		public void addNamedPlace(Position position) {
			NamedPlaceForm nform = new NamedPlaceForm();
			
			int i = JOptionPane.showConfirmDialog(null, nform, "New named place", 
					JOptionPane.OK_CANCEL_OPTION);
			if(i != JOptionPane.OK_OPTION) {
				return;
			} 
			name = nform.getNameInput();
			if(name == null || name.isEmpty()) {
				JOptionPane.showMessageDialog(null, "Name can not be empty!", "Error", 
						JOptionPane.ERROR_MESSAGE);
			} else {
				name = nform.getNameInput();
				category = categoryList.getSelectedValue();
				NamedPlace nplace = new NamedPlace(name, position, category);
				addPlace(nplace);
			}
		}
		
		public void addDescribedPlace(Position position) {
			DescPlaceForm dform = new DescPlaceForm();
			
			int i = JOptionPane.showConfirmDialog(null, dform, "New described place", 
					JOptionPane.OK_CANCEL_OPTION);
			if(i != JOptionPane.OK_OPTION) {
				return;
			}
			name = dform.getNameInput();
			if(name == null || name.isEmpty()) {
				JOptionPane.showMessageDialog(null, "Name can not be empty!", "Error", 
						JOptionPane.ERROR_MESSAGE);
			} else {
				name = dform.getNameInput();
				category = categoryList.getSelectedValue();
				String desc = dform.getDescInput();
				DescribedPlace dplace = new DescribedPlace(name, position, category, desc);
				addPlace(dplace);
			}
		}
		
		public void occupiedPosition() { 
			String error = "Coordinates " + position.toString() + " are already occupied. \n"
					+ "Only one place per position allowed.";
			
			JOptionPane.showMessageDialog(null, error, "Error", 
					JOptionPane.ERROR_MESSAGE);
		}
		
		@Override
		public void mouseClicked(MouseEvent mev) {
			x = mev.getX();
			y = mev.getY();
			position = new Position(x, y);

			if(enableNew) {
				if(uniquePosition(position)) {
					if(named.isSelected()) {
						addNamedPlace(position);
					} else if(described.isSelected()) {
						addDescribedPlace(position);
					}
				} else {
					occupiedPosition();
				}
			}
			mapImg.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			enableNew = false;
			enableClick = true;
		}
	}
	
	class SearchFieldListener extends MouseAdapter{
		@Override
		public void mouseClicked(MouseEvent mev) {
			searchField.setText("");
		}
	}

	class SearchButtonListener implements ActionListener {
		private String searchInput;
		
		public void findPlace() {
			searchInput = searchField.getText();
			if(placesByName.containsKey(searchInput)) {
				clearMap();
				markSearchResults();
			} else {
				placeNotFound();
			}
		}
		
		public void clearMap() {
			highlightedPlaces.forEach(place -> place.setHighlight(false));
			repaint();
			highlightedPlaces.clear();
		}
		
		public void markSearchResults() {
			for(Place markPlace : placesByName.get(searchInput)) {
				markPlace.setPin(true);
				markPlace.setHighlight(true);
				highlightedPlaces.add(markPlace);
			}
			repaint();
		}
		
		public void placeNotFound() {
			String error = "No place called '" + searchInput + "' found.";
			JOptionPane.showMessageDialog(null, error, "Error", 
					JOptionPane.ERROR_MESSAGE);
		}
		
		public void actionPerformed(ActionEvent act) {
			searchInput = searchField.getText();
			if(!searchInput.equals("") && !searchInput.equals("Search")) {
				findPlace();
			}
			searchField.setText("Search");
		}
	}
	
	class HideListener implements ActionListener {
		public void actionPerformed(ActionEvent act) {
			highlightedPlaces.forEach((place) ->
				place.setPin(false));
			highlightedPlaces.forEach((place) ->
				place.setHighlight(false));
			repaint();
			highlightedPlaces.clear();
		}
	}

	class RemoveListener implements ActionListener {
		public void actionPerformed(ActionEvent act) {
			removePlaces();
		}
	}	

	class CoordinatesListener implements ActionListener {
		private PositionForm pform = new PositionForm();
		private Place place;
		private int i;
		
		public void findPlace() {
			i = JOptionPane.showConfirmDialog(null, pform, "Input coordinates", 
					JOptionPane.OK_CANCEL_OPTION);
			place = placesByPosition.get(new Position(pform.getXField(), pform.getYField()));
		}
		
		public void highlightPlace() {
			highlightedPlaces.forEach(place -> place.setHighlight(false));
			highlightedPlaces.clear();
			
			place.setPin(true);
			place.setHighlight(true);
			highlightedPlaces.add(place);
			repaint();
		}
		
		
		public void placeNotFound() {
			String error = "No place found on coordinates: {" + pform.getXField() + ", " + pform.getYField() + "}";
			JOptionPane.showMessageDialog(null, error, "Error", 
				JOptionPane.ERROR_MESSAGE);
		}
		
		public void invalidInput() {
			JOptionPane.showMessageDialog(null, "Invalid input! Numbers required.", "Error", 
					JOptionPane.ERROR_MESSAGE);
		}
		
		public void actionPerformed(ActionEvent act) {
			if(mapImg != null) {
				try {
					findPlace();
					if(place != null) {
						highlightPlace();
					} else {
						placeNotFound();
					}
				} catch(NumberFormatException e) {
					if(i != JOptionPane.OK_OPTION) {
						return;
					}
					invalidInput();
				}
				pform.setXField();
				pform.setYField();
			}
		}
	}

	class HideCatListener implements ActionListener {
		public void actionPerformed(ActionEvent act) {
			String hideCat = categoryList.getSelectedValue();
			if(placesByCategory.get(hideCat) != null) {
				for(Place place : placesByCategory.get(hideCat)) {
					place.setPin(false);
					place.setHighlight(false);
				}
			}
			repaint();
		}
	}
	
	class ShowCatListener implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			String showCat = categoryList.getSelectedValue();
			if(placesByCategory.get(showCat) != null) {
				for(Place place : placesByCategory.get(showCat)) {
					place.setPin(true);
				}
			}
			repaint();
		}
	}
}
