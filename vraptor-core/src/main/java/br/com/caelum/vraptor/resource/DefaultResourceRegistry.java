package br.com.caelum.vraptor.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This default registry uses a Path annotation to discover path->method
 * mappings using the DefaultResourceAndMethodLookup.
 * 
 * @author Guilherme Silveira
 */
public class DefaultResourceRegistry implements ResourceRegistry {

	private final List<DefaultResourceAndMethodLookup> lookup = new ArrayList<DefaultResourceAndMethodLookup>();
    private final List<Resource> resources = new ArrayList<Resource>();
    
    public DefaultResourceRegistry() {
        register(Arrays.asList((Resource) new DefaultResource(VRaptorInfo.class)));
    }

	public void register(List<Resource> results) {
		for (Resource r : results) {
			this.lookup.add(new DefaultResourceAndMethodLookup(r));
			this.resources.add(r);
		}
	}

	public ResourceMethod gimmeThis(String id, String methodName) {
		for (DefaultResourceAndMethodLookup lookuper : lookup) {
			ResourceMethod method = lookuper.methodFor(id, methodName);
			if (method != null) {
				return method;
			}
		}
		return null;
	}

    public List<Resource> all() {
        return this.resources;
    }

}
