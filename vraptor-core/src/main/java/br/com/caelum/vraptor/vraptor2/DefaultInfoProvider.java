package br.com.caelum.vraptor.vraptor2;

import javax.servlet.http.HttpServletRequest;

import org.vraptor.annotations.Viewless;

import br.com.caelum.vraptor.resource.ResourceMethod;

public class DefaultInfoProvider implements InfoProvider {

    /**
     * Returns true if this is not a "Viewless" method, not an ajax or xml
     * request.
     */
    public boolean shouldShowView(HttpServletRequest request, ResourceMethod method) {
        if(method.getMethod().isAnnotationPresent(Viewless.class)) {
            return false;
        }
        return !isAjax(request);
    }

    public boolean isAjax(HttpServletRequest request) {
        if(request.getRequestURI().contains(".ajax.") || "ajax".equals(request.getParameter("view"))) {
            return true;
        }
        return false;
    }

}
