package br.com.caelum.vraptor.interceptor.multipart;

import br.com.caelum.vraptor.http.InvalidParameterException;
import br.com.caelum.vraptor.http.MutableRequest;
import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;

/**
 * Processes all elements in a multipart request.
 *
 * @author Guilherme Silveira
 * @author Paulo Silveira
 */
class MultipartItemsProcessor {

    private static final Logger logger = LoggerFactory.getLogger(MultipartItemsProcessor.class);
    private final List<FileItem> items;
    private final HttpServletRequest request;
    private final MutableRequest parameters;

    public MultipartItemsProcessor(List<FileItem> items, HttpServletRequest request, MutableRequest parameters) {
        this.items = items;
        this.request = request;
        this.parameters = parameters;
    }

    public void process() {
        for (FileItem item : items) {
            if (item.isFormField()) {
                parameters.setParameter(item.getFieldName(), item.getString());
                continue;
            }
            if (notEmpty(item)) {
                try {
                    // TODO concurrency issues?
                    File file = File.createTempFile("raptor.", ".upload");
                    file.deleteOnExit();
                    item.write(file);
                    UploadedFile fileInformation = new DefaultUploadedFile(file, item.getName(), item.getContentType());
                    parameters.setParameter(item.getFieldName(), file.getAbsolutePath());
                    request.setAttribute(file.getAbsolutePath(), fileInformation);
                    logger.info("Uploaded file: " + item.getFieldName() + " with " + fileInformation);
                } catch (Exception e) {
                    throw new InvalidParameterException("Nasty uploaded file " + item.getName(), e);
                }
            } else {
                logger.info("A file field was empy: " + item.getFieldName());
            }
        }
    }

    private boolean notEmpty(FileItem item) {
        return !item.getName().trim().equals("");
    }
}