package br.com.caelum.vraptor.interceptor.download;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;

import javax.servlet.http.HttpServletResponse;

/**
 * @author filipesabella
 */
public class FileDownload implements Download {
	private File file;
	private InputStream input;
	private FileDetails details;

	public FileDownload(File file, String contentType, String fileName) {
		this(file, contentType, fileName, false);
	}
	public FileDownload(File file, String contentType, String fileName, boolean doDownload) {
		this.file = file;
		this.details = new FileDetails(contentType, fileName, doDownload);
	}

	public void write(HttpServletResponse response) throws IOException {
		details.write(response);
		new FileInputStream(file).getChannel().transferTo(0, file.length(), Channels.newChannel(response.getOutputStream()));
	}
}
