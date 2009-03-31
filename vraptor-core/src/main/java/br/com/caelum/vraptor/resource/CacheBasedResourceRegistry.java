package br.com.caelum.vraptor.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A cached resource registry that avoids iterating over the entire set just in
 * order to find a resource already searched.
 * 
 * @author Guilherme Silveira
 */
public class CacheBasedResourceRegistry implements ResourceRegistry {

    private final ResourceRegistry delegate;

    private final Map<String, Map<String, ResourceMethod>> cache = new HashMap<String, Map<String, ResourceMethod>>();

    public CacheBasedResourceRegistry(ResourceRegistry delegate) {
        this.delegate = delegate;
    }

    public List<Resource> all() {
        return delegate.all();
    }

    public ResourceMethod gimmeThis(String name, String methodName) {
        if (!cache.containsKey(name)) {
            cache.put(name, new HashMap<String, ResourceMethod>());
        }
        Map<String, ResourceMethod> cachedMap = cache.get(name);
        if (!cachedMap.containsKey(methodName)) {
            cachedMap.put(methodName, delegate.gimmeThis(name, methodName));
        }
        return cachedMap.get(methodName);
    }

    public void register(List<Resource> resources) {
        delegate.register(resources);
    }

}
