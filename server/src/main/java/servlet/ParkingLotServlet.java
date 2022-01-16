package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.leshan.server.californium.LeshanServer;

import components.ParkingLot;

public class ParkingLotServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public ParkingLotServlet(LeshanServer server) {
		
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ParkingLot parkingLot = ParkingLot.getParkingLot();
        String parkingLotString = parkingLot.getJson();

        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        out.print(parkingLotString);
        out.flush();   
    }
}

