package es.uvigo.esei.dai.hybridserver.controller;

import java.util.Map;
import java.util.UUID;

import es.uvigo.esei.dai.hybridserver.dao.IDocumentDAO;
import es.uvigo.esei.dai.hybridserver.dao.XsdDAODB;
import es.uvigo.esei.dai.hybridserver.dao.XsltDAODB;
import es.uvigo.esei.dai.hybridserver.helpers.HTMLHelper;
import es.uvigo.esei.dai.hybridserver.http.HTTPHeaders;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;
import es.uvigo.esei.dai.hybridserver.http.MIME;

public class XSLTController implements Controller{
	private XsltDAODB xsltDAO;
	private IDocumentDAO xsdDAO;
	
	public XSLTController(String url, String user, String password) 
			throws ClassNotFoundException{
		xsltDAO = new XsltDAODB(url, user, password);
		xsdDAO = new XsdDAODB(url, user, password);
	}

	@Override
	public HTTPResponse list(HTTPRequest request, HTTPResponse response) {
		response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(),
				MIME.TEXT_HTML.getMime());
		
		try {
			response.setStatus(HTTPResponseStatus.S200);
			response = HTMLHelper.printList(response, request.getResourceName(), this.xsltDAO.list());
		} catch (Exception e) {
			response.setStatus(HTTPResponseStatus.S500);
			response = HTMLHelper.printError(response);
		}

		return response;
	}

	@Override
	public HTTPResponse delete(HTTPRequest request, HTTPResponse response) {
		if (!request.getResourceParameters().isEmpty()) {
			String uuid = request.getResourceParameters().get("uuid");

			try {
				if (this.xsltDAO.delete(uuid)) {
					response.setStatus(HTTPResponseStatus.S200);
				} else {
					response.setStatus(HTTPResponseStatus.S404);
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

	@Override
	public HTTPResponse post(HTTPRequest request, HTTPResponse response) {
		response.setStatus(HTTPResponseStatus.S200);
		Map<String, String> resources = request.getResourceParameters();
		
		//Test existence of xsd param
		if(resources.containsKey("xsd") && resources.containsKey("xslt")){
			try {
				//Text presence of xsd in DB
				String xsduuid = resources.get("xsd");
				if(xsdDAO.read(xsduuid) == null){
					response.setStatus(HTTPResponseStatus.S404);
				}else{
					String content = request.getResourceParameters().get("xslt");
					String insUuid = xsltDAO.create(content);
					xsltDAO.setXSD(insUuid, xsduuid);
					response = HTMLHelper.printPostSuccess(response, request.getResourceName(), insUuid);
				}
			} catch (Exception e) {
				response.setStatus(HTTPResponseStatus.S404);
				response = HTMLHelper.printError(response);
			}
		}else{
			response.setStatus(HTTPResponseStatus.S400);
			response = HTMLHelper.printError(response);
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

			if (this.xsltDAO.read(uuid) != null) {
				response.setContent(this.xsltDAO.read(uuid));
				response.setStatus(HTTPResponseStatus.S200);
			} else {
				response.setStatus(HTTPResponseStatus.S404);
				response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(),
						MIME.TEXT_HTML.getMime());
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

}
