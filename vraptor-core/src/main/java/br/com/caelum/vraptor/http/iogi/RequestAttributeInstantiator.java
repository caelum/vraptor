/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor.http.iogi;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.iogi.Instantiator;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.Target;

final class RequestAttributeInstantiator implements Instantiator<Object> {
	private final HttpServletRequest request;

	public RequestAttributeInstantiator(HttpServletRequest request) {
		this.request = request;
	}
	public Object instantiate(Target<?> target, Parameters params) {
		return request.getAttribute(target.getName());
	}

	public boolean isAbleToInstantiate(Target<?> target) {
		return request.getAttribute(target.getName()) != null;
	}

}