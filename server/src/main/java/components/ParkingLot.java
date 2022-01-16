package components;

import java.util.Arrays;

import com.google.gson.Gson;

public class ParkingLot {

	private static ParkingLot parkingLot;
	
	private final int width;
	private final int height;
	private ParkingSpot[] lot;
	
	private int filled = 0;
	
	private Gson gson = new Gson();
	
	private ParkingLot() {
		width = 8;
		height = 8;
		lot = new ParkingSpot[width * height];
		Arrays.setAll(lot, (index) -> new ParkingSpot(index % width, (int) Math.floor(index/width)));
	}
	
	public static ParkingLot getParkingLot() {
		if (parkingLot == null) {
			parkingLot = new ParkingLot();
		}
		return parkingLot;
	}
		
	public void registerSpot(String id) {
		ParkingSpot spot = lot[filled+1];
		spot.setId(id);
	}
	
	public void vehicleArrival(String plate) {
		Boolean placed = false;
		for (ParkingSpot spot : lot) {
			if (spot.getPlate() == plate && spot.getState() == "Reserved") {
				spot.setState("Occupied");
				placed = true;
			} 
		}
		if (!placed) {
			for (ParkingSpot spot : lot) {
				if (spot.getState() == "Free") {
					spot.setState("Occupied");
					spot.setPlate(plate);
				}
			}
		}
	}

	public void vehicleExit(String plate) {
		for (ParkingSpot spot : lot) {
			if (spot.getPlate() == plate && spot.getState() == "Occupied") {
				spot.setState("Free");
				spot.setPlate("");
			}
		}
	}

	public void setSpotState(String spotId, String state) {
		for (ParkingSpot spot : lot) {
			if (spot.getId() == spotId) {
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
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	// TODO
	public String getJson() {
		String lotJson = gson.toJson(lot);
		return "{\"width\": "+width+", \"height\": "+height+", \"parkingLot\": "+lotJson+"}";
	}
}
