package es.uvigo.esei.dai.hybridserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import es.uvigo.esei.dai.hybridserver.http.HTTPHeaders;
import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class ServiceThread implements Runnable {
	private Socket socket = null;
	private HTTPRequest request;

	public ServiceThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try(BufferedReader buffer = new BufferedReader(new InputStreamReader(socket.getInputStream()))){
			try {
				this.request = new HTTPRequest(buffer);
				System.out.println(this.request.toString());
			} catch (HTTPParseException e) {
				System.out.println("Parse exception: " + e.getMessage());
			}
			this.testHomepage();
		}catch(IOException e){
			System.err.println(e.getMessage());
		}
	}

	private void testHomepage() {
		try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())) ) {
			
			HTTPResponse response = new HTTPResponse();
			response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
			response.setStatus(HTTPResponseStatus.S200);
			response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(), "text/html");
			response.setContent("<html><h1>Hybrid Server</h1></html>");
			response.print(writer);
		} catch (IOException e) {
			System.out.println("Could not write to socket: " + e.getMessage());
		}
	}
	

}
