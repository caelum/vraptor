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


import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.converter.ConversionError;

/**
 * A converter capable of setting UploadedFiles based on files parsed by the
 * multipart interceptor.
 *
 * @author Guilherme Silveira
 */
@Convert(UploadedFile.class)
public class UploadedFileConverter implements Converter<UploadedFile> {

	private static final Logger logger = LoggerFactory.getLogger(UploadedFileConverter.class);

    private final HttpServletRequest request;

    public UploadedFileConverter(HttpServletRequest request) {
        this.request = request;
    }

    public UploadedFile convert(String value, Class<? extends UploadedFile> type, ResourceBundle bundle) {
        Object upload = request.getAttribute(value);
        if (upload == null) {
        	logger.warn("There was an error when uploading the file {}. " +
        			"Please verify if commons-fileupload jars are in your classpath or you are using a Servlet 3 Container.");
        	throw new ConversionError("Invalid upload");
        }
		return type.cast(upload);
    }

}
