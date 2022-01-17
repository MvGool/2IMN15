package servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.leshan.core.request.WriteRequest;
import org.eclipse.leshan.core.response.WriteResponse;
import org.eclipse.leshan.server.californium.LeshanServer;
import org.eclipse.leshan.server.registration.Registration;

import components.ParkingLot;

public class LicensePlateServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private LeshanServer server;
	private ParkingLot lot;

	public LicensePlateServlet(LeshanServer server, ParkingLot lot) {
		this.server = server;
		this.lot = lot;
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		BufferedReader reader = req.getReader();
	    String plate = reader.readLine();
	    String spot = reader.readLine();
        System.err.println("Plate: "+plate);
        System.err.println("Spot: "+spot);
        
        if (spot == null) {
        	lot.reserve(plate);
        } else {
	        // Update parking lot
	        lot.reserve(plate, spot);
			int x = Integer.parseInt(spot.substring(0,spot.indexOf(",")));
			int y = Integer.parseInt(spot.substring(spot.indexOf(",")+1));
	        // Update client
	    	try {
	    		Registration registration = lot.getSpot(x, y).getRegistration();
	            WriteResponse response1 = server.send(registration, new WriteRequest(32800,0,32701,"Reserved"));
	            WriteResponse response2 = server.send(registration, new WriteRequest(32800,0,32702,plate));
	            if (response1.isSuccess()) {
	                System.out.println("Updated parking spot state");
	            }else {
	                System.out.println("Failed to read:" + response1.getCode() + " " + response1.getErrorMessage());
	                System.out.println("Failed to read:" + response2.getCode() + " " + response2.getErrorMessage());
	            }
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
        }
        
        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        out.print("Success");
        out.flush();   
    }
}

