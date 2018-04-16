import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MouldDisplay extends JPanel implements ActionListener {

	private BufferedImage canvas;
	private ArrayList<MouldCell> cellArray;
	private ArrayList<FoodCell> foodArray;
	// for image output naming
	public int imgNameCount = 0;
	private BufferedImage chemoAttractantMap;
	private BufferedImage foodChemoAttractantMap;

	public MouldDisplay(int width, int height) {
		canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		fillCanvas(Color.GRAY, canvas);
		cellArray = new ArrayList<MouldCell>();
		foodArray = new ArrayList<FoodCell>();
		chemoAttractantMap = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		foodChemoAttractantMap = new BufferedImage(chemoAttractantMap.getWidth(), chemoAttractantMap.getHeight(), BufferedImage.TYPE_INT_ARGB);
		fillCanvas(Color.BLACK, chemoAttractantMap);

		JButton iterateBtn = new JButton("GO");
		iterateBtn.addActionListener(this);

		this.add(iterateBtn);
	}

	public Dimension getPreferredSize() {
		return new Dimension(canvas.getWidth(), canvas.getHeight());
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(canvas, null, null);
	}

	public void fillCanvas(Color c, BufferedImage canvas) {
		int color = c.getRGB();
		for (int x = 0; x < canvas.getWidth(); x++) {
			for (int y = 0; y < canvas.getHeight(); y++) {
				canvas.setRGB(x, y, color);
			}
		}
		repaint();
	}

	public void drawRect(Color c, int x1, int y1, int width, int height, BufferedImage canvas) {
		int color = c.getRGB();
		for (int x = x1; x < x1 + width; x++) {
			for (int y = y1; y < y1 + height; y++) {
				canvas.setRGB(x, y, color);
			}
		}
		repaint();
	}

	public void drawPixNoRepaint(Color c, int x, int y) {
		int color = c.getRGB();
		canvas.setRGB(x, y, color);
	}

	public void drawCenteredCircle(int x, int y, int r, Color col) {
		Graphics g = canvas.getGraphics();
		g.setColor(col);
		x = x-(r/2);
		y = y-(r/2);
		g.fillOval(x, y, r, r);
	}

	public void populateRandomCells(int left, int bottom, int top, int right) {
		for (int x = left; x < right; x++) {
			for (int y = top; y < bottom; y++) {
				// RANDOM PART
				Random r = new Random();
				int doesSpawn = r.nextInt(10);
				if (doesSpawn == 0) {
					String dirs = "NSEW1234";
					char ranDir = dirs.charAt(r.nextInt(dirs.length()));
					cellArray.add(new MouldCell(x, y, ranDir, 450, 8));
				}
			}
		}
		// cellArray.add(new MouldCell(100,100, 'W'));
		// cellArray.add(new MouldCell(9,10, 'W'));
		// cellArray.add(new MouldCell(10,10, 'W'));
		// cellArray.add(new MouldCell(10,10, 'W'));
		// Color col = new Color(100,100,100);
		// chemoAttractantMap.setRGB(10, 9, col.getRGB());

	}

	public void moveAllCells() {

		// shuffling was attempted to prevent westward bias
		// did not work
		// Collections.shuffle(cellArray);
		for (MouldCell cell : cellArray) {
			//cellProduceChemoAttractant(cell.getX(), cell.getY());
			// CHANGE OLD CELL POSITION TO GREY ON THE OUTPUT
			canvas.setRGB(cell.getX(), cell.getY(), Color.GRAY.getRGB());

			char dir = cell.getCardinality();
			if (dir == 'W') {
				int newX = cell.getX() - 1;
				if (!cellExistsAtPosition(newX, cell.getY())) {
					// make cell produce chemoattractant in its current location
					// before moving
					cellProduceChemoAttractant(cell.getX(), cell.getY());
					cell.setxPos(newX);
					// canvas.setRGB(cell.getX(), cell.getY(),
					// Color.YELLOW.getRGB());
				} else {
					// System.out.println("bump");
				}
			} else if (dir == '4') {
				int newX = cell.getX() - 1;
				int newY = cell.getY() - 1;
				if (!cellExistsAtPosition(newX, newY)) {
					// make cell produce chemoattractant in its current location
					// before moving
					cellProduceChemoAttractant(cell.getX(), cell.getY());
					cell.setxPos(newX);
					cell.setyPos(newY);
					// canvas.setRGB(cell.getX(), cell.getY(),
					// Color.YELLOW.getRGB());
				} else {
					// System.out.println("bump");
				}
			} else if (dir == 'N') {
				int newY = cell.getY() - 1;
				if (!cellExistsAtPosition(cell.getX(), newY)) {
					// make cell produce chemoattractant in its current location
					// before moving
					cellProduceChemoAttractant(cell.getX(), cell.getY());
					cell.setyPos(newY);
					// canvas.setRGB(cell.getX(), cell.getY(),
					// Color.BLUE.getRGB());
				} else {
					// System.out.println("bump");
				}
			} else if (dir == '1') {
				int newX = cell.getX() + 1;
				int newY = cell.getY() - 1;
				if (!cellExistsAtPosition(newX, newY)) {
					// make cell produce chemoattractant in its current location
					// before moving
					cellProduceChemoAttractant(cell.getX(), cell.getY());
					cell.setxPos(newX);
					cell.setyPos(newY);
					// canvas.setRGB(cell.getX(), cell.getY(),
					// Color.YELLOW.getRGB());
				} else {
					// System.out.println("bump");
				}
			} else if (dir == 'E') {
				int newX = cell.getX() + 1;
				if (!cellExistsAtPosition(newX, cell.getY())) {
					// make cell produce chemoattractant in its current location
					// before moving
					cellProduceChemoAttractant(cell.getX(), cell.getY());
					cell.setxPos(newX);
					// canvas.setRGB(cell.getX(), cell.getY(),
					// Color.GREEN.getRGB());
				} else {

				}
			} else if (dir == '2') {
				int newX = cell.getX() + 1;
				int newY = cell.getY() + 1;
				if (!cellExistsAtPosition(newX, newY)) {
					// make cell produce chemoattractant in its current location
					// before moving
					cellProduceChemoAttractant(cell.getX(), cell.getY());
					cell.setxPos(newX);
					cell.setyPos(newY);
					// canvas.setRGB(cell.getX(), cell.getY(),
					// Color.YELLOW.getRGB());
				} else {
					// System.out.println("bump");
				}
			} else if (dir == 'S') {
				int newY = cell.getY() + 1;
				if (!cellExistsAtPosition(cell.getX(), newY)) {
					// make cell produce chemoattractant in its current location
					// before moving
					cellProduceChemoAttractant(cell.getX(), cell.getY());
					cell.setyPos(newY);
					// canvas.setRGB(cell.getX(), cell.getY(),
					// Color.RED.getRGB());
				} else {
					// System.out.println("bump");
				}
			} else if (dir == '3') {
				int newX = cell.getX() - 1;
				int newY = cell.getY() + 1;
				if (!cellExistsAtPosition(newX, newY)) {
					// make cell produce chemoattractant in its current location
					// before moving
					cellProduceChemoAttractant(cell.getX(), cell.getY());
					cell.setxPos(newX);
					cell.setyPos(newY);
					// canvas.setRGB(cell.getX(), cell.getY(),
					// Color.YELLOW.getRGB());
				} else {
					// System.out.println("bump");
				}
			} else {
				System.out.println("MOVE GONE WRONG SHOULDN'T SEE DIR IS: " + dir);
			}
		}
	}

	public Boolean cellExistsAtPosition(int x, int y) {
		if (x > 199 || y > 199 || y < 0 || x < 0) {
			return true;
		}
		for (MouldCell cell : cellArray) {
			if (cell.getX() == x && cell.getY() == y) {
				return true;

			}
		}
		return false;
	}

	public void printCellsToCanvas() {
		for (MouldCell cell : this.cellArray) {
			if (cell.getCardinality() == 'N') {
				drawPixNoRepaint(Color.BLUE, cell.getX(), cell.getY());
			} else if (cell.getCardinality() == 'S') {
				drawPixNoRepaint(Color.RED, cell.getX(), cell.getY());
			} else if (cell.getCardinality() == 'E') {
				drawPixNoRepaint(Color.GREEN, cell.getX(), cell.getY());
			} else if (cell.getCardinality() == 'W') {
				drawPixNoRepaint(Color.YELLOW, cell.getX(), cell.getY());
			} else if (cell.getCardinality() == '1') {
				drawPixNoRepaint(Color.ORANGE, cell.getX(), cell.getY());
			} else if (cell.getCardinality() == '2') {
				drawPixNoRepaint(Color.MAGENTA, cell.getX(), cell.getY());
			} else if (cell.getCardinality() == '3') {
				drawPixNoRepaint(Color.CYAN, cell.getX(), cell.getY());
			} else if (cell.getCardinality() == '4') {
				drawPixNoRepaint(Color.PINK, cell.getX(), cell.getY());
			}

		}

		for (FoodCell cell : this.foodArray) {
			drawPixNoRepaint(Color.BLACK, cell.getX(), cell.getY());
			drawCenteredCircle(cell.getX(), cell.getY(), 3, Color.WHITE);
		}
		repaint();
	}

	// used after a cell moves, they leave a diminishing 'trail' of
	// chemoattractants
	public void cellProduceChemoAttractant(int x, int y) {
		Color chemoGray = new Color(50, 50, 50, 50);
		chemoAttractantMap.setRGB(x, y, chemoGray.getRGB());
	}

	// call every tick to reduce the strength of old elements of chemoattractant
	// trails
	public void cellAttractantEvaporation() {

		BufferedImage newChemoMap = new BufferedImage(chemoAttractantMap.getWidth(), chemoAttractantMap.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		
		float[] gaussian = new float[] {
				0.0625f, 0.125f, 0.0625f,
				0.125f, 0.25f, 0.125f,
				0.0625f, 0.125f, 0.0625f
		};
		Kernel gaussianKernel = new Kernel(3,3,gaussian);
		ConvolveOp op = new ConvolveOp(gaussianKernel);
		newChemoMap = op.filter(chemoAttractantMap, null);

		chemoAttractantMap = newChemoMap;

		/*
		 * Color foodGrey = new Color(200,200,200); for (int x = 100; x < 150;
		 * x++) { for (int y = 100; y < 150; y++) { chemoAttractantMap.setRGB(x,
		 * y, foodGrey.getRGB()); } }
		 */
	}

	public void cellsFaceAttractants() {
		// Collections.shuffle(cellArray);
		for (MouldCell cell : cellArray) {
			cell.setCardinality(findHighestNearbyChemo(cell));
		}
	}

	public char findHighestNearbyChemo(MouldCell cell) {
		int SENSOR_SIZE = 1;
		//int OFFSET = 3;
		int forwardOffsetX = cell.getForwardSensorX();
		int forwardOffsetY = cell.getForwardSensorY();
		
		int sideOffsetX = cell.getSideSensorX();
		int sideOffsetY = cell.getSideSensorY();
		
		int leftTotal;
		int rightTotal;
		int centreTotal;

		if (cell.getCardinality() == 'N') {
			leftTotal = sensorSquareValue(cell.getX() - sideOffsetX, cell.getY() - sideOffsetY, SENSOR_SIZE);
			rightTotal = sensorSquareValue(cell.getX() + sideOffsetX, cell.getY() -  sideOffsetY, SENSOR_SIZE);
			centreTotal = sensorSquareValue(cell.getX(), cell.getY() - forwardOffsetY, SENSOR_SIZE);

			if ((centreTotal > leftTotal) && (centreTotal > rightTotal)) {
				return 'N';
			} else if (leftTotal > rightTotal) {
				return '4';
			} else if (rightTotal > leftTotal) {
				return '1';
			} else
				return returnRandomCardinality();
		}

		if (cell.getCardinality() == '1') {
			leftTotal = sensorSquareValue(cell.getX() + sideOffsetY, cell.getY() - sideOffsetX, SENSOR_SIZE);
			rightTotal = sensorSquareValue(cell.getX() + sideOffsetX, cell.getY() - sideOffsetY, SENSOR_SIZE);
			centreTotal = sensorSquareValue(cell.getX() + forwardOffsetX, cell.getY() - forwardOffsetY, SENSOR_SIZE);

			if ((centreTotal > leftTotal) && (centreTotal > rightTotal)) {
				return '1';
			} else if (leftTotal > rightTotal) {
				return 'N';
			} else if (rightTotal > leftTotal) {
				return 'E';
			} else
				return returnRandomCardinality();
		}

		else if (cell.getCardinality() == 'E') {
			leftTotal = sensorSquareValue(cell.getX() + sideOffsetX, cell.getY() - sideOffsetY, SENSOR_SIZE);
			rightTotal = sensorSquareValue(cell.getX() + sideOffsetX, cell.getY() + sideOffsetY, SENSOR_SIZE);
			centreTotal = sensorSquareValue(cell.getX() + forwardOffsetX, cell.getY(), SENSOR_SIZE);

			if ((centreTotal > leftTotal) && (centreTotal > rightTotal)) {
				return 'E';
			} else if (leftTotal > rightTotal) {
				return '1';
			} else if (rightTotal > leftTotal) {
				return '2';
			} else
				return returnRandomCardinality();
		}

		if (cell.getCardinality() == '2') {
			leftTotal = sensorSquareValue(cell.getX() + sideOffsetX, cell.getY() + sideOffsetY, SENSOR_SIZE);
			rightTotal = sensorSquareValue(cell.getX() + sideOffsetY, cell.getY() + sideOffsetX, SENSOR_SIZE);
			centreTotal = sensorSquareValue(cell.getX() + forwardOffsetX, cell.getY() + forwardOffsetY, SENSOR_SIZE);

			if ((centreTotal > leftTotal) && (centreTotal > rightTotal)) {
				return '2';
			} else if (leftTotal > rightTotal) {
				return 'E';
			} else if (rightTotal > leftTotal) {
				return 'S';
			} else
				return returnRandomCardinality();
		}

		else if (cell.getCardinality() == 'S') {
			leftTotal = sensorSquareValue(cell.getX() + sideOffsetX, cell.getY() + sideOffsetY, SENSOR_SIZE);
			rightTotal = sensorSquareValue(cell.getX() - sideOffsetX, cell.getY() + sideOffsetY, SENSOR_SIZE);
			centreTotal = sensorSquareValue(cell.getX(), cell.getY() + forwardOffsetY, SENSOR_SIZE);

			if ((centreTotal > leftTotal) && (centreTotal > rightTotal)) {
				return 'S';
			} else if (leftTotal > rightTotal) {
				return '2';
			} else if (rightTotal > leftTotal) {
				return '3';
			} else
				return returnRandomCardinality();
		}

		if (cell.getCardinality() == '3') {
			leftTotal = sensorSquareValue(cell.getX() - sideOffsetY, cell.getY() + sideOffsetX, SENSOR_SIZE);
			rightTotal = sensorSquareValue(cell.getX() - sideOffsetX, cell.getY() + sideOffsetY, SENSOR_SIZE);
			centreTotal = sensorSquareValue(cell.getX() - forwardOffsetX, cell.getY() + forwardOffsetY, SENSOR_SIZE);

			if ((centreTotal > leftTotal) && (centreTotal > rightTotal)) {
				return '3';
			} else if (leftTotal > rightTotal) {
				return 'S';
			} else if (rightTotal > leftTotal) {
				return 'W';
			} else
				return returnRandomCardinality();
		}

		else if (cell.getCardinality() == 'W') {
			leftTotal = sensorSquareValue(cell.getX() - sideOffsetX, cell.getY() + sideOffsetY, SENSOR_SIZE);
			rightTotal = sensorSquareValue(cell.getX() - sideOffsetX, cell.getY() - sideOffsetY, SENSOR_SIZE);
			centreTotal = sensorSquareValue(cell.getX() - forwardOffsetX, cell.getY(), SENSOR_SIZE);

			if ((centreTotal > leftTotal) && (centreTotal > rightTotal)) {
				return 'W';
			} else if (leftTotal > rightTotal) {
				return '3';
			} else if (rightTotal > leftTotal) {
				return '4';
			} else
				return returnRandomCardinality();
		}

		if (cell.getCardinality() == '4') {
			leftTotal = sensorSquareValue(cell.getX() - sideOffsetX, cell.getY() - sideOffsetY, SENSOR_SIZE);
			rightTotal = sensorSquareValue(cell.getX() - sideOffsetY, cell.getY() - sideOffsetX, SENSOR_SIZE);
			centreTotal = sensorSquareValue(cell.getX() - forwardOffsetX, cell.getY() - forwardOffsetY, SENSOR_SIZE);

			if ((centreTotal > leftTotal) && (centreTotal > rightTotal)) {
				return '4';
			} else if (leftTotal > rightTotal) {
				return 'W';
			} else if (rightTotal > leftTotal) {
				return 'N';
			} else
				return returnRandomCardinality();
		}

		else {
			System.out.println("SOMETHING WENT VERY WRONG");
			return 'B';
		}
	}
	//maybe switch this to bottom/right sometimes depending on cardinality
	public int sensorSquareValue(int topleftX, int topleftY, int size) {
		try {
			int runningTotal = 0;
			for (int x = topleftX; x < topleftX + size; x++) {
				for (int y = topleftY; y < topleftY + size; y++) {
					Color tCol = new Color(chemoAttractantMap.getRGB(x, y));
					Color foodCol = new Color(foodChemoAttractantMap.getRGB(x, y));
					runningTotal += tCol.getRed();
					runningTotal += foodCol.getRed();
				}
			}
			return runningTotal;
		} catch (ArrayIndexOutOfBoundsException edge) {
			//System.out.println("cell at edge");
		}
		return 0;
	}
	
	public char returnRandomCardinality(){
		Random r = new Random();
		String cardinalities = "NSEW1234";
		char ranDir = cardinalities.charAt(r.nextInt(cardinalities.length()));
		return ranDir;
		
	}

	public void populateFood() {		
		foodArray.add(new FoodCell(75, 75));

		foodArray.add(new FoodCell(125, 75));
		
		foodArray.add(new FoodCell(100, 125));

	}

	public void foodProduceChemo() {
		
		//BufferedImage chemMap = this.chemoAttractantMap;
		Graphics2D g = foodChemoAttractantMap.createGraphics();
		
		Color foodGrey;
		
//		foodGrey = new Color(200,200,200,20);
//		g.setColor(foodGrey);
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, foodChemoAttractantMap.getWidth() - 1, foodChemoAttractantMap.getHeight() - 1);
		//g.setColor(foodGrey);
		g.setColor(Color.WHITE);
		/*
		for (FoodCell cell : foodArray) {
			g.fillOval(cell.getX()-2, cell.getY()-2, 5, 5);

		}
		
		BufferedImage newFoodMap = new BufferedImage(foodChemoAttractantMap.getWidth(), foodChemoAttractantMap.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		//float oneNinth = 0.1111111111111111111f;
		float[] diffuse = new float[] {
				 0.1f, 0.1f, 0.1f,
				 0.1f, 0.2f, 0.1f,
				 0.1f, 0.1f, 0.1f
			};
		
		Kernel diffusalKernel = new Kernel(3,3,diffuse);
		ConvolveOp op = new ConvolveOp(diffusalKernel);
		for(int i=0; i<10; i++){
			newFoodMap = op.filter(foodChemoAttractantMap, null);
			foodChemoAttractantMap = newFoodMap;
		}
		*/
		for (FoodCell cell: foodArray){
			g.fillRect(cell.getX(), cell.getY(), 1, 1);
		}
		//g.fillOval(0, 0, foodChemoAttractantMap.getWidth() - 1, foodChemoAttractantMap.getHeight() - 1);
		
		

	}

	public void actionPerformed(ActionEvent e) {
		
		for (int i =0; i<=100; i++){
			
			for (MouldCell cell:cellArray){
				 cell.calculateSensorDistances();
			}
			
		/*	for (MouldCell cell: cellArray){
				if(cell.getCardinality() == '1'){
					System.out.println("side x: "  + cell.getSideSensorX() + " side y: " + cell.getSideSensorY() +" forward x:" + cell.getForwardSensorX() + " forward y: " + cell.getForwardSensorY());
				}
			}*/
		// output current state as image file
		try {
			// retrieve image
			BufferedImage bi = canvas;
			File outputfile = new File("IMAGES/" + String.valueOf(imgNameCount) + ".png");
			imgNameCount++;
			ImageIO.write(bi, "png", outputfile);
		} catch (IOException g) {
			System.out.println("IOEXception" + g);
		}

		// FOR CHECKING CHEMOATTRACTANT MAP
		try {
			// retrieve image
			BufferedImage chem = chemoAttractantMap;
			File outputfile = new File("IMAGES/CHEMOATTRACTANT" + String.valueOf(imgNameCount - 1) + ".png");
			ImageIO.write(chem, "png", outputfile);
		} catch (IOException g) {
			System.out.println("IOEXception" + g);
		}
		
		try {
			// retrieve image
			BufferedImage chem = foodChemoAttractantMap;
			File outputfile = new File("IMAGES/FOOD" + String.valueOf(imgNameCount - 1) + ".png");
			ImageIO.write(chem, "png", outputfile);
		} catch (IOException g) {
			System.out.println("IOEXception" + g);
		}
		
		// Collections.shuffle(cellArray);
										// this.checkFacingDirections();
					this.cellsFaceAttractants();
					// Collections.shuffle(cellArray);
					this.cellAttractantEvaporation();
					// Collections.shuffle(cellArray);
					this.moveAllCells();
					// this.cellAttractantEvaporation();
					// Collections.shuffle(cellArray);
					this.printCellsToCanvas();
					// System.out.println(cellArray.size());
		}
	}
	
	
	
	// UPDATE FOR 8 COMPASS POINTS IF NEEDED
	/*
	 * public void checkFacingDirections(){ int faceN = 0, faceE = 0, faceS = 0,
	 * faceW = 0; for (MouldCell cell:cellArray){ if (cell.getCardinality() ==
	 * 'N') faceN++; if (cell.getCardinality() == 'E') faceE++; if
	 * (cell.getCardinality() == 'S') faceS++; if (cell.getCardinality() == 'W')
	 * faceW++; }
	 * 
	 * System.out.println("N: " + faceN + " E: " + faceE + " S: " + faceS +
	 * " W: " + faceW); }
	 */

	public static void main(String[] args) {
		int width = 200;
		int height = 200;
		JFrame frame = new JFrame("mould demo");

		MouldDisplay panel = new MouldDisplay(width, height);
		// System.out.println(panel.chemoAttractantMap.getRGB(199, 199));
		panel.populateRandomCells(50, 150, 50, 150);
		
		 for (MouldCell cell:panel.cellArray){
			 cell.calculateSensorDistances();
		}
		
		panel.populateFood();
		panel.foodProduceChemo();
		Collections.shuffle(panel.cellArray);
		panel.printCellsToCanvas();

		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
