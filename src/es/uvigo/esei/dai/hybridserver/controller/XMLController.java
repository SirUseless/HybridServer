package es.uvigo.esei.dai.hybridserver.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import es.uvigo.esei.dai.hybridserver.Configuration;
import es.uvigo.esei.dai.hybridserver.dao.IDocumentDAO;
import es.uvigo.esei.dai.hybridserver.dao.XmlDAODB;
import es.uvigo.esei.dai.hybridserver.dao.XsltDAODB;
import es.uvigo.esei.dai.hybridserver.helpers.HTMLHelper;
import es.uvigo.esei.dai.hybridserver.helpers.WSUtils;
import es.uvigo.esei.dai.hybridserver.helpers.XMLHelper;
import es.uvigo.esei.dai.hybridserver.http.HTTPHeaders;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;
import es.uvigo.esei.dai.hybridserver.http.MIME;

public class XMLController implements Controller {
	private IDocumentDAO xmlDAO;
	private XsltDAODB xsltDAO;

	public XMLController(String url, String user, String password)
			throws ClassNotFoundException {
		xmlDAO = new XmlDAODB(url, user, password);
		xsltDAO = new XsltDAODB(url, user, password);
	}

	@Override
	public HTTPResponse list(HTTPRequest request, HTTPResponse response,
			Configuration cfg) {
		response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(),
				MIME.TEXT_HTML.getMime());
		try {
			Map<UUID, String> list = this.xmlDAO.list();
			Map<String, List<String>> serverList = WSUtils.listResource(
					cfg.getServers(), request.getResourceName());

			response.setStatus(HTTPResponseStatus.S200);
			response = HTMLHelper.printList(response,
					request.getResourceName(), list, serverList);
		} catch (Exception e) {
			response.setStatus(HTTPResponseStatus.S500);
			response = HTMLHelper.printError(response, e.getMessage());
		}

		return response;
	}

	@Override
	public HTTPResponse get(HTTPRequest request, HTTPResponse response,
			Configuration cfg) {
		response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(),
				MIME.APPLICATION_XML.getMime());
		String uuid = request.getResourceParameters().get("uuid");
		String xsltUuid = request.getResourceParameters().get("xslt");
		String xsdUuid = null;
		String content = null;

		boolean transform = request.getResourceParameters().containsKey("xslt");
		String xsdPath = "http://127.0.0.1:" + cfg.getHttpPort() + "/xsd?uuid=";
		String xmlPath = "http://127.0.0.1:" + cfg.getHttpPort() + "/xml?uuid="
				+ uuid;
		String xsltPath = "http://127.0.0.1:" + cfg.getHttpPort()
				+ "/xslt?uuid=" + request.getResourceParameters().get("xslt");

		
		try {
			// Test if uuid is valid
			UUID.fromString(uuid);

			// 1.Retrieve xsd if transform
			if (transform) {

				if (this.xsltDAO.getXSD(xsltUuid) != null) {
					xsdUuid = this.xsltDAO.getXSD(xsltUuid);
				} else if (WSUtils.getXSDUUID(cfg.getServers(), xsltUuid) != null) {
					xsdUuid = WSUtils.getXSDUUID(cfg.getServers(), xsltUuid);
				} else {
					response.setStatus(HTTPResponseStatus.S404);
					response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(),
							MIME.TEXT_HTML.getMime());
					response = HTMLHelper.printError(response);
				}
			}

			// 2.Get XML content
			if (this.xmlDAO.read(uuid) != null) {
				content = this.xmlDAO.read(uuid);
				// response.setContent(this.xmlDAO.read(uuid));
				response.setStatus(HTTPResponseStatus.S200);
			} else if (WSUtils.getResource(cfg.getServers(),
					request.getResourceName(), uuid) != null) {
				content = WSUtils.getResource(cfg.getServers(),
						request.getResourceName(), uuid);
				// response.setContent(WSUtils.getResource(cfg.getServers(),
				// request.getResourceName(), uuid));
				response.setStatus(HTTPResponseStatus.S200);
			} else {
				response.setStatus(HTTPResponseStatus.S404);
				response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(),
						MIME.TEXT_HTML.getMime());
				response = HTMLHelper.printError(response);
			}

			if (transform && content != null) {
				// 3.Validate with xsd
				if (XMLHelper.checkWithXSDFromURI(xmlPath, xsdPath + xsdUuid)) {
					// 4.Get XSLT && transform
					content = XMLHelper.transformWithXSLTfromURI(xmlPath,
							xsltPath);
					response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(),
							MIME.TEXT_HTML.getMime());
				} else {
					response.setStatus(HTTPResponseStatus.S404);
					response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(),
							MIME.TEXT_HTML.getMime());
					response = HTMLHelper.printError(response);
				}
			}

			if (content != null) {
				response.setContent(content);
			}

		} catch (Exception e) {
			response.setStatus(HTTPResponseStatus.S400);
			response = HTMLHelper.printError(response, e.getMessage());
		}

		return response;
	}

	@Override
	public HTTPResponse post(HTTPRequest request, HTTPResponse response) {
		Map<String, String> resources = request.getResourceParameters();

		if (resources.containsKey("xml")) {
			try {
				String uuid = this.xmlDAO.create(resources.get("xml"));
				response = HTMLHelper.printPostSuccess(response,
						request.getResourceName(), uuid);
				response.setStatus(HTTPResponseStatus.S200);
			} catch (Exception e) {
				response.setStatus(HTTPResponseStatus.S500);
				response.putParameter(HTTPHeaders.CONTENT_TYPE.getHeader(),
						MIME.TEXT_HTML.getMime());
				response = HTMLHelper.printError(response);
			}
		} else {
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
				response = HTMLHelper.printError(response,
						"Missing UUID parameter");
			}
		}

		return response;
	}

}
