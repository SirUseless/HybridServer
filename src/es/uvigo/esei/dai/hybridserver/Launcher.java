package es.uvigo.esei.dai.hybridserver;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Launcher {
	private static final String CONF_EXTENSION = ".conf";
	private static HybridServer server;
	public static void main(String[] args) {
		
		if(args.length != 0){
			if(args[0].contains(CONF_EXTENSION)){
				try(FileReader file_reader = new FileReader(args[0])){
					Properties properties = new Properties();
					properties.load(file_reader);
					
					server = new HybridServer(properties);
				}catch(IOException e){
					System.err.println("Could not create server from config file: " + e.getMessage());
				}
			}else{
				System.err.println("Provided argument is not a .conf file.");
			}
		}else{
			System.out.println("No config file provided, starting server with default config.");
			server = new HybridServer();
		}
		
		server.start();
	}
}
