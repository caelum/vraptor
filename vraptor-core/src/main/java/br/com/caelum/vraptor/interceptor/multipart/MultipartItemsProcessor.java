package br.com.caelum.vraptor.interceptor.multipart;

import java.io.File;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.http.InvalidParameterException;
import br.com.caelum.vraptor.http.MutableRequest;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

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
    	Multimap<String, String> params = LinkedListMultimap.create();
        for (FileItem item : items) {
            if (item.isFormField()) {
                params.put(item.getFieldName(), item.getString());
                continue;
            }
            if (notEmpty(item)) {
                try {
                    // TODO concurrency issues?
                    File file = File.createTempFile("raptor.", ".upload");
                    file.deleteOnExit();
                    item.write(file);

                	// TODO if(item.isInMemory), should create InMemoryUploadedFile
                    UploadedFile fileInformation = new DefaultUploadedFile(file, item.getName(), item.getContentType());
                    parameters.setParameter(item.getFieldName(), file.getAbsolutePath());
                    request.setAttribute(file.getAbsolutePath(), fileInformation);

                    logger.debug("Uploaded file: " + item.getFieldName() + " with " + fileInformation);
                } catch (Exception e) {
                    throw new InvalidParameterException("Cant parse uploaded file " + item.getName(), e);
                }
            } else {
                logger.debug("A file field was empty: " + item.getFieldName());
            }
        }
        for (String paramName : params.keySet()) {
			Collection<String> paramValues = params.get(paramName);
			parameters.setParameter(paramName, paramValues.toArray(new String[paramValues.size()]));
		}
    }

    private static boolean notEmpty(FileItem item) {
        return !item.getName().trim().equals("");
    }
}