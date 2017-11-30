package es.uvigo.esei.dai.hybridserver.controller;

import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;

public interface Controller {
	
	public HTTPResponse list(HTTPRequest request, HTTPResponse response);
	public HTTPResponse delete(HTTPRequest request, HTTPResponse response);
	public HTTPResponse post(HTTPRequest request, HTTPResponse response);
	public HTTPResponse get(HTTPRequest request, HTTPResponse response);
}
