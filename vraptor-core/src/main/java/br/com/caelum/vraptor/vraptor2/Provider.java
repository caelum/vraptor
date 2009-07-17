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

import org.picocontainer.PicoContainer;
import org.vraptor.validator.BasicValidationErrors;
import org.vraptor.validator.ValidationErrors;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.core.RequestExecution;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.route.RoutesParser;
import br.com.caelum.vraptor.ioc.pico.PicoProvider;
import br.com.caelum.vraptor.ioc.pico.Scanner;
import br.com.caelum.vraptor.view.PageResult;
import br.com.caelum.vraptor.view.PathResolver;
import br.com.caelum.vraptor.vraptor2.outject.OutjectionInterceptor;

/**
 * Customized provider with support for both vraptor 2 and 3 components.
 *
 * @author Guilherme Silveira
 */
public class Provider extends PicoProvider {
	@Override
    protected void registerBundledComponents(ComponentRegistry registry) {
        super.registerBundledComponents(registry);
        registry.register(RoutesParser.class, ComponentRoutesParser.class);
        registry.register(PathResolver.class, VRaptor2PathResolver.class);
        registry.register(Config.class, VRaptor2Config.class);
        registry.register(ParameterNameProvider.class, LogicAnnotationWithParanamerParameterNameProvider.class);
        registry.register(RequestExecution.class, VRaptor2RequestExecution.class);
        registry.register(PageResult.class, ViewsPropertiesPageResult.class);
        registry.register(HibernateValidatorPluginInterceptor.class,HibernateValidatorPluginInterceptor.class);
        registry.register(Converters.class, VRaptor2Converters.class);
        registry.register(ValidatorInterceptor.class,ValidatorInterceptor.class);
        registry.register(ViewInterceptor.class,ViewInterceptor.class);
        registry.register(ComponentInfoProvider.class, DefaultComponentInfoProvider.class);
        registry.register(OutjectionInterceptor.class,OutjectionInterceptor.class);
        registry.register(AjaxInterceptor.class, AjaxInterceptor.class);
        registry.register(Validator.class, MessageCreatorValidator.class);
        registry.register(ValidationErrors.class, BasicValidationErrors.class);
        registry.register(VRaptor2ComponentRegistrar.class, VRaptor2ComponentRegistrar.class);
    }
    
    @Override
    protected void registerCustomComponents(PicoContainer picoContainer, Scanner scanner) {
    	super.registerCustomComponents(picoContainer, scanner);
    	picoContainer.getComponent(VRaptor2ComponentRegistrar.class).registerFrom(scanner);
    }
    
}
