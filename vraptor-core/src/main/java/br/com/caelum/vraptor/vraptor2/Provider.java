package br.com.caelum.vraptor.vraptor2;

import org.picocontainer.MutablePicoContainer;

import br.com.caelum.vraptor.http.AsmBasedTypeCreator;
import br.com.caelum.vraptor.http.StupidTranslator;
import br.com.caelum.vraptor.interceptor.DefaultInterceptorRegistry;
import br.com.caelum.vraptor.interceptor.InterceptorListPriorToExecutionExtractor;
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
    
    protected void registerComponents(MutablePicoContainer container) {
        container.addComponent(StupidTranslator.class);
        container.addComponent(DefaultResourceRegistry.class);
        container.addComponent(DefaultDirScanner.class);
        container.addComponent(WebInfClassesScanner.class);
        container.addComponent(InterceptorListPriorToExecutionExtractor.class);
        container.addComponent(DefaultInterceptorRegistry.class);
        container.addComponent(AsmBasedTypeCreator.class);
        container.addComponent(VRaptor2MethodLookupBuilder.class);
    }

}
