package es.uvigo.esei.dai.hybridserver.controller;

import java.util.Map;
import java.util.UUID;

import javax.swing.text.html.HTML;

import es.uvigo.esei.dai.hybridserver.dao.HtmlDAODB;
import es.uvigo.esei.dai.hybridserver.dao.IDocumentDAO;
import es.uvigo.esei.dai.hybridserver.helpers.HTMLHelper;
import es.uvigo.esei.dai.hybridserver.http.HTTPHeaders;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;
import es.uvigo.esei.dai.hybridserver.http.MIME;

public class HTMLController implements Controller{
	private IDocumentDAO htmlDAO;

	public HTMLController(String url, String user, String password)
			throws ClassNotFoundException {
		htmlDAO = new HtmlDAODB(url, user, password);
	}
	
	@Override
	public HTTPResponse list(HTTPRequest request, HTTPResponse response) {
		response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(),
				MIME.TEXT_HTML.getMime());
	
		try {
			Map<UUID, String> list = this.htmlDAO.list();
			response.setStatus(HTTPResponseStatus.S200);
			response = HTMLHelper.printList(response, request.getResourceName(), list);
		} catch (Exception e) {
			response.setStatus(HTTPResponseStatus.S500);
			response = HTMLHelper.printError(response, e.getMessage());
		}

		return response;
	}

	@Override
	public HTTPResponse get(HTTPRequest request, HTTPResponse response) {
		response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(),
				MIME.TEXT_HTML.getMime());
		
		String uuid = request.getResourceParameters().get("uuid");

		try {
			// Test if uuid is valid
			UUID.fromString(uuid);

			if (this.htmlDAO.read(uuid) != null) {
				response.setContent(this.htmlDAO.read(uuid));
				response.setStatus(HTTPResponseStatus.S200);
			} else {
				//not found, 404
				response.setStatus(HTTPResponseStatus.S404);
				response = HTMLHelper.printError(response);
			}
		} catch (Exception e) {
			//UUID not valid, bad request
			response.setStatus(HTTPResponseStatus.S400);
			response = HTMLHelper.printError(response, "UUID format is not valid");
		}

		return response;
	}

	@Override
	public HTTPResponse post(HTTPRequest request, HTTPResponse response) {
		response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(),
				MIME.TEXT_HTML.getMime());
		Map<String, String> resources = request.getResourceParameters();

		if(resources.containsKey("html")){
			try {
				String uuid = this.htmlDAO.create(resources.get("html"));
				response.setStatus(HTTPResponseStatus.S200);
				response = HTMLHelper.printPostSuccess(response, request.getResourceName(), uuid);
			} catch (Exception e) {
				response.setStatus(HTTPResponseStatus.S500);
				response = HTMLHelper.printError(response, e.getMessage());
			}
		}else{
			response.setStatus(HTTPResponseStatus.S400);
			response = HTMLHelper.printError(response, "No 'html' resource found");
		}

		return response;
	}

	@Override
	public HTTPResponse delete(HTTPRequest request, HTTPResponse response) {

		if (!request.getResourceParameters().isEmpty()) {
			String uuid = request.getResourceParameters().get("uuid");

			try {
				if (this.htmlDAO.delete(uuid)) {
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
				response = HTMLHelper.printError(response, e.getMessage());
			}
		}

		return response;
	}
}
