
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
