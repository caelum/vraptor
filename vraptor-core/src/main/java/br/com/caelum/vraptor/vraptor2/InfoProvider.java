package br.com.caelum.vraptor.vraptor2;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Provides information related to vraptor 2 for the request.
 * 
 * @author Guilherme Silveira
 */
public interface InfoProvider {

    /**
     * Whether a show view should be shown for this request.
     */
    boolean shouldShowView(HttpServletRequest request, ResourceMethod method);

    /**
     * Whether this is an ajax request.
     */
    boolean isAjax(HttpServletRequest request);

}
