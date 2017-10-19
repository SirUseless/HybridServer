package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.uvigo.esei.dai.hybridserver.dao.HtmlDAOMap;
import es.uvigo.esei.dai.hybridserver.dao.IDBConnection;
import es.uvigo.esei.dai.hybridserver.dao.IDocumentDAO;



public class HybridServer {
	private static final int DEFAULT_SERVICE_PORT = 8888;
	private static final int DEFAULT_MAX_SERVICES = 50;
	private static int servicePort;
	protected static IDocumentDAO documentDAO;
	private static IDBConnection dbConnection;
	private Thread serverThread;
	private boolean stop;
	private int maxServices;
	private String db_url;
	private String db_user;
	private String db_password;

	public HybridServer() {
		servicePort = DEFAULT_SERVICE_PORT;
		this.maxServices = DEFAULT_MAX_SERVICES;
		
		documentDAO = new HtmlDAOMap();
		
		String[][] pages = new String[][] {
				    { "6df1047e-cf19-4a83-8cf3-38f5e53f7725", "This is the html page 6df1047e-cf19-4a83-8cf3-38f5e53f7725." },
				    { "79e01232-5ea4-41c8-9331-1c1880a1d3c2", "This is the html page 79e01232-5ea4-41c8-9331-1c1880a1d3c2." },
				    { "a35b6c5e-22d6-4707-98b4-462482e26c9e", "This is the html page a35b6c5e-22d6-4707-98b4-462482e26c9e." },
				    { "3aff2f9c-0c7f-4630-99ad-27a0cf1af137", "This is the html page 3aff2f9c-0c7f-4630-99ad-27a0cf1af137." },
				    { "77ec1d68-84e1-40f4-be8e-066e02f4e373", "This is the html page 77ec1d68-84e1-40f4-be8e-066e02f4e373." },
				    { "8f824126-0bd1-4074-b88e-c0b59d3e67a3", "This is the html page 8f824126-0bd1-4074-b88e-c0b59d3e67a3." },
				    { "c6c80c75-b335-4f68-b7a7-59434413ce6c", "This is the html page c6c80c75-b335-4f68-b7a7-59434413ce6c." },
				    { "f959ecb3-6382-4ae5-9325-8fcbc068e446", "This is the html page f959ecb3-6382-4ae5-9325-8fcbc068e446." },
				    { "2471caa8-e8df-44d6-94f2-7752a74f6819", "This is the html page 2471caa8-e8df-44d6-94f2-7752a74f6819." },
				    { "fa0979ca-2734-41f7-84c5-e40e0886e408", "This is the html page fa0979ca-2734-41f7-84c5-e40e0886e408." }
				};
		
		for (String[] strings : pages) {
			try {
				documentDAO.rawCreate(strings[0], strings[1]);
			} catch (Exception e) {
				System.err.println("Duplicate key: " + strings[0]);
			}
		}
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
		this.maxServices = Integer.parseInt(properties.getProperty(PropertyNames.CLIENT_NUMBER.getName()).toString());
		servicePort = Integer.parseInt(properties.getProperty(PropertyNames.PORT.getName()));
		this.db_url = properties.getProperty(PropertyNames.DB_URL.getName());
		this.db_user = properties.getProperty(PropertyNames.DB_USER.getName());
		this.db_password = properties.getProperty(PropertyNames.DB_PASSWORD.getName());
		
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
