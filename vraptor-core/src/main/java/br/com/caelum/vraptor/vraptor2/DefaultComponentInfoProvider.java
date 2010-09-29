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
package br.com.caelum.vraptor.vraptor2;

import javax.servlet.http.HttpServletRequest;

import org.vraptor.annotations.Viewless;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.vraptor2.outject.DefaultOutjecter;
import br.com.caelum.vraptor.vraptor2.outject.JsonOutjecter;
import br.com.caelum.vraptor.vraptor2.outject.Outjecter;

@Component
public class DefaultComponentInfoProvider implements ComponentInfoProvider {

	private final HttpServletRequest request;
	private final Outjecter outjecter;

	public DefaultComponentInfoProvider(HttpServletRequest request) {
		this.request = request;
		// ignores if the view should be displayed or not
		if (isAjax()) {
			this.outjecter = new JsonOutjecter();
		} else {
			this.outjecter = new DefaultOutjecter(request);
		}
	}

	/**
	 * Returns true if this is not a "Viewless" method, not an ajax or xml
	 * request.
	 */
	public boolean shouldShowView(ResourceMethod method) {
		return !method.getMethod().isAnnotationPresent(Viewless.class) && !isAjax();
	}

	/**
	 * This is non-final so you can configure your own ajax discovery algorithm.
	 */
	public boolean isAjax() {
		return request.getRequestURI().contains(".ajax.") || "ajax".equals(request.getParameter("view"));
	}

	public Outjecter getOutjecter() {
		return this.outjecter;
	}

}
