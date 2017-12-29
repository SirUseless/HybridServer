package es.uvigo.esei.dai.hybridserver.controller;

import java.util.Map;
import java.util.UUID;

import es.uvigo.esei.dai.hybridserver.dao.IDocumentDAO;
import es.uvigo.esei.dai.hybridserver.dao.XsdDAODB;
import es.uvigo.esei.dai.hybridserver.helpers.HTMLHelper;
import es.uvigo.esei.dai.hybridserver.http.HTTPHeaders;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;
import es.uvigo.esei.dai.hybridserver.http.MIME;

public class XSDController implements Controller{
	private IDocumentDAO xsdDAO;
	
	public XSDController(String url, String user, String password) 
			throws ClassNotFoundException{
		xsdDAO = new XsdDAODB(url, user, password);
	}

	@Override
	public HTTPResponse list(HTTPRequest request, HTTPResponse response) {
		response.setStatus(HTTPResponseStatus.S200);
		response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(),
				MIME.TEXT_HTML.getMime());
		
		try {
			response = HTMLHelper.printList(response, request.getResourceName(), this.xsdDAO.list());
		} catch (Exception e) {
			response.setStatus(HTTPResponseStatus.S200);
			response = HTMLHelper.printError(response, e.getMessage());
		}

		return response;
	}

	@Override
	public HTTPResponse get(HTTPRequest request, HTTPResponse response) {
		response.setStatus(HTTPResponseStatus.S200);
		response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(),
				MIME.APPLICATION_XML.getMime());
		String uuid = request.getResourceParameters().get("uuid");

		try {
			// Test if uuid is valid
			UUID.fromString(uuid);

			if (this.xsdDAO.read(uuid) != null) {
				response.setContent(this.xsdDAO.read(uuid));
				response.setStatus(HTTPResponseStatus.S200);
			} else {
				response.setStatus(HTTPResponseStatus.S404);
				response = HTMLHelper.printError(response);
			}
		} catch (Exception e) {
			response.setStatus(HTTPResponseStatus.S404);
			response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(),
					MIME.TEXT_HTML.getMime());
			response = HTMLHelper.printError(response);
		}

		return response;
	}

	@Override
	public HTTPResponse post(HTTPRequest request, HTTPResponse response) {
		Map<String, String> resources = request.getResourceParameters();
		
		if(resources.containsKey("xsd")){
			try {
				String uuid = this.xsdDAO.create(resources.get("xsd"));
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
			response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(),
					MIME.TEXT_HTML.getMime());
			response = HTMLHelper.printError(response, "xml parameter not found");
		}

		return response;
	}

	@Override
	public HTTPResponse delete(HTTPRequest request, HTTPResponse response) {

		if (!request.getResourceParameters().isEmpty()) {
			String uuid = request.getResourceParameters().get("uuid");

			try {
				if (this.xsdDAO.delete(uuid)) {
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
				response = HTMLHelper.printError(response);
			}
		}

		return response;
	}

}
