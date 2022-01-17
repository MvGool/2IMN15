package listeners;

import java.util.Collection;

import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.core.request.ObserveRequest;
import org.eclipse.leshan.core.request.ReadRequest;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.server.californium.LeshanServer;
import org.eclipse.leshan.server.registration.Registration;
import org.eclipse.leshan.server.registration.RegistrationListener;
import org.eclipse.leshan.server.registration.RegistrationUpdate;

import components.ParkingLot;

public class ClientRegistrationListener implements RegistrationListener {

	private LeshanServer server;
	private ParkingLot lot;
	
	public ClientRegistrationListener(LeshanServer server, ParkingLot lot) {
		this.server = server;
		this.lot = lot;
	}
	
	public void registered(Registration registration, Registration previousReg,
			Collection<Observation> previousObsersations) {
		System.err.println("new device: " + registration);
		try {
			ReadResponse response = server.send(registration, new ReadRequest(32800,0,32700));
            if (response.isSuccess()) {
                System.out.println("Spot id: " + ((LwM2mResource)response.getContent()).getValue());
                lot.registerSpot(((LwM2mResource)response.getContent()).getValue().toString(), registration);
            }else {
                System.out.println("Failed to read:" + response.getCode() + " " + response.getErrorMessage());
            }
			ObserveRequest parkingSpotRequest = new ObserveRequest(32800,0,32701);
			ObserveRequest licensePlateRequest = new ObserveRequest(32801,0,32704);
			server.send(registration, parkingSpotRequest, 500000);
			server.send(registration, licensePlateRequest, 500000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void updated(RegistrationUpdate update, Registration updatedReg, Registration previousReg) {
		System.err.println("device is still here: " + updatedReg.getEndpoint());
	}
	
	public void unregistered(Registration registration, Collection<Observation> observations, boolean expired,
			Registration newReg) {
		System.out.println("device left: " + registration.getEndpoint());
	}
}
