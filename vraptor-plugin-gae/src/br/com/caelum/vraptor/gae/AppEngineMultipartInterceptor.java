/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.caelum.vraptor.gae;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemFactory;
import org.gmr.web.multipart.GFileItemFactory;

import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.interceptor.multipart.DefaultMultipartInterceptor;
import br.com.caelum.vraptor.interceptor.multipart.MultipartConfig;
import br.com.caelum.vraptor.ioc.Component;

@Component
public class AppEngineMultipartInterceptor extends DefaultMultipartInterceptor {

	public AppEngineMultipartInterceptor(HttpServletRequest request, MutableRequest parameters, MultipartConfig config, Validator validator)
			throws IOException {
		super(request, parameters, config, validator);
	}

	@Override
	protected FileItemFactory createFactoryForDiskBasedFileItems(File temporaryDirectory) {
		return new GFileItemFactory();
	}
}
