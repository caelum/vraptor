package br.com.caelum.vraptor.vraptor2;

import br.com.caelum.vraptor.resource.Resource;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.DefaultPathResolver;
import br.com.caelum.vraptor.view.PathResolver;

/**
 * Vraptor 2 and 3 compatible path resolver.
 * 
 * @author Guilherme Silveira
 */
public class VRaptor2PathResolver implements PathResolver {

    private final PathResolver vraptor3 = new DefaultPathResolver();

    public String pathFor(ResourceMethod method, String result) {
        Resource resource = method.getResource();
        if (Info.isOldComponent(resource)) {
            String component = Info.getComponentName(resource.getType());
            String logicName = Info.getLogicName(method.getMethod());
            return "/" + component + "/" + logicName + "." + result + ".jsp";
        }
        return vraptor3.pathFor(method, result);
    }

}
