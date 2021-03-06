package server;

import java.io.File;
import org.eclipse.leshan.core.observation.CompositeObservation;
import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.core.observation.SingleObservation;
import org.eclipse.leshan.core.request.ObserveRequest;
import org.eclipse.leshan.core.request.ReadRequest;
import org.eclipse.leshan.core.request.WriteRequest;
import org.eclipse.leshan.core.response.ObserveCompositeResponse;
import org.eclipse.leshan.core.response.ObserveResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;
import org.eclipse.leshan.server.registration.Registration;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.elements.config.Configuration;
import org.eclipse.californium.elements.util.CertPathUtil;
import org.eclipse.californium.scandium.config.DtlsConfig;
import org.eclipse.californium.scandium.config.DtlsConfig.DtlsRole;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.leshan.core.demo.cli.ShortErrorMessageHandler;
import org.eclipse.leshan.core.model.ObjectLoader;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.node.LwM2mNode;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.server.californium.LeshanServer;
import org.eclipse.leshan.server.californium.LeshanServerBuilder;
import org.eclipse.leshan.server.demo.cli.LeshanServerDemoCLI;
import org.eclipse.leshan.server.model.LwM2mModelProvider;
import org.eclipse.leshan.server.model.VersionedModelProvider;
import org.eclipse.leshan.server.observation.ObservationListener;
import org.eclipse.leshan.server.redis.RedisRegistrationStore;
import org.eclipse.leshan.server.redis.RedisSecurityStore;
import org.eclipse.leshan.server.registration.RegistrationListener;
import org.eclipse.leshan.server.registration.RegistrationUpdate;
import org.eclipse.leshan.server.security.EditableSecurityStore;
import org.eclipse.leshan.server.security.FileSecurityStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import components.ParkingLot;
import picocli.CommandLine;
import servlet.EventServlet;
import servlet.LicensePlateServlet;
import servlet.ParkingLotServlet;

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
			LeshanServer lwm2mServer = createLeshanServer(cli); 
			
			// Create Web Server
			Server webServer = createJettyServer(cli, lwm2mServer); 
			
			// Register a service to DNS-SD
			if (cli.main.mdns != null) { // Create a JmDNS instance
				JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost()); // Publish Leshan HTTP Service
				ServiceInfo httpServiceInfo = ServiceInfo.create("_http._tcp.local.", "leshan", cli.main.webPort, "");
				jmdns.registerService(httpServiceInfo); // Publish Leshan CoAP Service
				ServiceInfo coapServiceInfo = ServiceInfo.create("_coap._udp.local.", "leshan", cli.main.localPort, "");
				jmdns.registerService(coapServiceInfo); // Publish Leshan Secure CoAP Service
				ServiceInfo coapSecureServiceInfo = ServiceInfo.create("_coaps._udp.local.", "leshan",
						cli.main.secureLocalPort, "");
				jmdns.registerService(coapSecureServiceInfo);
			} 
			
			// Start servers
			lwm2mServer.start();
			webServer.start();
			LOG.info("Web server started at {}.", webServer.getURI());
