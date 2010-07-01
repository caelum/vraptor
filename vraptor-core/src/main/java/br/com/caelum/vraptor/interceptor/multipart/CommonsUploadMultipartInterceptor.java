/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.caelum.vraptor.interceptor.multipart;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.validator.Validations;

/**
 * A multipart interceptor based on Apache Commons Upload.
 * Provided parameters are injected through RequestParameters.set and uploaded files are made available through
 *
 * @author Guilherme Silveira
 */
@RequestScoped
public class CommonsUploadMultipartInterceptor implements MultipartInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(CommonsUploadMultipartInterceptor.class);

    private final long sizeLimit;

    private final HttpServletRequest request;

    private final MutableRequest parameters;

    private final MultipartConfig config;

    private final Validator validator;

    public CommonsUploadMultipartInterceptor(HttpServletRequest request, MutableRequest parameters,
    		MultipartConfig config, Validator validator) {
        this.request = request;
        this.parameters = parameters;
        this.validator = validator;
        this.sizeLimit = config.getSizeLimit();
        this.config = config;
    }

    @SuppressWarnings("unchecked")
    public void intercept(InterceptorStack stack, ResourceMethod method, Object instance)
        throws InterceptionException {
        logger.debug("Trying to parse multipart request.");

        FileItemFactory factory = createFactoryForDiskBasedFileItems(config.getDirectory());

        ServletFileUpload fileUploadHandler = new ServletFileUpload(factory);
        fileUploadHandler.setSizeMax(sizeLimit);

        try {
            List<FileItem> fileItems = fileUploadHandler.parseRequest(request);
            logger.debug("Found {} attributes in the multipart form submission. Parsing them.", fileItems.size());

            new MultipartItemsProcessor(fileItems, request, parameters).process();
        } catch (final SizeLimitExceededException e) {
            validator.checking(new Validations() {
                {
                    that(false, "upload", "file.limit.exceeded", e.getActualSize(), e.getPermittedSize());
                }
            });
            logger.warn("The file size limit was exceeded.", e);

        } catch (FileUploadException e) {
            logger.warn("There was some problem parsing this multipart request, "
                    + "or someone is not sending a RFC1867 compatible multipart request.", e);
        }

        // go to next hope
        stack.next(method, instance);

        // TODO should we delete the temporary files afterwards or onExit as
        // done by now? maybe also a config in .properties
    }

    protected FileItemFactory createFactoryForDiskBasedFileItems(File temporaryDirectory) {
        // TODO: may use all memory (Integer.MAX_VALUE), based on a config!
        // this is mandatory for environments as Google App Engine
        int thresholdForUsingDiskInsteadOfMemory = (int) sizeLimit;
        DiskFileItemFactory factory = new DiskFileItemFactory(thresholdForUsingDiskInsteadOfMemory, temporaryDirectory);
        logger.debug("Using repository {} for file upload", factory.getRepository());
        return factory;
    }

    /**
     * Will intercept the request if apache file upload says that this request is multipart
     */
    public boolean accepts(ResourceMethod method) {
        return FileUploadBase.isMultipartContent(new ServletRequestContext(request));
    }

}
