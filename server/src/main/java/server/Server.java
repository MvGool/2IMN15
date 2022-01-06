package server;

import java.util.Collection;

import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.core.request.ReadRequest;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.server.californium.LeshanServer;
import org.eclipse.leshan.server.californium.LeshanServerBuilder;
import org.eclipse.leshan.server.registration.Registration;
import org.eclipse.leshan.server.registration.RegistrationListener;
import org.eclipse.leshan.server.registration.RegistrationUpdate;

public class Server {
	
	
	public static void main(String[] args) {
		LeshanServerBuilder builder = new LeshanServerBuilder();
		final LeshanServer server = builder.build();
		server.start();
		
		server.getRegistrationService().addListener(new RegistrationListener() {

		    public void registered(Registration registration, Registration previousReg,
		            Collection<Observation> previousObsersations) {
		    	System.out.println("new device: " + registration.getEndpoint());
			    try {
			        ReadResponse response = server.send(registration, new ReadRequest(3,0,13));
			        if (response.isSuccess()) {
			            System.out.println("Device time:" + ((LwM2mResource)response.getContent()).getValue());
			        }else {
			            System.out.println("Failed to read:" + response.getCode() + " " + response.getErrorMessage());
			        }
			    } catch (InterruptedException e) {
			        e.printStackTrace();
			    }
			}

		    public void updated(RegistrationUpdate update, Registration updatedReg, Registration previousReg) {
		        System.out.println("device is still here: " + updatedReg.getEndpoint());
		    }

		    public void unregistered(Registration registration, Collection<Observation> observations, boolean expired,
		            Registration newReg) {
		        System.out.println("device left: " + registration.getEndpoint());
		    }
		});
	}
	
}
