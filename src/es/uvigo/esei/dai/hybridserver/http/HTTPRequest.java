package es.uvigo.esei.dai.hybridserver.http;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.Map;

import es.uvigo.esei.dai.hybridserver.http.HTTPParser;

public class HTTPRequest {
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

			// Parse first line and get method, chain and version
			String line = buffer.readLine();
			String[] components = line.split(" ");

			this.method = HTTPParser.parseMethod(components[0]);
			this.resourceChain = components[1];
			this.resourceName = HTTPParser
					.parseResourceName(this.resourceChain);
			this.resourcePath = HTTPParser
					.parseResourcePath(this.resourceChain);
			this.httpVersion = components[2];

			// Parse header and get parameters
			Map<Integer, String> headerParams = new LinkedHashMap<Integer, String>();
			int count = 0;
			while (!(line = buffer.readLine()).isEmpty()) {
				headerParams.put(count, line);
				count++;
			}
			this.headerParameters = HTTPParser
					.parseHeaderParameters(headerParams);
			this.contentLength = HTTPParser
					.parseContentLength(headerParameters);

			// Parse content
			Map<Integer, String> contentParams = new LinkedHashMap<Integer, String>();
			count = 0;

			while ((line = buffer.readLine()) != null) {
				contentParams.put(count, line);
				count++;
			}
			this.content = HTTPParser.parseContent(contentParams);
			this.resourceParameters = HTTPParser.parseResourceParameters(
					this.resourceChain, this.content);

		} catch (IOException e) {
			throw new IOException(e.getMessage());
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
