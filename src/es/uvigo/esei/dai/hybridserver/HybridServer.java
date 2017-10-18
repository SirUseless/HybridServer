package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.uvigo.esei.dai.hybridserver.dao.HtmlDAOMap;
import es.uvigo.esei.dai.hybridserver.dao.IDocumentDAO;



public class HybridServer {
	private static final int DEFAULT_SERVICE_PORT = 8888;
	private static final int DEFAULT_MAX_SERVICES = 50;
	private static int servicePort;
	private static IDocumentDAO documentDAO;
	private Thread serverThread;
	private boolean stop;
	private int maxServices;
	@SuppressWarnings("unused")
	private String db_url;
	@SuppressWarnings("unused")
	private String db_user;
	@SuppressWarnings("unused")
	private String db_password;

	public HybridServer() {
		servicePort = DEFAULT_SERVICE_PORT;
		this.maxServices = DEFAULT_MAX_SERVICES;
	}

	public HybridServer(Map<String, String> pages) {
		this.maxServices = DEFAULT_MAX_SERVICES;
		servicePort = DEFAULT_SERVICE_PORT;
		
		documentDAO = new HtmlDAOMap();
		for (@SuppressWarnings("rawtypes") Map.Entry page : pages.entrySet()) {
			try {
				documentDAO.rawCreate(page.getKey().toString(), page.getValue().toString());
			} catch (Exception e) {
				System.err.println("Duplicate key: " + page.getKey().toString());
			}
		}
	}

	public HybridServer(Properties properties) {
		this.maxServices = Integer.parseInt(properties.getProperty("numClients").toString());
		servicePort = Integer.parseInt(properties.getProperty("port"));
		this.db_url = properties.getProperty("db.url");
		this.db_user = properties.getProperty("db.user");
		this.db_password = properties.getProperty("db.password");
		
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
	}
}
