package br.com.caelum.vraptor.resource;

import br.com.caelum.vraptor.ioc.ApplicationScoped;

@ApplicationScoped
public class DefaultMethodLookupBuilder implements MethodLookupBuilder{

    public ResourceAndMethodLookup lookupFor(Resource r) {
        return new DefaultResourceAndMethodLookup(r);
    }

}
