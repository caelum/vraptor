package br.com.caelum.vraptor.interceptor.multipart;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.http.RequestParameters;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class MultipartInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(MultipartInterceptor.class);

    private final File temporaryDirectory;

    private final long sizeLimit;

    private final HttpServletRequest request;

    private final RequestParameters parameters;

    public MultipartInterceptor(HttpServletRequest request, RequestParameters parameters) throws IOException {
        this.request = request;
        this.parameters = parameters;
        this.sizeLimit = 2 * 1024 * 1024;
        // this directory must be configurable through the properties
        this.temporaryDirectory = File.createTempFile("raptor.", ".upload").getParentFile();
    }

    public void intercept(InterceptorStack stack, ResourceMethod method, Object instance) throws InterceptionException, IOException {

        logger.debug("Trying to parse multipart request.");

        DiskFileItemFactory factory = createFactoryForDiskBasedFileItems();

        ServletFileUpload fileUploadHandler = new ServletFileUpload(factory);

        fileUploadHandler.setSizeMax(sizeLimit);

        List<FileItem> fileItems;
        try {
            fileItems = fileUploadHandler.parseRequest(request);
        } catch (FileUploadException e) {
            logger.warn("There was some problem parsing this multipart request, "
                    + "or someone is not sending a RFC1867 compatible multipart request.", e);
            stack.next(method, instance);
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Found [" + fileItems.size() + "] attributes in the multipart form submission. Parsing them.");
        }

        new MultipartItemsProcessor(fileItems, request, parameters).process();

        stack.next(method, instance);

        // TODO should we delete the temporary files afterwards or onExit as
        // done by
        // now?maybe also a config in .properties

    }

    private DiskFileItemFactory createFactoryForDiskBasedFileItems() {
        DiskFileItemFactory factory = new DiskFileItemFactory(4096 * 16, this.temporaryDirectory);
        logger.debug("Using repository [" + factory.getRepository() + "]");
        return factory;
    }

    public boolean accepts(ResourceMethod method) {
        return ServletFileUpload.isMultipartContent(request);
    }

}