//			registerEventListeners(lwm2mServer);
		} catch (Exception e) { 
			// Handler Execution Error
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
		// these configuration values are always overwritten by CLI
		// therefore set them to transient.
		coapConfig.setTransient(DtlsConfig.DTLS_RECOMMENDED_CIPHER_SUITES_ONLY);
		coapConfig.setTransient(DtlsConfig.DTLS_CONNECTION_ID_LENGTH);
		if (configFile.isFile()) {
			coapConfig.load(configFile);
		} else {
			coapConfig.store(configFile, CF_CONFIGURATION_HEADER);
		}
		builder.setCoapConfig(coapConfig); // ports from CoAP Config if needed
		builder.setLocalAddress(cli.main.localAddress,
				cli.main.localPort == null ? coapConfig.get(CoapConfig.COAP_PORT) : cli.main.localPort);
		builder.setLocalSecureAddress(cli.main.secureLocalAddress,
				cli.main.secureLocalPort == null ? coapConfig.get(CoapConfig.COAP_SECURE_PORT)
						: cli.main.secureLocalPort); 
		
		// Create DTLS Config
		DtlsConnectorConfig.Builder dtlsConfig = DtlsConnectorConfig.builder(coapConfig);
		dtlsConfig.set(DtlsConfig.DTLS_RECOMMENDED_CIPHER_SUITES_ONLY, !cli.dtls.supportDeprecatedCiphers);
		if (cli.dtls.cid != null) {
			dtlsConfig.set(DtlsConfig.DTLS_CONNECTION_ID_LENGTH, cli.dtls.cid);
		}
		if (cli.identity.isx509()) {
			// use X.509 mode (+ RPK)
			builder.setPrivateKey(cli.identity.getPrivateKey());
			builder.setCertificateChain(cli.identity.getCertChain());
			X509Certificate serverCertificate = cli.identity.getCertChain()[0];
			// autodetect serverOnly
			if (dtlsConfig.getIncompleteConfig().getDtlsRole() == DtlsRole.BOTH) {
				if (serverCertificate != null) {
					if (CertPathUtil.canBeUsedForAuthentication(serverCertificate, false)) {
						if (!CertPathUtil.canBeUsedForAuthentication(serverCertificate, true)) {
							dtlsConfig.set(DtlsConfig.DTLS_ROLE, DtlsRole.SERVER_ONLY);
							LOG.warn("Server certificate does not allow Client Authentication usage."
									+ "\nThis will prevent this LWM2M server to initiate DTLS connection."
									+ "\nSee : https://github.com/eclipse/leshan/wiki/Server-Failover#about-connections");
						}
					}
				}
			} 
			// Define trust store
			List<Certificate> trustStore = cli.identity.getTrustStore();
			builder.setTrustedCertificates(trustStore.toArray(new Certificate[trustStore.size()]));
		} else if (cli.identity.isRPK()) {
			// use RPK only
			builder.setPublicKey(cli.identity.getPublicKey());
			builder.setPrivateKey(cli.identity.getPrivateKey());
		} 
		
		// Set DTLS Config
		builder.setDtlsConfig(dtlsConfig); 
		
		// Define model provider
		List<ObjectModel> models = ObjectLoader.loadAllDefault();
		models.addAll(ObjectLoader.loadDdfResources("/models/", ServerConstants.modelPaths));
		if (cli.main.modelsFolder != null) {
			models.addAll(ObjectLoader.loadObjectsFromDir(cli.main.modelsFolder, true));
		}
		LwM2mModelProvider modelProvider = new VersionedModelProvider(models);
		builder.setObjectModelProvider(modelProvider); 
		
		// Set securityStore & registrationStore
		EditableSecurityStore securityStore;
		if (cli.main.redis == null) {
			// use file persistence
			securityStore = new FileSecurityStore();
		} else {
			// use Redis Store
			securityStore = new RedisSecurityStore(cli.main.redis);
			builder.setRegistrationStore(new RedisRegistrationStore(cli.main.redis));
		}
		builder.setSecurityStore(securityStore); 
		
		// Create LWM2M server
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
		
		// Set context
		WebAppContext root = new WebAppContext();
		root.setContextPath("/");
		root.setResourceBase(LeshanServerIOT.class.getClassLoader().getResource("static").toExternalForm());
		root.setParentLoaderPriority(true);
		server.setHandler(root); 

		// Create Servlet
		ParkingLot lot = new ParkingLot("P1");
		
		ParkingLotServlet parkingServlet = new ParkingLotServlet(lwServer, lot);
		ServletHolder parkingServletHolder = new ServletHolder(parkingServlet);
		root.addServlet(parkingServletHolder, "/api/lot/*");

		LicensePlateServlet plateServlet = new LicensePlateServlet(lwServer, lot);
		ServletHolder plateServletHolder = new ServletHolder(plateServlet);
		root.addServlet(plateServletHolder, "/api/plate/*");

		return server;
	}
		
	public static void registerEventListeners(LeshanServer server) {
		
		server.getRegistrationService().addListener(new RegistrationListener() {
		    public void registered(Registration registration, Registration previousReg,
		            Collection<Observation> previousObsersations) {
		    	System.err.println("new device: " + registration);
		    	try {
		    		ObserveRequest licensePlateRequest = new ObserveRequest(32801,0,32704);
					server.send(registration, licensePlateRequest, 500000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		    	try {
		            ReadResponse response = server.send(registration, new ReadRequest(32800,0,32700));
		            if (response.isSuccess()) {
		                System.out.println("Spot id: " + ((LwM2mResource)response.getContent()).getValue());
		            }else {
		                System.out.println("Failed to read:" + response.getCode() + " " + response.getErrorMessage());
		            }
		        } catch (InterruptedException e) {
		            e.printStackTrace();
		        }
		    	try {
		            WriteResponse response = server.send(registration, new WriteRequest(32800,0,32706,"P1"));
		            if (response.isSuccess()) {
		                System.out.println("Updated parking lot name");
		            }else {
		                System.out.println("Failed to read:" + response.getCode() + " " + response.getErrorMessage());
		            }
		        } catch (InterruptedException e) {
		            e.printStackTrace();
		        }
			}

		    public void updated(RegistrationUpdate update, Registration updatedReg, Registration previousReg) {
		        System.err.println("device is still here: " + updatedReg.getEndpoint());
		    }

		    public void unregistered(Registration registration, Collection<Observation> observations, boolean expired,
		            Registration newReg) {
		        System.out.println("device left: " + registration.getEndpoint());
		    }
		});
		
		server.getObservationService().addListener(new ObservationListener() {
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
				LwM2mNode resp = response.getContent();
				System.err.println("Response from observation: " + resp);
				
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
			
		});
	}
	
}
