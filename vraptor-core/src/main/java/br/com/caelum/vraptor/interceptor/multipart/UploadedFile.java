package br.com.caelum.vraptor.interceptor.multipart;

import java.io.File;

/**
 * An interface which represents the information of an uploaded file.
 * 
 * @author Guilherme Silveira
 * @author Paulo Silveira
 */
public interface UploadedFile {

    /**
     * @return Returns the contentType.
     */
    String getContentType();

    /**
     * @return Returns the file.
     */
    File getFile();

    /**
     * 
     * @return Returns the fileName.
     */
    String getFileName();

    /**
     * The complete file name from this file, as it was uploaded from the
     * client.
     * 
     * @return Returns the fileName.
     */
    String getCompleteFileName();

}
