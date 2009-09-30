
package br.com.caelum.vraptor.view;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * The default vraptor3 path resolver uses the type and method name as
 * "/TypeName/methodName.result.jsp".
 *
 * @author Guilherme Silveira
 * @author Sérgio Lopes
 * @author Jonas Abreu
 */
@RequestScoped
public class DefaultPathResolver implements PathResolver {

	private final HttpServletRequest request;
	private final AcceptHeaderToFormat acceptHeaderToFormat;

	public DefaultPathResolver(HttpServletRequest request, AcceptHeaderToFormat acceptHeaderToFormat) {
		this.request = request;
		this.acceptHeaderToFormat = acceptHeaderToFormat;
	}

	public String pathFor(ResourceMethod method) {
		String acceptedHeader = request.getHeader("Accept");
		String format = "html"; 
		
		if (acceptedHeader != null)
			format = acceptHeaderToFormat.getFormat(acceptedHeader);
		
		String formatParam = request.getParameter("_format");
		if (formatParam != null)
			format = formatParam;
		
		String suffix = "";
		if (format != null && !format.equals("html")) {
			suffix = "." + format;
		}
        String name = method.getResource().getType().getSimpleName();
        String folderName = extractControllerFromName(name);
		return getPrefix() + folderName + "/" + method.getMethod().getName() + suffix
				+ "."+getExtension();
	}

	protected String getPrefix() {
		return "/WEB-INF/jsp/";
	}

	protected String getExtension() {
		return "jsp";
	}

    protected String extractControllerFromName(String baseName) {
        baseName = lowerFirstCharacter(baseName);
        if (baseName.endsWith("Controller")) {
            return baseName.substring(0, baseName.lastIndexOf("Controller"));
        }
        return baseName;
    }

    private String lowerFirstCharacter(String baseName) {
        return baseName.toLowerCase().substring(0, 1) + baseName.substring(1, baseName.length());
    }
}
