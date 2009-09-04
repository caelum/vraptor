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
     * @throw {@link IllegalArgumentException} if file is in memory
     * @return Returns the file.
     */
    File getFile();

    /**
     * TODO: redundant? same as getFile().getName() ?
     *
     * @throw {@link IllegalArgumentException} if file is in memory
     * @return Returns the fileName as in the server filesystem.
     */
    String getFileName();

    /**
     * The complete file name from this file, as it was uploaded from the
     * client.
     *
     * @return Returns the fileName.
     */
    String getCompleteFileName();

    // TODO: byte[] getContent
    // TODO: boolean isInMemory
}
