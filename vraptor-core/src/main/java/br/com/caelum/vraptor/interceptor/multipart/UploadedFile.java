package br.com.caelum.vraptor.interceptor.multipart;

import java.io.InputStream;

/**
 * An interface which represents the information of an uploaded file.
 *
 * @author Guilherme Silveira
 * @author Paulo Silveira
 * @author Lucas Cavalcanti
 */
public interface UploadedFile {

    /**
     * @return Returns the contentType.
     */
    String getContentType();

    /**
     * @return Returns the contents of uploaded file.
     */
    InputStream getFile();

    /**
     * @return Returns the fileName of the uploaded as it was uploaded from the
     * client
     */
    String getFileName();

}
