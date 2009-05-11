package br.com.caelum.vraptor.ioc.spring.components;

import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Fabio Kung
 */
@Component
public class ConstructorInjection {
    private final DummyComponent dependecy;

    public ConstructorInjection(DummyComponent dependecy) {
        this.dependecy = dependecy;
    }

    public DummyComponent getDependecy() {
        return dependecy;
    }
}
