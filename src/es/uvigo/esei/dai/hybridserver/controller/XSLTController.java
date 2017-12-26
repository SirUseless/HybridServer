package es.uvigo.esei.dai.hybridserver.controller;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

import es.uvigo.esei.dai.hybridserver.dao.IDocumentDAO;
import es.uvigo.esei.dai.hybridserver.dao.XsdDAODB;
import es.uvigo.esei.dai.hybridserver.dao.XsltDAODB;
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
		response.setStatus(HTTPResponseStatus.S200);
		response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(),
				MIME.TEXT_HTML.getMime());
		String content = "<html><h1>Hybrid Server</h1><h2>Adrian Simon Reboredo</h2><h2>Josue Pato Valcarcel</h2>";
		content = content.concat("<h3>Database Connection Status:&nbsp;");
		if (this.xsdDAO.isAvaliable()) {
			content = content
					.concat("<span style=\"color: green;\">CONNECTED</span>");
		} else {
			content = content
					.concat("<span style=\"color: red;\">NOT CONNECTED</span>");
		}
		content = content.concat("</h3><ul>");

		try {
			Map<UUID, String> db = this.xsltDAO.list();
			for (Map.Entry<UUID, String> row : db.entrySet()) {
				content = content.concat("<li>");
				content = content.concat("<a href=\""
						+ request.getResourceName() + "?uuid="
						+ row.getKey().toString() + "\">");
				content = content.concat(row.getKey().toString());
				content = content.concat("</a></li>");
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}
		content = content.concat("</ul>");
		content = content.concat("</html>");

		response.setContent(content);

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
					response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(),
							MIME.TEXT_HTML.getMime());
					String content = "<html><style>h1{color:red; padding: 300px; }</style><h1>"
							+ response.getStatus()
							+ ": "
							+ response.getStatus().getStatus() + "</h1></html>";
					response.setContent(content);
				}
			} catch (Exception e) {
				response.setStatus(HTTPResponseStatus.S400);
				response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(),
						MIME.TEXT_HTML.getMime());
				String content = "<html><style>h1{color:red; padding: 300px; }</style><h1>"
						+ response.getStatus()
						+ ": "
						+ response.getStatus().getStatus() + "</h1></html>";
				response.setContent(content);
				System.out.println("Delete failed");
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
					response.setContent("<a href=\"xslt?uuid=" + insUuid + "\">" + insUuid
							+ "</a>");
				}
			} catch (Exception e) {
				e.printStackTrace();
				//404 
				response.setStatus(HTTPResponseStatus.S404);
			}
		}else{
			//400
			response.setStatus(HTTPResponseStatus.S400);
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
				String content = "<html><style>h1{color:red; padding: 300px; }</style><h1>"
						+ response.getStatus()
						+ ": "
						+ response.getStatus().getStatus() + "</h1></html>";
				response.setContent(content);
			}
		} catch (Exception e) {
			response.setStatus(HTTPResponseStatus.S404);
			response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(),
					MIME.TEXT_HTML.getMime());
			String content = "<html><style>h1{color:red; padding: 300px; }</style><h1>"
					+ response.getStatus()
					+ ": "
					+ response.getStatus().getStatus() + "</h1></html>";
			response.setContent(content);
		}

		return response;
	}

}
