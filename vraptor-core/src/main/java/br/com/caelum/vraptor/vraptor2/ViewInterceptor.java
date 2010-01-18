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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.PageResult;

/**
 * The vraptor 2 compatible view interceptor.
 *
 * @author guilherme silveira
 */
public class ViewInterceptor implements Interceptor {

	private static final Logger logger = LoggerFactory.getLogger(ViewInterceptor.class);

	private final PageResult result;
	private final ComponentInfoProvider info;

	public ViewInterceptor(PageResult result, ComponentInfoProvider info) {
		this.result = result;
		this.info = info;
	}

	public boolean accepts(ResourceMethod method) {
		return true;
	}

	public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
			throws InterceptionException {
		boolean vraptor2 = Info.isOldComponent(method.getResource());
		if (vraptor2) {
			if (info.shouldShowView(method)) {
				logger.debug("VRaptor 2 component forward");
				this.result.forward();
			} else {
				logger.debug("Not forwarding (viewless component)");
			}
		} else {
			stack.next(method, resourceInstance);
		}
	}

}
