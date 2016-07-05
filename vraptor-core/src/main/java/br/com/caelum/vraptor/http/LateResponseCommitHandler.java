package br.com.caelum.vraptor.http;

import static com.google.common.base.Preconditions.checkState;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.ioc.Component;

@Component
public class LateResponseCommitHandler {

	private final HttpServletResponse response;
	
	private ByteArrayOutputStream stream;
	private PrintWriter writer;
	
	public LateResponseCommitHandler(HttpServletResponse response) {
		this.response = response;
	}
	
	public void commit() throws IOException {
		if (stream != null) {
			writer.flush();
			if (stream.size() > 0) {
				stream.writeTo(response.getOutputStream());
				writer.close();
			}
		}
	}

	public void writeJson(String data) {
		write(data);
		response.setContentType("application/json");
	}
	
	private void write(String data) {
		checkState(stream == null, "response data was already wrote");
		stream = new ByteArrayOutputStream(response.getBufferSize());
		writer = new PrintWriter(stream);
		writer.write(data);
	}
}
