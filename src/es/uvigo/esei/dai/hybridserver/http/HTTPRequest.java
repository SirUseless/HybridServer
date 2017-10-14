package es.uvigo.esei.dai.hybridserver.http;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.Reader;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import es.uvigo.esei.dai.hybridserver.http.HTTPParser;

public class HTTPRequest {
	private final String UTF8_ENCODING = "UTF-8";
	private HTTPRequestMethod method;
	private String[] resourcePath;
	private String resourceChain;
	private String httpVersion;
	private Map<String, String> resourceParameters;
	private Map<String, String> headerParameters;
	private String resourceName;
	private String content;
	private int contentLength;

	public HTTPRequest(Reader reader) throws IOException, HTTPParseException {

		try (BufferedReader buffer = new BufferedReader(reader)) {

			try {
				// Parse first line and get method, chain and version
				String firstLine = buffer.readLine();
				String[] components = firstLine.split("\\s+");

				this.method = HTTPRequestMethod.valueOf(components[0]);
				this.resourceChain = components[1];
				this.resourceName = this.resourceChain
						.split(Pattern.quote("?"))[0].substring(1);
				this.resourcePath = HTTPParser
						.parseResourcePath(this.resourceChain);
				this.httpVersion = components[2];

				// Parse header and get parameters
				Map<Integer, String> headerParams = new LinkedHashMap<Integer, String>();
				int count = 0;
				String headerLine = "";
				// this.resourceParameters = new LinkedHashMap<>();
				while (!(headerLine = buffer.readLine()).isEmpty()) {
					// String[] aux = headerLine.split(": ");
					// this.headerParameters.put(aux[0], aux[1]);
					headerParams.put(count, headerLine);
					count++;
				}
				this.headerParameters = HTTPParser
						.parseHeaderParameters(headerParams);
				this.contentLength = HTTPParser
						.parseContentLength(headerParameters);

				// Parse content
				Map<Integer, String> contentParams = new LinkedHashMap<Integer, String>();
				count = 0;

				while ((firstLine = buffer.readLine()) != null) {
					contentParams.put(count, firstLine);
					count++;
				}
				this.content = HTTPParser.parseContent(contentParams);

				String type = headerParameters.get(HTTPHeaders.CONTENT_TYPE
						.getHeader());

				if (type != null && type.startsWith(MIME.FORM.getMime())) {
					this.content = URLDecoder.decode(this.content,
							this.UTF8_ENCODING);
				}

				this.resourceParameters = HTTPParser.parseResourceParameters(
						this.resourceChain, this.content);
			} catch (Exception e) {
				throw new HTTPParseException("HTTP Request format error.");
			}

		} catch (IOException e) {
			throw e;
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
