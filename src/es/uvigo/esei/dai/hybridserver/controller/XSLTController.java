package es.uvigo.esei.dai.hybridserver.controller;

import es.uvigo.esei.dai.hybridserver.dao.IDocumentDAO;
import es.uvigo.esei.dai.hybridserver.dao.XsltDAODB;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;

public class XSLTController implements Controller{
	private IDocumentDAO xsltDAO;
	
	public XSLTController(String url, String user, String password) 
			throws ClassNotFoundException{
		xsltDAO = new XsltDAODB(url, user, password);
	}

	@Override
	public HTTPResponse list(HTTPRequest request, HTTPResponse response) {
		// TODO Auto-generated method stub
		return response;
	}

	@Override
	public HTTPResponse delete(HTTPRequest request, HTTPResponse response) {
		// TODO Auto-generated method stub
		return response;
	}

	@Override
	public HTTPResponse post(HTTPRequest request, HTTPResponse response) {
		// TODO Auto-generated method stub
		return response;
	}

	@Override
	public HTTPResponse get(HTTPRequest request, HTTPResponse response) {
		// TODO Auto-generated method stub
		return response;
	}

}
