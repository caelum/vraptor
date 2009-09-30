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
                    UploadedFile fileInformation = new DefaultUploadedFile(item.getInputStream(), item.getName(), item.getContentType());
                    parameters.setParameter(item.getFieldName(), item.getName());
                    request.setAttribute(item.getName(), fileInformation);

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