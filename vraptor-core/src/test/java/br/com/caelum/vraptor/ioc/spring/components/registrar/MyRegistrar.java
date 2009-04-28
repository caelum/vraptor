package br.com.caelum.vraptor.ioc.spring.components.registrar;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.http.UrlToResourceTranslator;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.ComponentRegistrar;
import br.com.caelum.vraptor.ioc.spring.components.CustomTranslator;

/**
 * @author Fabio Kung
 */
@Component
public class MyRegistrar implements ComponentRegistrar {
    public void register(ComponentRegistry registry) {
        registry.register(UrlToResourceTranslator.class, CustomTranslator.class);
    }
}
