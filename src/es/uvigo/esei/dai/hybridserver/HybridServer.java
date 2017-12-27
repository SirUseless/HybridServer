package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.ws.Endpoint;

import es.uvigo.esei.dai.hybridserver.webservice.HSWebService;



public class HybridServer {
	
	private static Configuration cfg;
	private static int servicePort;
	private ExecutorService threadPool;
	private Thread serverThread;
	private boolean stop;
	private int maxServices;
	private String db_url;
	private String db_user;
	private String db_password;
	private String webServiceURL;
	private Endpoint endpoint;

	public HybridServer() {
		cfg = new Configuration();
		
		servicePort = cfg.getHttpPort();
		this.maxServices = cfg.getNumClients();
		this.db_password = cfg.getDbPassword();
		this.db_url = cfg.getDbURL();
		this.db_user = cfg.getDbUser();
		this.webServiceURL = cfg.getWebServiceURL();
		this.stop = false;
		System.out.println("Server launched following default configuration.");
	}
	
	public HybridServer(Configuration configuration){
		cfg = configuration;
		
		servicePort = cfg.getHttpPort();
		this.maxServices = cfg.getNumClients();
		this.db_password = cfg.getDbPassword();
		this.db_url = cfg.getDbURL();
		this.db_user = cfg.getDbUser();
		this.webServiceURL = cfg.getWebServiceURL();
		this.stop = false;
		System.out.println("Server launched from XML file.");
	}

	public HybridServer(Properties properties) {
		this.maxServices = Integer.parseInt(properties.getProperty(PropertyNames.CLIENT_NUMBER.getName()).toString());
		servicePort = Integer.parseInt(properties.getProperty(PropertyNames.PORT.getName()));
		this.db_url = properties.getProperty(PropertyNames.DB_URL.getName());
		this.db_user = properties.getProperty(PropertyNames.DB_USER.getName());
		this.db_password = properties.getProperty(PropertyNames.DB_PASSWORD.getName());
		
		cfg = new Configuration();
		
		cfg.setNumClients(this.maxServices);
		cfg.setHttpPort(servicePort);
		cfg.setDbURL(this.db_url);
		cfg.setDbUser(this.db_user);
		cfg.setDbPassword(this.db_password);
		
		System.out.println("Server launched from Properties file.");
	}

	public int getPort() {
		return servicePort;
	}

	public void start() {
		if(this.webServiceURL != null){
			System.out.println("Publishing web service to " + webServiceURL);
			endpoint = Endpoint.publish(webServiceURL, 
									new HSWebService(db_url, db_user, db_password));
		}		
		
		this.serverThread = new Thread() {
			@Override
			public void run() {
				try (final ServerSocket serverSocket = new ServerSocket(
						servicePort)) {
					ExecutorService executor = Executors.newFixedThreadPool(maxServices);
					threadPool = executor;
					while (true) {
						Socket socket = serverSocket.accept();
							if (stop)
								break;
							executor.execute(new ServiceThread(socket, cfg));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		this.stop = false;
		this.serverThread.start();
	}

	public void stop() {
		this.stop = true;

		try (Socket socket = new Socket("localhost", servicePort)) {
			// Esta conexión se hace, simplemente, para "despertar" el hilo
			// servidor
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try {
			this.serverThread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		this.serverThread = null;
		
		threadPool.shutdownNow();
		 
		try {
		  threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException e) {
		  e.printStackTrace();
		}
		
		if(webServiceURL != null){
			endpoint.stop();
		}
	}
}
