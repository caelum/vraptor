
package br.com.caelum.vraptor.vraptor2;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceClass;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.AcceptHeaderToFormat;
import br.com.caelum.vraptor.view.DefaultPathResolver;
import br.com.caelum.vraptor.view.PathResolver;

/**
 * Vraptor 2 and 3 compatible path resolver.
 *
 * @author Guilherme Silveira
 */
@RequestScoped
public class VRaptor2PathResolver implements PathResolver {

    private final PathResolver vraptor3;
    private final String pattern;
	private final MethodInfo info;

    public VRaptor2PathResolver(Config config, HttpServletRequest request, MethodInfo info) {
        this.info = info;
		this.pattern = config.getViewPattern();
        this.vraptor3 = new DefaultPathResolver(request, new AcceptHeaderToFormat() {
			public String getFormat(String mimeType) {
				return "html";
			}
		});
    }

    public String pathFor(ResourceMethod method) {
        ResourceClass resource = method.getResource();
        if (Info.isOldComponent(resource)) {
            String component = Info.getComponentName(resource.getType());
            String logicName = Info.getLogicName(method.getMethod());
            return pattern.replaceAll("\\$component", component).replaceAll("\\$logic", logicName).replaceAll("\\$result", info.getResult().toString());
        }
        return vraptor3.pathFor(method);
    }

}
