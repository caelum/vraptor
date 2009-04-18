package br.com.caelum.vraptor.vraptor2;

import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Provides information related to vraptor 2 for the request.
 * 
 * @author Guilherme Silveira
 */
public interface ComponentInfoProvider {

    /**
     * Whether a show view should be shown for this request.
     */
    boolean shouldShowView(ResourceMethod method);

    /**
     * Whether this is an ajax request.
     */
    boolean isAjax();

}
