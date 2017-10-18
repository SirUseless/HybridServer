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

/**
 * 
 * @author Adrián Simón Reboredo & Josué Pato Valcárcel
 *
 */
public class ServiceThread implements Runnable {
	private Socket socket = null;
	private HTTPRequest request;
	private HTTPResponse response;

	public ServiceThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try (BufferedReader buffer = new BufferedReader(new InputStreamReader(
				socket.getInputStream()))) {
			try {
				this.request = new HTTPRequest(buffer);
				switch (this.request.getMethod()) {
				case GET:
					this.prepareGet();
					break;
				case POST:
					this.preparePost();
					break;
				case DELETE:
					this.prepareDelete();
					break;
				default:
					this.prepareUnsupported();
					break;
				}
				this.respond();
			} catch (HTTPParseException e) {
				System.out.println("Parse exception: " + e.getMessage());
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	private void respond() {
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				socket.getOutputStream()))) {
			response.print(writer);
		} catch (IOException e) {
			System.out.println("Could not write to socket: " + e.getMessage());
		}
	}

	private void prepareGet() {

	}

	private void preparePost() {

	}

	private void prepareDelete() {

	}

	private void prepareUnsupported() {
			this.response = new HTTPResponse();
			response.setVersion(HTTPHeaders.HTTP_1_1.getHeader());
			response.setStatus(HTTPResponseStatus.S405);
	}

}
