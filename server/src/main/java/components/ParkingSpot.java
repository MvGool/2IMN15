package components;

import org.eclipse.leshan.server.registration.Registration;

public class ParkingSpot {

	private String id = "";
	private String state = "Free";
	private String licensePlate = "";
	private Registration registration;
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
		System.err.println("State set");
	}
	
	public String getPlate() {
		return licensePlate;
	}
	
	public void setPlate(String plate) {
		this.licensePlate = plate;
	}
	
	public Registration getRegistration() {
		return registration;
	}
	
	public void setRegistration(Registration registration) {
		this.registration = registration;
	}
}
