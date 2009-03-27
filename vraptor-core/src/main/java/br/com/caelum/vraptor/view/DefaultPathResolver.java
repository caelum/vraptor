package br.com.caelum.vraptor.view;

import br.com.caelum.vraptor.resource.ResourceMethod;

public class DefaultPathResolver implements PathResolver {

    public String pathFor(ResourceMethod method, String result) {
        return "/" + method.getResource().getType().getSimpleName() + "/" + method.getMethod().getName() + "." + result
                + ".jsp";
    }

}
