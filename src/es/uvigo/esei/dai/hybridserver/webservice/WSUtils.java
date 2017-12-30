package es.uvigo.esei.dai.hybridserver.webservice;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import es.uvigo.esei.dai.hybridserver.ServerConfiguration;

public class WSUtils {

	private static HSWebService getWebService(ServerConfiguration sconf)
			throws MalformedURLException {
		URL url = new URL(sconf.getWsdl());
		QName qname = new QName(sconf.getNamespace(), sconf.getService());

		try {
			Service service = Service.create(url, qname);
			HSWebService webService = service.getPort(HSWebService.class);
			return webService;
		} catch (Exception e) {
			System.out.println("SWCreationFail: " + e.getMessage());
			return null;
		}
	}

	public static List<HSWebService> getAvaliableWS(
			List<ServerConfiguration> configs) {
		List<HSWebService> services = new ArrayList<>();
		for (ServerConfiguration serverConfiguration : configs) {
			try {
				services.add(getWebService(serverConfiguration));
			} catch (Exception e) {
				System.out.println("---->WS: " + serverConfiguration.getName()
						+ " not avaliable.");
			}
		}
		return services;
	}

	public static List<String> listResource(List<ServerConfiguration> configs,
			String resource) throws Exception {
		List<String> toret = null;
		List<HSWebService> services = getAvaliableWS(configs);

		if (!services.isEmpty()) {
			Iterator<HSWebService> iterator = services.iterator();
			boolean resFound = false;

			while (iterator.hasNext() && !resFound) {
				HSWebService service = iterator.next();
				try {
					switch (resource) {
					case "html":
						toret = service.listHTML();
						break;
					case "xml":
						toret = service.listXML();
						break;
					case "xsd":
						toret = service.listXSD();
						break;
					case "xslt":
						toret = service.listXSLT();
						break;
					default:
						throw new Exception("Unsupported resource");
					}
					resFound = true;
				} catch (Exception e) {
					resFound = false;
				}
			}
		}
		
		return toret;
	}

	public String getResource(List<ServerConfiguration> configs,
			String resource, String uuid) throws Exception {
		String toret = null;
		List<HSWebService> services = getAvaliableWS(configs);

		if (!services.isEmpty()) {
			Iterator<HSWebService> iterator = services.iterator();
			boolean resFound = false;

			while (iterator.hasNext() && !resFound) {
				HSWebService service = iterator.next();
				try {
					switch (resource) {
					case "html":
						toret = service.getHTML(uuid);
						break;
					case "xml":
						toret = service.getXML(uuid);
						break;
					case "xsd":
						toret = service.getXSD(uuid);
						break;
					case "xslt":
						toret = service.getXSLT(uuid);
						break;
					default:
						throw new Exception("Unsupported resource");
					}
					resFound = true;
				} catch (Exception e) {
					resFound = false;
				}
			}
		}
		
		return toret;
	}

	public String getXSDUUID(List<ServerConfiguration> configs, String xsltuuid){
		String toret = null;
		List<HSWebService> services = getAvaliableWS(configs);

		if (!services.isEmpty()) {
			Iterator<HSWebService> iterator = services.iterator();
			boolean resFound = false;

			while (iterator.hasNext() && !resFound) {
				HSWebService service = iterator.next();
				try {
					toret = service.getXSDUUID(xsltuuid);
					resFound = true;
				} catch (Exception e) {
					resFound = false;
				}
			}
		}
		
		return toret;
	}
}
