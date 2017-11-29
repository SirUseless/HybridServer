package es.uvigo.esei.dai.hybridserver;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Launcher {
	private static HybridServer server;
	public static void main(String[] args) {
		
		if(args.length  != 0){
				try(FileReader file_reader = new FileReader(args[0])){
					Properties properties = new Properties();
					properties.load(file_reader);
					
					server = new HybridServer(properties);
				}catch(IOException e){
					System.err.println("Could not create server from config file: " + e.getMessage());
				}
		}else{
			System.out.println("No config file provided. Aborting");
			System.exit(-1);
		}
		
		server.start();
	}
}
