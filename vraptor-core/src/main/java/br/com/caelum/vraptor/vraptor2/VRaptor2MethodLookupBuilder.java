package br.com.caelum.vraptor.vraptor2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.resource.MethodLookupBuilder;
import br.com.caelum.vraptor.resource.Resource;
import br.com.caelum.vraptor.resource.ResourceAndMethodLookup;

/**
 * A vraptor 2 compatible method lookup builder.
 * 
 * @author Guilherme Silveira
 */
public class VRaptor2MethodLookupBuilder implements MethodLookupBuilder {
    
    private static final Logger logger = LoggerFactory.getLogger(VRaptor2MethodLookupBuilder.class);

    public ResourceAndMethodLookup lookupFor(Resource r) {
        if(Info.isOldComponent(r)) {
            logger.warn("Old component found, remember to migrate to vraptor3: " + r.getType().getName());
        }
        return new VRaptor2MethodLookup(r);
    }

}
