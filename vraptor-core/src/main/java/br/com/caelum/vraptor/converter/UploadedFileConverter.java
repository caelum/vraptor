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

package br.com.caelum.vraptor.converter;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.interceptor.multipart.UploadedFile;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * VRaptor's file upload converter.
 *
 */
@Convert(UploadedFile.class)
public class UploadedFileConverter implements Converter<UploadedFile> {

	private final HttpServletRequest request;

	public UploadedFileConverter(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public UploadedFile convert(String value, Class<? extends UploadedFile> type, ResourceBundle bundle) {
		Object upload = request.getAttribute(value);
		return upload == null ? null : type.cast(upload);
	}
}
