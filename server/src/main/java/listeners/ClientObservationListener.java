package listeners;

import org.eclipse.leshan.core.node.LwM2mNode;
import org.eclipse.leshan.core.node.LwM2mPath;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.observation.CompositeObservation;
import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.core.observation.SingleObservation;
import org.eclipse.leshan.core.request.ReadRequest;
import org.eclipse.leshan.core.request.WriteRequest;
import org.eclipse.leshan.core.response.ObserveCompositeResponse;
import org.eclipse.leshan.core.response.ObserveResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;
import org.eclipse.leshan.server.californium.LeshanServer;
import org.eclipse.leshan.server.observation.ObservationListener;
import org.eclipse.leshan.server.registration.Registration;

import components.ParkingLot;
import components.ParkingSpot;

public class ClientObservationListener implements ObservationListener {

	private LeshanServer server;
	private ParkingLot lot;
	
	public ClientObservationListener(LeshanServer server, ParkingLot lot) {
		this.server = server;
		this.lot = lot;
	}
	
	@Override
	public void newObservation(Observation observation, Registration registration) {
		System.out.println("New observation: " + observation.toString());
	}
	
	@Override
	public void cancelled(Observation observation) {
		System.out.println("Cancelled observation: " + observation.toString());
		
	}
	
	@Override
	public void onResponse(SingleObservation observation, Registration registration, ObserveResponse response) {
		System.err.println(observation.getPath().toString());

		if (observation.getPath().equals(new LwM2mPath(32800,0,32701))) {			
			ReadResponse readResponse;
			String id = "";
			try {
				readResponse = server.send(registration, new ReadRequest(32800,0,32700));
				if (readResponse.isSuccess()) {
					id = ((LwM2mResource)readResponse.getContent()).getValue().toString();
					lot.setSpotState(id, ((LwM2mResource)response.getContent()).getValue().toString());
				}else {
					System.out.println("Failed to read:" + response.getCode() + " " + response.getErrorMessage());
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (observation.getPath().equals(new LwM2mPath(32801,0,32704))) {			
			ReadResponse readResponse;
			String direction = "";
			try {
				readResponse = server.send(registration, new ReadRequest(32801,0,32705));
				if (readResponse.isSuccess()) {
					direction = ((LwM2mResource)readResponse.getContent()).getValue().toString();
					System.err.println(direction);
					if (direction.toString().equals("0")) {
						ParkingSpot spot = lot.vehicleExit(((LwM2mResource)response.getContent()).getValue().toString());
						if (spot != null) {
				    		Registration spotRegistration = spot.getRegistration();
				    		System.err.println("reg: "+spotRegistration);
				            WriteResponse response1 = server.send(spotRegistration, new WriteRequest(32800,0,32701,"Free"));
				            WriteResponse response2 = server.send(spotRegistration, new WriteRequest(32800,0,32702,""));
				            if (response1.isSuccess()) {
				                System.out.println("Updated parking spot state");
				            }else {
				                System.out.println("Failed to read:" + response1.getCode() + " " + response1.getErrorMessage());
				                System.out.println("Failed to read:" + response2.getCode() + " " + response2.getErrorMessage());
				            }
						}
					} else {
						String plate = ((LwM2mResource)response.getContent()).getValue().toString();
						ParkingSpot spot = lot.vehicleArrival(plate);
						if (spot != null) {
				    		Registration spotRegistration = spot.getRegistration();
				            WriteResponse response1 = server.send(spotRegistration, new WriteRequest(32800,0,32701,"Occupied"));
				            WriteResponse response2 = server.send(spotRegistration, new WriteRequest(32800,0,32702,plate));
				            if (response1.isSuccess()) {
				                System.out.println("Updated parking spot state");
				            }else {
				                System.out.println("Failed to read:" + response1.getCode() + " " + response1.getErrorMessage());
				                System.out.println("Failed to read:" + response2.getCode() + " " + response2.getErrorMessage());
				            }
						}

					}
				} else {
					System.out.println("Failed to read:" + response.getCode() + " " + response.getErrorMessage());
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onError(Observation observation, Registration registration, Exception error) {
		System.out.println("Error from observation: " + observation.toString());
		
	}
	
	@Override
	public void onResponse(CompositeObservation observation, Registration registration,
			ObserveCompositeResponse response) {
		System.out.println("Response from observation: " + observation.toString());				
	}
}
