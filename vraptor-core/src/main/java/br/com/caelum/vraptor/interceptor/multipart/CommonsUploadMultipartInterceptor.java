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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.http.InvalidParameterException;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.interceptor.ResourceLookupInterceptor;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.validator.Validations;

import com.google.common.base.Strings;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

/**
 * A multipart interceptor based on Apache Commons Upload. Provided parameters are injected through
 * RequestParameters.set and uploaded files are made available through
 * 
 * @author Guilherme Silveira
 * @author Otávio Scherer Garcia
 */
@Intercepts(before = ResourceLookupInterceptor.class, after = {})
@RequestScoped
public class CommonsUploadMultipartInterceptor
    implements MultipartInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(CommonsUploadMultipartInterceptor.class);

    private final HttpServletRequest request;
    private final MutableRequest parameters;
    private final MultipartConfig config;
    private final Validator validator;
    private final ServletFileUploadCreator fileUploadCreator;

    final Multiset<String> indexes = HashMultiset.create();

    public CommonsUploadMultipartInterceptor(HttpServletRequest request, MutableRequest parameters, MultipartConfig cfg,
            Validator validator, ServletFileUploadCreator fileUploadCreator) {
        this.request = request;
        this.parameters = parameters;
        this.validator = validator;
        this.config = cfg;
        this.fileUploadCreator = fileUploadCreator;
    }

    /**
     * Will intercept the request if apache file upload says that this request is multipart
     */
    public boolean accepts(ResourceMethod method) {
        return ServletFileUpload.isMultipartContent(request);
    }

    public void intercept(InterceptorStack stack, ResourceMethod method, Object instance)
        throws InterceptionException {
        logger.info("Request contains multipart data. Try to parse with commons-upload.");

        FileItemFactory factory = createFactoryForDiskBasedFileItems(config.getDirectory());

        ServletFileUpload uploader = fileUploadCreator.create(factory);
        uploader.setSizeMax(config.getSizeLimit());

        try {
            final List<FileItem> items = uploader.parseRequest(request);
            logger.debug("Found {} attributes in the multipart form submission. Parsing them.", items.size());

            final Multimap<String, String> params = LinkedListMultimap.create();

            for (FileItem item : items) {
                String name = item.getFieldName();
                name = fixIndexedParameters(name);
                
                if (item.isFormField()) {
                    logger.debug("{} is a field", name);
                    params.put(name, getValue(item));

                } else if (isNotEmpty(item)) {
                    logger.debug("{} is a file", name);
                    processFile(item, name);

                } else {
                    logger.debug("A file field was empty: {}", item.getFieldName());
                }
            }

            for (String paramName : params.keySet()) {
                Collection<String> paramValues = params.get(paramName);
                parameters.setParameter(paramName, paramValues.toArray(new String[paramValues.size()]));
            }

        } catch (final SizeLimitExceededException e) {
            reportSizeLimitExceeded(e);

        } catch (FileUploadException e) {
            logger.warn("There was some problem parsing this multipart request, "
                    + "or someone is not sending a RFC1867 compatible multipart request.", e);
        }

        stack.next(method, instance);
    }

    private boolean isNotEmpty(FileItem item) {
        return item.getName().length() > 0;
    }

    /**
     * This method is called when the {@link SizeLimitExceededException} was thrown. By default, add the key
     * file.limit.exceeded using {@link Validations}.
     * 
     * @param e
     */
    protected void reportSizeLimitExceeded(final SizeLimitExceededException e) {
        validator.checking(new Validations() {
            {
                that(false, "upload", "file.limit.exceeded", e.getActualSize(), e.getPermittedSize());
            }
        });

        logger.warn("The file size limit was exceeded.", e);
    }

    protected void processFile(FileItem item, String name) {
        try {
            UploadedFile upload = new DefaultUploadedFile(item.getInputStream(), item.getName(), item.getContentType());
            parameters.setParameter(name, name);
            request.setAttribute(name, upload);

            logger.debug("Uploaded file: {} with {}", name, upload);
        } catch (IOException e) {
            throw new InvalidParameterException("Cant parse uploaded file " + item.getName(), e);
        }
    }

    protected FileItemFactory createFactoryForDiskBasedFileItems(File temporaryDirectory) {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setRepository(temporaryDirectory);

        logger.debug("Using repository {} for file upload", factory.getRepository());
        return factory;
    }

    protected String getValue(FileItem item) {
        String encoding = request.getCharacterEncoding();
        if (!Strings.isNullOrEmpty(encoding)) {
            try {
                return item.getString(encoding);
            } catch (UnsupportedEncodingException e) {
                logger.warn("Request have an invalid encoding. Ignoring it");
            }
        }
        return item.getString();
    }

    protected String fixIndexedParameters(String name) {
        if (name.contains("[]")) {
            String newName = name.replace("[]", "[" + (indexes.count(name)) + "]");
            indexes.add(name);
            logger.debug("{} was renamed to {}", name, newName);
            name = newName;
        }
        return name;
    }
}