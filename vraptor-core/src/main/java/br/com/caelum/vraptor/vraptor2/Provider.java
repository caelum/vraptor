package br.com.caelum.vraptor.vraptor2;

import java.util.ArrayList;
import java.util.List;

import org.vraptor.validator.BasicValidationErrors;

import br.com.caelum.vraptor.core.DefaultInterceptorStack;
import br.com.caelum.vraptor.core.DefaultResult;
import br.com.caelum.vraptor.core.MethodParameters;
import br.com.caelum.vraptor.core.URLParameterExtractorInterceptor;
import br.com.caelum.vraptor.http.DefaultRequestParameters;
import br.com.caelum.vraptor.http.OgnlParametersProvider;
import br.com.caelum.vraptor.http.StupidTranslator;
import br.com.caelum.vraptor.http.asm.AsmBasedTypeCreator;
import br.com.caelum.vraptor.interceptor.DefaultInterceptorRegistry;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.interceptor.InstantiateInterceptor;
import br.com.caelum.vraptor.interceptor.InterceptorListPriorToExecutionExtractor;
import br.com.caelum.vraptor.interceptor.ParametersInstantiator;
import br.com.caelum.vraptor.interceptor.ResourceLookupInterceptor;
import br.com.caelum.vraptor.ioc.pico.DefaultDirScanner;
import br.com.caelum.vraptor.ioc.pico.PicoProvider;
import br.com.caelum.vraptor.ioc.pico.WebInfClassesScanner;
import br.com.caelum.vraptor.resource.DefaultResourceRegistry;

/**
 * Customized provider with support for both vraptor 2 and 3 components.
 * 
 * @author Guilherme Silveira
 */
public class Provider extends PicoProvider {

    protected List<Class<?>> getCoreComponents() {
        List<Class<?>> components = new ArrayList<Class<?>>();
        components.add(StupidTranslator.class);
        components.add(DefaultResourceRegistry.class);
        components.add(DefaultDirScanner.class);
        components.add(WebInfClassesScanner.class);
        components.add(DefaultInterceptorRegistry.class);
        components.add(AsmBasedTypeCreator.class);
        components.add(VRaptor2MethodLookupBuilder.class);
        components.add(VRaptor2PathResolver.class);
        components.add(VRaptor2Config.class);
        components.add(LogicAnnotationWithParanamerParameterNameProvider.class);
        return components;
    }

    protected List<Class<?>> getChildComponentTypes() {
        List<Class<?>> components = new ArrayList<Class<?>>();
        components.add(ParametersInstantiator.class);
        components.add(MethodParameters.class);
        components.add(DefaultRequestParameters.class);
        components.add(DefaultInterceptorStack.class);
        components.add(URLParameterExtractorInterceptor.class);
        components.add(InterceptorListPriorToExecutionExtractor.class);
        components.add(VRaptor2RequestExecution.class);
        components.add(ResourceLookupInterceptor.class);
        components.add(InstantiateInterceptor.class);
        components.add(DefaultResult.class);
        components.add(ExecuteAndViewInterceptor.class);
        components.add(HibernateValidatorPluginInterceptor.class);
        components.add(ViewsPropertiesPageResult.class);
        components.add(OgnlParametersProvider.class);
        components.add(VRaptor2Converters.class);
        components.add(Validator.class);
        components.add(ViewInterceptor.class);
        components.add(OutjectionInterceptor.class);
        components.add(RequestResult.class);
        registerValidationErrorsComponent(components);
        // TODO the following components are not required by vraptor2/3
        // compatibility mode, but was added for unit tests
        components.add(ExecuteMethodInterceptor.class);
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
