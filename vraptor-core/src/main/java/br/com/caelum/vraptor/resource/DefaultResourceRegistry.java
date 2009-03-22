package br.com.caelum.vraptor.resource;

import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.ioc.Container;

/**
 * This default registry uses a Path annotation to discover path->method
 * mappings using the DefaultResourceAndMethodLookup.
 * 
 * @author Guilherme Silveira
 */
public class DefaultResourceRegistry implements ResourceRegistry {

	private final List<DefaultResourceAndMethodLookup> resources = new ArrayList<DefaultResourceAndMethodLookup>();
	private final Container container;

	public DefaultResourceRegistry(Container container) {
		this.container = container;
	}

	public void register(List<Resource> results) {
		for (Resource r : results) {
			this.resources.add(new DefaultResourceAndMethodLookup(r));
		}
	}

	public ResourceMethod gimmeThis(String id, String methodName) {
		for (DefaultResourceAndMethodLookup lookuper : resources) {
			ResourceMethod method = lookuper.methodFor(id, methodName);
			if (method != null) {
				return method;
			}
		}
		return null;
	}

}
