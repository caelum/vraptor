package br.com.caelum.vraptor.vraptor2;

import javax.servlet.http.HttpServletRequest;

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

    private final PathResolver vraptor3;
    private final String pattern;
    
    public VRaptor2PathResolver(Config config, HttpServletRequest request) {
        this.pattern = config.getViewPattern();
        this.vraptor3 = new DefaultPathResolver(request);
    }

    public String pathFor(ResourceMethod method, String result) {
        Resource resource = method.getResource();
        if (Info.isOldComponent(resource)) {
            String component = Info.getComponentName(resource.getType());
            String logicName = Info.getLogicName(method.getMethod());
            return pattern.replaceAll("\\$component", component).replaceAll("\\$logic", logicName).replaceAll("\\$result", result);
        }
        return vraptor3.pathFor(method, result);
    }

}
