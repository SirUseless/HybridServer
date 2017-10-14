package es.uvigo.esei.dai.hybridserver.http;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class HTTPParser {
	public static final String CONTENT_LENGTH = "Content-Length";

	@Deprecated
	public static HTTPRequestMethod parseMethod(String method) {
		switch (method.toUpperCase()) {
		case "HEAD":
			return HTTPRequestMethod.HEAD;

		case "GET":
			return HTTPRequestMethod.GET;

		case "POST":
			return HTTPRequestMethod.POST;

		case "PUT":
			return HTTPRequestMethod.PUT;

		case "DELETE":
			return HTTPRequestMethod.DELETE;

		case "TRACE":
			return HTTPRequestMethod.TRACE;

		case "OPTIONS":
			return HTTPRequestMethod.OPTIONS;

		case "CONNECT":
			return HTTPRequestMethod.CONNECT;

		default:
			return null;
		}
	}

	@Deprecated
	public static String parseResourceName(String resourceChain) {
		return resourceChain.split(Pattern.quote("?"))[0].substring(1);

	}

	public static String[] parseResourcePath(String resourceChain) {
		String resName = HTTPParser.parseResourceName(resourceChain);

		String[] aux = resName.split("/");

		if (aux[0].isEmpty()) {
			return new String[0];
		} else {
			return aux;
		}
	}

	public static Map<String, String> parseResourceParameters(
			String resourceChain, String content) {
		Map<String, String> toret = new LinkedHashMap<String, String>();

		try {
			String parameterChain[] = resourceChain.split(Pattern.quote("?"))[1]
					.split(Pattern.quote("&"));

			for (String parameter : parameterChain) {
				String[] aux = parameter.split("=");
				toret.put(aux[0], aux[1]);
			}

		} catch (IndexOutOfBoundsException e) {
		}

		try {
			String contentChain[] = content.split(Pattern.quote("&"));

			for (String cont : contentChain) {
				String[] aux = cont.split("=");
				toret.put(aux[0], aux[1]);
			}
		} catch (NullPointerException e) {
		}

		return toret;
	}

	public static Map<String, String> parseHeaderParameters(
			Map<Integer, String> headerParams) {
		Map<String, String> toret = new LinkedHashMap<String, String>();

		for (Map.Entry<Integer, String> parameter : headerParams.entrySet()) {
			String[] aux = parameter.getValue().split(": ");
			toret.put(aux[0], aux[1]);
		}

		return toret;
	}

	public static String parseContent(Map<Integer, String> contentParams) {
		if (!contentParams.isEmpty()) {
			String toret = "";

			for (Map.Entry<Integer, String> parameter : contentParams
					.entrySet()) {
				String aux = parameter.getValue().toString();
				toret = toret.concat(aux);
			}

			return toret;
		} else {
			return null;
		}

	}

	public static int parseContentLength(Map<String, String> headerParameters) {
		if (headerParameters.containsKey(HTTPParser.CONTENT_LENGTH)) {
			String strl = (String) headerParameters
					.get(HTTPParser.CONTENT_LENGTH);
			return Integer.parseInt(strl);
		} else {
			return 0;
		}

	}

}
