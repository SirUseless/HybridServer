package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import es.uvigo.esei.dai.hybridserver.dao.HtmlDAODB;
import es.uvigo.esei.dai.hybridserver.dao.HtmlDAOMap;
import es.uvigo.esei.dai.hybridserver.dao.IDocumentDAO;



public class HybridServer {
	private static final int DEFAULT_SERVICE_PORT = 8888;
	private static final int DEFAULT_MAX_SERVICES = 50;
	private static int servicePort;
	private ExecutorService threadPool;
	//TODO non-static access to DAO
	protected static IDocumentDAO documentDAO;
	private Thread serverThread;
	private boolean stop;
	private int maxServices;
	private String db_url;
	private String db_user;
	private String db_password;

	public HybridServer() {
		servicePort = DEFAULT_SERVICE_PORT;
		this.maxServices = DEFAULT_MAX_SERVICES;
		
		
		//TODO cargar config por defecto
		System.exit(0);
	}
	
	public HybridServer(Configuration configuration){
		//TODO implement
		System.exit(0);
	}

	public HybridServer(Properties properties) {
		this.maxServices = Integer.parseInt(properties.getProperty(PropertyNames.CLIENT_NUMBER.getName()).toString());
		servicePort = Integer.parseInt(properties.getProperty(PropertyNames.PORT.getName()));
		this.db_url = properties.getProperty(PropertyNames.DB_URL.getName());
		this.db_user = properties.getProperty(PropertyNames.DB_USER.getName());
		this.db_password = properties.getProperty(PropertyNames.DB_PASSWORD.getName());
		
		try {
			documentDAO = new HtmlDAODB(this.db_url, this.db_user, this.db_password);
		} catch (ClassNotFoundException e) {
			System.err.println("Could not find DB Driver. Exiting");
			System.exit(-1);
		}
		
		System.out.println("Server launched from config file.");
	}

	public int getPort() {
		return servicePort;
	}

	public void start() {
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
							executor.execute(new ServiceThread(socket));
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
	}
}
