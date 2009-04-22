/***
 *
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
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
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.vraptor2;

import java.util.ArrayList;
import java.util.List;

import org.vraptor.validator.BasicValidationErrors;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.core.DefaultInterceptorStack;
import br.com.caelum.vraptor.core.DefaultMethodParameters;
import br.com.caelum.vraptor.core.DefaultRequestInfo;
import br.com.caelum.vraptor.core.DefaultResult;
import br.com.caelum.vraptor.core.URLParameterExtractorInterceptor;
import br.com.caelum.vraptor.http.DefaultRequestParameters;
import br.com.caelum.vraptor.http.EmptyElementsRemoval;
import br.com.caelum.vraptor.http.OgnlParametersProvider;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.interceptor.InstantiateInterceptor;
import br.com.caelum.vraptor.interceptor.InterceptorListPriorToExecutionExtractor;
import br.com.caelum.vraptor.interceptor.ParametersInstantiator;
import br.com.caelum.vraptor.interceptor.ResourceLookupInterceptor;
import br.com.caelum.vraptor.ioc.pico.PicoProvider;

/**
 * Customized provider with support for both vraptor 2 and 3 components.
 *
 * @author Guilherme Silveira
 */
public class Provider extends PicoProvider {
    
    protected void registerComponents(ComponentRegistry container) {
        super.registerComponents(container);
        container.register(VRaptor2MethodLookupBuilder.class);
        container.register(VRaptor2PathResolver.class);
        container.register(VRaptor2Config.class);
        container.register(LogicAnnotationWithParanamerParameterNameProvider.class);
    }

    protected List<Class<?>> getRequestScopedComponents() {
        List<Class<?>> components = new ArrayList<Class<?>>();
        components.add(ParametersInstantiator.class);
        components.add(DefaultMethodParameters.class);
        components.add(DefaultRequestParameters.class);
        components.add(DefaultInterceptorStack.class);
        components.add(URLParameterExtractorInterceptor.class);
        components.add(InterceptorListPriorToExecutionExtractor.class);
        components.add(VRaptor2RequestExecution.class);
        components.add(ViewsPropertiesPageResult.class);
        components.add(ResourceLookupInterceptor.class);
        components.add(InstantiateInterceptor.class);
        components.add(DefaultResult.class);
        components.add(ExecuteAndViewInterceptor.class);
        components.add(HibernateValidatorPluginInterceptor.class);
        components.add(OgnlParametersProvider.class);
        components.add(VRaptor2Converters.class);
        components.add(ValidatorInterceptor.class);
        components.add(ViewInterceptor.class);
        components.add(DefaultComponentInfoProvider.class);
        components.add(OutjectionInterceptor.class);
        components.add(RequestResult.class);
        components.add(DefaultRequestInfo.class);
        components.add(EmptyElementsRemoval.class);
        components.add(ResultSupplierInterceptor.class);
        components.add(AjaxInterceptor.class);
        registerValidationErrorsComponent(components);
        // TODO the following components are not required by vraptor2/3
        // compatibility mode, but was added for unit tests
        components.add(ExecuteMethodInterceptor.class);
        components.add(MessageCreatorValidator.class);
        return components;
    }

    /**
     * Exists for backward compatibility with projects using an undocumented
     * feature of vraptor which makes it work with waffle taglib.
     */
    protected void registerValidationErrorsComponent(List<Class<?>> components) {
        components.add(BasicValidationErrors.class);
    }

}
