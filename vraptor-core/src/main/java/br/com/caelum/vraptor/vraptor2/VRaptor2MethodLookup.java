package br.com.caelum.vraptor.vraptor2;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import br.com.caelum.vraptor.resource.DefaultResourceAndMethodLookup;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.Resource;
import br.com.caelum.vraptor.resource.ResourceAndMethodLookup;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * A VRaptor 2 method lookup algorithm. Uses all public methods annotated with
 * Logic.
 * 
 * @author Guilherme Silveira
 */
public class VRaptor2MethodLookup implements ResourceAndMethodLookup {

    private final Resource resource;
    private final DefaultResourceAndMethodLookup delegate;

    public VRaptor2MethodLookup(Resource r) {
        this.delegate = new DefaultResourceAndMethodLookup(r);
        this.resource = r;
    }

    public ResourceMethod methodFor(String id, String methodName) {
        if(!Info.isOldComponent(resource)) {
            return delegate.methodFor(id, methodName);
        }
        Class<?> type = resource.getType();
        String componentName = Info.getComponentName(type);
        for (Method method : type.getDeclaredMethods()) {
            if ((!Modifier.isPublic(method.getModifiers())) || (Modifier.isStatic(method.getModifiers()))) {
                continue;
            }
            String logicName = Info.getLogicName(method);
            String entireName = "/" + componentName + "." + logicName + ".logic";
            if (entireName.equals(id)) {
                return new DefaultResourceMethod(resource, method);
            }
        }
        return null;
    }

}
