package br.com.caelum.vraptor.interceptor.download;

import javax.servlet.http.HttpServletResponse;

/**
 * @author filipesabella
 */
public class FileDetails {
	private String contentType;
	private String fileName;

	private boolean doDownload = false;

	public FileDetails(String contentType, String fileName, boolean doDownload) {
		this.contentType = contentType;
		this.fileName = fileName;
		this.doDownload = doDownload;
	}

	public void write(HttpServletResponse response) {
		if(contentType != null) {
			String header = String.format("%s; filename=%s", doDownload ? "attachment" : "inline", fileName);
			response.setHeader("Content-disposition", header);
		}
	}
}
