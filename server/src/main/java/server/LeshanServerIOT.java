package server;

import java.io.File;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collection;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.leshan.core.demo.cli.ShortErrorMessageHandler;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.core.request.ReadRequest;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.server.californium.LeshanServer;
import org.eclipse.leshan.server.californium.LeshanServerBuilder;
import org.eclipse.leshan.server.demo.cli.LeshanServerDemoCLI;
import org.eclipse.leshan.server.registration.Registration;
import org.eclipse.leshan.server.registration.RegistrationListener;
import org.eclipse.leshan.server.registration.RegistrationUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.CommandLine;

public class LeshanServerIOT {
	
	private static final Logger LOG = LoggerFactory.getLogger(LeshanServerIOT.class);
	private static final String CF_CONFIGURATION_FILENAME = "Californium.properties";
	private static final String CF_CONFIGURATION_HEADER = "Leshan Server Demo";
	
	public static void main(String[] args) {
		LeshanServerDemoCLI cli = new LeshanServerDemoCLI();
		CommandLine command = new CommandLine(cli).setParameterExceptionHandler(new ShortErrorMessageHandler());
		// Handle exit code error
		int exitCode = command.execute(args);
		if (exitCode != 0)
			System.exit(exitCode);
		// Handle help or version command
		if (command.isUsageHelpRequested() || command.isVersionHelpRequested())
			System.exit(0);
		try {
			// Create LWM2M Server
			LeshanServer lwm2mServer = createLeshanServer(cli); // Create Web Server
			Server webServer = createJettyServer(cli, lwm2mServer); // Register a service to DNS-SD
			if (cli.main.mdns != null) { // Create a JmDNS instance
				JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost()); // Publish Leshan HTTP Service
				ServiceInfo httpServiceInfo = ServiceInfo.create("_http._tcp.local.", "leshan", cli.main.webPort, "");
				jmdns.registerService(httpServiceInfo); // Publish Leshan CoAP Service
				ServiceInfo coapServiceInfo = ServiceInfo.create("_coap._udp.local.", "leshan", cli.main.localPort, "");
				jmdns.registerService(coapServiceInfo); // Publish Leshan Secure CoAP Service
				ServiceInfo coapSecureServiceInfo = ServiceInfo.create("_coaps._udp.local.", "leshan",
						cli.main.secureLocalPort, "");
				jmdns.registerService(coapSecureServiceInfo);
			} // Start servers
			lwm2mServer.start();
			webServer.start();
			LOG.info("Web server started at {}.", webServer.getURI());
			registerEventListeners(lwm2mServer);
		} catch (Exception e) { // Handler Execution Error
			PrintWriter printer = command.getErr();
			printer.print(command.getColorScheme().errorText("Unable to create and start server ..."));
			printer.printf("%n%n");
			printer.print(command.getColorScheme().stackTraceText(e));
			printer.flush();
			System.exit(1);
		}
	}
	
	public static LeshanServer createLeshanServer(LeshanServerDemoCLI cli) throws Exception {
		LeshanServerBuilder builder = new LeshanServerBuilder();
		File configFile = new File(CF_CONFIGURATION_FILENAME);

		Configuration coapConfig = LeshanServerBuilder.createDefaultCoapConfiguration();

		builder.setLocalAddress(cli.main.localAddress,
				cli.main.localPort == null ? coapConfig.get(CoapConfig.COAP_PORT) : cli.main.localPort);
		builder.setLocalSecureAddress(cli.main.secureLocalAddress,
				cli.main.secureLocalPort == null ? coapConfig.get(CoapConfig.COAP_SECURE_PORT)
						: cli.main.secureLocalPort); // Create DTLS Config
		
		return builder.build();
	}
	
	private static Server createJettyServer(LeshanServerDemoCLI cli, LeshanServer lwServer) {
		// Now prepare Jetty
		InetSocketAddress jettyAddr;
		if (cli.main.webhost == null) {
			jettyAddr = new InetSocketAddress(cli.main.webPort);
		} else {
			jettyAddr = new InetSocketAddress(cli.main.webhost, cli.main.webPort);
		}
		Server server = new Server(jettyAddr);
		WebAppContext root = new WebAppContext();
		root.setContextPath("/");
		root.setResourceBase(LeshanServerIOT.class.getClassLoader().getResource("webapp").toExternalForm());
		root.setParentLoaderPriority(true);
		server.setHandler(root); // Create Servlet
//		EventServlet eventServlet = new EventServlet(lwServer, lwServer.getSecuredAddress().getPort());
//		ServletHolder eventServletHolder = new ServletHolder(eventServlet);
//		root.addServlet(eventServletHolder, "/api/event/*");
//		ServletHolder clientServletHolder = new ServletHolder(new ClientServlet(lwServer));
//		root.addServlet(clientServletHolder, "/api/clients/*");
//		ServletHolder securityServletHolder;
//		if (cli.identity.isRPK()) {
//			securityServletHolder = new ServletHolder(new SecurityServlet(
//					(EditableSecurityStore) lwServer.getSecurityStore(), cli.identity.getPublicKey()));
//		} else {
//			securityServletHolder = new ServletHolder(new SecurityServlet(
//					(EditableSecurityStore) lwServer.getSecurityStore(), cli.identity.getCertChain()[0]));
//		}
//		root.addServlet(securityServletHolder, "/api/security/*");
//		ServletHolder serverServletHolder;
//		if (cli.identity.isRPK()) {
//			serverServletHolder = new ServletHolder(new ServerServlet(lwServer, cli.identity.getPublicKey()));
//		} else {
//			serverServletHolder = new ServletHolder(new ServerServlet(lwServer, cli.identity.getCertChain()[0]));
//		}
//		root.addServlet(serverServletHolder, "/api/server/*");
//		ServletHolder objectSpecServletHolder = new ServletHolder(
//				new ObjectSpecServlet(lwServer.getModelProvider(), lwServer.getRegistrationService()));
//		root.addServlet(objectSpecServletHolder, "/api/objectspecs/*");
		return server;
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
