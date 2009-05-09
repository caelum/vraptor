/***
 * 
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of the
 * copyright holders nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.core;

import br.com.caelum.vraptor.http.DefaultResourceTranslator;
import br.com.caelum.vraptor.http.OgnlParametersProvider;
import br.com.caelum.vraptor.http.ParanamerNameProvider;
import br.com.caelum.vraptor.http.route.DefaultRouter;
import br.com.caelum.vraptor.http.route.NoRoutesConfiguration;
import br.com.caelum.vraptor.http.route.PathAnnotationRoutesCreator;
import br.com.caelum.vraptor.interceptor.DefaultInterceptorRegistry;
import br.com.caelum.vraptor.resource.DefaultResourceNotFoundHandler;
import br.com.caelum.vraptor.validator.DefaultValidator;
import br.com.caelum.vraptor.view.DefaultPathResolver;

/**
 * List of base components to vraptor.<br/>
 * Those components should be available with any chosen ioc implementation.
 * 
 * @author guilherme silveira
 */
public class BaseComponents {

	public static Class<?>[] getApplicationScoped() {
		return new Class[] { DefaultResourceTranslator.class, DefaultRouter.class,
				DefaultResourceNotFoundHandler.class, DefaultInterceptorRegistry.class, ParanamerNameProvider.class,
				DefaultConverters.class, NoRoutesConfiguration.class, PathAnnotationRoutesCreator.class };
	}

	public static Class<?>[] getRequestScoped() {
		return new Class[] { DefaultPathResolver.class, DefaultMethodInfo.class, DefaultInterceptorStack.class,
				DefaultRequestExecution.class, DefaultResult.class, OgnlParametersProvider.class,
				DefaultMethodInfo.class, DefaultValidator.class, JstlLocalization.class};
	}

}
