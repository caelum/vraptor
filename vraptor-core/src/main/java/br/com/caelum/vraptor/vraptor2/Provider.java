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

import org.vraptor.validator.BasicValidationErrors;
import org.vraptor.validator.ValidationErrors;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.core.RequestExecution;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.route.RoutesParser;
import br.com.caelum.vraptor.ioc.spring.SpringProvider;
import br.com.caelum.vraptor.view.PageResult;
import br.com.caelum.vraptor.view.PathResolver;
import br.com.caelum.vraptor.vraptor2.outject.OutjectionInterceptor;

/**
 * Customized provider with support for both vraptor 2 and 3 components.
 *
 * @author Guilherme Silveira
 */
public class Provider extends SpringProvider {

    @Override
	protected void registerCustomComponents(ComponentRegistry registry) {
    	super.registerCustomComponents(registry);
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
        registry.register(VRaptor2ComponentHandler.class, VRaptor2ComponentHandler.class);
    }

}
