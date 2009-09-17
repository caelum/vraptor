package br.com.caelum.vraptor.mydvds.provider;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.util.hibernate.SessionCreator;
import br.com.caelum.vraptor.util.hibernate.SessionFactoryCreator;
import br.com.caelum.vraptor.vraptor2.Provider;

public class CustomProvider extends Provider {

    @Override
    public void registerBundledComponents(ComponentRegistry registry) {
        super.registerBundledComponents(registry);
        registry.register(SessionCreator.class, SessionCreator.class);
        registry.register(SessionFactoryCreator.class, SessionFactoryCreator.class);
    }

}
