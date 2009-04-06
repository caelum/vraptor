package br.com.caelum.vraptor.resource;

public class DefaultMethodLookupBuilder implements MethodLookupBuilder{

    public ResourceAndMethodLookup lookupFor(Resource r) {
        return new DefaultResourceAndMethodLookup(r);
    }

}
