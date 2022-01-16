package server;

import java.util.Arrays;

import com.google.gson.Gson;

public class ParkingLot {

	private final int width;
	private final int height;
	private String[] lot;
	
	private Gson gson = new Gson();
	
	public ParkingLot(int w, int h) {
		width = w;
		height = h;
		lot = new String[w * h];
		Arrays.setAll(lot, (index) -> "free");
	}
	
	public void setLot(String[] newLot) {
		lot = newLot;
		System.out.println("Parking lot has been updated!");
	}
	
	public void setSpot(int w, int h, String spot) {
		lot[h*width + w] = spot;
	}
	
	public String[] getLot() {
		return lot;
	}
	
	public String getSpot(int w, int h) {
		return lot[h*width + w];
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public String getJson() {
		String lotJson = gson.toJson(lot);
		return "{\"width\": "+width+", \"height\": "+height+", \"parkingLot\": "+lotJson+"}";
	}
}
