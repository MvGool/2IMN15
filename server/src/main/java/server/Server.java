package server;

import java.io.File;
import java.io.PrintWriter;
import java.util.Collection;

import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.core.request.ReadRequest;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.server.californium.LeshanServer;
import org.eclipse.leshan.server.californium.LeshanServerBuilder;
import org.eclipse.leshan.server.registration.Registration;
import org.eclipse.leshan.server.registration.RegistrationListener;
import org.eclipse.leshan.server.registration.RegistrationUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
	
	private static final Logger LOG = LoggerFactory.getLogger(Server.class);
	private static final String CF_CONFIGURATION_FILENAME = "Californium.properties";
	private static final String CF_CONFIGURATION_HEADER = "Leshan Server Demo";
	
//	public static LeshanServer createLeshanServer(ServerCLI cli) throws Exception {
	public static LeshanServer createLeshanServer() throws Exception {
		LeshanServerBuilder builder = new LeshanServerBuilder();
		File configFile = new File(CF_CONFIGURATION_FILENAME);
		// TODO: configuration stuff
		Configuration coapConfig = LeshanServerBuilder.createDefaultCoapConfiguration();

		// TODO: cli stuff
//		builder.setLocalAddress(cli.main.localAddress,
//				cli.main.localPort == null ? coapConfig.get(CoapConfig.COAP_PORT) : cli.main.localPort);
//		builder.setLocalSecureAddress(cli.main.secureLocalAddress,
//				cli.main.secureLocalPort == null ? coapConfig.get(CoapConfig.COAP_SECURE_PORT)
//						: cli.main.secureLocalPort); // Create DTLS Config
//		
		return builder.build();
	}
	
	
	public static void main(String[] args) {

		try {
			// Create LWM2M Server
			LeshanServer lwm2mServer = createLeshanServer(); // Create Web Server
//			Server webServer = createJettyServer(cli, lwm2mServer); // Register a service to DNS-SD
//			if (cli.main.mdns != null) { // Create a JmDNS instance
//				JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost()); // Publish Leshan HTTP Service
//				ServiceInfo httpServiceInfo = ServiceInfo.create("_http._tcp.local.", "leshan", cli.main.webPort, "");
//				jmdns.registerService(httpServiceInfo); // Publish Leshan CoAP Service
//				ServiceInfo coapServiceInfo = ServiceInfo.create("_coap._udp.local.", "leshan", cli.main.localPort, "");
//				jmdns.registerService(coapServiceInfo); // Publish Leshan Secure CoAP Service
//				ServiceInfo coapSecureServiceInfo = ServiceInfo.create("_coaps._udp.local.", "leshan",
//						cli.main.secureLocalPort, "");
//				jmdns.registerService(coapSecureServiceInfo);
//			} // Start servers
			lwm2mServer.start();
//			webServer.start();
//			LOG.info("Web server started at {}.", webServer.getURI());
			registerEventListeners(lwm2mServer);
		} catch (Exception e) { // Handler Execution Error
//			PrintWriter printer = command.getErr();
//			printer.print(command.getColorScheme().errorText("Unable to create and start server ..."));
//			printer.printf("%n%n");
//			printer.print(command.getColorScheme().stackTraceText(e));
//			printer.flush();
			System.exit(1);
		}
	}
		
	public static void registerEventListeners(LeshanServer server) {
		server.getRegistrationService().addListener(new RegistrationListener() {
		    public void registered(Registration registration, Registration previousReg,
		            Collection<Observation> previousObsersations) {
		    	System.out.println("new device: " + registration.getEndpoint());
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
