package br.com.caelum.vraptor.core;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class URLParameterExtractorInterceptor implements Interceptor {

//    private final HttpServletRequest request;

    public URLParameterExtractorInterceptor(HttpServletRequest request) {
//        this.request = request;
    }

    public boolean accepts(ResourceMethod method) {
        return true;
    }

    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws InterceptionException {
        Path path = method.getResource().getType().getAnnotation(Path.class);
        if (path != null) {
            /*List<String> groupNames = new ArrayList<String>();
            String uri= request.getRequestURI();
            // TODO extract jsessionid
            String currentPath = path.value();
            int lastFromIndex = 0;
            while(true) {
                int fromIndex = currentPath.indexOf(":{", lastFromIndex);
                if(fromIndex==-1) {
                    break;
                }
                int toIndex = currentPath.indexOf("}", fromIndex);
                groupNames.add(currentPath.substring(fromIndex+2, toIndex-1));
                lastFromIndex = fromIndex;
            }*/
        }
        stack.next(method, resourceInstance);
    }

}
