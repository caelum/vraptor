package br.com.caelum.vraptor.vraptor2;

import br.com.caelum.vraptor.resource.MethodLookupBuilder;
import br.com.caelum.vraptor.resource.Resource;
import br.com.caelum.vraptor.resource.ResourceAndMethodLookup;

/**
 * A vraptor 2 compatible method lookup builder.
 * 
 * @author Guilherme Silveira
 */
public class VRaptor2MethodLookupBuilder implements MethodLookupBuilder {

    public ResourceAndMethodLookup lookupFor(Resource r) {
        return new VRaptor2MethodLookup(r);
    }

}
