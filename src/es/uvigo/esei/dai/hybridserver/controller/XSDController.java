package es.uvigo.esei.dai.hybridserver.controller;

import es.uvigo.esei.dai.hybridserver.dao.IDocumentDAO;
import es.uvigo.esei.dai.hybridserver.dao.XsdDAODB;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;

public class XSDController implements Controller{
	private IDocumentDAO xsdDAO;
	
	public XSDController(String url, String user, String password) 
			throws ClassNotFoundException{
		xsdDAO = new XsdDAODB(url, user, password);
	}

	@Override
	public HTTPResponse list(HTTPRequest request, HTTPResponse response) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HTTPResponse delete(HTTPRequest request, HTTPResponse response) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HTTPResponse post(HTTPRequest request, HTTPResponse response) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HTTPResponse get(HTTPRequest request, HTTPResponse response) {
		// TODO Auto-generated method stub
		return null;
	}

}
