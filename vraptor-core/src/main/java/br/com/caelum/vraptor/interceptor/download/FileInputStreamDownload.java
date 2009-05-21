package br.com.caelum.vraptor.interceptor.download;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

/**
 * @author filipesabella
 */
public class FileInputStreamDownload implements Download {
	private InputStream input;
	private FileDetails details;

	public FileInputStreamDownload(InputStream input, String contentType, String fileName) {
		this(input, contentType, fileName, false);
	}
	public FileInputStreamDownload(InputStream input, String contentType, String fileName, boolean doDownload) {
		this.input = input;
		this.details = new FileDetails(contentType, fileName, doDownload);
	}

	public void write(HttpServletResponse response) throws IOException {
		details.write(response);

		OutputStream output = response.getOutputStream();
		int read = 0;
		while((read = input.read()) > -1) {
			output.write(read);
		}
	}
}
