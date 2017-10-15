package es.uvigo.esei.dai.hybridserver.http;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HTTPResponse {
	private HTTPResponseStatus status;
	private String version;
	private String content;
	private Map<String, String> parameters;

	public HTTPResponse() {
		this.parameters = new LinkedHashMap<String, String>();
	}

	public HTTPResponseStatus getStatus() {
		return this.status;
	}

	public void setStatus(HTTPResponseStatus status) {
		this.status = status;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Map<String, String> getParameters() {
		return this.parameters;
	}

	public String putParameter(String name, String value) {
		return this.parameters.put(name, value);
	}

	public boolean containsParameter(String name) {
		return this.parameters.containsKey(name);
	}

	public String removeParameter(String name) {
		return this.parameters.remove(name);
	}

	public void clearParameters() {
		this.parameters.clear();
	}

	public List<String> listParameters() {
		return (List<String>) this.parameters.values();
	}

	public void print(Writer writer) throws IOException {
		// Version, code, status
		writer.write(this.getVersion() + " ");
		writer.write(this.status.getCode() + " ");
		writer.write(this.status.getStatus() + "\r\n");

		// Parameters
		if (!this.parameters.equals(null)) {
			for (Map.Entry<String, String> parameter : this.parameters
					.entrySet()) {
				writer.write(parameter.getKey() + ": " + parameter.getValue()
						+ "\r\n");
			}
			if (this.content != null && !this.content.isEmpty()) {
				writer.write(HTTPHeaders.CONTENT_LENGTH.getHeader() + ": "
						+ content.length() + "\r\n");
			}
			writer.write("\r\n");
		}

		// Content
		if (this.content != null && !this.content.isEmpty()) {
			writer.write(this.content);
		}
	}

	@Override
	public String toString() {
		final StringWriter writer = new StringWriter();

		try {
			this.print(writer);
		} catch (IOException e) {
		}

		return writer.toString();
	}
}
