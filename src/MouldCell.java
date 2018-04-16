
public class MouldCell {
	
	//either N S E or W. Represents direction the cell is "facing"
	private char cardinality;

	private int xPos;
	private int yPos;
	
	private int sensorAngle;
	private int sensorDistance;
	
	private int forwardSensorX, forwardSensorY;
	private int sideSensorX, sideSensorY;
	
	public MouldCell(int x, int y, char card, int insensorAngle, int insensorDistance){
		this.xPos = x;
		this.yPos = y;
		this.cardinality = card;
		
		this.sensorAngle = insensorAngle;
		this.sensorDistance = insensorDistance;
	}
	
	public int getForwardSensorX(){
		return forwardSensorX;
	}
	
	public int getForwardSensorY(){
		return forwardSensorY;
	}
	
	public int getSideSensorX(){
		return sideSensorX;
	}
	
	public int getSideSensorY(){
		return sideSensorY;
	}
	
	public void calculateSensorDistances(){
		Integer SD = new Integer(sensorDistance);
		//different angles from forward sensors depending on card
		//E/W, N/S, NE/SE/SW/NW
		double ewAngle = sensorAngle;
		double nsAngle = 900-sensorAngle;
		double midCardinalityAngle = 450-sensorAngle;
		
		if (cardinality == '1' || cardinality == '2' || cardinality == '3' || cardinality == '4'){
			Long objDistance = (Math.round(Math.sqrt((SD.floatValue()*SD.floatValue())/2)));
			forwardSensorX = forwardSensorY = objDistance.intValue();
			
			Long longsideSensorX = Math.round(Math.cos(Math.toRadians(midCardinalityAngle/10))*sensorDistance);
			sideSensorX = longsideSensorX.intValue();
			Long longsideSensorY = Math.round(Math.sin(Math.toRadians(midCardinalityAngle/10))*sensorDistance);
			sideSensorY = longsideSensorY.intValue();
		}
		
		if (cardinality == 'N' || cardinality == 'S'){
			forwardSensorY = sensorDistance;
			forwardSensorX = 0;
			
			Long longsideSensorX = Math.round(Math.cos(Math.toRadians(nsAngle/10))*sensorDistance);
			sideSensorX = longsideSensorX.intValue();
			
			Long longsideSensorY = Math.round(Math.sin(Math.toRadians(nsAngle/10))*sensorDistance);
			sideSensorY = longsideSensorY.intValue();
		}
		
		if (cardinality == 'E' || cardinality == 'W'){
			forwardSensorX = sensorDistance;
			forwardSensorY = 0;	
			
			Long longsideSensorX = Math.round(Math.sin(Math.toRadians(ewAngle/10))*sensorDistance);
			sideSensorX = longsideSensorX.intValue();
			
			Long longsideSensorY = Math.round(Math.cos(Math.toRadians(ewAngle/10))*sensorDistance);
			sideSensorY = longsideSensorY.intValue();
		}
	}
	
	public int getX(){
		return this.xPos;
	}
	
	public int getY(){
		return this.yPos;
	}
	
	public char getCardinality() {
		return cardinality;
	}

	public void setCardinality(char cardinality) {
		this.cardinality = cardinality;
	}

	public void setxPos(int x) {
		this.xPos = x;
	}
	public void setyPos(int y) {
		this.yPos = y;
	}

	@Override
	public String toString(){
		return "X: " + xPos + "Y: " + yPos + "Facing: " + cardinality;
	}

}
