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
package br.com.caelum.vraptor.validator;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.view.RequestOutjectMap;

/**
 * Default implementation for {@link Outjector}.
 * It uses a magic map that mimics objects on request.
 *
 * @author Lucas Cavalcanti
 * @since 3.0.2
 */
public class DefaultOutjector implements Outjector {

	private final HttpServletRequest request;

	public DefaultOutjector(HttpServletRequest request) {
		this.request = request;
	}

	public void outjectRequestMap() {
		@SuppressWarnings("unchecked")
		Set<String> paramNames = request.getParameterMap().keySet();

		for (String paramName : paramNames) {
			paramName = extractBaseParamName(paramName);
			if (request.getAttribute(paramName) == null) {
				request.setAttribute(paramName, new RequestOutjectMap(paramName, request));
			}
		}
	}

	private String extractBaseParamName(String paramName) {
		int indexOf = paramName.indexOf('.');
		paramName = paramName.substring(0, indexOf != -1 ? indexOf : paramName.length());

		indexOf = paramName.indexOf('[');
		paramName = paramName.substring(0, indexOf != -1 ? indexOf : paramName.length());
		return paramName;
	}
}
