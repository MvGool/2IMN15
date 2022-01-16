package components;

public class ParkingSpot {

	private String id = "";
	private String state = "Free";
	private String licensePlate = "";
	private int x;
	private int y;
	
	public ParkingSpot(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public String getPlate() {
		return licensePlate;
	}
	
	public void setPlate(String plate) {
		this.licensePlate = "";
	}
}
