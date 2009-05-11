package br.com.caelum.vraptor.vraptor2;

import javax.servlet.http.HttpServletRequest;

import org.vraptor.annotations.Viewless;

import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.vraptor2.outject.DefaultOutjecter;
import br.com.caelum.vraptor.vraptor2.outject.JsonOutjecter;
import br.com.caelum.vraptor.vraptor2.outject.Outjecter;

public class DefaultComponentInfoProvider implements ComponentInfoProvider {

    private final HttpServletRequest request;
    private final Outjecter outjecter;

    public DefaultComponentInfoProvider(HttpServletRequest request) {
        this.request = request;
        // ignores if the view should be displayed or not
        if(isAjax()) {
            this.outjecter = new JsonOutjecter();
        } else {
            this.outjecter = new DefaultOutjecter(request);
        }
    }

    /**
     * Returns true if this is not a "Viewless" method, not an ajax or xml
     * request.
     */
    public boolean shouldShowView(ResourceMethod method) {
        return !method.getMethod().isAnnotationPresent(Viewless.class) && !isAjax();
    }

    /**
     * This is non-final so you can configure your own ajax discovery algorithm.
     */
    public boolean isAjax() {
        return request.getRequestURI().contains(".ajax.") || "ajax".equals(request.getParameter("view"));
    }

    public Outjecter getOutjecter() {
        return this.outjecter;
    }

}
