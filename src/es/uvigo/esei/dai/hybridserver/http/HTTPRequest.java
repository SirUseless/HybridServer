package es.uvigo.esei.dai.hybridserver.http;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.Reader;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class HTTPRequest {
	private final String UTF8_ENCODING = "UTF-8";
	private HTTPRequestMethod method;
	private String[] resourcePath;
	private String resourceChain;
	private String httpVersion;
	private Map<String, String> resourceParameters = new LinkedHashMap<String, String>();
	private Map<String, String> headerParameters = new LinkedHashMap<String, String>();
	private String resourceName;
	private String content;
	private int contentLength;

	public HTTPRequest(Reader reader) throws IOException, HTTPParseException {

		try (BufferedReader buffer = new BufferedReader(reader)) {
			String firstLine = buffer.readLine();

			String[] components;
			try {
				components = firstLine.split("\\s+");
			} catch (Exception e) {
				throw new HTTPParseException("Missing first line");
			}

			try {
				this.method = HTTPRequestMethod.valueOf(components[0]);
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new HTTPParseException("Missing method");
			} catch (IllegalArgumentException e) {
				throw new HTTPParseException("Invalid method");
			}

			try {
				this.resourceChain = components[1];

				if (this.resourceChain.contains("?")) {
					this.resourceName = this.resourceChain.split(Pattern
							.quote("?"))[0].substring(1);
				} else {
					this.resourceName = this.resourceChain.substring(1);
				}

				if (this.resourceName.contains("/")
						|| !this.resourceName.isEmpty()) {
					this.resourcePath = resourceName.split(Pattern.quote("/"));
				} else {
					this.resourcePath = new String[0];
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new HTTPParseException("Missing resource chain");
			}

			try {
				this.httpVersion = components[2];
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new HTTPParseException("Missing version");
			}

			try {
				String headerLine = "";
				while (!(headerLine = buffer.readLine()).isEmpty()) {
					String[] headerLineComponents = headerLine.split(": ");
					this.headerParameters.put(headerLineComponents[0],
							headerLineComponents[1]);
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new HTTPParseException("Invalid header");
			}

			if (this.headerParameters.containsKey(HTTPHeaders.CONTENT_LENGTH
					.getHeader())) {
				this.contentLength = Integer.parseInt(this.headerParameters
						.get(HTTPHeaders.CONTENT_LENGTH.getHeader()));
			} else {
				this.contentLength = 0;
			}

			String contentLine = "";
			this.content = "";
			while ((contentLine = buffer.readLine()) != null) {
				this.content = this.content.concat(contentLine);
			}
			if (this.content.isEmpty())
				this.content = null;

			// Simple way of taking Content-Length into account
			if (this.content != null
					&& this.content.length() > this.contentLength)
				this.content = this.content.substring(0, contentLength);

			String type = headerParameters.get(HTTPHeaders.CONTENT_TYPE
					.getHeader());

			if (type != null && type.startsWith(MIME.FORM.getMime())) {
				this.content = URLDecoder.decode(this.content,
						this.UTF8_ENCODING);
			}

			if (this.resourceChain.contains("?")) {
				String[] aux = this.resourceChain.split(Pattern.quote("?"));

				if (aux[1].contains("&")) {
					for (String resChainElement : aux[1].split(Pattern
							.quote("&"))) {
						String[] resChainParams = resChainElement.split("=");
						this.resourceParameters.put(resChainParams[0],
								resChainParams[1]);
					}
				} else {
					String[] resChainParams = aux[1].split("=");
					this.resourceParameters.put(resChainParams[0],
							resChainParams[1]);
				}
			}

			if (this.content != null && this.content.contains("&")) {
				String[] contentElements = this.content.split(Pattern
						.quote("&"));
				for (String contentParams : contentElements) {
					String[] contentParam = contentParams.split("=");
					this.resourceParameters.put(contentParam[0],
							contentParam[1]);
				}
			} else if (this.content != null) {
				String[] contentParam = content.split("=");
				this.resourceParameters.put(contentParam[0], contentParam[1]);
			}
		} catch (IOException e) {
			System.out.println("M'ha petao el reader");
		}

	}

	public HTTPRequestMethod getMethod() {
		return this.method;
	}

	public String getResourceChain() {
		return resourceChain;
	}

	public String[] getResourcePath() {
		return resourcePath;
	}

	public String getResourceName() {
		return resourceName;
	}

	public Map<String, String> getResourceParameters() {
		return resourceParameters;
	}

	public String getHttpVersion() {
		return httpVersion;
	}

	public Map<String, String> getHeaderParameters() {
		return headerParameters;
	}

	public String getContent() {
		return content;
	}

	public int getContentLength() {
		return contentLength;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(this.getMethod().name())
				.append(' ').append(this.getResourceChain()).append(' ')
				.append(this.getHttpVersion()).append("\r\n");

		for (Map.Entry<String, String> param : this.getHeaderParameters()
				.entrySet()) {
			sb.append(param.getKey()).append(": ").append(param.getValue())
					.append("\r\n");
		}

		if (this.getContentLength() > 0) {
			sb.append("\r\n").append(this.getContent());
		}

		return sb.toString();
	}

}
