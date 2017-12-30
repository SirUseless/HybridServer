package es.uvigo.esei.dai.hybridserver.helpers;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import es.uvigo.esei.dai.hybridserver.HybridServerService;
import es.uvigo.esei.dai.hybridserver.ServerConfiguration;

public class WSUtils {

	private static HybridServerService getWebService(ServerConfiguration sconf)
			throws Exception {
		URL url = new URL(sconf.getWsdl());
		QName qname = new QName(sconf.getNamespace(), sconf.getService());

		try {
			Service service = Service.create(url, qname);
			HybridServerService webService = service.getPort(HybridServerService.class);
			return webService;
		} catch (Exception e) {
			//System.out.println("WSCreationFail: " + e.getMessage());
			throw new Exception("WS Creation Fail");
		}
	}

	public static List<HybridServerService> getAvaliableWS(
			List<ServerConfiguration> configs) {
		List<HybridServerService> services = new ArrayList<>();
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

	public static Map<String, List<String>> listResource(List<ServerConfiguration> configs,
			String resource){
		Map<String, List<String>> toret = new HashMap<>();
		List<HybridServerService> services = getAvaliableWS(configs);

		if (!services.isEmpty()) {
			Iterator<HybridServerService> iterator = services.iterator();
			int index = 0;

			while (iterator.hasNext()) {
				HybridServerService service = iterator.next();
				List<String> resList = new ArrayList<>();
				try {
					switch (resource) {
					case "html":
						resList = service.listHTML();
						break;
					case "xml":
						resList = service.listXML();
						break;
					case "xsd":
						resList = service.listXSD();
						break;
					case "xslt":
						resList = service.listXSLT();
						break;
					default:
						throw new Exception("Unsupported resource");
					}
					
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				toret.put(configs.get(index).getName(), resList);
				index++;
			}
		}
		
		return toret;
	}

	public static String getResource(List<ServerConfiguration> configs,
			String resource, String uuid) throws Exception {
		String toret = null;
		List<HybridServerService> services = getAvaliableWS(configs);

		if (!services.isEmpty()) {
			Iterator<HybridServerService> iterator = services.iterator();
			boolean resFound = false;

			while (iterator.hasNext() && !resFound) {
				HybridServerService service = iterator.next();
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
					if(toret != null){
						resFound = true;
					}
				} catch (Exception e) {
					resFound = false;
				}
			}
		}
		return toret;
	}

	public static String getXSDUUID(List<ServerConfiguration> configs, String xsltuuid){
		String toret = null;
		List<HybridServerService> services = getAvaliableWS(configs);

		if (!services.isEmpty()) {
			Iterator<HybridServerService> iterator = services.iterator();
			boolean resFound = false;

			while (iterator.hasNext() && !resFound) {
				HybridServerService service = iterator.next();
				try {
					toret = service.getXSDUUID(xsltuuid);
					if(toret != null){
						resFound = true;
					}
				} catch (Exception e) {
					resFound = false;
				}
			}
		}
		
		return toret;
	}
}
