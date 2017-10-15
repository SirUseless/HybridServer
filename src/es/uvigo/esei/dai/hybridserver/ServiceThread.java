package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import es.uvigo.esei.dai.hybridserver.http.HTTPHeaders;
import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;


public class ServiceThread implements Runnable{
	private Socket socket = null;

	public ServiceThread(Socket socket){
		this.socket = socket;
	}

	@Override
	public void run() {
		this.returnWelcome();
	}
	
	private void returnWelcome(){
		try(Socket socket =  this.socket){
			InputStreamReader reader = new InputStreamReader(socket.getInputStream());
			
			try {
				HTTPRequest request = new HTTPRequest(reader);
				System.out.println(request.toString());
			}catch (HTTPParseException e) {
				System.out.println("Failed to parse HTTPRequest: " + e.getMessage());
			}
			//socket.shutdownInput();
			
			OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());
			HTTPResponse response = new HTTPResponse();
			response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
			response.setStatus(HTTPResponseStatus.S200);
			response.setContent("Hybrid Server");
			System.out.println(response.toString());
			writer.write("HTTP/1.1 200 OK\r\nContent-Length: 13\r\n\r\nHybrid Server\r\n");
			//response.print(writer);

		} catch (IOException e) {
			System.out.println("Failed to create input stream from socket: " + e.getMessage());
		}
		
	}

}
