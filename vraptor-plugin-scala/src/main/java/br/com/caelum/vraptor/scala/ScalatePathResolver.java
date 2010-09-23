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
package br.com.caelum.vraptor.scala;

import java.io.File;

import javax.servlet.ServletContext;

import br.com.caelum.vraptor.http.FormatResolver;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.DefaultPathResolver;

/**
 * Path resolver for view files. It will default to ssp files in
 * "ssp/typeName/methodName.ssp" and fallback to
 * "jsp/typeName/methodName.jsp".
 * 
 * @author Alberto Souza <alberto.souza@caelum.com.br>
 * @author Bruno Oliveira
 * @author Pedro Matiello <pmatiello@gmail.com>
 */

@Component
@RequestScoped
public class ScalatePathResolver extends DefaultPathResolver {

	private ServletContext context;

	public ScalatePathResolver(ServletContext context, FormatResolver format) {
		super(format);
		this.context = context;
	}

	protected String getPrefix() {
		return "/WEB-INF/ssp/";
	}

	protected String getExtension() {
		return "ssp";
	}

	@Override
	public String pathFor(ResourceMethod method) {
		String path = super.pathFor(method);
		String realPathToViewFile = context.getRealPath(path);

		return new File(realPathToViewFile).exists() ? path : path.replace(
				"/WEB-INF/ssp", "/WEB-INF/jsp").replace(".ssp", ".jsp");
	}

}
