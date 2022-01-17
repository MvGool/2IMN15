package servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import components.ParkingLot;

public class LicensePlateServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public LicensePlateServlet() {
		
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		BufferedReader reader = req.getReader();
	    String plate = reader.readLine();
	    String spot = reader.readLine();
        System.err.println("Plate: "+plate);
        System.err.println("Spot: "+spot);
        
        ParkingLot.getParkingLot().reserve(plate, spot);
        
        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        out.print("Success");
        out.flush();   
    }
}

