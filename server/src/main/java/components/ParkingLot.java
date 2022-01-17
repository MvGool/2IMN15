package components;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.leshan.server.registration.Registration;

import com.google.gson.Gson;

public class ParkingLot {
	
	private final String name;
	private final int width;
	private final int height;
	private ArrayList<String> ids = new ArrayList<>();
	private ArrayList<String> reserved = new ArrayList<>();
	private ParkingSpot[] lot;
	
	private int filled = 0;
	
	private Gson gson = new Gson();
	
	public ParkingLot(String name) {
		this.name = name;
		width = 8;
		height = 8;
		lot = new ParkingSpot[width * height];
		Arrays.setAll(lot, (index) -> new ParkingSpot(index % width, (int) Math.floor(index/width)));
	}
		
	public void registerSpot(String id, Registration registration) {
		if (!ids.contains(id)) {
			ids.add(id);
			ParkingSpot spot = lot[filled++];
			spot.setId(id);
			spot.setRegistration(registration);
		}
	}
	
	public void reserve(String plate) {
		reserved.add(plate);
	}
	
	public void reserve(String plate, String spotLocation) {
		int x = Integer.parseInt(spotLocation.substring(0,spotLocation.indexOf(",")));
		int y = Integer.parseInt(spotLocation.substring(spotLocation.indexOf(",")+1));
		
		ParkingSpot spot = getSpot(x,y);
		spot.setState("Reserved");
		spot.setPlate(plate);
	}
	
	public ParkingSpot vehicleArrival(String plate) {
		ParkingSpot updatedSpot = null;
		Boolean placed = false;
		for (ParkingSpot spot : lot) {
			if (spot.getPlate().equals(plate) && spot.getState().equals("Reserved")) {
				spot.setState("Occupied");
				placed = true;
				updatedSpot = spot;
				break;
			} 
		}
		if (!placed) {
			for (ParkingSpot spot : lot) {
				if (spot.getState().equals("Free")) {
					spot.setState("Occupied");
					spot.setPlate(plate);
					reserved.remove(plate);
					updatedSpot = spot;
					break;
				}
			}
		}
		
		return updatedSpot;
	}

	public ParkingSpot vehicleExit(String plate) {
		for (ParkingSpot spot : lot) {
			if (spot.getPlate().equals(plate) && spot.getState().equals("Occupied")) {
				spot.setState("Free");
				spot.setPlate("");
				return spot;
			}
		}
		return null;
	}

	public void setSpotState(String spotId, String state) {
		System.err.println(spotId + " " + state);
		for (ParkingSpot spot : lot) {
			if (spot.getId().equals(spotId)) {
				System.err.println(spot.getId());
				spot.setState(state);
			}
		}
	}
	
	public ParkingSpot[] getLot() {
		return lot;
	}
	
	public ParkingSpot getSpot(int x, int y) {
		return lot[y*width + x];
	}
	
	public String getName() {
		return name;
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public String getJson() {
		String lotJson = gson.toJson(lot);
		String reservedJson = gson.toJson(reserved);
		return "{\"width\": "+width+", \"height\": "+height+", \"parkingLot\": "+lotJson+", \"reserved\": "+reservedJson+"}";
	}
}
