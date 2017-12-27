package es.uvigo.esei.dai.hybridserver;

import java.io.File;


public class Launcher {
	private static HybridServer server;
	private static Configuration cfg;
	public static void main(String[] args) {
		
		if(args.length  != 0){
			File cfgFile = new File(args[0]);
			try {
				cfg = XMLConfigurationLoader.load(cfgFile);
				server = new HybridServer(cfg);
			} catch (Exception e) {
				System.out.println("Couldn't load cfg: " + e.getMessage());
				System.out.println("Exiting");
				System.exit(-1);
			}
		}else{
			System.out.println("Starting server from default Configuration");
			server = new HybridServer();
		}
		server.start();
	}
}
