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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.validator.ValidationMessage;

import com.google.common.base.Strings;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;

/**
 * <p>
 * A multipart interceptor based on Servlet3 File Upload. It's only enabled if you're in Servlet 3 container.
 * </p>
 * <p>
 * If you're using servlet3 upload features, the {@link MultipartConfig#getDirectory()} has no effect.
 * </p>
 * 
 * @author Otávio Scherer Garcia
 * @since 3.2
 * @see DefaultMultipartConfig
 * @see NullMultipartInterceptor
 * @see MultipartInterceptor
 */

@Intercepts
@RequestScoped
public class Servlet3MultipartInterceptor
    implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(Servlet3MultipartInterceptor.class);

    private static final Pattern EXTRACT_FILENAME = Pattern.compile("(.*)filename=\"(.*)\"");

    public static final String ACCEPT_MULTIPART = "multipart/form-data";
    public static final String CONTENT_DISPOSITION_KEY = "content-disposition";

    private final HttpServletRequest request;
    private final MutableRequest parameters;
    private final Validator validator;
    private final MultipartConfig cfg;

    public Servlet3MultipartInterceptor(HttpServletRequest request, MutableRequest parameters, Validator validator, MultipartConfig cfg) {
        this.request = request;
        this.parameters = parameters;
        this.validator = validator;
        this.cfg = cfg;
    }

    /**
     * Only accept requests that contains multipart headers.
     */
    public boolean accepts(ResourceMethod method) {
        String contentType = request.getContentType();
        return contentType != null && contentType.startsWith(ACCEPT_MULTIPART);
    }

    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
        throws InterceptionException {
        logger.debug("Request contains multipart data. Try to parse with Servlet3 Part");

        final Multimap<String, String> params = LinkedListMultimap.create();

        try {
            for (Part part : getParts(request)) {
                String name = part.getName();

                if (isField(part)) {
                    logger.debug("{} is a field", name);
                    params.put(name, getStringValue(part));

                } else if (isNotEmpty(part)) {
                    logger.debug("{} is a file", name);

                    String fileName = getFileName(part);
                    UploadedFile upload = new DefaultUploadedFile(part.getInputStream(), fileName, part.getContentType());

                    parameters.setParameter(part.getName(), fileName);
                    request.setAttribute(fileName, upload);
                } else {
                    logger.warn("{} is an empty file", name);
                }
            }
        } catch (IOException e) {
            throw new InterceptionException(e);
        }

        for (String paramName : params.keySet()) {
            Collection<String> paramValues = params.get(paramName);
            parameters.setParameter(paramName, paramValues.toArray(new String[paramValues.size()]));
        }

        stack.next(method, resourceInstance);
    }

    /**
     * Gets all the Part components of this request, provided that it is of type multipart/form-data. If an
     * IllegalStateException occurs (the max file size is reached) this method adds a message in validator.
     */
    protected Collection<Part> getParts(HttpServletRequest request) {
        try {
            checkMaxSize();
            return request.getParts();

        } catch (IllegalStateException e) {
            logger.warn("The file size limit was exceeded.", e);
            validator.add(new ValidationMessage("upload", "file.limit.exceeded"));

            return Collections.emptyList();

        } catch (Exception e) {
            throw new InterceptionException(e);
        }
    }

    /**
     * By the spec, if the request lengh (not each file) is gt configured max uploaded size we need to throw an
     * {@link IllegalStateException}.
     */
    private void checkMaxSize() {
        int length = request.getContentLength();
        long limit = cfg.getSizeLimit();
        if (length > limit) {
            throw new IllegalStateException("max " + limit + ", actual " + length);
        }
    }

    /**
     * Check if the part has empty content.
     */
    private boolean isNotEmpty(Part part) {
        return part.getSize() > 0;
    }

    /**
     * Returns true if the part is a field, false otherwise.
     */
    private boolean isField(Part part) {
        return Strings.isNullOrEmpty(part.getContentType());
    }

    /**
     * Get the filename of the part. The filename is extracted by header.
     */
    private String getFileName(Part part) {
        String name = part.getHeader(CONTENT_DISPOSITION_KEY);
        return EXTRACT_FILENAME.matcher(name).replaceAll("$2");
    }

    /**
     * Get the content of a part as String.
     */
    private String getStringValue(Part part)
        throws IOException {
        String encoding = request.getCharacterEncoding();

        InputStream in = part.getInputStream();
        byte[] out = ByteStreams.toByteArray(in);
        Closeables.closeQuietly(in);

        if (!Strings.isNullOrEmpty(encoding)) {
            try {
                return new String(out, encoding);
            } catch (UnsupportedEncodingException e) {
                logger.warn("Request have an invalid encoding. Ignoring it");
            }
        }

        return new String(out);
    }
}
