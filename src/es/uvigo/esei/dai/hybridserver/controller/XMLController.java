package es.uvigo.esei.dai.hybridserver.controller;

import java.util.Map;
import java.util.UUID;

import es.uvigo.esei.dai.hybridserver.dao.IDocumentDAO;
import es.uvigo.esei.dai.hybridserver.dao.XmlDAODB;
import es.uvigo.esei.dai.hybridserver.helpers.HTMLHelper;
import es.uvigo.esei.dai.hybridserver.http.HTTPHeaders;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;
import es.uvigo.esei.dai.hybridserver.http.MIME;

public class XMLController implements Controller{
	private IDocumentDAO xmlDAO;
	
	public XMLController(String url, String user, String password) 
			throws ClassNotFoundException{
		xmlDAO = new XmlDAODB(url, user, password);
	}

	@Override
	public HTTPResponse list(HTTPRequest request, HTTPResponse response) {
		response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(),
				MIME.TEXT_HTML.getMime());
		try {
			response = HTMLHelper.printList(response, request.getResourceName(), this.xmlDAO.list());
			response.setStatus(HTTPResponseStatus.S200);
		} catch (Exception e) {
			response.setStatus(HTTPResponseStatus.S500);
			response = HTMLHelper.printError(response, e.getMessage());
		}

		return response;
	}

	@Override
	public HTTPResponse get(HTTPRequest request, HTTPResponse response) {
		response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(),
				MIME.APPLICATION_XML.getMime());
		String uuid = request.getResourceParameters().get("uuid");

		//TODO check xslt, comprobar con xsd y transformar
		//XSDDao
		//XSLTDAO
		
		try {
			// Test if uuid is valid
			UUID.fromString(uuid);

			if (this.xmlDAO.read(uuid) != null) {
				response.setContent(this.xmlDAO.read(uuid));
				response.setStatus(HTTPResponseStatus.S200);
			} else {
				response.setStatus(HTTPResponseStatus.S404);
				response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(),
						MIME.TEXT_HTML.getMime());
				response = HTMLHelper.printError(response);
			}
		} catch (Exception e) {
			response.setStatus(HTTPResponseStatus.S400);
			response = HTMLHelper.printError(response, "UUID format is not valid");
		}

		return response;
	}

	@Override
	public HTTPResponse post(HTTPRequest request, HTTPResponse response) {
		Map<String, String> resources = request.getResourceParameters();

		if(resources.containsKey("xml")){
			try {
				String uuid = this.xmlDAO.create(resources.get("xml"));
				response = HTMLHelper.printPostSuccess(response, request.getResourceName(), uuid);
				response.setStatus(HTTPResponseStatus.S200);
			} catch (Exception e) {
				response.setStatus(HTTPResponseStatus.S500);
				response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(),
						MIME.TEXT_HTML.getMime());
				response = HTMLHelper.printError(response);
			}
		}else{
			response.setStatus(HTTPResponseStatus.S400);
			response = HTMLHelper.printError(response, "Missing resource name");
		}

		return response;
	}

	@Override
	public HTTPResponse delete(HTTPRequest request, HTTPResponse response) {

		if (!request.getResourceParameters().isEmpty()) {
			String uuid = request.getResourceParameters().get("uuid");

			try {
				if (this.xmlDAO.delete(uuid)) {
					response.setStatus(HTTPResponseStatus.S200);
				} else {
					response.setStatus(HTTPResponseStatus.S404);
					response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(),
							MIME.TEXT_HTML.getMime());
					response = HTMLHelper.printError(response);
				}
			} catch (Exception e) {
				response.setStatus(HTTPResponseStatus.S400);
				response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(),
						MIME.TEXT_HTML.getMime());
				response = HTMLHelper.printError(response, "Missing UUID parameter");
			}
		}

		return response;
	}

}
