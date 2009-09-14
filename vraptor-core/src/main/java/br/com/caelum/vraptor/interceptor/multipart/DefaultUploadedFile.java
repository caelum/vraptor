package br.com.caelum.vraptor.interceptor.multipart;

import java.io.InputStream;

public class DefaultUploadedFile implements UploadedFile {
    private static final String NOT_UNIX_LIKE_SEPARATOR = "\\";

    private static final String UNIX_LIKE_SEPARATOR = "/";

    private final String contentType;

    private final String fileName;

    private final String completeFileName;

	private final InputStream content;

    public DefaultUploadedFile(InputStream content, String completeFileName,
            String contentType) {
        this.content = content;
		// depends upon the UPLOADER operating system, not on File.separator
        // File.separator is the separator for the server machine, not the
        // client, of course
        // TODO: use File methods to get the fileName from the completeFileName?
        if (completeFileName.indexOf(UNIX_LIKE_SEPARATOR) == -1) {
            this.fileName = completeFileName.substring(completeFileName
                    .lastIndexOf(NOT_UNIX_LIKE_SEPARATOR) + 1);
        } else {
            this.fileName = completeFileName.substring(completeFileName
                    .lastIndexOf(UNIX_LIKE_SEPARATOR) + 1);
        }
        this.completeFileName = completeFileName;
        this.contentType = contentType;
    }

    @Override
	public String toString() {
        return "[uploadedFile uploadedCompleteName="
                + this.completeFileName + " uploadedName=" + this.fileName
                + " contentType=" + this.contentType + "]";
    }

    public String getContentType() {
        return this.contentType;
    }

    public InputStream getFile() {
    	return content;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getCompleteFileName() {
        return this.completeFileName;
    }

}
